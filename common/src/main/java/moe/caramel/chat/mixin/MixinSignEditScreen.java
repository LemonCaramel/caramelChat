package moe.caramel.chat.mixin;

import moe.caramel.chat.controller.ScreenController;
import moe.caramel.chat.wrapper.AbstractIMEWrapper;
import moe.caramel.chat.wrapper.WrapperSignEditScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import java.util.function.Consumer;

/**
 * SignEdit Screen Mixin
 */
@Mixin(AbstractSignEditScreen.class)
public final class MixinSignEditScreen implements ScreenController {

    @Unique private WrapperSignEditScreen caramelChat$wrapper;
    @Shadow @Final public SignBlockEntity sign;
    @Shadow public int line;

    @Inject(method = "init", at = @At("TAIL"))
    private void init(final CallbackInfo ci) {
        this.caramelChat$wrapper = new WrapperSignEditScreen((AbstractSignEditScreen) (Object) this);
        this.caramelChat$wrapper.setOrigin();
    }

    @ModifyArg(
        method = "init",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/font/TextFieldHelper;<init>(Ljava/util/function/Supplier;Ljava/util/function/Consumer;Ljava/util/function/Supplier;Ljava/util/function/Consumer;Ljava/util/function/Predicate;)V"
        ), index = 1
    )
    private Consumer<String> setCurrentPageText(final Consumer<String> consumer) {
        return value -> {
            consumer.accept(value);
            this.caramelChat$wrapper.setOrigin();
        };
    }

    @Inject(
        method = "keyPressed",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/font/TextFieldHelper;setCursorToEnd()V"
        )
    )
    private void keyPressed(final int key, final int scancode, final int action, final CallbackInfoReturnable<Boolean> cir) {
        this.caramelChat$wrapper.setOrigin();
    }

    @ModifyArgs(
        method = "renderSignText",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Font;drawInBatch(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;IIZ)I",
            ordinal = 0
        )
    )
    private void renderCaret(final Args args) {
        // Check IME Status
        final String original = args.get(0);
        if (original.isEmpty() || caramelChat$wrapper.getStatus() == AbstractIMEWrapper.InputStatus.NONE) {
            return;
        }

        // Line Check (stupid way...)
        final int lineHeight = this.sign.getTextLineHeight();
        final int centerHeight = (4 * lineHeight / 2);
        final int line = (((int) (float) args.get(2) + centerHeight) / lineHeight);
        if (line != this.line) {
            return;
        }

        // Render Caret
        final int firstEndPos = caramelChat$wrapper.getFirstEndPos();
        final int secondStartPos = caramelChat$wrapper.getSecondStartPos();

        final String first = original.substring(0, firstEndPos);
        final String input = original.substring(firstEndPos, secondStartPos);
        final String second = original.substring(secondStartPos);
        args.set(0, first + ChatFormatting.UNDERLINE + input + ChatFormatting.RESET + second); // OMG..
    }
}
