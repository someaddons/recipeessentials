package com.recipeessentials.mixin;

import com.recipeessentials.config.CommonConfiguration;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerGamePacketListenerImpl.class)
public class RecipebookMixin {
    @Redirect(method = "handlePlaceRecipe", at = @At(value = "INVOKE", target = "Lnet/minecraft/stats/ServerRecipeBook;contains(Lnet/minecraft/resources/ResourceKey;)Z"))
    private boolean recipeessentials$contains(final ServerRecipeBook instance, final ResourceKey<Recipe<?>> recipe)
    {
        if (recipe != null && CommonConfiguration.config.getCommonConfig().recipebookShowAll && !CommonConfiguration.config.getCommonConfig().disableRecipebook)
        {
            return true;
        }

        return instance.contains(recipe);
    }
}
