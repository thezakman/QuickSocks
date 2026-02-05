package com.tzm.dirtysocks.ui;

import burp.api.montoya.MontoyaApi;
import com.tzm.dirtysocks.model.ProfileManager;
import com.tzm.dirtysocks.model.SocksProfile;
import com.tzm.dirtysocks.service.SocksProxyService;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

/**
 * Main user interface tab for QuickSocks extension.
 * Provides profile management, quick toggle, and connection testing.
 * 
 * @author TheZakMan
 */
public class MainTab extends JPanel {
    private static final Color GREEN = new Color(46, 204, 113);
    private static final Color GRAY = new Color(149, 165, 166);
    private static final Color ORANGE = new Color(230, 126, 34);
    private static final Color RED = new Color(231, 76, 60);
    private static final Color BLUE = new Color(52, 152, 219);

    private final ProfileManager profileManager;
    private final SocksProxyService proxyService;
    private final MontoyaApi api;

    private JTable profileTable;
    private DefaultTableModel tableModel;
    private JComboBox<SocksProfile> profileSelector;
    private JToggleButton toggleButton;
    private JLabel statusLabel;
    private JLabel ipResultLabel;
    private JButton testButton;

    public MainTab(ProfileManager profileManager, SocksProxyService proxyService, MontoyaApi api) {
        this.profileManager = profileManager;
        this.proxyService = proxyService;
        this.api = api;

        setLayout(new BorderLayout(0, 20));
        setBorder(new EmptyBorder(20, 25, 15, 25));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);

        profileManager.addChangeListener(this::refreshUI);
        refreshUI();
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(0, 0));

        // Left side: Logo + Title (big)
        JPanel leftSide = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));

        try {
            InputStream is = getClass().getResourceAsStream("/logo.png");
            if (is != null) {
                BufferedImage img = ImageIO.read(is);
                Image scaled = img.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                leftSide.add(new JLabel(new ImageIcon(scaled)));
            }
        } catch (Exception ignored) {}

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JLabel title = new JLabel("QuickSocks");
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        title.setForeground(ORANGE);

        JLabel subtitle = new JLabel("SOCKS Proxy Manager");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitle.setForeground(GRAY);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitle);

        leftSide.add(titlePanel);
        header.add(leftSide, BorderLayout.WEST);

        // Right side: Profile selector + Status + Toggle
        JPanel rightSide = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 30));

        rightSide.add(new JLabel("Profile:"));

        profileSelector = new JComboBox<>();
        profileSelector.setPreferredSize(new Dimension(150, 30));
        profileSelector.addActionListener(e -> onProfileSelected());
        rightSide.add(profileSelector);

        statusLabel = new JLabel();
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        rightSide.add(statusLabel);

        toggleButton = new JToggleButton("OFF");
        toggleButton.setPreferredSize(new Dimension(100, 50));
        toggleButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        toggleButton.setFocusPainted(false);
        toggleButton.addActionListener(e -> onToggle());
        rightSide.add(toggleButton);

        header.add(rightSide, BorderLayout.EAST);

        return header;
    }

    private JPanel createCenterPanel() {
        JPanel center = new JPanel(new BorderLayout(0, 10));

        // Table
        String[] columns = {"", "Name", "Host", "Port"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        profileTable = new JTable(tableModel);
        profileTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        profileTable.setRowHeight(28);
        profileTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        profileTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        profileTable.getColumnModel().getColumn(0).setMaxWidth(30);
        profileTable.getColumnModel().getColumn(0).setMinWidth(30);
        profileTable.getColumnModel().getColumn(3).setMaxWidth(80);

        profileTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setHorizontalAlignment(c == 3 ? CENTER : LEFT);

                String marker = (String) t.getValueAt(r, 0);
                if (!sel) {
                    if ("•".equals(marker)) {
                        comp.setBackground(new Color(46, 204, 113, 35));
                        comp.setForeground(GREEN);
                    } else {
                        comp.setBackground(t.getBackground());
                        comp.setForeground(t.getForeground());
                    }
                }
                return comp;
            }
        });

        profileTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) onEditProfile();
            }
        });

        JScrollPane scroll = new JScrollPane(profileTable);
        center.add(scroll, BorderLayout.CENTER);

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));

        buttons.add(makeButton("Activate", GREEN, this::onActivateSelected));
        buttons.add(makeButton("Add", null, this::onAddProfile));
        buttons.add(makeButton("Edit", null, this::onEditProfile));
        buttons.add(makeButton("Delete", null, this::onDeleteProfile));
        buttons.add(Box.createHorizontalStrut(25));
        buttons.add(makeButton("Import", null, () -> profileManager.importProfiles(this)));
        buttons.add(makeButton("Export", null, () -> profileManager.exportProfiles(this)));

        center.add(buttons, BorderLayout.SOUTH);

        return center;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(80, 80, 80)),
            new EmptyBorder(12, 0, 5, 0)
        ));

        // Left: Test IP
        JPanel testPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));

        testButton = new JButton("Test IP");
        testButton.setPreferredSize(new Dimension(90, 30));
        testButton.addActionListener(e -> onTestConnection());
        testPanel.add(testButton);

        ipResultLabel = new JLabel("Click 'Test IP' to verify your connection");
        ipResultLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        ipResultLabel.setForeground(GRAY);
        testPanel.add(ipResultLabel);

        footer.add(testPanel, BorderLayout.WEST);

        // Right: Credits with GitHub link
        JPanel creditPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        JLabel creditText = new JLabel("by ");
        creditText.setFont(new Font("SansSerif", Font.ITALIC, 11));
        creditText.setForeground(new Color(120, 120, 120));
        
        JLabel githubLink = new JLabel("TheZakMan");
        githubLink.setFont(new Font("SansSerif", Font.ITALIC | Font.BOLD, 11));
        githubLink.setForeground(new Color(52, 152, 219));
        githubLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        githubLink.setToolTipText("https://github.com/thezakman - Click to open");
        githubLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new java.net.URI("https://github.com/thezakman"));
                } catch (Exception ex) {
                    api.logging().logToError("Failed to open browser: " + ex.getMessage());
                }
            }
            private final Font normalFont = new Font("SansSerif", Font.ITALIC | Font.BOLD, 11);
            private final Font underlineFont = new Font("SansSerif", Font.ITALIC | Font.BOLD, 11);
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                // Criar fonte sublinhada via atributos
                java.util.Map<java.awt.font.TextAttribute, Object> attributes = new java.util.HashMap<>();
                attributes.put(java.awt.font.TextAttribute.UNDERLINE, java.awt.font.TextAttribute.UNDERLINE_ON);
                attributes.put(java.awt.font.TextAttribute.FAMILY, "SansSerif");
                attributes.put(java.awt.font.TextAttribute.POSTURE, java.awt.font.TextAttribute.POSTURE_OBLIQUE);
                attributes.put(java.awt.font.TextAttribute.WEIGHT, java.awt.font.TextAttribute.WEIGHT_BOLD);
                attributes.put(java.awt.font.TextAttribute.SIZE, 11);
                githubLink.setFont(new Font(attributes));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                githubLink.setFont(normalFont);
            }
        });
        
        creditPanel.add(creditText);
        creditPanel.add(githubLink);
        creditPanel.add(new JLabel("  "));
        footer.add(creditPanel, BorderLayout.EAST);

        return footer;
    }

    private JButton makeButton(String text, Color bg, Runnable action) {
        JButton btn = new JButton(text);
        btn.setMargin(new Insets(6, 14, 6, 14));
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        if (bg != null) {
            btn.setBackground(bg);
            btn.setForeground(Color.WHITE);
        }
        btn.addActionListener(e -> action.run());
        return btn;
    }

    private void updateToggleAppearance() {
        boolean enabled = profileManager.isProxyEnabled();
        toggleButton.setSelected(enabled);
        toggleButton.setText(enabled ? "ON" : "OFF");
        toggleButton.setBackground(enabled ? GREEN : GRAY);
        toggleButton.setForeground(Color.WHITE);

        statusLabel.setText(enabled ? "ACTIVE" : "DISABLED");
        statusLabel.setForeground(enabled ? GREEN : GRAY);
    }

    // === Event handlers ===

    private void onToggle() {
        boolean enabled = toggleButton.isSelected();
        profileManager.setProxyEnabled(enabled);
        profileManager.getActiveProfile().ifPresent(p -> proxyService.applyProfile(p, enabled));
        profileManager.saveToPreferences();
        updateToggleAppearance();
    }

    private void onProfileSelected() {
        SocksProfile sel = (SocksProfile) profileSelector.getSelectedItem();
        if (sel != null && !sel.getId().equals(profileManager.getActiveProfile().map(SocksProfile::getId).orElse(""))) {
            profileManager.setActiveProfile(sel.getId());
            if (profileManager.isProxyEnabled()) {
                proxyService.applyProfile(sel, true);
            }
            profileManager.saveToPreferences();
            refreshProfileTable();
        }
    }

    private void onActivateSelected() {
        int row = profileTable.getSelectedRow();
        if (row >= 0) {
            SocksProfile p = profileManager.getProfiles().get(row);
            profileManager.setActiveProfile(p.getId());
            profileManager.setProxyEnabled(true);
            proxyService.applyProfile(p, true);
            profileManager.saveToPreferences();
            refreshUI();
        }
    }

    private void onAddProfile() {
        ProfileEditDialog dlg = new ProfileEditDialog(SwingUtilities.getWindowAncestor(this), null);
        dlg.setVisible(true);
        if (dlg.isConfirmed()) {
            profileManager.addProfile(dlg.getProfile());
            profileManager.saveToPreferences();
        }
    }

    private void onEditProfile() {
        int row = profileTable.getSelectedRow();
        if (row >= 0) {
            SocksProfile p = profileManager.getProfiles().get(row);
            ProfileEditDialog dlg = new ProfileEditDialog(SwingUtilities.getWindowAncestor(this), p);
            dlg.setVisible(true);
            if (dlg.isConfirmed()) {
                profileManager.updateProfile(dlg.getProfile());
                if (profileManager.isProxyEnabled() && p.getId().equals(profileManager.getActiveProfile().map(SocksProfile::getId).orElse(""))) {
                    proxyService.applyProfile(p, true);
                }
                profileManager.saveToPreferences();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Please select a profile to edit.",
                "No Profile Selected",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void onDeleteProfile() {
        int row = profileTable.getSelectedRow();
        if (row >= 0) {
            if (profileManager.getProfiles().size() <= 1) {
                JOptionPane.showMessageDialog(this, 
                    "Cannot delete the last profile.\nAt least one profile must exist.", 
                    "Cannot Delete", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            SocksProfile p = profileManager.getProfiles().get(row);
            int result = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete '" + p.getName() + "'?\nThis action cannot be undone.", 
                "Confirm Deletion", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                profileManager.deleteProfile(p.getId());
                profileManager.saveToPreferences();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Please select a profile to delete.",
                "No Profile Selected",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void onTestConnection() {
        testButton.setEnabled(false);
        testButton.setText("Testing...");
        ipResultLabel.setText("Connecting...");
        ipResultLabel.setForeground(GRAY);

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                try {
                    URL url = new URL("https://ipinfo.io/json");
                    HttpURLConnection conn;

                    if (profileManager.isProxyEnabled()) {
                        SocksProfile profile = profileManager.getActiveProfile().orElse(null);
                        if (profile != null) {
                            Proxy proxy = new Proxy(Proxy.Type.SOCKS,
                                new InetSocketAddress(profile.getHost(), profile.getPort()));
                            conn = (HttpURLConnection) url.openConnection(proxy);
                        } else {
                            conn = (HttpURLConnection) url.openConnection();
                        }
                    } else {
                        conn = (HttpURLConnection) url.openConnection();
                    }

                    conn.setConnectTimeout(15000);
                    conn.setReadTimeout(15000);
                    conn.setRequestProperty("User-Agent", "curl/7.64.1");

                    int code = conn.getResponseCode();
                    if (code != 200) {
                        return "ERROR|HTTP " + code;
                    }

                    StringBuilder response = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                    }

                    String json = response.toString();
                    String ip = parseJsonValue(json, "ip");
                    String city = parseJsonValue(json, "city");
                    String region = parseJsonValue(json, "region");
                    String country = parseJsonValue(json, "country");
                    String org = parseJsonValue(json, "org");

                    return String.format("OK|%s|%s|%s|%s|%s", ip, city, region, country, org);

                } catch (Exception e) {
                    return "ERROR|" + e.getClass().getSimpleName() + ": " + e.getMessage();
                }
            }

            @Override
            protected void done() {
                try {
                    String result = get();
                    if (result.startsWith("OK|")) {
                        String[] parts = result.split("\\|");
                        String display = String.format("IP: %s  |  %s, %s, %s  |  %s",
                            parts[1], parts[2], parts[3], parts[4], parts[5]);
                        ipResultLabel.setText(display);
                        ipResultLabel.setForeground(GREEN);
                    } else {
                        String error = result.substring(6);
                        ipResultLabel.setText("Error: " + error);
                        ipResultLabel.setForeground(RED);
                    }
                } catch (Exception e) {
                    ipResultLabel.setText("Error: " + e.getMessage());
                    ipResultLabel.setForeground(RED);
                }
                testButton.setEnabled(true);
                testButton.setText("Test IP");
            }
        };
        worker.execute();
    }

    private String parseJsonValue(String json, String key) {
        try {
            String pattern = "\"" + key + "\"";
            int keyIndex = json.indexOf(pattern);
            if (keyIndex < 0) return "-";

            int colonIndex = json.indexOf(":", keyIndex);
            if (colonIndex < 0) return "-";

            int start = json.indexOf("\"", colonIndex);
            if (start < 0) return "-";
            start++;

            int end = json.indexOf("\"", start);
            if (end < 0) return "-";

            return json.substring(start, end);
        } catch (Exception e) {
            return "-";
        }
    }

    // === UI refresh ===

    private void refreshUI() {
        SwingUtilities.invokeLater(() -> {
            updateProfileSelector();
            refreshProfileTable();
            updateToggleAppearance();
        });
    }

    private void updateProfileSelector() {
        profileSelector.removeAllItems();
        for (SocksProfile p : profileManager.getProfiles()) {
            profileSelector.addItem(p);
        }
        profileManager.getActiveProfile().ifPresent(profileSelector::setSelectedItem);
    }

    private void refreshProfileTable() {
        tableModel.setRowCount(0);
        String activeId = profileManager.getActiveProfile().map(SocksProfile::getId).orElse("");
        for (SocksProfile p : profileManager.getProfiles()) {
            tableModel.addRow(new Object[]{
                p.getId().equals(activeId) ? "*" : "",
                p.getName(),
                p.getHost(),
                p.getPort()
            });
        }
    }
}
