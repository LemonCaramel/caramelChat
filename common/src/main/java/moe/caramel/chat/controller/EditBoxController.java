package moe.caramel.chat.controller;

import moe.caramel.chat.wrapper.WrapperEditBox;
import net.minecraft.client.gui.components.EditBox;

/**
 * EditBox Controller
 */
public interface EditBoxController {

    /**
     * Gets the EditBox Wrapper.
     *
     * @param box EditBox object
     * @return Wrapper object
     */
    static WrapperEditBox getWrapper(final EditBox box) {
        return ((EditBoxController) box).caramelChat$wrapper();
    }

    /**
     * Gets the EditBox Wrapper.
     *
     * @return Wrapper object
     */
    WrapperEditBox caramelChat$wrapper();
}
