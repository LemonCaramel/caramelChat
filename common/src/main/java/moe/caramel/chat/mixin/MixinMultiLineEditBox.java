package moe.caramel.chat.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moe.caramel.chat.Main;
import moe.caramel.chat.driver.arch.wayland.WaylandController;
import moe.caramel.chat.wrapper.AbstractIMEWrapper;
import moe.caramel.chat.wrapper.AbstractIMEWrapper.InputStatus;
import moe.caramel.chat.wrapper.WrapperMultilineEditBox;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.MultilineTextField;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import java.util.function.Consumer;

/**
 * MultiLineEditBox Component Mixin
 */
@Mixin(MultiLineEditBox.class)
public final class MixinMultiLineEditBox {

    @Unique private WrapperMultilineEditBox caramelChat$wrapper;
    @Unique private int caramelChat$viewBeginPos = -1, caramelChat$viewEndPos = -1;
    @Shadow @Final public MultilineTextField textField;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(final CallbackInfo ci) {
        this.caramelChat$wrapper = new WrapperMultilineEditBox((MultiLineEditBox) (Object) this);
        this.caramelChat$replaceValueListener(this.textField.valueListener);
    }

    // ================================ (Formatter)

    @ModifyArgs(
        method = "renderContents",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/String;substring(II)Ljava/lang/String;",
            ordinal = 1
        )
    )
    private void captureLineRenderPositionsMiddle(final Args args) {
        this.captureLineRenderPositionsEnd(args);
    }

    @ModifyArgs(
        method = "renderContents",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/String;substring(II)Ljava/lang/String;",
            ordinal = 2
        )
    )
    private void captureLineRenderPositionsEnd(final Args args) {
        this.caramelChat$viewBeginPos = args.get(0);
        this.caramelChat$viewEndPos = args.get(1);
    }

    @WrapOperation(
        method = "renderContents",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)V",
            ordinal = 1
        )
    )
    private void renderCaretMiddle(final GuiGraphics instance, final Font font, final String text, final int x, final int y, final int color, final boolean dropShadow, final Operation<Integer> original) {
        this.renderCaretEnd(instance, font, text, x, y, color, dropShadow, original);
    }

    @WrapOperation(
        method = "renderContents",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)V",
            ordinal = 2
        )
    )
    private void renderCaretEnd(final GuiGraphics instance, final Font font, final String text, final int x, final int y, final int color, final boolean dropShadow, final Operation<Integer> original) {
        // Check IME Status
        if (text.isEmpty() || caramelChat$wrapper.getStatus() == AbstractIMEWrapper.InputStatus.NONE) {
            original.call(instance, font, text, x, y, color, dropShadow);
            return;
        }

        // Render Caret
        final int firstEnd = caramelChat$wrapper.getFirstEndPos();
        final int secondStart = caramelChat$wrapper.getSecondStartPos();

        if (firstEnd < this.caramelChat$viewEndPos && this.caramelChat$viewBeginPos < secondStart) {
            final int localStart = Math.max(0, firstEnd - this.caramelChat$viewBeginPos);
            final int localEnd = Math.min(text.length(), secondStart - this.caramelChat$viewBeginPos);

            if (localStart >= localEnd) {
                original.call(instance, font, text, x, y, color, dropShadow);
                return;
            }

            final String before = text.substring(0, localStart);
            final String underlined = text.substring(localStart, localEnd);
            final String after = text.substring(localEnd);

            final String result = before + ChatFormatting.UNDERLINE + underlined + ChatFormatting.RESET + after;
            original.call(instance, font, result, x, y, color, dropShadow);
            return;
        }

        // No need to render caret
        original.call(instance, font, text, x, y, color, dropShadow);
    }

    // ================================ (IME)

    @Inject(method = "setValueListener", at = @At("TAIL"), cancellable = true)
    private void setValueListener(final Consumer<String> valueListener, final CallbackInfo ci) {
        ci.cancel();
        this.caramelChat$replaceValueListener(valueListener);
    }

    @Inject(method = "seekCursorScreen", at = @At("TAIL"))
    private void seekCursorScreen(final double mouseX, final double mouseY, final CallbackInfo ci) {
        if (this.caramelChat$wrapper != null) {
            this.caramelChat$wrapper.setOrigin();
            this.caramelChat$wrapper.setToNoneStatus();
        }
    }

    @Inject(method = "setFocused", at = @At("TAIL"))
    private void setFocused(final boolean focused, final CallbackInfo ci) {
        if (this.caramelChat$wrapper != null) {
            this.caramelChat$wrapper.setFocused(focused);
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!Main.getController().getClass().equals(WaylandController.class)) {
            return;
        }
        if (caramelChat$wrapper.getStatus() == AbstractIMEWrapper.InputStatus.NONE) {
            return;
        }

        if (Screen.isSelectAll(keyCode) || Screen.isCopy(keyCode) || Screen.isCut(keyCode) || Screen.isPaste(keyCode)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Unique
    private void caramelChat$replaceValueListener(final Consumer<String> valueListener) {
        this.textField.setValueListener((value) -> {
            if (this.caramelChat$wrapper == null || this.caramelChat$wrapper.getStatus() == InputStatus.NONE) {
                valueListener.accept(value);
                this.caramelChat$wrapper.setOrigin(value);
            }
        });
    }
}
