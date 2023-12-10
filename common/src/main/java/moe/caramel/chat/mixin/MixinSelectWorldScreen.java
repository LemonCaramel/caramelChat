package moe.caramel.chat.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * SelectWorldScreen Mixin (Fix MC-265273)
 * I don't have a good idea right now...
 */
@Mixin(SelectWorldScreen.class)
public final class MixinSelectWorldScreen {

    @Inject(
        method = "init",
        at = @At(
            value = "INVOKE", shift = At.Shift.AFTER,
            target = "Lnet/minecraft/client/gui/screens/worldselection/WorldSelectionList;<init>(Lnet/minecraft/client/gui/screens/worldselection/SelectWorldScreen;Lnet/minecraft/client/Minecraft;IIIILjava/lang/String;Lnet/minecraft/client/gui/screens/worldselection/WorldSelectionList;)V"
        ), cancellable = true
    )
    private void initWorldSelectionList(final CallbackInfo ci) {
        if (Minecraft.getInstance().screen instanceof CreateWorldScreen) {
            ci.cancel();
        }
    }
}
