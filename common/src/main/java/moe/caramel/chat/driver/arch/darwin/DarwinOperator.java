package moe.caramel.chat.driver.arch.darwin;

import com.mojang.blaze3d.platform.Window;
import moe.caramel.chat.driver.IOperator;
import moe.caramel.chat.util.ModLogger;
import moe.caramel.chat.wrapper.AbstractIMEWrapper;
import net.minecraft.client.Minecraft;
import java.util.UUID;

/**
 * Darwin IME Operator
 */
public class DarwinOperator implements IOperator {

    private final DarwinController controller;
    private final AbstractIMEWrapper wrapper;
    private final String uuid;
    private boolean nowFocused;

    public DarwinOperator(final DarwinController controller, final AbstractIMEWrapper wrapper) {
        this.controller = controller;
        this.wrapper = wrapper;
        this.uuid = UUID.randomUUID().toString();

        ModLogger.debug("[Native|Java] IMEOperator addInstance: " + uuid);
        this.controller.getDriver().addInstance(
            // UUID
            this.uuid,
            // Insert Text
            (str, position, length) -> {
                ModLogger.debug("[Native|Java] Textfield (" + uuid + ") received inserted text.");
                this.wrapper.insertText(str);
            },
            // Set Marked Text
            (str, position1, length1, position2, length2) -> {
                ModLogger.debug("[Native|Java] MarkedText changed at (" + uuid + ").");
                this.wrapper.appendPreviewText(str);
            },
            // Rect Range
            (pointer) -> {
                ModLogger.debug("[Native|Java] Called to determine where to draw.");
                final float[] buff = this.wrapper.getRect().copy();
                final Window window = Minecraft.getInstance().getWindow();
                final float factor = (float) window.getGuiScale();
                buff[0] *= factor;
                buff[1] *= factor;
                buff[2] *= factor;
                buff[3] *= factor;

                buff[0] += window.getX();
                buff[1] += window.getY();

                pointer.write(0, buff, 0, 4);
            }
        );
    }

    @Override
    public void setFocused(final boolean focus) {
        if (focus != this.nowFocused) {
            ModLogger.debug("[Native|Java] IMEOperator.setFocused: " + focus);
            this.controller.getDriver().setIfReceiveEvent(this.uuid, (focus ? 1 : 0));
            this.nowFocused = focus;
        }
    }

    @Override
    public boolean isFocused() {
        return nowFocused;
    }
}
