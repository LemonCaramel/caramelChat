package moe.caramel.chat.driver.arch.win;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import moe.caramel.chat.driver.KeyboardStatus.Language;
import java.util.Map;

/**
 * CocoaInput Windows Driver
 */
public interface Driver_Win extends Library {

    int LAYOUT_CHINESE_TRADITIONAL = 0x0404;
    int LAYOUT_JAPANESE = 0x0411;
    int LAYOUT_KOREAN = 0x0412;
    int LAYOUT_CHINESE_SIMPLIFIED = 0x0804;

    Map<Integer, Language> LAYOUT_MAP = Map.of(
        LAYOUT_KOREAN, Language.KOREAN,
        LAYOUT_JAPANESE, Language.JAPANESE,
        LAYOUT_CHINESE_SIMPLIFIED, Language.CHINESE_SIMPLIFIED,
        LAYOUT_CHINESE_TRADITIONAL, Language.CHINESE_TRADITIONAL
    );

    // ================================

    /**
     * Initialize CocoaInput Windows Driver.
     *
     * @param windowId Window Id
     * @param preEdit PreEdit Callback
     * @param done Done Callback
     * @param rect Rect Callback
     * @param log Log Info Callback
     * @param error Log Error Callback
     * @param debug Log Debug Callback
     */
    void initialize(
        final long windowId,
        final PreeditCallback preEdit,
        final DoneCallback done,
        final RectCallback rect,
        final LogInfoCallback log,
        final LogErrorCallback error,
        final LogDebugCallback debug
    );

    /**
     * Set whether to focus or not.
     *
     * @param flag focus
     */
    void set_focus(final int flag);

    /**
     * Gets the current keyboard layout.
     *
     * @return current keyboard layout
     */
    int getKeyboardLayout();

    /**
     * Gets the current IME status.
     *
     * @return IME status. (Native if 1)
     */
    int getStatus();

    // ================================

    interface PreeditCallback extends Callback {
        void invoke(final WString string, final int cursor, final int length);
    }

    interface DoneCallback extends Callback {
        void invoke(final WString string);
    }

    interface RectCallback extends Callback {
        int invoke(final Pointer pointer);
    }

    interface LogInfoCallback extends Callback {
        void invoke(final String log);
    }

    interface LogErrorCallback extends Callback {
        void invoke(final String log);
    }

    interface LogDebugCallback extends Callback {
        void invoke(final String log);
    }
}
