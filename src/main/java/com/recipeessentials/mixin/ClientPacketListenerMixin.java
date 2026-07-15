package com.recipeessentials.mixin;

import com.recipeessentials.config.CommonConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.Connection;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin
{
    @Shadow
    @Final
    @Mutable
    private RecipeManager recipeManager;

    @Shadow
    public abstract RegistryAccess.Frozen registryAccess();

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void createManager(final Minecraft minecraft, final Connection connection, final CommonListenerCookie commonListenerCookie, final CallbackInfo ci)
    {
        if (CommonConfiguration.config.getCommonConfig().cacheRecipes)
        {
            recipeManager = new com.recipeessentials.recipecache.RecipeManager(registryAccess());
        }
    }
}
