package moe.caramel.chat.driver.arch.unknown;

import moe.caramel.chat.driver.IController;
import moe.caramel.chat.driver.IOperator;
import moe.caramel.chat.util.ModLogger;
import moe.caramel.chat.wrapper.AbstractIMEWrapper;
import net.minecraft.client.gui.screens.Screen;

/**
 * Unknown Controller
 */
public final class UnknownController implements IController {

    public static final IController INSTANCE = new UnknownController();

    private UnknownController() {
        ModLogger.log("[Native] Load the Unknown Controller.");
    }

    @Override
    public IOperator createOperator(final AbstractIMEWrapper wrapper) {
        return new UnknownOperator();
    }

    @Override
    public void changeFocusedScreen(final Screen screen) {
    }

    @Override
    public void setFocus(final boolean focus) {
    }
}
