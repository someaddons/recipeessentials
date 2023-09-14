package com.recipeessentials.mixin;

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
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(RecipeManager.class)
/**
 * Caches recipes for faster lookups, helps with autocrafting mods
 */
public abstract class RecipeManagerMixin
{
    @Shadow
    private Map<ResourceLocation, Recipe<?>> byName;

    @Shadow
    public abstract <C extends Container, T extends Recipe<C>> List<T> getRecipesFor(final RecipeType<T> p_44057_, final C p_44058_, final Level p_44059_);

    @Unique
    private Long2ObjectOpenHashMap<List<Recipe<?>>> recipeCache = new Long2ObjectOpenHashMap<>();

    @Unique
    private Object2IntOpenHashMap<Recipe> recipeIndexes = new Object2IntOpenHashMap();

    @Inject(method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;", at = @At("HEAD"), cancellable = true)
    public <C extends Container, T extends Recipe<C>> void onGetRecipe(RecipeType<T> recipeTypeIn, C inventoryIn, Level worldIn, CallbackInfoReturnable<Optional> c)
    {
        final List<Recipe<?>> recipes = recipeCache.get(calcHash(inventoryIn, recipeTypeIn));
        if (recipes != null)
        {
            for (int i = 0, recipesSize = recipes.size(); i < recipesSize; i++)
            {
                final Recipe recipe = recipes.get(i);
                if (recipe.matches(inventoryIn, worldIn))
                {
                    c.setReturnValue(Optional.of(recipe));
                    return;
                }
            }
        }
        else
        {
            getRecipesFor(recipeTypeIn, inventoryIn, worldIn);
        }
    }

    @Inject(method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;", at = @At("RETURN"))
    private <C extends Container, T extends Recipe<C>> void onEndGetRecipe(RecipeType<T> recipeTypeIn, C inventoryIn, Level worldIn, CallbackInfoReturnable<Optional<T>> c)
    {
        Optional<T> val = c.getReturnValue();

        if (val.isPresent())
        {
            long hash = calcHash(inventoryIn, recipeTypeIn);
            if (hash != -1)
            {
                List<Recipe<?>> recipeList = recipeCache.get(hash);

                if (recipeList == null)
                {
                    recipeList = new ArrayList<>();
                    recipeCache.put(hash, recipeList);
                }

                if (!recipeList.contains(val.get()))
                {
                    recipeList.add(val.get());
                    recipeList.sort(Comparator.comparingInt(recipeIndexes::getInt));
                }
            }
        }
    }

    @Inject(method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;Lnet/minecraft/resources/ResourceLocation;)Ljava/util/Optional;", at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;", remap = false), cancellable = true)
    public <C extends Container, T extends Recipe<C>> void onGetRecipe(
      final RecipeType<T> recipeTypeIn,
      final C inventoryIn, final Level worldIn, final ResourceLocation p_220252_, final CallbackInfoReturnable<Optional<Pair<ResourceLocation, T>>> cir)
    {
        final List<Recipe<?>> recipes = recipeCache.get(calcHash(inventoryIn, recipeTypeIn));
        if (recipes != null)
        {
            for (int i = 0, recipesSize = recipes.size(); i < recipesSize; i++)
            {
                final Recipe recipe = recipes.get(i);
                if (recipe.matches(inventoryIn, worldIn))
                {
                    cir.setReturnValue(Optional.of(new Pair<>(recipe.getId(), (T) recipe)));
                    return;
                }
            }
        }
        else
        {
            getRecipesFor(recipeTypeIn, inventoryIn, worldIn);
        }
    }

    @Inject(method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;Lnet/minecraft/resources/ResourceLocation;)Ljava/util/Optional;", at = @At("TAIL"))
    private <C extends Container, T extends Recipe<C>> void onEndGetRecipe(
      final RecipeType<T> recipeTypeIn,
      final C inventoryIn, final Level p_220251_, final ResourceLocation p_220252_, final CallbackInfoReturnable<Optional<Pair<ResourceLocation, T>>> cir)
    {
        Optional<Pair<ResourceLocation, T>> val = cir.getReturnValue();

        if (val.isPresent())
        {
            long hash = calcHash(inventoryIn, recipeTypeIn);
            if (hash != -1)
            {
                List<Recipe<?>> recipeList = recipeCache.get(hash);

                if (recipeList == null)
                {
                    recipeList = new ArrayList<>();
                    recipeCache.put(hash, recipeList);
                }

                if (!recipeList.contains(val.get().getSecond()))
                {
                    recipeList.add(val.get().getSecond());
                    recipeList.sort(Comparator.comparingInt(recipeIndexes::getInt));
                }
            }
        }
    }

    @Inject(method = "getRecipesFor", at = @At(value = "HEAD"), cancellable = true)
    private <C extends Container, T extends Recipe<C>> void onGetRecipes(RecipeType<T> recipeTypeIn, C inventoryIn, Level worldIn, CallbackInfoReturnable c)
    {
        final List<Recipe<?>> recipes = recipeCache.get(calcHash(inventoryIn, recipeTypeIn));
        if (recipes != null)
        {
            List<Recipe<?>> matches = new ArrayList<>();

            for (final Recipe recipe : recipes)
            {
                if (recipe.matches(inventoryIn, worldIn))
                {
                    matches.add(recipe);
                }
            }

            if (!matches.isEmpty())
            {
                matches.sort(Comparator.comparing((recipe) -> recipe.getResultItem(worldIn.registryAccess()).getDescriptionId()));
                c.setReturnValue(matches);
                return;
            }
        }
    }

    @Inject(method = "getRecipesFor", at = @At("RETURN"))
    private <C extends Container, T extends Recipe<C>> void onEndgetRecipes(RecipeType<T> recipeTypeIn, C inventoryIn, Level worldIn, CallbackInfoReturnable<List<T>> c)
    {
        List<T> recipes = c.getReturnValue();

        if (recipes != null)
        {
            long hash = calcHash(inventoryIn, recipeTypeIn);
            if (hash != -1)
            {
                List<Recipe<?>> recipeList = recipeCache.get(hash);

                if (recipeList == null)
                {
                    recipeList = new ArrayList<>();
                    recipeCache.put(hash, recipeList);
                }

                boolean added = false;
                for (final Recipe recipe : recipes)
                {
                    if (!recipeList.contains(recipe))
                    {
                        added = true;
                        recipeList.add(recipe);
                    }
                }

                if (added)
                {
                    recipeList.sort(Comparator.comparingInt(recipeIndexes::getInt));
                }
            }
        }
    }

    /**
     * Reset cache
     *
     * @param p_44037_
     * @param p_44038_
     * @param p_44039_
     * @param ci
     */
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("RETURN"))
    private void onApply(final Map<ResourceLocation, JsonElement> p_44037_, final ResourceManager p_44038_, final ProfilerFiller p_44039_, final CallbackInfo ci)
    {
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
     *
     * @param p_44025_
     * @param ci
     */
    @Inject(method = "replaceRecipes", at = @At("RETURN"))
    private void onReplace(final Iterable<Recipe<?>> p_44025_, final CallbackInfo ci)
    {
        recipeCache = new Long2ObjectOpenHashMap<>();

        int index = 0;
        for (final Recipe recipe : byName.values())
        {
            recipeIndexes.put(recipe, index++);
        }
    }

    @Unique
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
