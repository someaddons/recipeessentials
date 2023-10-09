package com.recipeessentials.mixin;

import com.recipeessentials.RecipeEssentials;
import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlaceRecipe.class)
public class RecipebookMixin {
    @Redirect(method = "recipeClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/stats/ServerRecipeBook;contains(Lnet/minecraft/world/item/crafting/Recipe;)Z"))
    private boolean recipeessentials$contains(ServerRecipeBook instance, Recipe recipe) {
        if (recipe != null && RecipeEssentials.config.getCommonConfig().recipebookShowAll && !RecipeEssentials.config.getCommonConfig().disableRecipebook) {
            return true;
        }

        return instance.contains(recipe);
    }
}
