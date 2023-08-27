package moe.caramel.chat.wrapper;

import moe.caramel.chat.util.Rect;
import net.minecraft.client.gui.components.EditBox;

/**
 * EditBox Component Wrapper
 */
public final class WrapperEditBox extends AbstractIMEWrapper {

    private final EditBox wrapped;
    private Runnable insertCallback;
    public boolean valueChanged;

    public WrapperEditBox(final EditBox box) {
        super(box.value);
        this.wrapped = box;
        this.insertCallback = () -> {}; // Empty Callback
    }

    @Override
    protected void insert(final String text) {
        if (this.editable()) {
            this.wrapped.insertText(text);
            this.insertCallback.run();
        }
    }

    @Override
    protected int getCursorPos() {
        return wrapped.cursorPos;
    }

    @Override
    protected int getHighlightPos() {
        return wrapped.highlightPos;
    }

    @Override
    public boolean editable() {
        return wrapped.canConsumeInput();
    }

    @Override
    public boolean blockTyping() {
        if (!this.wrapped.canConsumeInput()) {
            return true;
        }

        final int start = Math.min(wrapped.cursorPos, wrapped.highlightPos);
        final int end = Math.max(wrapped.cursorPos, wrapped.highlightPos);
        final int remain = (wrapped.maxLength - wrapped.value.length()) - (start - end);
        return remain <= 0;
    }

    @Override
    protected String getTextWithPreview() {
        return wrapped.value;
    }

    @Override
    protected void setPreviewText(final String text) {
        this.valueChanged = true;
        this.wrapped.setValue(text);

        if (this.wrapped.isFocused()) {
            this.insertCallback.run();
        }
    }

    @Override
    public Rect getRect() {
        final int xWidth = wrapped.font.width(wrapped.value.substring(0, wrapped.getCursorPosition()));
        final float x = xWidth + wrapped.getX() + (wrapped.bordered ? 4 : 0);
        final float y = wrapped.font.lineHeight + wrapped.getY() + (wrapped.bordered ? ((wrapped.getHeight() - 8) / 2.0f) : 0);
        return new Rect(x, y, wrapped.getWidth(), wrapped.getHeight());
    }

    /**
     * Sets the callback to be executed upon insert.
     *
     * @param callback insert callback
     */
    public void setInsertCallback(final Runnable callback) {
        this.insertCallback = callback;
    }
}
