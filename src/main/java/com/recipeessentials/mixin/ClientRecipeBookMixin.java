package com.recipeessentials.mixin;

import net.minecraft.client.ClientRecipeBook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.recipeessentials.RecipeEssentialsClient.USED_GHOST_RECIPES;

@Mixin(ClientRecipeBook.class)
public class ClientRecipeBookMixin
{
    @Inject(method = "clear", at = @At("HEAD"))
    private void recipeessentials$resetUsage(final CallbackInfo ci)
    {
        // Display IDs are assigned anew when joining a server or reloading recipes.
        USED_GHOST_RECIPES.clear();
    }
}
