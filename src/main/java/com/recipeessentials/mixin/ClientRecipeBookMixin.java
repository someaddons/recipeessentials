package com.recipeessentials.mixin;

import com.recipeessentials.RecipeEssentials;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * TODO: Disabled, seems to affect login with multiple mods around? replace by specific redirects
 */
@Mixin(RecipeBook.class)
public class ClientRecipeBookMixin
{
    @Inject(method = "contains(Lnet/minecraft/world/item/crafting/Recipe;)Z", at = @At("HEAD"), cancellable = true)
    public void recipeessentials$containsRecipe(final Recipe<?> recipe, final CallbackInfoReturnable<Boolean> cir)
    {
        if (RecipeEssentials.config.getCommonConfig().recipebookShowAll && recipe != null && !recipe.isSpecial())
        {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "contains(Lnet/minecraft/resources/ResourceLocation;)Z", at = @At("HEAD"), cancellable = true)
    public void recipeessentials$contains(final ResourceLocation p_12712_, final CallbackInfoReturnable<Boolean> cir)
    {
        if (RecipeEssentials.config.getCommonConfig().recipebookShowAll && p_12712_ != null)
        {
            cir.setReturnValue(true);
        }
    }
}
