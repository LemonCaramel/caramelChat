package moe.caramel.chat.driver.arch.x11;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import moe.caramel.chat.controller.ScreenController;
import moe.caramel.chat.driver.IController;
import moe.caramel.chat.driver.IOperator;
import moe.caramel.chat.util.ModLogger;
import moe.caramel.chat.wrapper.AbstractIMEWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWNativeX11;

/**
 * X11 Controller
 */
public final class X11Controller implements IController {

    private static final long windowId = Minecraft.getInstance().getWindow().getWindow();
    static X11Operator focused;

    private final Driver_X11 driver;

    /**
     * Create X11 Controller
     */
    public X11Controller() {
        X11Controller.setupKeyboardEvent();

        ModLogger.log("[Native] Load the X11 Controller.");
        this.driver = Native.load(IController.getNativeName("libx11cocoainput.so"), Driver_X11.class);

        final long windowId = Minecraft.getInstance().getWindow().getWindow();
        this.driver.initialize(
            // Windows Id
            windowId,
            // X11 Windows Id
            GLFWNativeX11.glfwGetX11Window(windowId),
            // Draw Callback
            (caret, chg_first, chg_length, length, iswstring, rawstring, rawwstring, primary, secondary, tertiary) -> {
                ModLogger.debug("[Native|Java] Draw begin");
                final String string = (iswstring ? rawwstring.toString() : rawstring);

                if (X11Controller.focused != null) {
                    GLFW.glfwSetKeyCallback(windowId, null);
                    X11Controller.focused.getWrapper().appendPreviewText(string);
                }

                ModLogger.debug(
                    "[Native|Java] PreEdit: {} {} {} {} {} {} {} {}",
                    caret, chg_first, chg_length, length,
                    primary, secondary, tertiary, string
                );

                final int[] point = { 600, 600 };
                final Memory memory = new Memory(8L);
                memory.write(0L, point, 0, 2);
                ModLogger.debug("[Native|Java] Draw End");
                return memory;
            },
            // Done Callback
            () -> {
                ModLogger.debug("[Native|Java] Preedit Done");
                if (X11Controller.focused != null) {
                    X11Controller.focused.getWrapper().insertText("");
                }
                X11Controller.setupKeyboardEvent();
            },
            // Info
            (log) -> ModLogger.debug("[Native|C] " + log), // lib issue (info -> debug)
            // Error
            (log) -> ModLogger.error("[Native|C] " + log),
            // Debug
            (log) -> ModLogger.debug("[Native|C] " + log)
        );

        this.setFocus(false);
    }

    public static void setupKeyboardEvent() {
        final Minecraft minecraft = Minecraft.getInstance();
        minecraft.keyboardHandler.setup(windowId);
        GLFW.glfwSetCharModsCallback(windowId, (window, codepoint, mods) -> {
            minecraft.execute(() -> {
                if (X11Controller.focused != null) {
                    X11Controller.focused.getWrapper().insertText(String.valueOf(Character.toChars(codepoint)));
                } else {
                    minecraft.keyboardHandler.charTyped(window, codepoint, mods);
                }
            });
        });
    }

    @Override
    public IOperator createOperator(final AbstractIMEWrapper wrapper) {
        return new X11Operator(this, wrapper);
    }

    @Override
    public void changeFocusedScreen(final Screen screen) {
        if (screen instanceof ScreenController) {
            return;
        }

        if (X11Controller.focused != null) {
            X11Controller.focused.setFocused(false);
            X11Controller.focused = null;
        }
    }

    @Override
    public void setFocus(final boolean focus) {
        this.driver.set_focus(focus ? 1 : 0);
    }
}
