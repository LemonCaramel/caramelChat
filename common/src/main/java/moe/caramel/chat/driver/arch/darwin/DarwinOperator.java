package moe.caramel.chat.driver.arch.darwin;

import com.mojang.blaze3d.platform.Window;
import moe.caramel.chat.driver.IController;
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
    private final DarwinFocusState focusState;
    private final Driver_Darwin.InsertText insertText;
    private final Driver_Darwin.SetMarkedText setMarkedText;
    private final Driver_Darwin.FirstRectForCharacterRange firstRectForCharacterRange;

    public DarwinOperator(final DarwinController controller, final AbstractIMEWrapper wrapper) {
        this.controller = controller;
        this.wrapper = wrapper;
        this.uuid = UUID.randomUUID().toString();
        this.focusState = new DarwinFocusState(controller.getRegistryGeneration());
        this.insertText = (str, position, length) -> {
            ModLogger.debug("[Native|Java] Textfield (" + uuid + ") received inserted text.");
            this.wrapper.insertText(str);
        };
        this.setMarkedText = (str, position1, length1, position2, length2) -> {
            ModLogger.debug("[Native|Java] MarkedText changed at (" + uuid + ").");
            this.wrapper.appendPreviewText(str);
        };
        this.firstRectForCharacterRange = (pointer) -> {
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
        };

        this.registerInstance();
    }

    private void registerInstance() {
        ModLogger.debug("[Native|Java] IMEOperator addInstance: " + uuid);
        this.controller.getDriver().addInstance(
            this.uuid,
            this.insertText,
            this.setMarkedText,
            this.firstRectForCharacterRange
        );
    }

    @Override
    public IController getController() {
        return controller;
    }

    @Override
    public void setFocused(final boolean focus) {
        this.focusState.setFocused(focus, this.controller.getRegistryGeneration(), this::registerInstance, receive -> {
            ModLogger.debug("[Native|Java] IMEOperator.setFocused: " + focus);
            this.controller.getDriver().setIfReceiveEvent(this.uuid, receive);
        });
    }

    @Override
    public boolean isFocused() {
        return this.focusState.isFocused();
    }
}
