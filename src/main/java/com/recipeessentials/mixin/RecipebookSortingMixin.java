package com.recipeessentials.mixin;

import com.recipeessentials.config.CommonConfiguration;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Comparator;
import java.util.List;

import static com.recipeessentials.RecipeEssentialsClient.USED_GHOST_RECIPES;

@Mixin(RecipeBookComponent.class)
public class RecipebookSortingMixin
{
    @Shadow
    @Final
    private RecipeBookPage recipeBookPage;

    @Shadow
    protected RecipeBookMenu menu;
    @Unique
    private   RecipeHolder   lastRecipe = null;

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void recipeessentials$onSelectedRecipe(
      final double p_100294_,
      final double p_100295_,
      final int p_100296_,
      final CallbackInfoReturnable<Boolean> cir)
    {
        if (CommonConfiguration.config.getCommonConfig().enableBetterRecipebookSorting)
        {
            RecipeHolder<?> recipe = this.recipeBookPage.getLastClickedRecipe();

            if (lastRecipe != recipe && recipe != null)
            {
                USED_GHOST_RECIPES.put(recipe.id(), USED_GHOST_RECIPES.getOrDefault(recipe.id(), 0) + 1);
                lastRecipe = recipe;
            }
        }
    }

    @Inject(method = "updateCollections", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookPage;updateCollections(Ljava/util/List;Z)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void recipeessentials$sortCollection(final boolean p_100383_, final CallbackInfo ci, List<RecipeCollection> list, List<RecipeCollection> resultRecipes)
    {
        if (CommonConfiguration.config.getCommonConfig().enableBetterRecipebookSorting)
        {
            resultRecipes.sort(Comparator.<RecipeCollection>comparingInt(r -> {
                int sum = 0;
                for (final RecipeHolder<?> recipe : r.getRecipes())
                {
                    sum += USED_GHOST_RECIPES.getOrDefault(recipe.id(), 0);
                }
                return (r.hasCraftable() ? sum + 1000 : sum);
            }).reversed());
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void recipeessentials$onTick(final CallbackInfo ci)
    {
        // Prevent crash of unknown cause, might be from a different mod
        if (menu == null)
        {
            ci.cancel();
        }
    }
}
