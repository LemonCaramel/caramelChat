package moe.caramel.chat.driver;

import com.sun.jna.Platform;
import moe.caramel.chat.driver.arch.darwin.DarwinController;
import moe.caramel.chat.driver.arch.unknown.UnknownController;
import moe.caramel.chat.driver.arch.win.WinController;
import moe.caramel.chat.driver.arch.x11.X11Controller;
import moe.caramel.chat.util.ModLogger;
import moe.caramel.chat.wrapper.AbstractIMEWrapper;
import net.minecraft.client.gui.screens.Screen;

/**
 * Controller Interface
 */
public interface IController {

    /**
     * Create the IME Operator.
     *
     * @param wrapper IME Wrapper
     * @return IME Operator
     */
    IOperator createOperator(final AbstractIMEWrapper wrapper);

    /**
     * Replace to current focused screen.
     *
     * @param screen focused screen
     */
    void changeFocusedScreen(final Screen screen);

    /**
     * Set whether to focus or not. (Driver)
     *
     * @param focus focus
     */
    void setFocus(final boolean focus);

    /**
     * Gets the controller.
     *
     * @return controller
     */
    static IController getController() {
        try {
            // Windows
            if (Platform.isWindows()) {
                return new WinController();
            }
            // macOS
            else if (Platform.isMac()) {
                return new DarwinController();
            }
            // X11
            else if (Platform.isX11()) {
                return new X11Controller();
            }
            // What?
            else {
                throw new UnsupportedOperationException();
            }
        } catch (final Exception exception) {
            ModLogger.error("Error while loading the CocoaInput Driver", exception);
            return new UnknownController();
        }
    }
}
