package moe.caramel.chat.wrapper;

/**
 * Distinguishes IME preview updates from external value changes.
 */
final class InternalValueChange {

    private boolean active;

    public void run(final Runnable update) {
        this.active = true;
        try {
            update.run();
        } finally {
            this.active = false;
        }
    }

    public boolean isActive() {
        return active;
    }
}
