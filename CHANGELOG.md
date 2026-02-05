# Changelog

All notable changes to QuickSocks will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Planned
- SOCKS4/SOCKS5 protocol selection
- Profile groups and categories
- Keyboard shortcuts
- Automated testing suite
- Profile templates

## [1.0.0] - 2026-02-05

### Added
- Initial release of QuickSocks
- Multiple SOCKS proxy profile management
- Quick toggle button for enabling/disabling proxy
- Profile CRUD operations (Create, Read, Update, Delete)
- Import/Export profiles to JSON
- SOCKS authentication support (username/password)
- DNS over SOCKS option
- Connection testing with IP geolocation
- Visual active profile indicator
- Profile duplication feature
- Persistent storage using Burp Suite preferences
- Clean and intuitive user interface
- Real-time status updates
- Profile dropdown selector
- Table-based profile management
- Double-click to activate profiles
- Validation for all input fields
- Error handling and user-friendly messages
- JavaDoc documentation for all classes
- Comprehensive README with installation instructions
- Contributing guidelines
- Apache 2.0 License

### Features

#### Profile Management
- Create unlimited proxy profiles
- Edit existing profiles with validation
- Delete profiles with confirmation
- Duplicate profiles with custom names
- Store profiles across Burp Suite sessions

#### Proxy Configuration
- Host and port configuration
- Optional username/password authentication
- DNS over SOCKS toggle
- Quick enable/disable functionality
- Active profile tracking

#### Import/Export
- Export all profiles to JSON file
- Import profiles from JSON file
- Merge or replace existing profiles
- Backup and share configurations

#### Connection Testing
- Test proxy connection with one click
- Display current IP address
- Show geolocation (City, Region, Country)
- Display ISP/Organization information
- Uses ipinfo.io API

#### User Interface
- Large, accessible toggle button
- Real-time status indicator (ACTIVE/DISABLED)
- Color-coded active profile highlighting
- Intuitive profile table with sorting
- Quick profile selector dropdown
- Responsive dialog boxes
- Clean, professional design
- Integration with Burp Suite theme

### Technical Details
- Built with Burp Suite Montoya API 2023.12.1
- Requires Java 17 or higher
- Uses Gson 2.11.0 for JSON handling
- Maven-based build system
- No external dependencies required at runtime
- Thread-safe profile management
- Proper exception handling throughout
- Extensive input validation

### Documentation
- Comprehensive README with usage examples
- JavaDoc comments on all public APIs
- Contributing guidelines
- Apache 2.0 License file
- Build instructions
- Installation guide

### Known Limitations
- Only SOCKS proxy support (no HTTP/HTTPS proxy)
- No protocol version selection (uses Burp default)
- Single proxy at a time (no proxy chains)
- Requires internet connection for IP testing

## Release Notes

### Version 1.0.0 Highlights

This is the first stable release of QuickSocks! 🎉

**QuickSocks** makes managing SOCKS proxies in Burp Suite effortless. Whether you're switching between VPNs, testing through different networks, or managing multiple proxy configurations, QuickSocks provides a clean, intuitive interface to get the job done.

**Key Features:**
- ✅ Multiple profile support with persistent storage
- ✅ One-click proxy enable/disable
- ✅ Full authentication support
- ✅ Import/Export for easy sharing
- ✅ Built-in connection testing
- ✅ Clean, professional UI

**Perfect for:**
- Penetration testers working through various networks
- Bug bounty hunters using VPNs and proxies
- Security researchers managing multiple proxy configurations
- Teams sharing standardized proxy setups

**Installation:**
1. Download the JAR from releases
2. Load in Burp Suite Extensions
3. Start managing your proxies!

[Unreleased]: https://github.com/thezakman/QuickSocks/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/thezakman/QuickSocks/releases/tag/v1.0.0
