package com.tzm.dirtysocks;

/**
 * Application-wide constants for QuickSocks.
 * Centralized location for version and other constant values.
 * 
 * @author TheZakMan
 */
public final class Constants {
    
    /** Extension name */
    public static final String EXTENSION_NAME = "QuickSocks";
    
    /** Extension version - SINGLE SOURCE OF TRUTH for version number */
    public static final String VERSION = "1.0.0";
    
    /** Full extension display name with version */
    public static final String FULL_NAME = EXTENSION_NAME + " v" + VERSION;
    
    /** Author name */
    public static final String AUTHOR = "TheZakMan";
    
    /** GitHub repository URL */
    public static final String GITHUB_URL = "https://github.com/thezakman/QuickSocks";
    
    // Prevent instantiation
    private Constants() {
        throw new AssertionError("Constants class cannot be instantiated");
    }
}
