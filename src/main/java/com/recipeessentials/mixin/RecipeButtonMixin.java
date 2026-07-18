package com.recipeessentials.mixin;

import com.recipeessentials.config.CommonConfiguration;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(RecipeButton.class)
public class RecipeButtonMixin
{
    @Shadow
    private int currentIndex;

    @Redirect(method = "renderWidget", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"))
    private Object getFirst(final List instance, final int i)
    {
        if (CommonConfiguration.config.getCommonConfig().enableBetterRecipebookSorting)
        {
            currentIndex = 0;
            return instance.get(0);
        }
        else
        {
            return instance.get(i);
        }
    }
}
