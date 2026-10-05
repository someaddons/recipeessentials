package com.recipeessentials.mixin;

import com.recipeessentials.config.CommonConfiguration;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.recipeessentials.RecipeEssentialsClient.USED_GHOST_RECIPES;

@Mixin(RecipeBookComponent.class)
public class RecipebookSortingMixin
{
    @Shadow
    @Final
    protected RecipeBookMenu menu;

    @Inject(method = "tryPlaceRecipe", at = @At("RETURN"))
    private void recipeessentials$onSelectedRecipe(final RecipeCollection collection, final RecipeDisplayId recipe,
      final boolean useMaxItems, final CallbackInfoReturnable<Boolean> cir)
    {
        if (cir.getReturnValueZ() && CommonConfiguration.config.getCommonConfig().enableBetterRecipebookSorting)
        {
            USED_GHOST_RECIPES.addTo(recipe, 1);
        }
    }

    @Redirect(method = "updateCollections", at = @At(value = "INVOKE",
      target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeBookPage;updateCollections(Ljava/util/List;ZZ)V"))
    private void recipeessentials$sortCollection(final RecipeBookPage page, List<RecipeCollection> collections,
      final boolean resetPage, final boolean isFiltering)
    {
        if (CommonConfiguration.config.getCommonConfig().enableBetterRecipebookSorting)
        {
            collections = new ArrayList<>(collections);
            collections.sort(Comparator.comparing(RecipeCollection::hasCraftable).reversed()
              .thenComparing(Comparator.comparingInt((RecipeCollection collection) -> collection.getRecipes().stream()
                .map(RecipeDisplayEntry::id).mapToInt(USED_GHOST_RECIPES::getInt).sum()).reversed()));
        }
        page.updateCollections(collections, resetPage, isFiltering);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void recipeessentials$onTick(final CallbackInfo ci)
    {
        if (menu == null)
        {
            ci.cancel();
        }
    }
}
