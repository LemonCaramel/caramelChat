package moe.caramel.chat.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moe.caramel.chat.Main;
import moe.caramel.chat.controller.ScreenController;
import moe.caramel.chat.driver.arch.wayland.WaylandController;
import moe.caramel.chat.wrapper.AbstractIMEWrapper;
import moe.caramel.chat.wrapper.WrapperSignEditScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.function.Consumer;

/**
 * SignEdit Screen Mixin
 */
@Mixin(value = AbstractSignEditScreen.class, priority = 0)
public final class MixinSignEditScreen implements ScreenController {

    @Unique private WrapperSignEditScreen caramelChat$wrapper;
    @Unique private boolean caramelChat$lazyInit;
    @Unique private int caramelChat$currentRenderLine = -1;
    @Shadow @Nullable public TextFieldHelper signField;
    @Shadow public int line;

    @Inject(method = "init", at = @At("HEAD"))
    private void init(final CallbackInfo ci) {
        this.caramelChat$wrapper = new WrapperSignEditScreen((AbstractSignEditScreen) (Object) this);
        this.caramelChat$wrapper.setOrigin();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void lazyInit(final CallbackInfo ci) {
        // Stendhal mod creates a new signField... :scream:
        if (!caramelChat$lazyInit && signField != null) {
            this.caramelChat$lazyInit = true;

            final Consumer<String> previous = (signField.setMessageFn);
            this.signField.setMessageFn = (value) -> {
                previous.accept(value);
                this.caramelChat$wrapper.setOrigin();
            };
        }
    }

    @Inject(method = "keyPressed", at = {
        @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/gui/font/TextFieldHelper;setCursorToEnd()V"),
        @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/client/gui/font/TextFieldHelper;setCursorToEnd()V")
    })
    private void keyPressed(final int key, final int scancode, final int action, final CallbackInfoReturnable<Boolean> cir) {
        this.caramelChat$wrapper.setOrigin();
    }

    @Redirect(
        method = "keyPressed",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/font/TextFieldHelper;keyPressed(I)Z"
        )
    )
    private boolean helperKeyPressed(final TextFieldHelper helper, final int key) {
        if (Main.getController().getClass().equals(WaylandController.class) && caramelChat$wrapper.getStatus() != AbstractIMEWrapper.InputStatus.NONE) {
            if (Screen.isSelectAll(key) || Screen.isCopy(key) || Screen.isCut(key) || Screen.isPaste(key)) {
                return true;
            }
        }

        final boolean result = helper.keyPressed(key);
        if (result) {
            this.caramelChat$wrapper.setToNoneStatus();
        }
        return result;
    }

    @Inject(method = "renderSignText", at = @At("HEAD"))
    private void captureRenderLine(final GuiGraphics instance, final CallbackInfo ci) {
        this.caramelChat$currentRenderLine = -1;
    }

    @Redirect(
        method = "renderSignText",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/String;substring(II)Ljava/lang/String;"
        )
    )
    private String temporaryFixOverflow(final String value, final int beginIndex, final int endIndex) {
        // TODO What's wrong with this? (Fixes #34)
        return value.substring(beginIndex, Math.min(value.length(), endIndex));
    }

    @WrapOperation(
        method = "renderSignText",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)V",
            ordinal = 0
        )
    )
    private void renderCaret(final GuiGraphics instance, final Font font, final String text, final int x, final int y, final int color, final boolean dropShadow, final Operation<Integer> original) {
        this.caramelChat$currentRenderLine++;

        // Check IME Status
        if (text.isEmpty() || caramelChat$wrapper.getStatus() == AbstractIMEWrapper.InputStatus.NONE) {
            original.call(instance, font, text, x, y, color, dropShadow);
            return;
        }

        // Skip caret render
        if (
            this.caramelChat$currentRenderLine != this.line || // Line Check
            this.caramelChat$wrapper.getSecondStartPos() > text.length() // TODO What's wrong with this? (Fixes #34)
        ) {
            original.call(instance, font, text, x, y, color, dropShadow);
            return;
        }

        // Render Caret
        final int firstEndPos = caramelChat$wrapper.getFirstEndPos();
        final int secondStartPos = caramelChat$wrapper.getSecondStartPos();

        final String first = text.substring(0, firstEndPos);
        final String input = text.substring(firstEndPos, secondStartPos);
        final String second = text.substring(secondStartPos);
        final String result = (first + ChatFormatting.UNDERLINE + input + ChatFormatting.RESET + second); // OMG..
        original.call(instance, font, result, x, y, color, dropShadow);
    }
}
