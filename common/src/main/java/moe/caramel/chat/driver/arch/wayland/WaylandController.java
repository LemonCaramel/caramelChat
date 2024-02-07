package moe.caramel.chat.driver.arch.wayland;

import com.mojang.blaze3d.platform.Window;
import com.sun.jna.Native;
import moe.caramel.chat.Main;
import moe.caramel.chat.controller.ScreenController;
import moe.caramel.chat.driver.IController;
import moe.caramel.chat.driver.IOperator;
import moe.caramel.chat.util.ModLogger;
import moe.caramel.chat.wrapper.AbstractIMEWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFWNativeWayland;

/**
 * Wayland Controller
 */
public final class WaylandController implements IController {

    static WaylandOperator focused;

    private final Driver_Wayland driver;

    /**
     * Create Wayland Controller
     */
    public WaylandController() {
        ModLogger.log("[Native] Load the Wayland Controller.");
        this.driver = Native.load(Main.copyLibrary("libcaramelchatwl.so"), Driver_Wayland.class);

        this.driver.initialize(
            // Wayland Display Id
            GLFWNativeWayland.glfwGetWaylandDisplay(),
            // PreEdit
            (str) -> {
                if (focused != null) {
                    ModLogger.debug("[Native|Java] Preedit Callback (" + str.toString() + ")");
                    focused.getWrapper().appendPreviewText(str.toString());
                }
            },
            // PreEdit (Null)
            () -> {
                if (focused != null) {
                    ModLogger.debug("[Native|Java] Preedit Null Callback");
                    focused.getWrapper().appendPreviewText("");
                }
            },
            // Done
            (str) -> {
                if (focused != null) {
                    ModLogger.debug("[Native|Java] Done Callback (" + str.toString() + ")");
                    focused.getWrapper().insertText(str.toString());
                }
            },
            // Rect
            (rect) -> {
                if (focused != null) {
                    ModLogger.debug("[Native|Java] Rect Callback");
                    final Window window = Minecraft.getInstance().getWindow();
                    final int osScale = (window.getHeight() / window.getScreenHeight());

                    final float[] buff = focused.getWrapper().getRect().copy();
                    final float factor = (float) window.getGuiScale();
                    buff[0] *= factor;
                    buff[1] *= factor;
                    buff[2] *= factor;
                    buff[3] *= factor;

                    buff[1] /= osScale;

                    rect.write(0, buff, 0, 4);
                    return 0;
                }
                return 1;
            },
            // Info
            (log) -> ModLogger.log("[Native|C] " + log),
            // Error
            (log) -> ModLogger.error("[Native|C] " + log),
            // Debug
            (log) -> ModLogger.debug("[Native|C] " + log)
        );

        this.setFocus(false);
    }

    @Override
    public IOperator createOperator(final AbstractIMEWrapper wrapper) {
        return new WaylandOperator(this, wrapper);
    }

    @Override
    public void changeFocusedScreen(final Screen screen) {
        if (screen instanceof ScreenController) {
            return;
        }

        if (WaylandController.focused != null) {
            WaylandController.focused.setFocused(false);
            WaylandController.focused = null;
        }
    }

    @Override
    public void setFocus(final boolean focus) {
        this.driver.setFocus(focus);
    }
}
