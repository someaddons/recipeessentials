package com.recipeessentials.mixin;

import com.google.gson.internal.reflect.ReflectionHelper;
import com.recipeessentials.RecipeEssentials;
import com.recipeessentials.config.CommonConfiguration;
import net.minecraft.commands.Commands;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.resource.VanillaServerListeners;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin
{
    @Shadow
    @Final
    @Mutable
    private RecipeManager recipes;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void createManager(
        final LayeredRegistryAccess fullLayers,
        final HolderLookup.Provider registryLookup,
        final FeatureFlagSet enabledFeatures,
        final Commands.CommandSelection commandSelection,
        final List postponedTags,
        final PermissionSet functionCompilationPermissions,
        final List newComponents,
        final CallbackInfo ci)
    {
        if (CommonConfiguration.config.getCommonConfig().cacheRecipes)
        {
            recipes = new com.recipeessentials.recipecache.RecipeManager(registryLookup);


            try
            {
                Field field = VanillaServerListeners.class.getDeclaredField("KNOWN_CLASSES");
                field.setAccessible(true);
                Map<Class<?>, Identifier> value = (Map<Class<?>, Identifier>) field.get(VanillaServerListeners.class);
                value.put(recipes.getClass(), VanillaServerListeners.RECIPES);
            }
            catch (Exception e)
            {
                RecipeEssentials.LOGGER.warn("");
            }
        }
    }
}
