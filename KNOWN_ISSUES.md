# Known Issues & Limitations

## Burp Suite Tab Switching (Montoya API Limitation)

### Issue Description
When QuickSocks applies SOCKS proxy settings (enabling/disabling or changing profiles), Burp Suite automatically switches focus to the **Dashboard** tab instead of staying on the QuickSocks tab.

### Why This Happens
This is a **known behavior of the Burp Suite Montoya API**, specifically the `importUserOptionsFromJson()` method. When this method is called to apply settings, Burp Suite's core functionality forces the UI to switch to the Dashboard.

### Technical Details
```java
// This call triggers the tab switch:
api.burpSuite().importUserOptionsFromJson(json);
```

The Montoya API does not provide:
- A way to prevent this tab switch
- An alternative method to apply SOCKS settings without tab switching
- Any API to programmatically control which tab is focused

### Implemented Workaround (v1.0.1+)
**QuickSocks now automatically attempts to return to the extension tab** after applying settings!

#### How It Works
1. After `importUserOptionsFromJson()` is called, QuickSocks schedules a refocus action
2. A small delay (50ms) allows Burp to complete its tab switch
3. The extension navigates the Swing component tree to find the parent `JTabbedPane`
4. If found, it programmatically selects the QuickSocks tab

#### Implementation
```java
// In SocksProxyService.java
SwingUtilities.invokeLater(() -> {
    Thread.sleep(50); // Small delay
    refocusCallback.run(); // Attempt to refocus on QuickSocks tab
});
```

#### Success Rate
- ✅ **Works most of the time**: The tab automatically returns to QuickSocks
- ⚠️ **May fail occasionally**: Due to timing or Burp's internal UI structure
- 🔄 **Best-effort approach**: Uses standard Swing APIs to find and select the tab

### Impact
- ✅ **Greatly improved UX**: Tab usually returns automatically
- ⚠️ **Occasional manual click needed**: If workaround fails (rare)
- ✅ **Functionality 100%**: The proxy settings are always applied correctly
- ✅ **No data loss**: All configurations remain intact

### Manual Fallback
If the automatic refocus fails, simply click the QuickSocks tab again. The proxy has been applied correctly.

### Status
- **Partial workaround implemented** in v1.0.1
- **Cannot be fully fixed**: Requires changes to Burp Suite's core API
- **Affects all extensions** that use `importUserOptionsFromJson()`
- **Reported to PortSwigger**: This is a known limitation in the Montoya API

### Related Discussions
- [Montoya API Documentation](https://portswigger.github.io/burp-extensions-montoya-api/)
- This behavior is consistent across all Burp Suite extensions using settings import

---

## Other Known Limitations

### SOCKS Protocol Version
- **Current**: Uses Burp's default SOCKS protocol (typically SOCKS5)
- **Limitation**: Cannot specify SOCKS4 vs SOCKS5
- **Planned**: Protocol selection feature in future version

### Single Proxy Only
- **Current**: One active proxy at a time
- **Limitation**: Cannot chain multiple proxies
- **Workaround**: Use system-level proxy chaining tools

### Connection Testing Requires Internet
- **Current**: Uses ipinfo.io API
- **Limitation**: Won't work in air-gapped environments
- **Workaround**: Manual proxy verification through Burp's requests

---

## Reporting Issues

If you encounter issues **not** listed above, please report them:
- **GitHub Issues**: [QuickSocks Issues](https://github.com/thezakman/QuickSocks/issues)
- **Include**: Burp version, Java version, OS, steps to reproduce
- **Expected vs Actual**: What you expected vs what happened

Thank you for using QuickSocks! 🚀
