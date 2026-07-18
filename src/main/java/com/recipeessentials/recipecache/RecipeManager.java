package com.recipeessentials.recipecache;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.recipeessentials.RecipeEssentials;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.*;

/**
 * Caching recipe manager
 */
public class RecipeManager extends net.minecraft.world.item.crafting.RecipeManager
{
    public RecipeManager(net.minecraftforge.common.crafting.conditions.ICondition.IContext context)
    {
        super(context);
    }

    public RecipeManager()
    {
        super(net.minecraftforge.common.crafting.conditions.ICondition.IContext.EMPTY);
    }

    public static IRecipeCompat compat = new IRecipeCompat()
    {
        @Override
        public <C extends Container, T extends Recipe<C>> Optional getRecipe(
          final RecipeType<T> recipeTypeIn,
          final C inventoryIn,
          final Level worldIn,
          final CachedRecipeList recipes)
        {
            return Optional.empty();
        }
    };

    /**
     * Map of hash key to recipe list matching it
     */
    private Long2ObjectOpenHashMap<CachedRecipeList> recipeCache = new Long2ObjectOpenHashMap<>();

    /**
     * Map of recipe to index position for sorting
     */
    private Object2IntOpenHashMap<Recipe> recipeIndexes = new Object2IntOpenHashMap();

    @Override
    public <C extends Container, T extends Recipe<C>> Optional getRecipeFor(RecipeType<T> recipeTypeIn, C inventoryIn, Level worldIn)
    {
        long hash = calcHash(inventoryIn, recipeTypeIn);
        final CachedRecipeList recipes = recipeCache.get(hash);
        if (recipes != null && recipes.used() > 10 && RecipeEssentials.rand.nextInt(recipes.used() * 30) != 0)
        {
            recipes.increaseUsed();

            final Optional compatRecipe = compat.getRecipe(recipeTypeIn, inventoryIn, worldIn, recipes);
            if (compatRecipe.isPresent())
            {
                return compatRecipe;
            }

            for (int i = 0, recipesSize = recipes.recipes.size(); i < recipesSize; i++)
            {
                final Recipe recipe = recipes.recipes.get(i);
                if (recipe.matches(inventoryIn, worldIn))
                {
                    return Optional.of(recipe);
                }
            }
        }
        else
        {
            getRecipesFor(recipeTypeIn, inventoryIn, worldIn);
        }

        final Optional<T> result = super.getRecipeFor(recipeTypeIn, inventoryIn, worldIn);
        return result;
    }

    @Override
    public <C extends Container, T extends Recipe<C>> Optional<Pair<ResourceLocation, T>> getRecipeFor(
      final RecipeType<T> recipeTypeIn,
      final C inventoryIn,
      final Level worldIn,
      final ResourceLocation resourceLocation)
    {
        long hash = calcHash(inventoryIn, recipeTypeIn);
        final CachedRecipeList recipes = recipeCache.get(hash);
        if (recipes != null && recipes.used() > 10 && RecipeEssentials.rand.nextInt(recipes.used() * 30) != 0)
        {
            recipes.increaseUsed();

            final Optional<Recipe> compatRecipe = compat.getRecipe(recipeTypeIn, inventoryIn, worldIn, recipes);
            if (compatRecipe.isPresent())
            {
                return Optional.of(new Pair<>(compatRecipe.get().getId(), (T) compatRecipe.get()));
            }

            for (int i = 0, recipesSize = recipes.recipes.size(); i < recipesSize; i++)
            {
                final Recipe recipe = recipes.recipes.get(i);
                if (recipe.matches(inventoryIn, worldIn))
                {
                    return Optional.of(new Pair<>(recipe.getId(), (T) recipe));
                }
            }
        }
        else
        {
            getRecipesFor(recipeTypeIn, inventoryIn, worldIn);
        }

        final Optional<Pair<ResourceLocation, T>> result = super.getRecipeFor(recipeTypeIn, inventoryIn, worldIn, resourceLocation);

        if (result.isPresent())
        {
            if (hash != -1)
            {
                CachedRecipeList recipeList = recipeCache.get(hash);

                if (recipeList == null)
                {
                    recipeList = new CachedRecipeList(recipeTypeIn, inventoryIn);
                    recipeCache.put(hash, recipeList);
                }

                recipeList.increaseUsed();
                ;
                if (!recipeList.recipes.contains(result.get().getSecond()))
                {
                    recipeList.recipes.add(result.get().getSecond());
                    recipeList.recipes.sort(Comparator.comparingInt(recipeIndexes::getInt));
                }
            }
        }

        return result;
    }

    @Override
    public <C extends Container, T extends Recipe<C>> List<T> getRecipesFor(RecipeType<T> recipeTypeIn, C inventoryIn, Level worldIn)
    {
        final CachedRecipeList recipes = recipeCache.get(calcHash(inventoryIn, recipeTypeIn));
        if (recipes != null && recipes.used() > 10 && RecipeEssentials.rand.nextInt(recipes.used() * 30) != 0)
        {
            recipes.increaseUsed();
            List<T> matches = new ArrayList<>();

            for (final Recipe<C> recipe : recipes.recipes)
            {
                if (recipe.matches(inventoryIn, worldIn))
                {
                    matches.add((T) recipe);
                }
            }

            if (!matches.isEmpty())
            {
                matches.sort(Comparator.comparing((recipe) -> recipe.getResultItem(worldIn.registryAccess()).getDescriptionId()));
                return matches;
            }
        }

        final List<T> result = super.getRecipesFor(recipeTypeIn, inventoryIn, worldIn);

        if (result != null && !result.isEmpty())
        {
            long hash = calcHash(inventoryIn, recipeTypeIn);
            if (hash != -1)
            {
                CachedRecipeList recipeList = recipeCache.get(hash);

                if (recipeList == null)
                {
                    recipeList = new CachedRecipeList(recipeTypeIn, inventoryIn);
                    recipeCache.put(hash, recipeList);
                }
                else
                {
                    List<Recipe<?>> matches = new ArrayList<>();

                    for (final Recipe recipe : recipeList.recipes)
                    {
                        if (recipe.matches(inventoryIn, worldIn))
                        {
                            matches.add(recipe);
                        }
                    }

                    matches.sort(Comparator.comparing((recipe) -> recipe.getResultItem(worldIn.registryAccess()).getDescriptionId()));
                    result.sort(Comparator.comparing((recipe) -> recipe.getResultItem(worldIn.registryAccess()).getDescriptionId()));
                    if (!result.equals(matches))
                    {
                        recipeList.report(recipeTypeIn, inventoryIn, result);
                    }
                }

                recipeList.increaseUsed();

                boolean added = false;
                for (final Recipe recipe : result)
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
    public void apply(final Map<ResourceLocation, JsonElement> dataMap, final ResourceManager resourceManager, final ProfilerFiller profilerFiller)
    {
        super.apply(dataMap, resourceManager, profilerFiller);
        recipeCache = new Long2ObjectOpenHashMap<>();

        int index = 0;
        for (final Map.Entry<ResourceLocation, Recipe<?>> recipe : byName.entrySet())
        {
            if (!recipe.getValue().getId().equals(recipe.getKey()))
            {
                RecipeEssentials.LOGGER.warn("Recipe without matching ID:" + recipe.getValue().getId());
            }

            recipeIndexes.put(recipe.getValue(), index++);
        }
    }

    /**
     * Reset cache
     */
    @Override
    public void replaceRecipes(final Iterable<Recipe<?>> recipeIterator)
    {
        super.replaceRecipes(recipeIterator);
        recipeCache = new Long2ObjectOpenHashMap<>();

        int index = 0;
        for (final Recipe recipe : byName.values())
        {
            recipeIndexes.put(recipe, index++);
        }
    }

    /**
     * Caculates a hash for the input params
     *
     * @param inventory
     * @param type
     * @return
     */
    private long calcHash(Container inventory, RecipeType type)
    {
        if (inventory == null)
        {
            return type.hashCode();
        }

        long hash = type.hashCode();
        int size = inventory.getContainerSize();

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
                hash = 31 * hash + stack.getItem().hashCode();
                /*
                Less precise hashcode for now, to avoid filling too many entries if there is random changes
                hash = 31 * hash + stack.getDamageValue();
                if (stack.hasTag())
                {
                    hash = 31 * hash + stack.getTag().hashCode();
                }
                 */
            }
        }

        return hash;
    }
}
