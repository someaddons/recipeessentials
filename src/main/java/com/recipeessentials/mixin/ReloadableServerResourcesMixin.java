package com.recipeessentials.mixin;

import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin
{
    @Shadow
    @Final
    @Mutable
    private RecipeManager recipes;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void createManager(
      final RegistryAccess.Frozen p_206857_,
      final FeatureFlagSet p_250695_,
      final Commands.CommandSelection p_206858_,
      final int p_206859_, final CallbackInfo ci)
    {
        recipes = new com.recipeessentials.recipecache.RecipeManager();
    }
}
