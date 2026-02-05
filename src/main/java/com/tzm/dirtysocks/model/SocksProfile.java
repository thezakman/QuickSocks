package com.tzm.dirtysocks.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a SOCKS proxy profile configuration.
 * Stores all necessary information to configure a SOCKS proxy in Burp Suite.
 * 
 * @author TheZakMan
 */
public class SocksProfile {
    private String id;
    private String name;
    private String host;
    private int port;
    private String username;
    private String password;
    private boolean dnsOverSocks;

    public SocksProfile() {
        this.id = UUID.randomUUID().toString();
        this.name = "";
        this.host = "localhost";
        this.port = 1080;
        this.username = "";
        this.password = "";
        this.dnsOverSocks = false;
    }

    public SocksProfile(String name, String host, int port) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.host = host;
        this.port = port;
        this.username = "";
        this.password = "";
        this.dnsOverSocks = false;
    }

    /**
     * Creates a new SOCKS profile with all parameters.
     * 
     * @param id Unique identifier for the profile
     * @param name Display name of the profile
     * @param host SOCKS proxy host address
     * @param port SOCKS proxy port (1-65535)
     * @param username Optional username for authentication
     * @param password Optional password for authentication
     * @param dnsOverSocks Whether to route DNS queries through SOCKS
     * @throws IllegalArgumentException if port is invalid or host/name is null
     */
    public SocksProfile(String id, String name, String host, int port,
                        String username, String password, boolean dnsOverSocks) {
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Port must be between 1 and 65535");
        }
        if (host == null || host.trim().isEmpty()) {
            throw new IllegalArgumentException("Host cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.name = name.trim();
        this.host = host.trim();
        this.port = port;
        this.username = username != null ? username : "";
        this.password = password != null ? password : "";
        this.dnsOverSocks = dnsOverSocks;
    }

    public SocksProfile copy(String newName) {
        return new SocksProfile(
            UUID.randomUUID().toString(),
            newName,
            this.host,
            this.port,
            this.username,
            this.password,
            this.dnsOverSocks
        );
    }

    public String toBurpJson(boolean useProxy) {
        String escapedHost = escapeJson(host);
        String escapedUsername = escapeJson(username);
        String escapedPassword = escapeJson(password);

        return """
            {
                "user_options": {
                    "connections": {
                        "socks_proxy": {
                            "dns_over_socks": %b,
                            "host": "%s",
                            "password": "%s",
                            "port": %d,
                            "use_proxy": %b,
                            "username": "%s"
                        }
                    }
                }
            }
            """.formatted(dnsOverSocks, escapedHost, escapedPassword, port, useProxy, escapedUsername);
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    /**
     * Sets the profile display name.
     * 
     * @param name Display name (cannot be null or empty)
     * @throws IllegalArgumentException if name is null or empty
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name.trim();
    }

    public String getHost() {
        return host;
    }

    /**
     * Sets the SOCKS proxy host address.
     * 
     * @param host Host address (cannot be null or empty)
     * @throws IllegalArgumentException if host is null or empty
     */
    public void setHost(String host) {
        if (host == null || host.trim().isEmpty()) {
            throw new IllegalArgumentException("Host cannot be null or empty");
        }
        this.host = host.trim();
    }

    public int getPort() {
        return port;
    }

    /**
     * Sets the SOCKS proxy port.
     * 
     * @param port Port number (must be between 1 and 65535)
     * @throws IllegalArgumentException if port is invalid
     */
    public void setPort(int port) {
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Port must be between 1 and 65535");
        }
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username != null ? username : "";
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password != null ? password : "";
    }

    public boolean isDnsOverSocks() {
        return dnsOverSocks;
    }

    public void setDnsOverSocks(boolean dnsOverSocks) {
        this.dnsOverSocks = dnsOverSocks;
    }

    public boolean hasAuthentication() {
        return username != null && !username.isEmpty();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SocksProfile that = (SocksProfile) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
