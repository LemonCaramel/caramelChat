package moe.caramel.chat.driver.arch.darwin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

final class DarwinFocusStateTest {

    @Test
    void reRegistersAndReactivatesAfterNativeRegistryRefresh() {
        final DarwinFocusState state = new DarwinFocusState(0);
        final AtomicInteger registrations = new AtomicInteger();
        final List<Integer> receiverChanges = new ArrayList<>();

        state.setFocused(true, 0, registrations::incrementAndGet, receiverChanges::add);

        // Native refreshInstance() forgets every registration, but Java-side focus can remain true.
        state.setFocused(true, 1, registrations::incrementAndGet, receiverChanges::add);

        assertEquals(1, registrations.get());
        assertEquals(List.of(1, 1), receiverChanges);
        assertTrue(state.isFocused());

        state.setFocused(false, 1, registrations::incrementAndGet, receiverChanges::add);
        state.setFocused(false, 1, registrations::incrementAndGet, receiverChanges::add);

        assertEquals(1, registrations.get());
        assertEquals(List.of(1, 1, 0), receiverChanges);
        assertFalse(state.isFocused());
    }

    @Test
    void doesNotReRegisterWhileNativeRegistryIsUnchanged() {
        final DarwinFocusState state = new DarwinFocusState(0);
        final AtomicInteger registrations = new AtomicInteger();
        final List<Integer> receiverChanges = new ArrayList<>();

        state.setFocused(true, 0, registrations::incrementAndGet, receiverChanges::add);
        state.setFocused(true, 0, registrations::incrementAndGet, receiverChanges::add);

        assertEquals(0, registrations.get());
        assertEquals(List.of(1), receiverChanges);
    }
}
