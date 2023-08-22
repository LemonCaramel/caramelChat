package moe.caramel.chat.driver.arch.win;

import moe.caramel.chat.driver.IOperator;
import moe.caramel.chat.util.ModLogger;
import moe.caramel.chat.wrapper.AbstractIMEWrapper;

/**
 * Windows IME Operator
 */
public final class WinOperator implements IOperator {

    private final WinController controller;
    private final AbstractIMEWrapper wrapper;
    private boolean nowFocused;

    public WinOperator(final WinController controller, final AbstractIMEWrapper wrapper) {
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
            WinController.focused = this;
            this.controller.setFocus(true);
        } else if (WinController.focused == this) {
            WinController.focused = null;
            this.controller.setFocus(false);
        }
    }
}
