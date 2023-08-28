package com.recipeessentials;

import com.cupboard.config.CupboardConfig;
import com.recipeessentials.config.CommonConfiguration;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class RecipeEssentials implements ModInitializer
{
    public static final String                              MOD_ID = "recipeessentials";
    public static final Logger                              LOGGER = LogManager.getLogger(MOD_ID);
    public static       CupboardConfig<CommonConfiguration> config = new CupboardConfig<>(MOD_ID, new CommonConfiguration());
    public static       Random                              rand   = new Random();

    @Override
    public void onInitialize()
    {
    }

    public static ResourceLocation id(String name)
    {
        return new ResourceLocation(MOD_ID, name);
    }
}
