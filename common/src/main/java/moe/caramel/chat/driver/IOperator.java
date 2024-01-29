package moe.caramel.chat.driver;

/**
 * IME Operator Interface
 */
public interface IOperator {

    /**
     * Gets the IME controller.
     *
     * @return controller
     */
    IController getController();

    /**
     * Set whether to focus or not. (Wrapper)
     *
     * @param focus focus
     */
    void setFocused(final boolean focus);

    /**
     * Get whether to focus or not. (Wrapper)
     *
     * @return focus
     */
    boolean isFocused();
}
