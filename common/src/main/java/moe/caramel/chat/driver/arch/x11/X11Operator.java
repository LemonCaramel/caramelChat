package moe.caramel.chat.driver.arch.x11;

import moe.caramel.chat.driver.IOperator;
import moe.caramel.chat.util.ModLogger;
import moe.caramel.chat.wrapper.AbstractIMEWrapper;

/**
 * X11 IME Operator
 */
public final class X11Operator implements IOperator {

    private final X11Controller controller;
    private final AbstractIMEWrapper wrapper;
    private boolean nowFocused;

    public X11Operator(final X11Controller controller, final AbstractIMEWrapper wrapper) {
        this.controller = controller;
        this.wrapper = wrapper;
    }

    /**
     * Gets the IME wrapper.
     *
     * @return IME wrapper
     */
    public AbstractIMEWrapper getWrapper() {
        return wrapper;
    }

    @Override
    public void setFocused(final boolean focus) {
        if (focus == this.nowFocused) {
            return;
        }

        ModLogger.debug("[Native|Java] Called setFocused: " + focus);
        this.nowFocused = focus;

        if (focus) {
            X11Controller.focused = this;
            this.controller.setFocus(true);
        } else if (X11Controller.focused == this) {
            this.wrapper.insertText("");
            X11Controller.focused = null;
            this.controller.setFocus(false);
            X11Controller.setupKeyboardEvent();
        }
    }
}
