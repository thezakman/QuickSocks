package com.tzm.dirtysocks;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.editor.extension.ExtensionProvidedEditor;
import com.tzm.dirtysocks.model.ProfileManager;
import com.tzm.dirtysocks.service.SocksProxyService;
import com.tzm.dirtysocks.ui.MainTab;

import javax.swing.*;

/**
 * Main UI controller for the QuickSocks extension.
 * Manages initialization, lifecycle, and coordination between components.
 * 
 * @author TheZakMan
 */
public class DirtySocksUI {
    public static final String EXTENSION_NAME = Constants.EXTENSION_NAME;
    public static final String VERSION = Constants.VERSION;

    private final MontoyaApi api;
    private final ProfileManager profileManager;
    private final SocksProxyService proxyService;

    private MainTab mainTab;

    public DirtySocksUI(MontoyaApi api) {
        this.api = api;
        this.profileManager = new ProfileManager(api.persistence().preferences());
        this.proxyService = new SocksProxyService(api);
    }

    public void initializeUI() {
        SwingUtilities.invokeLater(() -> {
            mainTab = new MainTab(profileManager, proxyService, api);

            api.userInterface().registerSuiteTab(EXTENSION_NAME, mainTab);

            // Set up refocus callback to attempt returning to extension tab
            // This is a workaround for the Montoya API's tab-switching behavior
            proxyService.setRefocusCallback(() -> {
                try {
                    // Request focus on our tab component
                    if (mainTab != null && mainTab.isDisplayable()) {
                        mainTab.requestFocusInWindow();
                        
                        // Try to find and click our tab in the parent TabbedPane
                        java.awt.Container parent = mainTab.getParent();
                        while (parent != null) {
                            if (parent instanceof javax.swing.JTabbedPane) {
                                javax.swing.JTabbedPane tabbedPane = (javax.swing.JTabbedPane) parent;
                                int index = tabbedPane.indexOfComponent(mainTab);
                                if (index >= 0) {
                                    tabbedPane.setSelectedIndex(index);
                                    api.logging().logToOutput("QuickSocks: Refocused on extension tab");
                                    break;
                                }
                            }
                            parent = parent.getParent();
                        }
                    }
                } catch (Exception e) {
                    // Silently fail - this is a best-effort workaround
                    api.logging().logToError("QuickSocks: Could not refocus tab - " + e.getMessage());
                }
            });

            api.logging().logToOutput(
                Constants.FULL_NAME + " loaded successfully!\n" +
                "Profiles loaded: " + profileManager.getProfiles().size()
            );
        });
    }

    public void onUnload() {
        profileManager.saveToPreferences();
        api.logging().logToOutput(Constants.EXTENSION_NAME + ": Settings saved on unload.");
    }

    public String getExtensionName() {
        return EXTENSION_NAME;
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }
}
