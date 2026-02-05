package com.tzm.dirtysocks.ui;

import com.tzm.dirtysocks.model.SocksProfile;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for adding or editing SOCKS proxy profiles.
 * Provides input validation and user-friendly error messages.
 * 
 * @author TheZakMan
 */
public class ProfileEditDialog extends JDialog {
    private JTextField nameField;
    private JTextField hostField;
    private JSpinner portSpinner;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox dnsOverSocksCheckbox;

    private SocksProfile profile;
    private boolean confirmed = false;

    public ProfileEditDialog(Window parent, SocksProfile existingProfile) {
        super(parent, existingProfile == null ? "Add Profile" : "Edit Profile", ModalityType.APPLICATION_MODAL);
        this.profile = existingProfile;

        initComponents();
        if (existingProfile != null) {
            populateFields(existingProfile);
        }

        pack();
        setMinimumSize(new Dimension(400, 300));
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Profile Name:"), gbc);
        nameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainPanel.add(nameField, gbc);

        // Host
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Host:"), gbc);
        hostField = new JTextField(20);
        hostField.setText("localhost");
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainPanel.add(hostField, gbc);

        // Port
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Port:"), gbc);
        portSpinner = new JSpinner(new SpinnerNumberModel(1080, 1, 65535, 1));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(portSpinner, "#");
        portSpinner.setEditor(editor);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainPanel.add(portSpinner, gbc);

        // Separator
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        // Username (optional)
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Username (optional):"), gbc);
        usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainPanel.add(usernameField, gbc);

        // Password (optional)
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Password (optional):"), gbc);
        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainPanel.add(passwordField, gbc);

        // Separator
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        // DNS over SOCKS
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        dnsOverSocksCheckbox = new JCheckBox("DNS over SOCKS");
        dnsOverSocksCheckbox.setToolTipText("Route DNS queries through the SOCKS proxy");
        mainPanel.add(dnsOverSocksCheckbox, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> onSave());
        getRootPane().setDefaultButton(saveButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);
    }

    private void populateFields(SocksProfile profile) {
        nameField.setText(profile.getName());
        hostField.setText(profile.getHost());
        portSpinner.setValue(profile.getPort());
        usernameField.setText(profile.getUsername());
        passwordField.setText(profile.getPassword());
        dnsOverSocksCheckbox.setSelected(profile.isDnsOverSocks());
    }

    private void onSave() {
        String name = nameField.getText().trim();
        String host = hostField.getText().trim();
        int port = (Integer) portSpinner.getValue();

        // Validate name
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Profile Name is required",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return;
        }

        // Validate host
        if (host.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Host is required",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            hostField.requestFocus();
            return;
        }

        // Validate port range
        if (port < 1 || port > 65535) {
            JOptionPane.showMessageDialog(this,
                "Port must be between 1 and 65535",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            portSpinner.requestFocus();
            return;
        }

        try {
            if (profile == null) {
                profile = new SocksProfile(name, host, port);
            } else {
                profile.setName(name);
                profile.setHost(host);
                profile.setPort(port);
            }

            profile.setUsername(usernameField.getText().trim());
            profile.setPassword(new String(passwordField.getPassword()));
            profile.setDnsOverSocks(dnsOverSocksCheckbox.isSelected());

            confirmed = true;
            dispose();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                "Invalid input: " + e.getMessage(),
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public SocksProfile getProfile() {
        return profile;
    }
}
