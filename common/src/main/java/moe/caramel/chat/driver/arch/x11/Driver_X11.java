package moe.caramel.chat.driver.arch.x11;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.WString;

/**
 * CocoaInput X11 Driver
 */
public interface Driver_X11 extends Library {

    /**
     * Initialize CocoaInput X11 Driver.
     *
     * @param windowId Window Id
     * @param xWindowId X11 Window Id
     * @param draw Draw Callback
     * @param done Done Callback
     * @param log Log Info Callback
     * @param error Log Error Callback
     * @param debug Log Debug Callback
     */
    void initialize(
        final long windowId,
        final long xWindowId,
        final DrawCallback draw,
        final DoneCallback done,
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

    interface DrawCallback extends Callback {
        Pointer invoke(
            final int caret, final int chg_first, final int chg_length, final short length,
            final boolean iswstring, final String rawstring, final WString rawwstring,
            final int primary, final int secondary, final int tertiary
        );
    }

    interface DoneCallback extends Callback {
        void invoke();
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
