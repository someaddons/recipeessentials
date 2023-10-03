package com.recipeessentials.mixin.nbt;

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
    @Inject(method = "tagMatches", at = @At("HEAD"), cancellable = true)
    private static void OnAreItemStackTagsEqual(ItemStack stackA, ItemStack stackB, CallbackInfoReturnable<Boolean> re)
    {
        if (stackA == stackB)
        {
            re.setReturnValue(true);
        }
    }

    @Inject(method = "matches(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private static void OnAreItemStacksEqual(ItemStack stackA, ItemStack stackB, CallbackInfoReturnable<Boolean> re)
    {
        if (stackA == stackB)
        {
            re.setReturnValue(true);
        }
    }

    @Redirect(method = "matches(Lnet/minecraft/world/item/ItemStack;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;equals(Ljava/lang/Object;)Z"))
    private boolean onTagCompare(final CompoundTag compoundNBT, final Object other)
    {
        if (compoundNBT == other)
        {
            return true;
        }

        if (other == null)
        {
            return false;
        }

        if (compoundNBT.hashCode() != other.hashCode())
        {
            return false;
        }

        return compoundNBT.equals(other);
    }
}
