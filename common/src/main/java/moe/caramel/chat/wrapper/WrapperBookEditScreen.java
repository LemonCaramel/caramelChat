package moe.caramel.chat.wrapper;

import moe.caramel.chat.util.Rect;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * BookEdit Screen Wrapper
 */
public final class WrapperBookEditScreen extends AbstractIMEWrapper {

    private final BookEditScreen wrapped;

    public WrapperBookEditScreen(final BookEditScreen screen) {
        this.wrapped = screen;
        this.setFocused(true);
    }

    @Override
    protected void insert(final String text) {
        if (this.wrapped.isSigning) {
            this.wrapped.titleEdit.insertText(text);
        } else {
            this.wrapped.pageEdit.insertText(text);
        }
    }

    @Override
    protected int getCursorPos() {
        if (this.wrapped.isSigning) {
            return wrapped.titleEdit.getCursorPos();
        } else {
            return wrapped.pageEdit.getCursorPos();
        }
    }

    @Override
    protected int getHighlightPos() {
        if (this.wrapped.isSigning) {
            return wrapped.titleEdit.getSelectionPos();
        } else {
            return wrapped.pageEdit.getSelectionPos();
        }
    }

    @Override
    public boolean blockTyping() {
        final Predicate<String> tester;
        final String testTarget;
        if (this.wrapped.isSigning) {
            tester = wrapped.titleEdit.stringValidator;
            testTarget = wrapped.title;
        } else {
            tester = wrapped.pageEdit.stringValidator;
            testTarget = wrapped.getCurrentPageText();
        }

        return !tester.test(testTarget + "A"); // Dummy character
    }

    @Override
    public String getTextWithPreview() {
        if (this.wrapped.isSigning) {
            return wrapped.title;
        } else {
            return wrapped.getCurrentPageText();
        }
    }

    @Override
    protected void setPreviewText(final String text) {
        if (wrapped.isSigning) {
            this.wrapped.title = text;
            this.updateFinalizeBtn();
        } else {
            this.wrapped.setCurrentPageText(text);
        }
    }

    @Override
    public Rect getRect() {
        final Font font = (wrapped.font);
        if (wrapped.isSigning) {
            final float xWidth = font.width(wrapped.title.substring(0, wrapped.titleEdit.getCursorPos())) / 2.0f;
            final float x = (xWidth + ((wrapped.width - 192) / 2.0f) + 36 + 116 / 2.0f);
            final float y = (50 + font.lineHeight);
            return new Rect(x, y, 0, 0);
        } else {
            final List<FormattedText> lines = font.getSplitter().splitLines(wrapped.getCurrentPageText(), BookEditScreen.TEXT_WIDTH, Style.EMPTY);
            if (lines.isEmpty()) {
                return Rect.EMPTY;
            }

            final String[] lastLine = new String[1];
            lines.get(lines.size() - 1).visit(str -> {
                lastLine[0] = str;
                return Optional.empty();
            });

            // BookEditScreen#convertLocalToScreen
            final float x = font.width(lastLine[0]) + ((wrapped.width - 192) / 2.0f + 36);
            final float y = (lines.size() * font.lineHeight + 32);
            return new Rect(x, y, 0, 0);
        }
    }

    public Button.OnPress changeStatusBtnClick(final Button.OnPress original) {
        return (btn) -> {
            // Reset IME
            this.setFocused(false);
            this.setOrigin();
            this.setToNoneStatus();
            this.setFocused(true);

            // Original Task
            original.onPress(btn);
        };
    }

    public void updateFinalizeBtn() {
        this.wrapped.finalizeButton.active =
            !this.wrapped.title.isEmpty() &&
            this.wrapped.titleEdit.stringValidator.test(this.wrapped.title);
    }
}
