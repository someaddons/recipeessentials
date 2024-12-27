package com.recipeessentials;

import com.cupboard.config.CupboardConfig;
import com.recipeessentials.config.CommonConfiguration;
import com.recipeessentials.polymorphcompat.Polymorph;
import com.recipeessentials.recipecache.RecipeManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

import static com.recipeessentials.RecipeEssentials.MOD_ID;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MOD_ID)
public class RecipeEssentials
{
    public static final String                              MOD_ID          = "recipeessentials";
    public static final Logger                              LOGGER          = LogManager.getLogger();
    public static       CupboardConfig<CommonConfiguration> config          = new CupboardConfig<>(MOD_ID, new CommonConfiguration());
    public static       Random                              rand            = new Random();
    public static       boolean                             polymorphCompat = false;

    public RecipeEssentials(IEventBus modEventBus, ModContainer modContainer)
    {
        if (FMLLoader.getLoadingModList().getModFileById("polymorph") != null)
        {
            RecipeManager.compat = new Polymorph();
        }
    }
}
