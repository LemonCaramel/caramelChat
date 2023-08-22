package moe.caramel.chat.mixin;

import moe.caramel.chat.controller.EditBoxController;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * CreativeModeInventory Screen Mixin
 */
@Mixin(CreativeModeInventoryScreen.class)
public abstract class MixinCreativeModeInventoryScreen {

    @Shadow private EditBox searchBox;
    @Shadow public abstract void refreshSearchResults();

    @Inject(method = "init", at = @At(value = "TAIL"))
    private void init(final CallbackInfo ci) {
        EditBoxController.getWrapper(this.searchBox)
            .setInsertCallback(this::refreshSearchResults);
    }
}
