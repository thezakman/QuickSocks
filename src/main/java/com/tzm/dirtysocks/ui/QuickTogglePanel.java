package com.tzm.dirtysocks.ui;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Reusable quick toggle panel component for enabling/disabling SOCKS proxy.
 * Provides a clean toggle button with status indicator.
 * 
 * @author TheZakMan
 */
public class QuickTogglePanel extends JPanel {
    private static final Color ENABLED_COLOR = new Color(46, 204, 113);
    private static final Color DISABLED_COLOR = new Color(149, 165, 166);
    private static final Color TEXT_COLOR = Color.WHITE;

    private final JToggleButton toggleButton;
    private final JLabel statusLabel;
    private final Consumer<Boolean> onToggle;

    public QuickTogglePanel(boolean initialState, Consumer<Boolean> onToggle) {
        this.onToggle = onToggle;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Quick Toggle"
            ),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        toggleButton = new JToggleButton(initialState ? "ON" : "OFF");
        toggleButton.setSelected(initialState);
        toggleButton.setPreferredSize(new Dimension(120, 50));
        toggleButton.setMaximumSize(new Dimension(120, 50));
        toggleButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        toggleButton.setFocusPainted(false);
        toggleButton.setOpaque(true);
        toggleButton.setBorderPainted(false);
        updateToggleAppearance(initialState);

        toggleButton.addActionListener(e -> {
            boolean enabled = toggleButton.isSelected();
            toggleButton.setText(enabled ? "ON" : "OFF");
            updateToggleAppearance(enabled);
            onToggle.accept(enabled);
        });

        statusLabel = new JLabel(initialState ? "Proxy Active" : "Proxy Disabled");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        updateStatusLabel(initialState);

        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonWrapper.setOpaque(false);
        buttonWrapper.add(toggleButton);

        add(buttonWrapper);
        add(Box.createVerticalStrut(8));
        add(statusLabel);
    }

    private void updateToggleAppearance(boolean enabled) {
        toggleButton.setBackground(enabled ? ENABLED_COLOR : DISABLED_COLOR);
        toggleButton.setForeground(TEXT_COLOR);
    }

    private void updateStatusLabel(boolean enabled) {
        statusLabel.setText(enabled ? "Proxy Active" : "Proxy Disabled");
        statusLabel.setForeground(enabled ? ENABLED_COLOR : DISABLED_COLOR);
    }

    public void setToggleState(boolean enabled) {
        toggleButton.setSelected(enabled);
        toggleButton.setText(enabled ? "ON" : "OFF");
        updateToggleAppearance(enabled);
        updateStatusLabel(enabled);
    }

    public boolean isToggleEnabled() {
        return toggleButton.isSelected();
    }
}
