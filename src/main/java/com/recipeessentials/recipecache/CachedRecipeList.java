package com.recipeessentials.recipecache;

import com.recipeessentials.RecipeEssentials;
import com.recipeessentials.config.CommonConfiguration;
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
    private final Class<?> inputClass;
    private final int width;
    private final int height;
    private int useCount = 0;
    public       boolean         reported = false;

    public CachedRecipeList(final RecipeType recipeTypeIn, final RecipeInput inventoryIn)
    {
        originType = recipeTypeIn;
        inputClass = inventoryIn.getClass();
        if (inventoryIn instanceof net.minecraft.world.item.crafting.CraftingInput crafting)
        {
            width = crafting.width();
            height = crafting.height();
        }
        else
        {
            width = 0;
            height = 0;
        }
        recipes = new ArrayList<>();
        originStacks = new ArrayList<>();

        if (inventoryIn != null)
        {
            for (int i = 0; i < inventoryIn.size(); i++)
            {
                ItemStack stack = inventoryIn.getItem(i);

                originStacks.add(stack.copy());
            }
        }
    }

    public <T extends Recipe<C>, C extends RecipeInput> void report(final RecipeType recipeTypeIn, final RecipeInput inventoryIn, final List<RecipeHolder<T>> recipes)
    {
        if (!reported && CommonConfiguration.config.getCommonConfig().logCachingErrors)
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

    public boolean matches(final RecipeType<?> type, final RecipeInput input)
    {
        if (type != originType || input.getClass() != inputClass || input.size() != originStacks.size())
        {
            return false;
        }
        if (input instanceof net.minecraft.world.item.crafting.CraftingInput crafting
          && (crafting.width() != width || crafting.height() != height))
        {
            return false;
        }
        for (int i = 0; i < input.size(); i++)
        {
            if (!ItemStack.matches(input.getItem(i), originStacks.get(i)))
            {
                return false;
            }
        }
        return true;
    }

    public void increaseUsed()
    {
        if (useCount < 100)
        {
            useCount++;
        }
    }

    public int used()
    {
        return useCount;
    }
}
