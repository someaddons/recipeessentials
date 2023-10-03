package com.recipeessentials.mixin;

import com.recipeessentials.RecipeEssentials;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBook.class)
public class RecipebookMixin
{
    @Inject(method = "contains(Lnet/minecraft/world/item/crafting/Recipe;)Z", at = @At("HEAD"), cancellable = true)
    private void showAll(final Recipe<?> p_12710_, final CallbackInfoReturnable<Boolean> cir)
    {
        if (p_12710_ != null && RecipeEssentials.config.getCommonConfig().recipebookShowAll)
        {
            cir.setReturnValue(true);
        }
    }
}
