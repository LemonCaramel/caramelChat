package moe.caramel.chat.mixin.emi;

import dev.emi.emi.screen.widget.EmiSearchWidget;
import moe.caramel.chat.controller.EditBoxController;
import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * (EMI Mixin) Refresh the search results immediately.
 */
@Mixin(EmiSearchWidget.class)
public final class MixinPluginEmiSearchWidget {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(final CallbackInfo ci) {
        final EditBox editBox = ((EditBox) (Object) this);
        EditBoxController.getWrapper(editBox)
            .setInsertCallback(() -> {
                if (editBox.responder != null) {
                    editBox.responder.accept(editBox.value);
                }
            });
    }
}
