package com.recipeessentials.mixin;

import com.recipeessentials.config.CommonConfiguration;
import net.minecraft.commands.Commands;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin
{
    @Shadow
    @Final
    @Mutable
    private RecipeManager recipes;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void createManager(
        final ReloadableServerRegistries.LoadResult loadingContext,
        final FeatureFlagSet enabledFeatures,
        final Commands.CommandSelection commandSelection,
        final List postponedTags,
        final PermissionSet functionCompilationPermissions,
        final List newComponents,
        final CallbackInfo ci)
    {
        if (CommonConfiguration.config.getCommonConfig().cacheRecipes)
        {
            recipes = new com.recipeessentials.recipecache.RecipeManager(loadingContext.lookupWithUpdatedTags());
        }
    }
}
