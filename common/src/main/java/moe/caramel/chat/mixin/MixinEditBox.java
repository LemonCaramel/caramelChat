package moe.caramel.chat.mixin;

import moe.caramel.chat.controller.EditBoxController;
import moe.caramel.chat.wrapper.AbstractIMEWrapper;
import moe.caramel.chat.wrapper.WrapperEditBox;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * EditBox Component Mixin
 */
@Mixin(EditBox.class)
public abstract class MixinEditBox implements EditBoxController {

    @Unique private WrapperEditBox caramelChat$wrapper;
    @Unique private BiFunction<String, Integer, FormattedCharSequence> caramelChat$formatter;
    @Shadow private BiFunction<String, Integer, FormattedCharSequence> formatter;
    @Shadow private boolean canLoseFocus;
    @Shadow public String value;

    @Inject(
        method = "<init>(Lnet/minecraft/client/gui/Font;IIIILnet/minecraft/client/gui/components/EditBox;Lnet/minecraft/network/chat/Component;)V",
        at = @At("TAIL")
    )
    private void init(final CallbackInfo ci) {
        this.caramelChat$wrapper = new WrapperEditBox((EditBox) (Object) this);
        this.caramelChat$formatter = this.formatter; // Cache
        this.caramelChat$caretFormatter();
    }

    @Override
    public WrapperEditBox caramelChat$wrapper() {
        return caramelChat$wrapper;
    }

    // ================================ (Formatter)

    @Inject(method = "setFormatter", at = @At("TAIL"))
    private void setFormatter(final BiFunction<String, Integer, FormattedCharSequence> formatter, final CallbackInfo ci) {
        this.caramelChat$formatter = formatter; // Cache
        this.caramelChat$caretFormatter();
    }

    @Unique
    private void caramelChat$caretFormatter() {
        // Set caret renderer
        this.formatter = ((original, firstPos) -> {
            /* Original */
            if (caramelChat$wrapper.getStatus() == AbstractIMEWrapper.InputStatus.NONE) {
                return caramelChat$formatter.apply(original, firstPos);
            }
            /* Warning */
            else if (caramelChat$wrapper.blockTyping()) {
                return FormattedCharSequence.forward(original, Style.EMPTY.withColor(ChatFormatting.RED));
            }
            /* Custom */
            else {
                // Check Position
                // Empty
                // FirstPos ex. [ ABCD|EFG}(INPUT)HIJK ]
                // LastPos ex. [ ABCDEFG(INPUT)HI|JK} ]
                final int lastPos = (firstPos + original.length()); // firstPos ~ lastPos
                if (lastPos <= caramelChat$wrapper.getFirstEndPos() || caramelChat$wrapper.getSecondStartPos() < firstPos) {
                    return caramelChat$formatter.apply(original, firstPos);
                }

                // Process
                final int firstLen = (caramelChat$wrapper.getFirstEndPos() - firstPos);
                final int previewLen = (caramelChat$wrapper.getSecondStartPos() - caramelChat$wrapper.getFirstEndPos());
                final int inputEndPoint = Math.min(original.length(), (firstLen + previewLen));

                final List<FormattedCharSequence> list = new ArrayList<>();
                final String first = original.substring(0, firstLen);
                final String input = original.substring(firstLen, inputEndPoint);
                final String second = original.substring(inputEndPoint);
                list.add(FormattedCharSequence.forward(first, Style.EMPTY));
                list.add(FormattedCharSequence.forward(input, Style.EMPTY.withUnderlined(true)));
                list.add(FormattedCharSequence.forward(second, Style.EMPTY));

                return FormattedCharSequence.composite(list);
            }
        });
    }

    // ================================ (IME)

    @Inject(method = { "setValue", "insertText" }, at = @At("HEAD"))
    private void updateTextHead(final String text, final CallbackInfo ci) {
        // setStatusToNone -> forceUpdateOrigin -> onValueChange
        this.caramelChat$setStatusToNone();
    }

    @Inject(
        method = "setValue",
        at = @At(
            value = "INVOKE", shift = At.Shift.BEFORE,
            target = "Lnet/minecraft/client/gui/components/EditBox;moveCursorToEnd()V"
        )
    )
    private void setValueInvoke(final String text, final CallbackInfo ci) {
        this.caramelChat$forceUpdateOrigin();
    }

    @Inject(
        method = "insertText",
        at = @At(
            value = "INVOKE", shift = At.Shift.BEFORE,
            target = "Lnet/minecraft/client/gui/components/EditBox;onValueChange(Ljava/lang/String;)V"
        )
    )
    private void insertTextInvoke(final String text, final CallbackInfo ci) {
        this.caramelChat$forceUpdateOrigin();
    }

    @Inject(method = "onValueChange", at = @At("HEAD"))
    private void onValueChange(final String text, final CallbackInfo ci) {
        if (this.caramelChat$wrapper != null) {
            this.value = this.caramelChat$wrapper.getOrigin();
        }
    }

    @Inject(
        method = "deleteChars",
        at = @At(
            value = "INVOKE", shift = At.Shift.BEFORE,
            target = "Lnet/minecraft/client/gui/components/EditBox;moveCursorTo(I)V"
        )
    )
    private void deleteChars(final int pos, final CallbackInfo ci) {
        this.caramelChat$wrapper.setOrigin(this.value);
    }

    @Inject(method = "setFocused", at = @At("TAIL"))
    private void setFocused(final boolean focused, final CallbackInfo ci) {
        if (this.caramelChat$wrapper != null) {
            this.caramelChat$wrapper.setFocused(focused || !this.canLoseFocus);
        }
    }

    @Inject(method = "setCanLoseFocus", at = @At("HEAD"))
    private void setCanLoseFocus(final boolean canLoseFocus, final CallbackInfo ci) {
        if (this.caramelChat$wrapper != null && !canLoseFocus) {
            this.caramelChat$wrapper.setFocused(true);
        }
    }

    @Unique
    private void caramelChat$setStatusToNone() {
        if (this.caramelChat$wrapper != null) {
            this.caramelChat$wrapper.setToNoneStatus();
        }
    }

    @Unique
    private void caramelChat$forceUpdateOrigin() {
        if (this.caramelChat$wrapper != null) {
            this.caramelChat$wrapper.setOrigin(value);
        }
    }

    // ================================ (Fix MC-140646)

    @Shadow private boolean shiftPressed;
    @Shadow public int highlightPos;
    @Shadow public abstract void setHighlightPos(int i);

    @Inject(
        method = "moveCursorTo",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/EditBox;setCursorPosition(I)V"
        )
    )
    private void moveCursorTo(final int cursor, final CallbackInfo ci) {
        if (this.shiftPressed) {
            final int previous = highlightPos;
            this.setHighlightPos(cursor);
            this.highlightPos = previous;
        }

        // Set Status to None
        this.caramelChat$setStatusToNone();
    }

    @ModifyArg(
        method = "renderWidget",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/String;substring(II)Ljava/lang/String;",
            ordinal = 1
        ), index = 1
    )
    private int renderWidget(final int selectionEnd) {
        return Math.max(0, selectionEnd);
    }
}
