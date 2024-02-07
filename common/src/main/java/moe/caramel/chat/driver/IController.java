package moe.caramel.chat.driver;

import moe.caramel.chat.driver.arch.darwin.DarwinController;
import moe.caramel.chat.driver.arch.unknown.UnknownController;
import moe.caramel.chat.driver.arch.wayland.WaylandController;
import moe.caramel.chat.driver.arch.win.WinController;
import moe.caramel.chat.driver.arch.x11.X11Controller;
import moe.caramel.chat.util.ModLogger;
import moe.caramel.chat.wrapper.AbstractIMEWrapper;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

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
     * Gets the current keyboard status.
     *
     * @return keyboard status (if {@code null}, OS isn't supported)
     */
    @Nullable
    default KeyboardStatus getKeyboardStatus() {
        return null;
    }

    /**
     * Gets the controller.
     *
     * @return controller
     */
    static IController getController() {
        try {
            return switch (GLFW.glfwGetPlatform()) {
                // Windows
                case GLFW.GLFW_PLATFORM_WIN32 -> new WinController();
                // macOS
                case GLFW.GLFW_PLATFORM_COCOA -> new DarwinController();
                // Linux (X11)
                case GLFW.GLFW_PLATFORM_X11 -> new X11Controller();
                // Linux (Wayland)
                case GLFW.GLFW_PLATFORM_WAYLAND -> new WaylandController();
                // What?
                default -> throw new UnsupportedOperationException();
            };
        } catch (final UnsupportedOperationException ignored) {
            ModLogger.error("This platform is not supported by CocoaInput Driver.");
        } catch (final Exception exception) {
            ModLogger.error("Error while loading the CocoaInput Driver.", exception);
        }
        return UnknownController.INSTANCE;
    }
}
