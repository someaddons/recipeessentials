package com.recipeessentials.mixin;

import com.recipeessentials.config.CommonConfiguration;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBookComponent.class)
public class RecipebookHidingMixin
{
    @Inject(method = "isVisible", at = @At("HEAD"), cancellable = true)
    private void recipeessentials$onInit(final CallbackInfoReturnable<Boolean> cir)
    {
        if (CommonConfiguration.config.getCommonConfig().disableRecipebook)
        {
            cir.setReturnValue(false);
        }
    }
}
