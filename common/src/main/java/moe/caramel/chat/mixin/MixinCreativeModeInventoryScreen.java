package moe.caramel.chat.mixin;

import moe.caramel.chat.controller.EditBoxController;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * CreativeModeInventory Screen Mixin
 */
@Mixin(CreativeModeInventoryScreen.class)
public abstract class MixinCreativeModeInventoryScreen {

    @Shadow private EditBox searchBox;
    @Shadow public abstract void refreshSearchResults();
    @Shadow protected abstract boolean isCreativeSlot(@Nullable Slot slot);

    @Inject(method = "init", at = @At("TAIL"))
    private void init(final CallbackInfo ci) {
        EditBoxController.getWrapper(this.searchBox)
            .setInsertCallback(this::refreshSearchResults);
    }

    @Redirect(
        method = "slotClicked",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen;isCreativeSlot(Lnet/minecraft/world/inventory/Slot;)Z"
        )
    )
    private boolean slotClicked(final CreativeModeInventoryScreen screen, final Slot slot) {
        final boolean imeFocused = EditBoxController.getWrapper(this.searchBox).getIme().isFocused();
        return isCreativeSlot(slot) && !(imeFocused && this.searchBox.isFocused()); // OMG
    }
}
