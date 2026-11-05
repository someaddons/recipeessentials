package com.recipeessentials.mixin;

import com.recipeessentials.config.CommonConfiguration;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

import static net.minecraft.client.gui.screens.recipebook.RecipeBookComponent.RECIPE_BUTTON_SPRITES;

@Mixin(InventoryScreen.class)
public abstract class InventoryButtonMixin extends AbstractRecipeBookScreen<InventoryMenu>
{
    public InventoryButtonMixin(final InventoryMenu menu, final RecipeBookComponent<?> recipeBookComponent, final Inventory inventory, final Component title)
    {
        super(menu, recipeBookComponent, inventory, title);
    }

    @Shadow
    protected abstract void init();

    @Inject(method = "init", at = @At("TAIL"))
    private void recipeessentials$onInit(final CallbackInfo ci)
    {
        if (CommonConfiguration.config.getCommonConfig().disableRecipebook)
        {
            for (final var widget : new ArrayList<>(renderables))
            {
                if (widget instanceof ImageButton && ((ImageButton) widget).sprites.equals(RECIPE_BUTTON_SPRITES))
                {
                    removeWidget((GuiEventListener) widget);
                }
            }
        }
    }
}
