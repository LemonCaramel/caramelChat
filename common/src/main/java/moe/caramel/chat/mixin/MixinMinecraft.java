package moe.caramel.chat.mixin;

import moe.caramel.chat.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Minecraft Client Mixin
 */
@Mixin(Minecraft.class)
public final class MixinMinecraft {

    @Inject(method = "setScreen", at = @At("HEAD"))
    private void setScreen(final Screen screen, final CallbackInfo ci) {
        Main.setScreen(screen);
    }
}
