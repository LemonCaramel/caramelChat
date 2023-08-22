package moe.caramel.chat.wrapper;

import moe.caramel.chat.Main;
import moe.caramel.chat.driver.IOperator;
import moe.caramel.chat.util.ModLogger;
import moe.caramel.chat.util.Rect;

/**
 * Abstract IME Wrapper
 */
public abstract class AbstractIMEWrapper {

    private final IOperator ime;
    private InputStatus status = InputStatus.NONE;
    private int firstEndPos = -1, secondStartPos = -1;
    protected String origin;

    protected AbstractIMEWrapper() {
        this("");
    }

    protected AbstractIMEWrapper(final String defValue) {
        this.ime = Main.getController().createOperator(this);
        this.origin = defValue;
    }

    // ================================

    /**
     * Input Status
     */
    public enum InputStatus {
        NONE, // Done
        PREVIEW // Preview
    }

    /**
     * Gets the current input status.
     *
     * @return current input status
     */
    public final InputStatus getStatus() {
        return status;
    }

    /**
     * Sets the current input status to none.
     */
    public final void setToNoneStatus() {
        this.status = InputStatus.NONE;
        this.setPreviewText(this.origin);
    }

    /**
     * Gets the end position of the first text.
     *
     * @return end position of the first text
     */
    public final int getFirstEndPos() {
        return firstEndPos;
    }

    /**
     * Gets the start position of the second text.
     *
     * @return start position of the second text
     */
    public final int getSecondStartPos() {
        return secondStartPos;
    }

    // ================================

    /**
     * Change whether IME is enabled or disabled.
     *
     * @param focused whether IME is enabled or not
     */
    public final void setFocused(final boolean focused) {
        this.ime.setFocused(focused);
    }

    /**
     * Gets the current final input value.
     *
     * @return current final input value
     */
    public final String getOrigin() {
        return origin;
    }

    /**
     * Save the current input value with preview.
     */
    public final void setOrigin() {
        this.setOrigin(this.getTextWithPreview());
    }

    /**
     * Changes the current final input value.
     *
     * @param value input value
     */
    public final void setOrigin(final String value) {
        this.origin = value;
    }

    // ================================

    /**
     * (1) Appends preview text to the current input value.
     *
     * @param typing preview text
     */
    public final void appendPreviewText(final String typing) {
        if (!this.editable()) {
            return;
        }

        ModLogger.debug("[Preview] Current: ({}) / Preview: ({})", this.origin, typing);
        this.status = InputStatus.PREVIEW;

        final int start = Math.min(this.getCursorPos(), this.getHighlightPos());
        final int end = Math.max(this.getCursorPos(), this.getHighlightPos());
        final boolean samePos = (start == end);
        final int lastPos = origin.length();

        // Other Pos
        if (lastPos != end && samePos) {
            final String first = this.origin.substring(0, end);
            final String second = this.origin.substring(end, lastPos);
            this.firstEndPos = first.length();
            this.secondStartPos = (this.firstEndPos + typing.length());
            this.setPreviewText(first + typing + second);
        }
        // Last Pos
        else if (samePos) {
            final String result = (this.origin + typing);
            this.firstEndPos = this.origin.length();
            this.secondStartPos = result.length();
            this.setPreviewText(result);
        }
        // Selected
        else {
            //this.setPreviewText(new StringBuilder(this.origin).replace(start, end, typing).toString()); // strange..

            // Cache
            final String first = this.origin.substring(0, start);
            final String second = this.origin.substring(end, lastPos);

            // Delete Selected section & Force Update
            this.insert("");
            this.origin = this.getTextWithPreview();

            // Add Preview
            this.firstEndPos = first.length();
            this.secondStartPos = (this.firstEndPos + typing.length());
            this.setPreviewText(first + typing + second);
        }
    }

    /**
     * (2) Put the completed text in the final input value.
     *
     * @param input completed text
     */
    public final void insertText(final String input) {
        if (this.blockTyping() || !this.editable()) {
            return;
        }

        ModLogger.debug("[Complete] Current: ({}) / Preview: ({})", this.origin, input);
        this.status = InputStatus.NONE;
        this.firstEndPos = -1;
        this.secondStartPos = -1;

        this.setPreviewText(this.origin);
        this.insert(input);
        this.origin = this.getTextWithPreview();
    }

    /**
     * (2-1) Insert text value into the input component.
     *
     * @param text text value
     */
    protected abstract void insert(final String text);

    // ================================

    /**
     * Gets the position of the cursor.
     *
     * @return cursor position
     */
    protected abstract int getCursorPos();

    /**
     * Gets the position of the highlight cursor.
     *
     * @return highlight cursor position
     */
    protected abstract int getHighlightPos();

    /**
     * Gets whether to editable.
     *
     * @return editable
     */
    public boolean editable() {
        return true;
    }

    /**
     * Gets whether to block typing.
     *
     * @return block typing
     */
    public abstract boolean blockTyping();

    /**
     * Gets the current input value, including the preview.
     *
     * @return current input value
     */
    protected abstract String getTextWithPreview();

    /**
     * Sets the current input value, including the preview.
     *
     * @param text current input value
     */
    protected abstract void setPreviewText(final String text);

    /**
     * Gets the rect square structure.
     *
     * @return rect square structure
     */
    public abstract Rect getRect();
}
