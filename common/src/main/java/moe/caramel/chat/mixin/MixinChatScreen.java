package moe.caramel.chat.mixin;

import static moe.caramel.chat.PlatformProvider.getProvider;
import static net.minecraft.network.chat.Component.translatable;
import moe.caramel.chat.Main;
import moe.caramel.chat.controller.EditBoxController;
import moe.caramel.chat.driver.KeyboardStatus;
import moe.caramel.chat.wrapper.WrapperEditBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.HoverEvent.ShowText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Chat screen Mixin
 */
@Mixin(ChatScreen.class)
public abstract class MixinChatScreen {

    @Shadow protected EditBox input;
    @Shadow CommandSuggestions commandSuggestions;

    @Unique private static final int TOOLTIP_TIME = 500;
    @Unique private static final int FADE_TIME = 250;
    @Unique private static final Component MARK_VERSION = translatable("caramelChat v%s", getProvider().getVersion())
        .setStyle(Style.EMPTY.withHoverEvent(new ShowText(translatable("caramel.chat.redistribution_warn"))));
    @Unique private static final FontDescription UNIFORM_RESOURCE = new FontDescription.Resource(Identifier.withDefaultNamespace("uniform"));

    @Unique private KeyboardStatus.Language caramelChat$lastLanguage;
    @Unique private long caramelChat$changeTime;

    @Inject(method = "extractRenderState", at = @At("HEAD"))
    private void render(final GuiGraphicsExtractor helper, final int mouseX, final int mouseY, final float tickDelta, final CallbackInfo ci) {
        final Screen screen = ((Screen) (Object) this);
        this.caramelChat$renderMark(screen, helper, mouseX, mouseY, tickDelta);
        this.caramelChat$renderImeStatus(screen, helper, mouseX, mouseY, tickDelta);
    }

    @Unique
    private void caramelChat$renderMark(final Screen screen, final GuiGraphicsExtractor helper, final int mouseX, final int mouseY, final float tickDelta) {
        final int markStartY = 10;
        final int markEndY = (markStartY + screen.font.lineHeight);
        final int markEndX = (screen.width - 10);
        final int markStartX = (markEndX - screen.font.width(MARK_VERSION));

        helper.text(screen.font, MARK_VERSION, markStartX, markStartY, 0x33FFFFFF, false);
        if ( (markStartX <= mouseX && mouseX <= markEndX) && (markStartY <= mouseY && mouseY <= markEndY) ) {
            helper.setTooltipForNextFrame(screen.font, MARK_VERSION, mouseX, mouseY);
        }
    }

    @Unique
    private void caramelChat$renderImeStatus(final Screen screen, final GuiGraphicsExtractor helper, final int mouseX, final int mouseY, final float tickDelta) {
        final WrapperEditBox wrapper = EditBoxController.getWrapper(this.input);
        final KeyboardStatus status = wrapper.getIme().getController().getKeyboardStatus();
        if (status == null) {
            return; // Unsupported OS or Not yet initialized
        }

        /* Render Debug information */
        if (Main.DEBUG) {
            helper.text(screen.font, status.toString(), 10, 10, 0xFFFFFFFF, true);
        }

        /* Check Render condition */
        final long currentTime = System.currentTimeMillis();
        if (this.caramelChat$lastLanguage != status.language()) {
            this.caramelChat$lastLanguage = status.language();
            this.caramelChat$changeTime = currentTime;
        }

        final int elapsed = (int) (currentTime - this.caramelChat$changeTime);
        if (elapsed > TOOLTIP_TIME) {
            return;
        }

        /* Calculate Position */
        final CommandSuggestions suggestions = this.commandSuggestions;
        final Component display = Component.literal(status.display()).withStyle(style -> style.withFont(UNIFORM_RESOURCE));

        int borderStartX = 2;
        int borderEndX = (borderStartX + Mth.floor(screen.font.getSplitter().stringWidth(display) - status.offset()) + 4);
        final int suggestionWidth = (suggestions.suggestions != null) ? suggestions.suggestions.rect.getX() : Integer.MAX_VALUE;
        if (suggestionWidth <= borderEndX) {
            final int safeRange = (suggestionWidth - borderEndX - 2);
            borderStartX += safeRange;
            borderEndX += safeRange;
        }

        final int usageHeight = (suggestions.suggestions == null && suggestions.commandUsagePosition == 0) ? (12 * suggestions.commandUsage.size()) : 0;
        final int borderEndY = (screen.height - 14 - usageHeight - 2);
        final int borderStartY = (borderEndY - screen.font.lineHeight - 2);

        /* Render */
        final int backColor = this.caramelChat$color(Minecraft.getInstance().options.getBackgroundColor(Integer.MIN_VALUE), elapsed);
        final int textColor = this.caramelChat$color(0xFFFFFFFF, elapsed);
        helper.fill(borderStartX, borderStartY, borderEndX, borderEndY, backColor);
        helper.text(screen.font, display, (borderStartX + 2), (borderStartY + 1), textColor, false);
    }

    @Unique
    private int caramelChat$color(final int color, final int elapsed) {
        // Fade time hasn't started yet.
        if (elapsed < FADE_TIME) {
            return color;
        }

        // Calculate alpha value
        final int initialAlpha = ( (color >> 24) & 0xFF );
        final float progress = ((float) (TOOLTIP_TIME - elapsed) / FADE_TIME);
        final int alpha = (int) (progress * initialAlpha);
        if (alpha < 5) {
            return 0;
        }

        return (alpha << 24) | (color & 0x00FFFFFF);
    }
}
