package moe.caramel.chat.wrapper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

final class InternalValueChangeTest {

    @Test
    void clearsFlagWhenWidgetSkipsAnUnchangedValueUpdate() {
        final InternalValueChange change = new InternalValueChange();

        change.run(() -> assertTrue(change.isActive()));

        assertFalse(change.isActive());
    }

    @Test
    void clearsFlagWhenWidgetUpdateThrows() {
        final InternalValueChange change = new InternalValueChange();

        assertThrows(IllegalStateException.class, () -> change.run(() -> {
            throw new IllegalStateException("widget update failed");
        }));

        assertFalse(change.isActive());
    }
}
