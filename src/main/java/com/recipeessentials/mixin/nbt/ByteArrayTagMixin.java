package com.recipeessentials.mixin.nbt;

import com.recipeessentials.nbt.IParentAwareTag;
import com.recipeessentials.nbt.IParentTag;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ByteArrayTag.class)
public class ByteArrayTagMixin implements IParentAwareTag
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

    @Inject(method = "set(ILnet/minecraft/nbt/ByteTag;)Lnet/minecraft/nbt/ByteTag;", at = @At("RETURN"))
    private void onSet(
      final int p_128196_, final ByteTag p_128197_, final CallbackInfoReturnable<ByteTag> cir)
    {
        if (parentTag != null)
        {
            parentTag.markDirty();
        }
    }

    @Inject(method = "add(ILnet/minecraft/nbt/ByteTag;)V", at = @At("RETURN"))
    private void onAddByteTag(
      final int p_128215_,
      final ByteTag p_128216_,
      final CallbackInfo ci)
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

    @Inject(method = "remove(I)Lnet/minecraft/nbt/ByteTag;", at = @At("RETURN"))
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
