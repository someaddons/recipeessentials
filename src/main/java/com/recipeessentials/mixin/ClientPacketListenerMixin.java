package com.recipeessentials.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.telemetry.WorldSessionTelemetryManager;
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
public class ClientPacketListenerMixin
{
    @Shadow
    @Final
    @Mutable
    private RecipeManager recipeManager;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void createManager(
      final Minecraft p_253924_,
      final Screen p_254239_,
      final Connection p_253614_,
      final ServerData p_254072_,
      final GameProfile p_254079_,
      final WorldSessionTelemetryManager p_262115_,
      final CallbackInfo ci)
    {
        recipeManager = new com.recipeessentials.recipecache.RecipeManager();
    }
}
