package moe.caramel.chat.driver.arch.win;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.WString;

/**
 * CocoaInput Windows Driver
 */
public interface Driver_Win extends Library {

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
