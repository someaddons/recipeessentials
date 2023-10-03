package com.recipeessentials.mixin;

import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(RecipeButton.class)
public class RecipeButtonMixin
{
    @Shadow
    private RecipeCollection collection;

    @Redirect(method = "getOrderedRecipes", at = @At(value = "INVOKE", target = "Lnet/minecraft/stats/RecipeBook;isFiltering(Lnet/minecraft/world/inventory/RecipeBookMenu;)Z"))
    private boolean isFilter(final RecipeBook instance, final RecipeBookMenu<?> p_12690_)
    {
        List<Recipe<?>> list = this.collection.getDisplayRecipes(true);
        return !list.isEmpty();
    }
}
