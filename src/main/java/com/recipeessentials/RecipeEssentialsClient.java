package com.recipeessentials;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;

@Environment(EnvType.CLIENT)
public class RecipeEssentialsClient implements ClientModInitializer
{
    public final static Object2IntOpenHashMap<RecipeDisplayId> USED_GHOST_RECIPES = new Object2IntOpenHashMap<>();

    @Override
    public void onInitializeClient()
    {

    }
}
