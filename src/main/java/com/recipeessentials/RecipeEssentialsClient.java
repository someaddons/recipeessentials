package com.recipeessentials;

import com.recipeessentials.event.ClientEventHandler;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class RecipeEssentialsClient
{
    public final static Object2IntOpenHashMap<ResourceLocation> USED_GHOST_RECIPES = new Object2IntOpenHashMap<>();

    public static void onInitializeClient(final FMLClientSetupEvent event)
    {
        Mod.EventBusSubscriber.Bus.FORGE.bus().get().register(ClientEventHandler.class);
    }
}
