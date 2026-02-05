package burp;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import com.tzm.dirtysocks.DirtySocksUI;

/**
 * Main entry point for the QuickSocks Burp Suite extension.
 * Initializes the extension and registers handlers.
 * 
 * @author TheZakMan
 */
public class BurpExtender implements BurpExtension {
    @Override
    public void initialize(MontoyaApi api) {
        try {
            api.logging().logToOutput("Initializing QuickSocks extension...");
            
            DirtySocksUI mainUI = new DirtySocksUI(api);
            mainUI.initializeUI();

            api.extension().setName(mainUI.getExtensionName());
            api.extension().registerUnloadingHandler(mainUI::onUnload);

            // Set up global exception handler
            Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
                api.logging().logToError("Uncaught exception in " + thread.getName() + ": " + throwable.getMessage());
                api.logging().logToError(throwable);
            });
            
            api.logging().logToOutput("QuickSocks extension initialized successfully");

        } catch (Exception e) {
            api.logging().logToError("Failed to initialize QuickSocks: " + e.getMessage());
            api.logging().logToError(e);
            throw new RuntimeException("QuickSocks initialization failed", e);
        }
    }
}
