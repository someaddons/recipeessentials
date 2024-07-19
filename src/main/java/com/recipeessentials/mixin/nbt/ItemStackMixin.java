package com.recipeessentials.mixin.nbt;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemStack.class)
public class ItemStackMixin
{
    // TODO: Hashcode caching in PatchedDataComponentMap

    @Inject(method = "isSameItemSameComponents", at = @At("HEAD"), cancellable = true)
    private static void OnAreItemStackTagsEqual(ItemStack stackA, ItemStack stackB, CallbackInfoReturnable<Boolean> re)
    {
        if (stackA == stackB)
        {
            re.setReturnValue(true);
        }
    }
}
