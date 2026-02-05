# Contributing to QuickSocks

First off, thank you for considering contributing to QuickSocks! It's people like you that make QuickSocks such a great tool.

## Code of Conduct

This project and everyone participating in it is governed by respect and professionalism. By participating, you are expected to uphold this standard.

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check the existing issues to avoid duplicates. When you create a bug report, include as many details as possible:

* **Use a clear and descriptive title**
* **Describe the exact steps to reproduce the problem**
* **Provide specific examples** (configurations, screenshots, etc.)
* **Describe the behavior you observed** and what you expected
* **Include details about your environment:**
  * Burp Suite version
  * Java version
  * Operating System
  * QuickSocks version

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion, please include:

* **Use a clear and descriptive title**
* **Provide a detailed description** of the suggested enhancement
* **Explain why this enhancement would be useful**
* **List any similar features** in other tools if applicable

### Pull Requests

1. **Fork the repository** and create your branch from `main`
2. **Follow the existing code style** (see below)
3. **Add tests** if applicable
4. **Update documentation** for any new features
5. **Ensure the code compiles** (`mvn clean package`)
6. **Write a clear commit message**

#### Branch Naming Convention

* `feature/description` - for new features
* `bugfix/description` - for bug fixes
* `docs/description` - for documentation updates
* `refactor/description` - for code refactoring

#### Commit Message Guidelines

* Use present tense ("Add feature" not "Added feature")
* Use imperative mood ("Move cursor to..." not "Moves cursor to...")
* Limit the first line to 72 characters or less
* Reference issues and pull requests liberally

Example:
```
Add profile export/import validation

- Validate JSON structure before importing
- Add error handling for malformed files
- Update user feedback messages

Fixes #123
```

## Development Setup

### Prerequisites

```bash
# Java 17+
java -version

# Maven 3.6+
mvn -version

# Git
git --version
```

### Setup Steps

```bash
# Clone your fork
git clone https://github.com/YOUR_USERNAME/QuickSocks.git
cd QuickSocks

# Add upstream remote
git remote add upstream https://github.com/thezakman/QuickSocks.git

# Create a branch
git checkout -b feature/my-new-feature

# Build the project
mvn clean package

# The JAR will be in target/
ls target/*.jar
```

### Testing Your Changes

1. Build the JAR file (`mvn clean package`)
2. Load it in Burp Suite (Extensions → Add)
3. Test all affected functionality
4. Check for any errors in Burp Suite's Extension output

## Code Style Guidelines

### Java Style

* **Indentation**: 4 spaces (no tabs)
* **Line length**: Maximum 120 characters
* **Braces**: K&R style (opening brace on same line)
* **Naming conventions**:
  * Classes: `PascalCase`
  * Methods/variables: `camelCase`
  * Constants: `UPPER_SNAKE_CASE`
  * Private fields: prefix with `this.` in constructors

### JavaDoc

* Add JavaDoc comments for all public classes and methods
* Include `@param`, `@return`, `@throws` where applicable
* Add `@author` tag for new classes

Example:
```java
/**
 * Applies a SOCKS proxy profile to Burp Suite.
 * 
 * @param profile The profile to apply
 * @param enable Whether to enable or disable the proxy
 * @throws IllegalArgumentException if profile is null
 */
public void applyProfile(SocksProfile profile, boolean enable) {
    // Implementation
}
```

### Best Practices

* **Keep methods small** - aim for single responsibility
* **Handle exceptions** properly - don't swallow errors silently
* **Use meaningful variable names** - avoid single letter names (except in loops)
* **Add validation** for input parameters
* **Log important operations** using Burp's logging API
* **Use constants** instead of magic numbers/strings

## Project Structure

```
src/main/java/
├── burp/
│   └── BurpExtender.java           # Entry point (minimal changes)
└── com/tzm/dirtysocks/
    ├── Constants.java               # VERSION HERE! Single source of truth
    ├── DirtySocksUI.java            # Main controller
    ├── model/                       # Data models
    │   ├── ProfileManager.java      # Business logic
    │   └── SocksProfile.java        # Data class
    ├── service/                     # Services
    │   └── SocksProxyService.java   # Burp API integration
    └── ui/                          # UI components
        ├── MainTab.java             # Main interface
        ├── ProfileEditDialog.java   # Dialogs
        └── QuickTogglePanel.java    # Reusable components
```

**Important:** When releasing a new version, update the `VERSION` constant in `Constants.java` - this is the single source of truth for the version number.

## Areas for Contribution

### High Priority

* [ ] Add SOCKS4/SOCKS5 protocol selection
* [ ] Implement profile groups/categories
* [ ] Add keyboard shortcuts
* [ ] Create automated tests
* [ ] Improve error messages

### Medium Priority

* [ ] Add profile templates
* [ ] Implement profile search/filter
* [ ] Add connection retry logic
* [ ] Create profile validation rules
* [ ] Add dark mode support

### Low Priority

* [ ] Add profile statistics
* [ ] Implement profile backup/restore
* [ ] Add more connection test options
* [ ] Create profile sharing via URL
* [ ] Add internationalization (i18n)

## Getting Help

* Open an issue with the `question` label
* Check existing documentation in the README
* Review closed issues for similar problems

## Recognition

Contributors will be recognized in:
* The project README
* Release notes for significant contributions
* GitHub's contributor list

Thank you for contributing to QuickSocks! 🎉
