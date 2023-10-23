package com.recipeessentials.mixin;

import com.recipeessentials.RecipeEssentials;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(InventoryScreen.class)
public abstract class InventoryButtonMixin extends EffectRenderingInventoryScreen<InventoryMenu>
{
    public InventoryButtonMixin(final InventoryMenu p_98701_, final Inventory p_98702_, final Component p_98703_)
    {
        super(p_98701_, p_98702_, p_98703_);
    }

    @Shadow
    protected abstract void init();

    @Shadow
    @Final
    private static ResourceLocation RECIPE_BUTTON_LOCATION;

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
