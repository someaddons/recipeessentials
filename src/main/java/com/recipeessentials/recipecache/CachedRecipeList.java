package com.recipeessentials.recipecache;

import com.recipeessentials.RecipeEssentials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;

public class CachedRecipeList
{
    public final List<RecipeHolder> recipes;
    public final List<ItemStack>    originStacks;
    public final RecipeType      originType;
    public       int             useCount = 0;
    public       boolean         reported = false;

    public CachedRecipeList(final List<RecipeHolder> recipes, final List<ItemStack> originStacks, final RecipeType originType)
    {
        this.recipes = recipes;
        this.originStacks = originStacks;
        this.originType = originType;
    }

    public CachedRecipeList(final RecipeType recipeTypeIn, final RecipeInput inventoryIn)
    {
        originType = recipeTypeIn;
        recipes = new ArrayList<>();
        originStacks = new ArrayList<>();

        if (inventoryIn != null)
        {
            for (int i = 0; i < inventoryIn.size(); i++)
            {
                ItemStack stack = inventoryIn.getItem(i);

                if (stack != null && !stack.isEmpty())
                {
                    originStacks.add(stack.copy());
                }
            }
        }
    }

    public <T extends Recipe<C>, C extends RecipeInput> void report(final RecipeType recipeTypeIn, final RecipeInput inventoryIn, final List<RecipeHolder<T>> recipes)
    {
        if (!reported && RecipeEssentials.config.getCommonConfig().logCachingErrors)
        {
            reported = true;
            CachedRecipeList temp = new CachedRecipeList(recipeTypeIn, inventoryIn);
            RecipeEssentials.LOGGER.warn("Unable to optimize some recipes, printing mismatch");
            RecipeEssentials.LOGGER.warn("Mismatching cached recipe lists for input: type:" + recipeTypeIn + " stacks:" + temp.originStacks);
            RecipeEssentials.LOGGER.warn("Result recipe list:" + recipes.stream().map(RecipeHolder::id).toList());
            RecipeEssentials.LOGGER.warn("Cached values: type:" + originType + " stacks:" + originStacks);
            RecipeEssentials.LOGGER.warn("Cached recipe list:" + this.recipes.stream().map(RecipeHolder::id).toList());
        }

        // Reset successful uses on mismatch
        useCount = 0;
    }
}
