package com.recipeessentials.recipecache;

import com.recipeessentials.RecipeEssentials;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Caching recipe manager
 */
public class RecipeManager extends net.minecraft.world.item.crafting.RecipeManager
{
    public RecipeManager(HolderLookup.Provider registryAccess)
    {
        super(registryAccess);
    }

    /**
     * Map of hash key to recipe list matching it
     */
    private final Long2ObjectOpenHashMap<CachedRecipeList> recipeCache = new Long2ObjectOpenHashMap<>();

    /**
     * Map of recipe to index position for sorting
     */
    private final Object2IntOpenHashMap<RecipeHolder<?>> recipeIndexes = new Object2IntOpenHashMap<>();

    @Override
    public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> getRecipeFor(RecipeType<T> recipeTypeIn, I inventoryIn, Level worldIn)
    {
        if (inventoryIn.isEmpty())
        {
            return Optional.empty();
        }
        long hash = calcHash(inventoryIn, recipeTypeIn);
        final CachedRecipeList recipes = recipeCache.get(hash);
        if (recipes != null && recipes.matches(recipeTypeIn, inventoryIn) && recipes.used() > 10 && RecipeEssentials.rand.nextInt(recipes.used() * 30) != 0)
        {
            recipes.increaseUsed();

            for (int i = 0, recipesSize = recipes.recipes.size(); i < recipesSize; i++)
            {
                final RecipeHolder recipe = recipes.recipes.get(i);
                if (recipe.value().matches(inventoryIn, worldIn))
                {
                    return Optional.of(recipe);
                }
            }
        }
        else
        {
            getRecipesFor(recipeTypeIn, inventoryIn, worldIn);
        }

        final Optional<RecipeHolder<T>> result = super.getRecipeFor(recipeTypeIn, inventoryIn, worldIn);

        if (result.isPresent())
        {
            if (hash != -1)
            {
                CachedRecipeList recipeList = recipeCache.get(hash);

                if (recipeList == null || !recipeList.matches(recipeTypeIn, inventoryIn))
                {
                    recipeList = new CachedRecipeList(recipeTypeIn, inventoryIn);
                    recipeCache.put(hash, recipeList);
                }

                recipeList.increaseUsed();
                if (!recipeList.recipes.contains(result.get()))
                {
                    recipeList.recipes.add(result.get());
                    recipeList.recipes.sort(Comparator.comparingInt(recipeIndexes::getInt));
                }
            }
        }

        return result;
    }

    public <C extends RecipeInput, T extends Recipe<C>> List<RecipeHolder<T>> getRecipesFor(RecipeType<T> recipeTypeIn, C inventoryIn, Level worldIn)
    {
        if (inventoryIn.isEmpty())
        {
            return List.of();
        }
        final CachedRecipeList recipes = recipeCache.get(calcHash(inventoryIn, recipeTypeIn));
        if (recipes != null && recipes.matches(recipeTypeIn, inventoryIn) && recipes.used() > 10 && RecipeEssentials.rand.nextInt(recipes.used() * 30) != 0)
        {
            recipes.increaseUsed();
            List<RecipeHolder<T>> matches = new ArrayList<>();

            for (final RecipeHolder<T> recipe : recipes.recipes)
            {
                if (recipe.value().matches(inventoryIn, worldIn))
                {
                    matches.add(recipe);
                }
            }

            if (!matches.isEmpty())
            {
                matches.sort(Comparator.comparingInt(recipeIndexes::getInt));
                return matches;
            }
        }

        final List<RecipeHolder<T>> result = recipeMap().getRecipesFor(recipeTypeIn, inventoryIn, worldIn).toList();

        if (result != null && !result.isEmpty())
        {
            long hash = calcHash(inventoryIn, recipeTypeIn);
            if (hash != -1)
            {
                CachedRecipeList recipeList = recipeCache.get(hash);

                if (recipeList == null || !recipeList.matches(recipeTypeIn, inventoryIn))
                {
                    recipeList = new CachedRecipeList(recipeTypeIn, inventoryIn);
                    recipeCache.put(hash, recipeList);
                }
                else
                {
                    List<RecipeHolder> matches = new ArrayList<>();

                    for (final RecipeHolder recipe : recipeList.recipes)
                    {
                        if (recipe.value().matches(inventoryIn, worldIn))
                        {
                            matches.add(recipe);
                        }
                    }

                    matches.sort(Comparator.comparingInt(recipeIndexes::getInt));
                    if (!result.equals(matches))
                    {
                        recipeList.report(recipeTypeIn, inventoryIn, result);
                    }
                }

                recipeList.increaseUsed();

                boolean added = false;
                for (final RecipeHolder recipe : result)
                {
                    if (!recipeList.recipes.contains(recipe))
                    {
                        added = true;
                        recipeList.recipes.add(recipe);
                    }
                }

                if (added)
                {
                    recipeList.recipes.sort(Comparator.comparingInt(recipeIndexes::getInt));
                }
            }
        }

        return result;
    }

    /**
     * Reset cache
     */
    @Override
    public void finalizeRecipeLoading(FeatureFlagSet enabledFlags)
    {
        super.finalizeRecipeLoading(enabledFlags);
        recipeCache.clear();
        recipeIndexes.clear();

        for (final RecipeType<?> type : recipeMap().values().stream().map(recipe -> recipe.value().getType()).distinct().toList())
        {
            indexRecipes((RecipeType) type);
        }
    }

    private <I extends RecipeInput, T extends Recipe<I>> void indexRecipes(final RecipeType<T> type)
    {
        for (final RecipeHolder<T> recipe : recipeMap().byType(type))
        {
            recipeIndexes.put(recipe, recipeIndexes.size());
        }
    }

    /**
     * Caculates a hash for the input params
     *
     * @param inventory
     * @param type
     * @return
     */
    private long calcHash(RecipeInput inventory, RecipeType type)
    {
        if (inventory == null)
        {
            return type.hashCode();
        }

        long hash = 31L * type.hashCode() + inventory.getClass().hashCode();
        hash = 31 * hash + inventory.size();
        if (inventory instanceof net.minecraft.world.item.crafting.CraftingInput crafting)
        {
            hash = 31 * hash + crafting.width();
            hash = 31 * hash + crafting.height();
        }
        int size = inventory.size();
        // Keep custom input metadata when the input provides a hash
        if (inventory.hashCode() != System.identityHashCode(inventory))
        {
            hash = 31 * hash + inventory.hashCode();
        }

        for (int i = 0; i < size; i++)
        {
            ItemStack stack = inventory.getItem(i);

            if (stack != null && !stack.isEmpty())
            {
                hash = 31 * hash + i;
                hash = 31 * hash + ItemStack.hashItemAndComponents(stack);
                hash = 31 * hash + stack.getCount();
            }
        }

        return hash;
    }

    private RecipeMap recipeMap()
    {
        return recipes;
    }
}
