package com.recipeessentials.polymorphcompat;

import com.illusivesoulworks.polymorph.api.PolymorphApi;
import com.illusivesoulworks.polymorph.api.common.base.IRecipeContext;
import com.illusivesoulworks.polymorph.api.common.capability.IBlockEntityRecipeData;
import com.recipeessentials.recipecache.CachedRecipeList;
import com.recipeessentials.recipecache.IRecipeCompat;
import com.recipeessentials.recipecache.RecipeManager;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.Optional;

public class Polymorph implements IRecipeCompat
{
    @Override
    public <C extends RecipeInput, T extends Recipe<C>> Optional<RecipeHolder> getRecipe(RecipeType<T> recipeTypeIn, C inventoryIn, Level worldIn, CachedRecipeList recipes,
      final RecipeManager recipeManager)
    {
        if (recipeManager instanceof IRecipeContext polyManager)
        {
            Object context = polyManager.polymorph$getContext();
            if (context instanceof BlockEntity beInventory)
            {
                IBlockEntityRecipeData maybeData = PolymorphApi.getInstance().getBlockEntityRecipeData(beInventory);
                if (maybeData != null && !maybeData.isEmpty())
                {
                    return Optional.ofNullable(maybeData.getRecipe((RecipeType) recipeTypeIn, inventoryIn, worldIn, new ArrayList<>()));
                }
            }
        }
        return Optional.empty();
    }
}
