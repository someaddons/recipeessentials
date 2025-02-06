package com.recipeessentials.mixin;

import net.minecraft.world.item.crafting.RecipeManager;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = RecipeManager.class)
/**
 * Reports without full stucktrace, as it isnt useful here
 */
public abstract class RecipeManagerMixin
{
    @Shadow
    @Final
    public static Logger LOGGER;

    @Redirect(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
        at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"), require = 0)
    private void logErrorOnce(final Logger instance, final String string, final Object resloc, final Object e)
    {
        LOGGER.error("Parsing error loading recipe {} {}", resloc, ((Exception) e).getLocalizedMessage());
    }
}
