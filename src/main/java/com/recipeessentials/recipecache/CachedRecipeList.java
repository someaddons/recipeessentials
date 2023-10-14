package com.recipeessentials.recipecache;

import com.recipeessentials.RecipeEssentials;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;

public class CachedRecipeList
{
    public final List<Recipe>    recipes;
    public final List<ItemStack> originStacks;
    public final RecipeType      originType;
    public       int             useCount = 0;
    public       boolean         reported = false;

    public CachedRecipeList(final List<Recipe> recipes, final List<ItemStack> originStacks, final RecipeType originType)
    {
        this.recipes = recipes;
        this.originStacks = originStacks;
        this.originType = originType;
    }

    public <T extends Recipe<C>, C extends Container> CachedRecipeList(final RecipeType<T> recipeTypeIn, final C inventoryIn)
    {
        originType = recipeTypeIn;
        recipes = new ArrayList<>();
        originStacks = new ArrayList<>();

        if (inventoryIn != null)
        {
            for (int i = 0; i < inventoryIn.getContainerSize(); i++)
            {
                ItemStack stack = inventoryIn.getItem(i);

                if (stack != null && !stack.isEmpty())
                {
                    originStacks.add(stack.copy());
                }
            }
        }
    }

    public <T extends Recipe<C>, C extends Container> void report(final RecipeType<T> recipeTypeIn, final C inventoryIn, final List<T> recipes)
    {
        if (!reported && RecipeEssentials.config.getCommonConfig().logCachingErrors)
        {
            reported = true;
            CachedRecipeList temp = new CachedRecipeList(recipeTypeIn, inventoryIn);
            RecipeEssentials.LOGGER.warn("Unable to optimize some recipes, printing mismatch");
            RecipeEssentials.LOGGER.warn("Mismatching cached recipe lists for input: type:" + recipeTypeIn + " stacks:" + temp.originStacks);
            RecipeEssentials.LOGGER.warn("Result recipe list:" + recipes.stream().map(Recipe::getId).toList());
            RecipeEssentials.LOGGER.warn("Cached values: type:" + originType + " stacks:" + originStacks);
            RecipeEssentials.LOGGER.warn("Cached recipe list:" + this.recipes.stream().map(Recipe::getId).toList());
        }

        // Reset successful uses on mismatch
        useCount = 0;
    }
}
