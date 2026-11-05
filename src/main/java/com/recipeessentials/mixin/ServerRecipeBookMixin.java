package com.recipeessentials.mixin;

import com.recipeessentials.config.CommonConfiguration;
import net.minecraft.network.protocol.game.ClientboundRecipeBookAddPacket;
import net.minecraft.network.protocol.game.ClientboundRecipeBookSettingsPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

@Mixin(ServerRecipeBook.class)
public class ServerRecipeBookMixin extends RecipeBook
{
    @Inject(method = "sendInitialRecipeBook", at = @At("HEAD"), cancellable = true)
    private void recipeessentials$disableRecipebookInit(final ServerPlayer p_12790_, final CallbackInfo ci)
    {
        if (CommonConfiguration.config.getCommonConfig().disableRecipebook)
        {
            ci.cancel();
        }
        else if (CommonConfiguration.config.getCommonConfig().recipebookShowAll)
        {
            recipeessentials$sendAll(p_12790_);
            ci.cancel();
        }
    }

    @Unique
    private void recipeessentials$sendAll(final ServerPlayer player)
    {
        player.connection.send(new ClientboundRecipeBookSettingsPacket(bookSettings.copy()));
        player.connection.send(recipeessentials$allRecipes(player.level().getServer().getRecipeManager()));
    }

    @Unique
    private ClientboundRecipeBookAddPacket recipeessentials$allRecipes(final RecipeManager manager)
    {
        final List<ClientboundRecipeBookAddPacket.Entry> displays = new ArrayList<>();
        for (final RecipeHolder<?> recipe : manager.getRecipes())
        {
            if (!recipe.value().isSpecial())
            {
                manager.listDisplaysForRecipe(recipe.id(), display -> displays.add(new ClientboundRecipeBookAddPacket.Entry(display, false, false)));
            }
        }
        return new ClientboundRecipeBookAddPacket(displays, true);
    }

    @Inject(method = "pack", at = @At("HEAD"), cancellable = true)
    private void recipeessentials$disableRecipebookNbt(final CallbackInfoReturnable<ServerRecipeBook.Packed> cir)
    {
        if (CommonConfiguration.config.getCommonConfig().disableRecipebook || CommonConfiguration.config.getCommonConfig().recipebookShowAll)
        {
            cir.setReturnValue(new ServerRecipeBook.Packed(bookSettings.copy(), List.of(), List.of()));
        }
    }

    @Inject(method = {"addRecipes", "removeRecipes"}, at = @At("HEAD"), cancellable = true)
    private void recipeessentials$skipUnlocks(
        final Collection<RecipeHolder<?>> recipes, final ServerPlayer player,
        final CallbackInfoReturnable<Integer> cir)
    {
        if (CommonConfiguration.config.getCommonConfig().disableRecipebook || CommonConfiguration.config.getCommonConfig().recipebookShowAll)
        {
            cir.setReturnValue(0);
        }
    }

    @Inject(method = "loadUntrusted", at = @At("HEAD"), cancellable = true)
    private void recipeessentials$skipSavedRecipes(
        final ServerRecipeBook.Packed packed,
        final Predicate<ResourceKey<Recipe<?>>> validator, final CallbackInfo ci)
    {
        if (CommonConfiguration.config.getCommonConfig().disableRecipebook || CommonConfiguration.config.getCommonConfig().recipebookShowAll)
        {
            bookSettings.replaceFrom(packed.settings());
            ci.cancel();
        }
    }
}
