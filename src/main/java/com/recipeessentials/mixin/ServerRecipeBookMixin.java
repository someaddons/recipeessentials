package com.recipeessentials.mixin;

import com.recipeessentials.config.CommonConfiguration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundRecipePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerRecipeBook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ServerRecipeBook.class)
public class ServerRecipeBookMixin
{
    @Inject(method = "sendInitialRecipeBook", at = @At("HEAD"), cancellable = true)
    private void recipeessentials$disableRecipebookInit(final ServerPlayer p_12790_, final CallbackInfo ci)
    {
        if (CommonConfiguration.config.getCommonConfig().disableRecipebook || CommonConfiguration.config.getCommonConfig().recipebookShowAll)
        {
            ci.cancel();
        }
    }

    @Inject(method = "toNbt", at = @At("HEAD"), cancellable = true)
    private void recipeessentials$disableRecipebookNbt(final CallbackInfoReturnable<CompoundTag> cir)
    {
        if (CommonConfiguration.config.getCommonConfig().disableRecipebook || CommonConfiguration.config.getCommonConfig().recipebookShowAll)
        {
            cir.setReturnValue(new CompoundTag());
        }
    }

    @Inject(method = "sendRecipes", at = @At("HEAD"), cancellable = true)
    private void recipeessentials$disableRecipebookSend(
      final ClientboundRecipePacket.State p_12802_,
      final ServerPlayer p_12803_,
      final List<ResourceLocation> p_12804_, final CallbackInfo ci)
    {
        if (CommonConfiguration.config.getCommonConfig().disableRecipebook || CommonConfiguration.config.getCommonConfig().recipebookShowAll)
        {
            ci.cancel();
        }
    }
}
