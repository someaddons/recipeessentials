package com.recipeessentials.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemStack.class)
public class ItemStackMixin
{
    @Inject(method = "isSameItemSameTags", at = @At("HEAD"), cancellable = true)
    private static void OnAreItemStackTagsEqual(ItemStack stackA, ItemStack stackB, CallbackInfoReturnable<Boolean> re)
    {
        if (stackA == stackB)
        {
            re.setReturnValue(true);
        }
    }

    @Redirect(method = "isSameItemSameTags", at = @At(value = "INVOKE", target = "Ljava/util/Objects;equals(Ljava/lang/Object;Ljava/lang/Object;)Z"))
    private static boolean onTagCompare(final Object a, final Object b)
    {
        if (a == b)
        {
            return true;
        }

        if (!(a instanceof CompoundTag) || !(b instanceof CompoundTag))
        {
            return false;
        }

        if (a.hashCode() != b.hashCode())
        {
            return false;
        }

        return a.equals(b);
    }
}
