package moe.caramel.chat.driver.arch.wayland;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.WString;

/**
 * caramelChat Wayland Driver
 */
public interface Driver_Wayland extends Library {

    /**
     * Initialize caramelChat Wayland Driver.
     *
     * @param wlDisplay Wayland Display
     * @param preEdit PreEdit Callback
     * @param preEditNull PreEdit Null Callback
     * @param done Done Callback
     * @param rect Rect Callback
     * @param log Log Info Callback
     * @param error Log Error Callback
     * @param debug Log Debug Callback
     */
    void initialize(
        final long wlDisplay,
        final PreeditCallback preEdit,
        final PreeditNullCallback preEditNull,
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
    void setFocus(final boolean flag);

    // ================================

    interface PreeditCallback extends Callback {
        void invoke(final WString string);
    }

    interface PreeditNullCallback extends Callback {
        void invoke();
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
