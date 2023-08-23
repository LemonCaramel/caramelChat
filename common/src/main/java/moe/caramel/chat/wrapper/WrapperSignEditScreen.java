package moe.caramel.chat.wrapper;

import moe.caramel.chat.util.Rect;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.gui.screens.inventory.HangingSignEditScreen;
import net.minecraft.world.level.block.StandingSignBlock;

/**
 * SignEdit Screen Wrapper
 */
public final class WrapperSignEditScreen extends AbstractIMEWrapper {

    private final AbstractSignEditScreen wrapped;

    public WrapperSignEditScreen(final AbstractSignEditScreen screen) {
        this.wrapped = screen;
        this.setFocused(true);
    }

    @Override
    protected void insert(final String text) {
        this.wrapped.signField.insertText(text);
    }

    @Override
    protected int getCursorPos() {
        return wrapped.signField.getCursorPos();
    }

    @Override
    protected int getHighlightPos() {
        return wrapped.signField.getSelectionPos();
    }

    @Override
    public boolean blockTyping() {
        return !wrapped.signField.stringValidator.test(this.origin + "A");
    }

    @Override
    public String getTextWithPreview() {
        return wrapped.messages[wrapped.line];
    }

    @Override
    protected void setPreviewText(final String text) {
        this.wrapped.messages[wrapped.line] = text;
    }

    @Override
    public Rect getRect() {
        /* Calc Position */
        final int xWidth = wrapped.font.width(this.getTextWithPreview().substring(0, wrapped.signField.getCursorPos()));
        final float x = ( (wrapped.width / 2.0f) + (xWidth / 2.0f) );

        final float yHeight = 90.0f + ( (wrapped.line - 1) * wrapped.sign.getTextLineHeight() );
        final float y;
        if (wrapped instanceof HangingSignEditScreen || !(wrapped.sign.getBlockState().getBlock() instanceof StandingSignBlock)) {
            y = yHeight + 35.0f; // offsetSign
        } else {
            y = yHeight;
        }

        return new Rect(x, y, 0, 0);
    }
}
