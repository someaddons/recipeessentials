package com.recipeessentials;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

import static com.recipeessentials.RecipeEssentials.MOD_ID;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MOD_ID)
public class RecipeEssentials
{
    public static final String  MOD_ID          = "recipeessentials";
    public static final Logger  LOGGER          = LogManager.getLogger();
    public static       Random  rand            = new Random();

    public RecipeEssentials(IEventBus modEventBus, ModContainer modContainer)
    {
    }
}
