package com.tzm.dirtysocks.service;

import burp.api.montoya.MontoyaApi;
import com.tzm.dirtysocks.model.SocksProfile;

/**
 * Service for applying SOCKS proxy configurations to Burp Suite.
 * Handles enabling/disabling proxies and applying profile settings.
 * 
 * @author TheZakMan
 */
public class SocksProxyService {
    private final MontoyaApi api;

    public SocksProxyService(MontoyaApi api) {
        this.api = api;
    }

    /**
     * Applies a SOCKS proxy profile to Burp Suite.
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
            api.burpSuite().importUserOptionsFromJson(json);

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
            api.logging().logToOutput("QuickSocks: SOCKS proxy DISABLED");
        } catch (Exception e) {
            api.logging().logToError("QuickSocks ERROR disabling proxy: " + e.getMessage());
        }
    }

    public String exportCurrentSettings() {
        return api.burpSuite().exportUserOptionsAsJson();
    }
}
