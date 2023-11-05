package com.recipeessentials.mixin;

import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.Recipe;
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
    protected RecipeBookMenu<?> menu;
    @Unique
    private   Recipe            lastRecipe = null;

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void recipeessentials$onSelectedRecipe(
      final double p_100294_,
      final double p_100295_,
      final int p_100296_,
      final CallbackInfoReturnable<Boolean> cir)
    {
        Recipe<?> recipe = this.recipeBookPage.getLastClickedRecipe();

        if (lastRecipe != recipe && recipe != null)
        {
            USED_GHOST_RECIPES.put(recipe.getId(), USED_GHOST_RECIPES.getOrDefault(recipe.getId(), 0) + 1);
            lastRecipe = recipe;
        }
    }

    @Inject(method = "updateCollections", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookPage;updateCollections(Ljava/util/List;Z)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void recipeessentials$sortCollection(final boolean p_100383_, final CallbackInfo ci, List<RecipeCollection> list, List<RecipeCollection> resultRecipes)
    {
        resultRecipes.sort(Comparator.<RecipeCollection>comparingInt(r -> {
            int sum = 0;
            for (final Recipe<?> recipe : r.getRecipes())
            {
                sum += USED_GHOST_RECIPES.getOrDefault(recipe.getId(), 0);
            }
            return (r.hasCraftable() ? sum + 1000 : sum);
        }).reversed());
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
