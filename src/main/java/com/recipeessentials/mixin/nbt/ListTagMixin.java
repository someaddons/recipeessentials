package com.recipeessentials.mixin.nbt;

import com.recipeessentials.nbt.IParentAwareTag;
import com.recipeessentials.nbt.IParentTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ListTag.class)
public class ListTagMixin implements IParentAwareTag
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

    @Inject(method = "remove(I)Lnet/minecraft/nbt/Tag;", at = @At("RETURN"))
    private void onRemove(
      final int p_128751_, final CallbackInfoReturnable<Tag> cir)
    {
        if (parentTag != null)
        {
            parentTag.markDirty();
        }
    }

    @Inject(method = "getCompound", at = @At("RETURN"))
    private void onGetCompound(
      final int p_128729_, final CallbackInfoReturnable<CompoundTag> cir)
    {
        if (parentTag != null)
        {
            CompoundTag r = cir.getReturnValue();
            if (r instanceof IParentAwareTag)
            {
                ((IParentAwareTag) r).setParent(parentTag);
            }
        }
    }

    @Inject(method = "getList", at = @At("RETURN"))
    private void onGetList(
      final int p_128729_, final CallbackInfoReturnable<ListTag> cir)
    {
        if (parentTag != null)
        {
            ListTag r = cir.getReturnValue();
            if (r instanceof IParentAwareTag)
            {
                ((IParentAwareTag) r).setParent(parentTag);
            }
        }
    }

    @Inject(method = "set(ILnet/minecraft/nbt/Tag;)Lnet/minecraft/nbt/Tag;", at = @At("RETURN"))
    private void onSet(final int p_128760_, final Tag p_128761_, final CallbackInfoReturnable<Tag> cir)
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

    @Inject(method = "clear", at = @At("RETURN"))
    private void onClear(final CallbackInfo ci)
    {
        if (parentTag != null)
        {
            parentTag.markDirty();
        }
    }
}
