package moe.caramel.chat.mixin.xaeromap;

import moe.caramel.chat.controller.EditBoxController;
import moe.caramel.chat.wrapper.AbstractIMEWrapper;
import moe.caramel.chat.wrapper.WrapperEditBox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.gui.GuiAddWaypoint;

/**
 * (Xaero's Minimap Mixin) Fix IME.
 */
@Mixin(GuiAddWaypoint.class)
public abstract class MixinPluginXaeroMapWayPoint {

    @Shadow private EditBox nameTextField;
    @Shadow private EditBox initialTextField;
    @Shadow(remap = false) private boolean ignoreEditBoxChanges;
    @Shadow(remap = false) protected abstract void postType(final GuiEventListener focused);

    @Inject(method = "init", at = @At("TAIL"))
    private void init(final CallbackInfo ci) {
        this.caramelChat$fixIme(this.nameTextField);
        this.caramelChat$fixIme(this.initialTextField);
    }

    @Unique
    private void caramelChat$fixIme(final EditBox box) {
        final WrapperEditBox wrapper = EditBoxController.getWrapper(box);
        wrapper.setInsertCallback(() -> {
            if (wrapper.getStatus() == AbstractIMEWrapper.InputStatus.PREVIEW) {
                this.ignoreEditBoxChanges = false;
                this.postType(box);
            }
        });
    }
}
