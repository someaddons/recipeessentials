package com.recipeessentials.recipecache;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Optional;

public interface IRecipeCompat
{
    <C extends RecipeInput, T extends Recipe<C>> Optional<RecipeHolder> getRecipe(
        RecipeType<T> recipeTypeIn, C inventoryIn, Level worldIn, CachedRecipeList recipes,
        final RecipeManager recipeManager);
}
