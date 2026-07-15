package com.recipeessentials.mixin;

import com.recipeessentials.config.CommonConfiguration;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeCollection.class)
public class RecipeCollectionMixin
{
    @Inject(method = "hasKnownRecipes", at = @At("HEAD"), cancellable = true)
    private void recipeessentials$hasKnown(final CallbackInfoReturnable<Boolean> cir)
    {
        if (CommonConfiguration.config.getCommonConfig().recipebookShowAll)
        {
            cir.setReturnValue(true);
        }
    }

    @Redirect(method = "updateKnownRecipes", at = @At(value = "INVOKE", target = "Lnet/minecraft/stats/RecipeBook;contains(Lnet/minecraft/world/item/crafting/RecipeHolder;)Z"))
    private boolean recipeessentials$hasKnown(final RecipeBook instance, final RecipeHolder<?> recipe)
    {
        if (CommonConfiguration.config.getCommonConfig().recipebookShowAll && !recipe.value().isSpecial())
        {
            return true;
        }

        return instance.contains(recipe);
    }

    @Redirect(method = "canCraft", at = @At(value = "INVOKE", target = "Lnet/minecraft/stats/RecipeBook;contains(Lnet/minecraft/world/item/crafting/RecipeHolder;)Z"))
    private boolean recipeessentials$contains(final RecipeBook instance, final RecipeHolder<?> recipe)
    {
        if (CommonConfiguration.config.getCommonConfig().recipebookShowAll)
        {
            return true;
        }

        return instance.contains(recipe);
    }
}
