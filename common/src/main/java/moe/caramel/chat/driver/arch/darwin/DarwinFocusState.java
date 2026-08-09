package moe.caramel.chat.driver.arch.darwin;

import java.util.function.IntConsumer;

/**
 * Tracks Java-side focus for a native CocoaInput text field.
 */
final class DarwinFocusState {

    private boolean focused;
    private long registeredGeneration;

    public DarwinFocusState(final long registeredGeneration) {
        this.registeredGeneration = registeredGeneration;
    }

    public void setFocused(
        final boolean focus, final long registryGeneration,
        final Runnable register, final IntConsumer setReceiver
    ) {
        final boolean registrationExpired = (this.registeredGeneration != registryGeneration);
        if (focus && registrationExpired) {
            register.run();
            this.registeredGeneration = registryGeneration;
        }

        if (focus != this.focused || (focus && registrationExpired)) {
            setReceiver.accept(focus ? 1 : 0);
        }

        this.focused = focus;
    }

    public boolean isFocused() {
        return focused;
    }
}
