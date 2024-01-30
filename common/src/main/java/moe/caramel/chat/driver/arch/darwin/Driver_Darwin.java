package moe.caramel.chat.driver.arch.darwin;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Pointer;

/**
 * CocoaInput Darwin Driver
 */
public interface Driver_Darwin extends Library {

    /**
     * Initialize CocoaInput Darwin Driver.
     *
     * @param log Log Info Callback
     * @param error Log Error Callback
     * @param debug Log Debug Callback
     */
    void initialize(final LogInfoCallback log, final LogErrorCallback error, final LogDebugCallback debug);

    void addInstance(
        final String uuid, final InsertText insertText,
        final SetMarkedText setMarkedText, final FirstRectForCharacterRange range
    );

    void removeInstance(final String uuid);

    void refreshInstance();

    void discardMarkedText(final String uuid);

    void setIfReceiveEvent(final String uuid, final int yn);

    float invertYCoordinate(final float y);

    String getStatus();

    // ================================

    interface InsertText extends Callback {
        void invoke(final String str, final int position, final int length);
    }

    interface SetMarkedText extends Callback {
        void invoke(final String str, final int position1, final int length1, final int position2, final int length2);
    }

    interface FirstRectForCharacterRange extends Callback {
        void invoke(final Pointer pointer);
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
