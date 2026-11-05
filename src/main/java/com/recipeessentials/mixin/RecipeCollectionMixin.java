package com.recipeessentials.mixin;

import com.recipeessentials.config.CommonConfiguration;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;
import java.util.List;

import static com.recipeessentials.RecipeEssentialsClient.USED_GHOST_RECIPES;

@Mixin(RecipeCollection.class)
public abstract class RecipeCollectionMixin
{
    @Shadow
    public abstract boolean isCraftable(RecipeDisplayId recipe);

    @Inject(method = "getSelectedRecipes", at = @At("RETURN"))
    private void recipeessentials$sortRecipes(
        final RecipeCollection.CraftableStatus selector,
        final CallbackInfoReturnable<List<RecipeDisplayEntry>> cir)
    {
        if (CommonConfiguration.config.getCommonConfig().enableBetterRecipebookSorting)
        {
            cir.getReturnValue().sort(Comparator.comparing((RecipeDisplayEntry entry) -> isCraftable(entry.id())).reversed()
                .thenComparing(Comparator.comparingInt((RecipeDisplayEntry entry) -> USED_GHOST_RECIPES.getInt(entry.id())).reversed()));
        }
    }
}
