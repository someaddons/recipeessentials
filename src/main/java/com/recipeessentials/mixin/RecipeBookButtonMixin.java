package com.recipeessentials.mixin;

import com.recipeessentials.config.CommonConfiguration;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractContainerScreen.class)
public abstract class RecipeBookButtonMixin extends Screen
{
    private static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");

    protected RecipeBookButtonMixin(final Component p_96550_)
    {
        super(p_96550_);
    }

    @Override
    protected <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T widget)
    {
        if (CommonConfiguration.config.getCommonConfig().disableRecipebook
              && ((Object) this instanceof CraftingScreen || (Object) this instanceof InventoryScreen || (Object) this instanceof EffectRenderingInventoryScreen)
              && widget instanceof ImageButton && ((ImageButton) widget).resourceLocation.equals(RECIPE_BUTTON_LOCATION))
        {
            return widget;
        }

        this.renderables.add(widget);
        return this.addWidget(widget);
    }
}
