package moe.caramel.chat.driver.arch.unknown;

import moe.caramel.chat.driver.IController;
import moe.caramel.chat.driver.IOperator;

/**
 * Unknown IME Operator
 */
public final class UnknownOperator implements IOperator {

    @Override
    public IController getController() {
        return UnknownController.INSTANCE;
    }

    @Override
    public void setFocused(final boolean focus) {
    }

    @Override
    public boolean isFocused() {
        return false;
    }
}
