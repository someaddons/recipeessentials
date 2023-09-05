package com.recipeessentials.mixin;

import com.recipeessentials.RecipeEssentials;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(CraftingScreen.class)
public abstract class RecipeBookButtonMixin extends AbstractContainerScreen<CraftingMenu>
{
    @Shadow
    protected abstract void init();

    @Shadow
    @Final
    private static ResourceLocation RECIPE_BUTTON_LOCATION;

    public RecipeBookButtonMixin(final CraftingMenu p_97741_, final Inventory p_97742_, final Component p_97743_)
    {
        super(p_97741_, p_97742_, p_97743_);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void recipeessentials$onInit(final CallbackInfo ci)
    {
        if (RecipeEssentials.config.getCommonConfig().disableRecipebook)
        {
            for (final var widget : new ArrayList<>(renderables))
            {
                if (widget instanceof ImageButton && ((ImageButton) widget).resourceLocation.equals(RECIPE_BUTTON_LOCATION))
                {
                    removeWidget((GuiEventListener) widget);
                }
            }
        }
    }
}
