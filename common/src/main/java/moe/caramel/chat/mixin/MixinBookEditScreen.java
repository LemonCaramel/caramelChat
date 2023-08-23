package moe.caramel.chat.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.caramel.chat.controller.ScreenController;
import moe.caramel.chat.wrapper.AbstractIMEWrapper;
import moe.caramel.chat.wrapper.WrapperBookEditScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * BookEdit Screen Mixin
 */
@Mixin(BookEditScreen.class)
public abstract class MixinBookEditScreen extends Screen implements ScreenController {

    @Shadow protected abstract void renderHighlight(final PoseStack stack, final Rect2i[] selection);
    @Shadow protected abstract void renderCursor(final PoseStack stack, final BookEditScreen.Pos2i pos, final boolean atEnd);
    @Shadow protected abstract BookEditScreen.DisplayCache getDisplayCache();

    @Shadow public String title;
    @Shadow @Final public TextFieldHelper titleEdit;
    @Shadow public Button finalizeButton;
    @Unique private WrapperBookEditScreen caramelChat$wrapper;

    private MixinBookEditScreen(final Component title) {
        super(title);
    }

    // ================================ (IME)

    @Inject(method = "init", at = @At("HEAD"))
    private void init(final CallbackInfo ci) {
        this.caramelChat$wrapper = new WrapperBookEditScreen((BookEditScreen) (Object) this);
        this.caramelChat$wrapper.setOrigin();
    }

    @ModifyArg(
        method = "<init>",
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

    @ModifyArg(
        method = "init",
        at = @At(
            value = "INVOKE", ordinal = 0,
            target = "Lnet/minecraft/client/gui/components/Button;builder(Lnet/minecraft/network/chat/Component;Lnet/minecraft/client/gui/components/Button$OnPress;)Lnet/minecraft/client/gui/components/Button$Builder;"
        ), index = 1
    )
    private Button.OnPress onSignBtnClick(final Button.OnPress onPress) {
        return caramelChat$wrapper.changeStatusBtnClick(onPress);
    }

    @ModifyArg(
        method = "init",
        at = @At(
            value = "INVOKE", ordinal = 3,
            target = "Lnet/minecraft/client/gui/components/Button;builder(Lnet/minecraft/network/chat/Component;Lnet/minecraft/client/gui/components/Button$OnPress;)Lnet/minecraft/client/gui/components/Button$Builder;"
        ), index = 1
    )
    private Button.OnPress onCancelBtnClick(final Button.OnPress onPress) {
        return caramelChat$wrapper.changeStatusBtnClick(onPress);
    }

    @Redirect(
        method = "titleKeyPressed",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/String;isEmpty()Z"
        )
    )
    private boolean titleKeyPressed(final String text) {
        return !this.finalizeButton.active || text.isEmpty();
    }

    @Inject(method = "updateButtonVisibility", at = @At("RETURN"))
    private void updateButtonVisibility(final CallbackInfo ci) {
        this.title = title.substring(0, Math.min(title.length(), 16 - 1));
        this.titleEdit.setCursorToEnd();
        this.caramelChat$wrapper.setOrigin();
        this.caramelChat$wrapper.updateFinalizeBtn();
    }

    // ================================ (Render Caret)

    @Redirect(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/FormattedCharSequence;composite(Lnet/minecraft/util/FormattedCharSequence;Lnet/minecraft/util/FormattedCharSequence;)Lnet/minecraft/util/FormattedCharSequence;"
        )
    )
    private FormattedCharSequence rewriteDrawTitle(final FormattedCharSequence firstFormat, final FormattedCharSequence secondFormat) {
        // Check IME Status
        if (caramelChat$wrapper.getStatus() == AbstractIMEWrapper.InputStatus.NONE) {
            return FormattedCharSequence.composite(firstFormat, secondFormat);
        }

        // Render Caret
        final int firstEndPos = Math.min(caramelChat$wrapper.getFirstEndPos(), title.length());
        final int secondStartPos = Math.min(caramelChat$wrapper.getSecondStartPos(), title.length());

        final List<FormattedCharSequence> list = new ArrayList<>();
        final String first = title.substring(0, firstEndPos);
        final String input = title.substring(firstEndPos, secondStartPos);
        final String second = title.substring(secondStartPos);
        list.add(FormattedCharSequence.forward(first, Style.EMPTY));
        list.add(FormattedCharSequence.forward(input, Style.EMPTY.withUnderlined(true)));
        list.add(FormattedCharSequence.forward(second, Style.EMPTY));
        list.add(secondFormat);

        return FormattedCharSequence.composite(list);
    }

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            shift = At.Shift.BEFORE,
            target = "Lnet/minecraft/client/gui/screens/inventory/BookEditScreen;getDisplayCache()Lnet/minecraft/client/gui/screens/inventory/BookEditScreen$DisplayCache;"
        ), cancellable = true
    )
    private void rewriteDrawPage(final PoseStack stack, final int mouseX, final int mouseY, final float delta, final CallbackInfo ci) {
        // Check IME Status
        if (caramelChat$wrapper.getStatus() == AbstractIMEWrapper.InputStatus.NONE) {
            return;
        }

        // Cancel Default Renderer
        ci.cancel();

        // Render Caret
        final BookEditScreen.DisplayCache cache = this.getDisplayCache();
        final BookEditScreen.LineInfo[] infos = cache.lines;
        for (int line = 0; line < infos.length; line++) {
            final BookEditScreen.LineInfo info = infos[line];
            final int color = (caramelChat$wrapper.blockTyping() ? 0x00FF5555 : 0xFF000000);

            // Check Line
            final int lineStartPos = (cache.lineStarts[line]);
            final int lineEndPos = (lineStartPos + info.contents.length());
            if (info.contents.isEmpty() || lineEndPos < caramelChat$wrapper.getFirstEndPos() || caramelChat$wrapper.getSecondStartPos() <= lineStartPos) {
                this.font.draw(stack, info.contents, info.x, info.y, color);
                continue;
            }

            // Calculate
            final int lineFirstEndPos = (caramelChat$wrapper.getFirstEndPos() - lineStartPos);
            final int lineSecondStartPos = Math.abs(caramelChat$wrapper.getSecondStartPos() - lineStartPos);
            final int firstEndPos = Math.max(0, lineFirstEndPos);
            final int secondStartPos = Math.min(info.contents.length(), lineSecondStartPos);

            final String first = info.contents.substring(0, firstEndPos);
            final String input = info.contents.substring(firstEndPos, secondStartPos);
            final String second = info.contents.substring(secondStartPos);

            this.font.draw(stack,
                Component.literal(first).append(Component.literal(input).withStyle(ChatFormatting.UNDERLINE)).append(second),
                info.x, info.y, color
            );
        }

        // Run cancelled task
        this.renderHighlight(stack, cache.selection);
        this.renderCursor(stack, cache.cursor, cache.cursorAtEnd);
        super.render(stack, mouseX, mouseY, delta);
    }
}
