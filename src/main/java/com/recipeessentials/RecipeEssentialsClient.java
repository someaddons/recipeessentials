package com.recipeessentials;

import com.recipeessentials.event.ClientEventHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class RecipeEssentialsClient
{
    public static void onInitializeClient(final FMLClientSetupEvent event)
    {
        Mod.EventBusSubscriber.Bus.FORGE.bus().get().register(ClientEventHandler.class);
    }
}
