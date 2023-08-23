package moe.caramel.chat.driver.arch.darwin;

import com.sun.jna.Native;
import moe.caramel.chat.Main;
import moe.caramel.chat.controller.ScreenController;
import moe.caramel.chat.driver.IController;
import moe.caramel.chat.driver.IOperator;
import moe.caramel.chat.util.ModLogger;
import moe.caramel.chat.wrapper.AbstractIMEWrapper;
import net.minecraft.client.gui.screens.Screen;

/**
 * Darwin Controller
 */
public final class DarwinController implements IController {

    private final Driver_Darwin driver;

    /**
     * Create Darwin Controller
     */
    public DarwinController() {
        ModLogger.log("[Native] Load the Darwin Controller.");
        this.driver = Native.load(Main.copyLibrary("libdarwincocoainput.dylib"), Driver_Darwin.class);
        this.driver.initialize(
            // Info
            (log) -> ModLogger.debug("[Native|C] " + log), // lib issue (info -> debug)
            // Error
            (log) -> ModLogger.error("[Native|C] " + log),
            // Debug
            (log) -> ModLogger.debug("[Native|C] " + log)
        );
    }

    @Override
    public IOperator createOperator(final AbstractIMEWrapper wrapper) {
        return new DarwinOperator(this, wrapper);
    }

    @Override
    public void changeFocusedScreen(final Screen screen) {
        if (screen instanceof ScreenController) {
            return;
        }

        this.driver.refreshInstance();
    }

    @Override
    public void setFocus(final boolean focus) {
    }

    /**
     * Gets the Driver
     *
     * @return driver
     */
    public Driver_Darwin getDriver() {
        return driver;
    }
}
