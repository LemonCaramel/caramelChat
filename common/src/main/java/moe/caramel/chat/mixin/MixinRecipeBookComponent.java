package moe.caramel.chat.mixin;

import moe.caramel.chat.controller.EditBoxController;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * RecipeBook Component Mixin
 */
@Mixin(RecipeBookComponent.class)
public abstract class MixinRecipeBookComponent {

    @Shadow private EditBox searchBox;
    @Shadow protected abstract void checkSearchStringUpdate();

    @Inject(method = "initVisuals", at = @At("TAIL"))
    private void initVisuals(final CallbackInfo ci) {
        EditBoxController.getWrapper(this.searchBox)
            .setInsertCallback(this::checkSearchStringUpdate);
    }
}
