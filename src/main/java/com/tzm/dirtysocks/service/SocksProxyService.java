package com.tzm.dirtysocks.service;

import burp.api.montoya.MontoyaApi;
import com.tzm.dirtysocks.model.SocksProfile;

import javax.swing.*;

/**
 * Service for applying SOCKS proxy configurations to Burp Suite.
 * Handles enabling/disabling proxies and applying profile settings.
 * 
 * @author TheZakMan
 */
public class SocksProxyService {
    private final MontoyaApi api;
    private Runnable refocusCallback;

    public SocksProxyService(MontoyaApi api) {
        this.api = api;
    }

    /**
     * Sets a callback to attempt refocusing the extension tab after applying settings.
     * This is a workaround for the Montoya API's tab-switching behavior.
     * 
     * @param callback Runnable to execute after settings are applied
     */
    public void setRefocusCallback(Runnable callback) {
        this.refocusCallback = callback;
    }

    /**
     * Applies a SOCKS proxy profile to Burp Suite.
     * 
     * Note: Due to a Montoya API limitation, calling importUserOptionsFromJson()
     * causes Burp Suite to switch focus to the Dashboard tab. This is a known
     * behavior that cannot be prevented from extensions.
     * 
     * @param profile The profile to apply
     * @param enable Whether to enable or disable the proxy
     * @throws IllegalArgumentException if profile is null
     */
    public void applyProfile(SocksProfile profile, boolean enable) {
        if (profile == null) {
            throw new IllegalArgumentException("Profile cannot be null");
        }
        
        String json = profile.toBurpJson(enable);

        try {
            // KNOWN LIMITATION: importUserOptionsFromJson() forces focus to Dashboard tab
            // This is a Montoya API behavior that cannot be overridden by extensions
            api.burpSuite().importUserOptionsFromJson(json);

            // WORKAROUND: Attempt to refocus on the extension tab
            // Schedule this to run after Burp's tab switch completes
            if (refocusCallback != null) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        // Small delay to ensure Burp has finished tab switching
                        Thread.sleep(50);
                        refocusCallback.run();
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                    }
                });
            }

            if (enable) {
                String authInfo = profile.hasAuthentication() ? " [Authenticated]" : "";
                api.logging().logToOutput(
                    String.format("QuickSocks: SOCKS proxy ENABLED - %s (%s:%d)%s",
                        profile.getName(), 
                        profile.getHost(), 
                        profile.getPort(),
                        authInfo)
                );
            } else {
                api.logging().logToOutput("QuickSocks: SOCKS proxy DISABLED");
            }
        } catch (Exception e) {
            api.logging().logToError("QuickSocks ERROR applying profile: " + e.getMessage());
            throw new RuntimeException("Failed to apply SOCKS profile", e);
        }
    }

    /**
     * Disables the SOCKS proxy in Burp Suite.
     */
    public void disableProxy() {
        String json = """
            {
                "user_options": {
                    "connections": {
                        "socks_proxy": {
                            "use_proxy": false
                        }
                    }
                }
            }
            """;
        try {
            api.burpSuite().importUserOptionsFromJson(json);
            
            // WORKAROUND: Attempt to refocus on the extension tab
            if (refocusCallback != null) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        Thread.sleep(50);
                        refocusCallback.run();
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
            
            api.logging().logToOutput("QuickSocks: SOCKS proxy DISABLED");
        } catch (Exception e) {
            api.logging().logToError("QuickSocks ERROR disabling proxy: " + e.getMessage());
        }
    }

    public String exportCurrentSettings() {
        return api.burpSuite().exportUserOptionsAsJson();
    }
}
