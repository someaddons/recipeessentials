package com.recipeessentials.mixin.nbt;

import com.recipeessentials.nbt.IParentAwareTag;
import com.recipeessentials.nbt.IParentTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LongArrayTag.class)
public class LongArrayTagMixin implements IParentAwareTag
{
    @Unique
    private IParentTag parentTag = null;

    @Override
    public void setParent(final IParentTag tag)
    {
        parentTag = tag;
    }

    @Override
    public IParentTag parent()
    {
        return parentTag;
    }

    @Inject(method = "set(ILnet/minecraft/nbt/LongTag;)Lnet/minecraft/nbt/LongTag;", at = @At("RETURN"))
    private void onSet(
      final int p_128813_, final LongTag p_128814_, final CallbackInfoReturnable<LongTag> cir)
    {
        if (parentTag != null)
        {
            parentTag.markDirty();
        }
    }

    @Inject(method = "add(ILnet/minecraft/nbt/LongTag;)V", at = @At("RETURN"))
    private void onAddIntTag(
      final int p_128832_, final LongTag p_128833_, final CallbackInfo ci)
    {
        if (parentTag != null)
        {
            parentTag.markDirty();
        }
    }

    @Inject(method = "setTag", at = @At("RETURN"))
    private void onSetTag(final int p_128218_, final Tag p_128219_, final CallbackInfoReturnable<Boolean> cir)
    {
        if (parentTag != null)
        {
            parentTag.markDirty();
        }
    }

    @Inject(method = "addTag", at = @At("RETURN"))
    private void onAddTag(final int p_128218_, final Tag p_128219_, final CallbackInfoReturnable<Boolean> cir)
    {
        if (parentTag != null)
        {
            parentTag.markDirty();
        }
    }

    @Inject(method = "remove(I)Lnet/minecraft/nbt/LongTag;", at = @At("RETURN"))
    private void onRemove(final int p_128213_, final CallbackInfoReturnable<ByteTag> cir)
    {
        if (parentTag != null)
        {
            parentTag.markDirty();
        }
    }

    @Inject(method = "clear", at = @At("RETURN"))
    private void onClear(final CallbackInfo ci)
    {
        if (parentTag != null)
        {
            parentTag.markDirty();
        }
    }
}
