package com.recipeessentials;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class RecipeEssentials implements ModInitializer
{
    public static final String                              MOD_ID = "recipeessentials";
    public static final Logger                              LOGGER = LogManager.getLogger(MOD_ID);
    public static       Random                              rand   = new Random();
    public static       boolean                             polymorphCompat = false;

    @Override
    public void onInitialize()
    {
        if (FabricLoader.getInstance().isModLoaded("polymorph"))
        {
            polymorphCompat = true;
        }
    }

    public static ResourceLocation id(String name)
    {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }
}
