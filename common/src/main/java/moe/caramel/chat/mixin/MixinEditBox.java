package moe.caramel.chat.mixin;

import moe.caramel.chat.controller.EditBoxController;
import moe.caramel.chat.wrapper.AbstractIMEWrapper;
import moe.caramel.chat.wrapper.WrapperEditBox;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * EditBox Component Mixin
 */
@Mixin(value = EditBox.class, priority = 0)
public abstract class MixinEditBox implements EditBoxController {

    @Unique private WrapperEditBox caramelChat$wrapper;
    @Unique private int caramelChat$cacheCursorPos, caramelChat$cacheHighlightPos;
    @Shadow private boolean canLoseFocus;
    @Shadow public int highlightPos;
    @Shadow public int cursorPos;
    @Shadow public String value;
    @Shadow @Final private List<EditBox.TextFormatter> formatters;

    @Shadow
    public int maxLength;

    @Redirect(
        method = "<init>(Lnet/minecraft/client/gui/Font;IIIILnet/minecraft/client/gui/components/EditBox;Lnet/minecraft/network/chat/Component;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/EditBox;setValue(Ljava/lang/String;)V")
    )
    private void init(final EditBox self, final String value) {
        this.caramelChat$wrapper = new WrapperEditBox((EditBox) (Object) this);
        self.setValue(value);
    }

    @Inject(
        method = "<init>(Lnet/minecraft/client/gui/Font;IIIILnet/minecraft/client/gui/components/EditBox;Lnet/minecraft/network/chat/Component;)V",
        at = @At("TAIL")
    )
    private void lazyInit(final CallbackInfo ci) {
        if (this.caramelChat$wrapper == null) {
            this.caramelChat$wrapper = new WrapperEditBox((EditBox) (Object) this);
        }
        this.formatters.add(this.caramelChat$caretFormatter());
    }

    @Override
    public WrapperEditBox caramelChat$wrapper() {
        return caramelChat$wrapper;
    }

    // ================================ (Formatter)

    @Unique
    private EditBox.TextFormatter caramelChat$caretFormatter() {
        // Set caret renderer
        return ((original, firstPos) -> {
            /* Original */
            if (caramelChat$wrapper.getStatus() == AbstractIMEWrapper.InputStatus.NONE) {
                return null;
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
                    return null;
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

    @Inject(method = "setValue", at = @At("HEAD"))
    private void setValueHead(final String text, final CallbackInfo ci) {
        this.caramelChat$cacheCursorPos = this.cursorPos;
        this.caramelChat$cacheHighlightPos = this.highlightPos;

        if (this.caramelChat$wrapper == null || !this.caramelChat$wrapper.valueChanged) {
            this.caramelChat$setStatusToNone();
        }
    }

    @Inject(
        method = "setValue",
        at = @At(
            value = "INVOKE", shift = At.Shift.BEFORE,
            target = "Lnet/minecraft/client/gui/components/EditBox;moveCursorToEnd(Z)V"
        ), cancellable = true
    )
    private void setValueInvoke(final String text, final CallbackInfo ci) {
        if (this.caramelChat$wrapper != null && this.caramelChat$wrapper.valueChanged) {
            ci.cancel();
            // caxton Compatibility
            this.cursorPos = this.caramelChat$cacheCursorPos;
            this.highlightPos = this.caramelChat$cacheHighlightPos;
            this.caramelChat$wrapper.valueChanged = false;
            return;
        }

        this.caramelChat$forceUpdateOrigin();
    }

    @Inject(method = "insertText", at = @At("HEAD"))
    private void insertTextHead(final String text, final CallbackInfo ci) {
        // setStatusToNone -> forceUpdateOrigin -> onValueChange
        this.caramelChat$setStatusToNone();
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
        method = "deleteCharsToPos",
        at = @At(
            value = "INVOKE", shift = At.Shift.BEFORE,
            target = "Lnet/minecraft/client/gui/components/EditBox;onValueChange(Ljava/lang/String;)V"
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
}
