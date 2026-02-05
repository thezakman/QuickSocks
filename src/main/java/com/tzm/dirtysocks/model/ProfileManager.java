package com.tzm.dirtysocks.model;

import burp.api.montoya.persistence.Preferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Manages SOCKS proxy profiles including persistence, import/export, and change notifications.
 * Handles loading/saving profiles to Burp Suite preferences and file system.
 * 
 * @author TheZakMan
 */
public class ProfileManager {
    private static final String PROFILES_KEY = "quicksocks.profiles";
    private static final String ACTIVE_PROFILE_KEY = "quicksocks.active_profile_id";
    private static final String PROXY_ENABLED_KEY = "quicksocks.proxy_enabled";

    private final Preferences preferences;
    private final Gson gson;
    private final List<SocksProfile> profiles;
    private final List<Runnable> changeListeners;

    private String activeProfileId;
    private boolean proxyEnabled;

    public ProfileManager(Preferences preferences) {
        this.preferences = preferences;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.profiles = new ArrayList<>();
        this.changeListeners = new ArrayList<>();

        loadFromPreferences();
    }

    private void loadFromPreferences() {
        String json = preferences.getString(PROFILES_KEY);
        if (json != null && !json.isBlank()) {
            try {
                List<SocksProfile> loaded = gson.fromJson(json,
                    new TypeToken<List<SocksProfile>>(){}.getType());
                if (loaded != null && !loaded.isEmpty()) {
                    profiles.addAll(loaded);
                }
            } catch (Exception e) {
                // If parsing fails, log the error and start with empty list
                System.err.println("Failed to load profiles from preferences: " + e.getMessage());
            }
        }

        // Ensure at least one default profile exists
        if (profiles.isEmpty()) {
            profiles.add(new SocksProfile("Default", "localhost", 1080));
        }

        // Load and validate active profile
        activeProfileId = preferences.getString(ACTIVE_PROFILE_KEY);
        if (activeProfileId == null || getProfileById(activeProfileId).isEmpty()) {
            activeProfileId = profiles.get(0).getId();
        }

        // Load proxy enabled state
        Boolean enabled = preferences.getBoolean(PROXY_ENABLED_KEY);
        proxyEnabled = enabled != null && enabled;
    }

    public void saveToPreferences() {
        String json = gson.toJson(profiles);
        preferences.setString(PROFILES_KEY, json);
        preferences.setString(ACTIVE_PROFILE_KEY, activeProfileId);
        preferences.setBoolean(PROXY_ENABLED_KEY, proxyEnabled);
    }

    // CRUD Operations
    public List<SocksProfile> getProfiles() {
        return new ArrayList<>(profiles);
    }

    /**
     * Adds a new profile to the manager.
     * 
     * @param profile The profile to add (cannot be null)
     * @throws IllegalArgumentException if profile is null
     */
    public void addProfile(SocksProfile profile) {
        if (profile == null) {
            throw new IllegalArgumentException("Profile cannot be null");
        }
        profiles.add(profile);
        notifyListeners();
    }

    /**
     * Updates an existing profile.
     * 
     * @param profile The profile to update (must have a matching ID)
     * @throws IllegalArgumentException if profile is null
     */
    public void updateProfile(SocksProfile profile) {
        if (profile == null) {
            throw new IllegalArgumentException("Profile cannot be null");
        }
        for (int i = 0; i < profiles.size(); i++) {
            if (profiles.get(i).getId().equals(profile.getId())) {
                profiles.set(i, profile);
                notifyListeners();
                return;
            }
        }
    }

    public void deleteProfile(String profileId) {
        if (profiles.size() <= 1) {
            return; // Keep at least one profile
        }
        profiles.removeIf(p -> p.getId().equals(profileId));
        if (activeProfileId.equals(profileId) && !profiles.isEmpty()) {
            activeProfileId = profiles.get(0).getId();
        }
        notifyListeners();
    }

    public SocksProfile duplicateProfile(SocksProfile profile) {
        SocksProfile copy = profile.copy(profile.getName() + " (Copy)");
        profiles.add(copy);
        notifyListeners();
        return copy;
    }

    public Optional<SocksProfile> getProfileById(String id) {
        return profiles.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst();
    }

    public Optional<SocksProfile> getActiveProfile() {
        return getProfileById(activeProfileId);
    }

    public void setActiveProfile(String profileId) {
        if (getProfileById(profileId).isPresent()) {
            this.activeProfileId = profileId;
            notifyListeners();
        }
    }

    public boolean isProxyEnabled() {
        return proxyEnabled;
    }

    public void setProxyEnabled(boolean enabled) {
        this.proxyEnabled = enabled;
        notifyListeners();
    }

    // Change Listeners
    public void addChangeListener(Runnable listener) {
        changeListeners.add(listener);
    }

    private void notifyListeners() {
        for (Runnable listener : changeListeners) {
            listener.run();
        }
    }

    // Import/Export
    public void exportProfiles(JComponent parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Profiles");
        fileChooser.setSelectedFile(new File("quicksocks-profiles.json"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON files", "json"));

        int result = fileChooser.showSaveDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(".json")) {
                file = new File(file.getAbsolutePath() + ".json");
            }
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                gson.toJson(profiles, writer);
                JOptionPane.showMessageDialog(parent,
                    "Exported " + profiles.size() + " profile(s) to:\n" + file.getAbsolutePath(),
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent,
                    "Failed to export profiles: " + e.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void importProfiles(JComponent parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import Profiles");
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON files", "json"));

        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                List<SocksProfile> imported = gson.fromJson(reader,
                    new TypeToken<List<SocksProfile>>(){}.getType());

                if (imported != null && !imported.isEmpty()) {
                    int option = JOptionPane.showConfirmDialog(parent,
                        "Found " + imported.size() + " profile(s).\nDo you want to replace existing profiles or merge?",
                        "Import Profiles",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null);

                    if (option == JOptionPane.YES_OPTION) {
                        // Replace all
                        profiles.clear();
                        profiles.addAll(imported);
                        activeProfileId = profiles.get(0).getId();
                    } else if (option == JOptionPane.NO_OPTION) {
                        // Merge (add imported profiles)
                        profiles.addAll(imported);
                    } else {
                        return; // Cancel
                    }

                    notifyListeners();
                    saveToPreferences();
                    JOptionPane.showMessageDialog(parent,
                        "Imported " + imported.size() + " profile(s).",
                        "Import Successful",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(parent,
                        "No profiles found in the file.",
                        "Import Warning",
                        JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(parent,
                    "Failed to import profiles: " + e.getMessage(),
                    "Import Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
