package com.recipeessentials.mixin;

import com.recipeessentials.config.CommonConfiguration;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.SlotSelectTime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RecipeButton.class)
public class RecipeButtonMixin
{
    @Redirect(method = {"getCurrentRecipe", "getDisplayStack"}, at = @At(value = "INVOKE",
      target = "Lnet/minecraft/client/gui/screens/recipebook/SlotSelectTime;currentIndex()I"))
    private int recipeessentials$selectFirst(final SlotSelectTime time)
    {
        return CommonConfiguration.config.getCommonConfig().enableBetterRecipebookSorting ? 0 : time.currentIndex();
    }
}
