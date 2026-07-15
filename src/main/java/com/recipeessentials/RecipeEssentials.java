package com.recipeessentials;

import com.recipeessentials.event.EventHandler;
import com.recipeessentials.polymorphcompat.Polymorph;
import com.recipeessentials.recipecache.RecipeManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.recipeessentials.RecipeEssentials.MOD_ID;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MOD_ID)
public class RecipeEssentials
{
    public static       List<byte[]>                        data            = new ArrayList<>();
    public static final String                              MOD_ID          = "recipeessentials";
    public static final Logger                              LOGGER          = LogManager.getLogger();
    public static       Random                              rand            = new Random();
    public static       boolean                             polymorphCompat = false;

    public RecipeEssentials()
    {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "", (a, b) -> true));
        Mod.EventBusSubscriber.Bus.FORGE.bus().get().register(EventHandler.class);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        if (FMLLoader.getLoadingModList().getModFileById("polymorph") != null)
        {
            RecipeManager.compat = new Polymorph();
        }
    }

    @SubscribeEvent
    public void clientSetup(FMLClientSetupEvent event)
    {
        // Side safe client event handler
        RecipeEssentialsClient.onInitializeClient(event);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        LOGGER.info(MOD_ID + " mod initialized");
    }
}
