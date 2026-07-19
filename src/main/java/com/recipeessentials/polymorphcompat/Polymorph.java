package com.recipeessentials.polymorphcompat;

import com.illusivesoulworks.polymorph.api.PolymorphApi;
import com.illusivesoulworks.polymorph.api.common.capability.IBlockEntityRecipeData;
import com.recipeessentials.recipecache.CachedRecipeList;
import com.recipeessentials.recipecache.IRecipeCompat;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.Optional;

public class Polymorph implements IRecipeCompat
{
    @Override
    public <C extends Container, T extends Recipe<C>> Optional getRecipe(final RecipeType<T> recipeTypeIn, final C inventoryIn, final Level worldIn, final CachedRecipeList recipes)
    {
        if (inventoryIn instanceof BlockEntity beInventory)
        {
            Optional<? extends IBlockEntityRecipeData> maybeData = PolymorphApi.common().getRecipeData(beInventory);
            if (maybeData.isPresent())
            {
                return maybeData.get().getRecipe((RecipeType) recipeTypeIn, inventoryIn, worldIn, new ArrayList<>());
            }
        }
        return Optional.empty();
    }
}
