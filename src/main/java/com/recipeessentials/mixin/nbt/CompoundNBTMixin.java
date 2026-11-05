package com.recipeessentials.mixin.nbt;

import com.recipeessentials.nbt.IEqualTag;
import com.recipeessentials.nbt.IParentAwareTag;
import com.recipeessentials.nbt.IParentTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(CompoundTag.class)
/**
 * Caches the hashcode and marks dirty for recalculation, to avoid calculating it more than necessary. And use it for comparison
 * Saves a cross-reference to the last compared compound to avoid equalling the maps too often
 */
public abstract class CompoundNBTMixin implements IParentTag, IParentAwareTag, IEqualTag
{
    @Shadow
    @Final
    @Mutable
    private Map<String, Tag> tags;
    @Unique
    private int              hashCode  = -1;
    @Unique
    private IEqualTag        equalTo   = null;
    @Unique
    private IParentTag       parentTag = null;

    @Overwrite
    public int hashCode()
    {
        if (hashCode == -1)
        {
            hashCode = tags.hashCode();
        }

        return hashCode;
    }

    @Redirect(method = {"put", "putByte", "putShort", "putInt", "putLong", "putFloat", "putDouble", "putString", "putByteArray", "putIntArray", "putLongArray",
      "putBoolean"}, at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object onPut(final Map map, final Object key, final Object value)
    {
        markDirty();
        return map.put(key, value);
    }

    @Inject(method = "get", at = @At("RETURN"))
    public void get(final String p_128424_, final CallbackInfoReturnable<Tag> cir)
    {
        Tag v = cir.getReturnValue();
        if ((Object) v instanceof IParentAwareTag)
        {
            ((IParentAwareTag) (Object) v).setParent(this);
        }
    }

    @Inject(method = "put", at = @At("RETURN"))
    public void put(final String p_128366_, final Tag tag, final CallbackInfoReturnable<Tag> cir)
    {
        if ((Object) tag instanceof IParentAwareTag)
        {
            ((IParentAwareTag) (Object) tag).setParent(this);
        }
        markDirty();
    }

    @Inject(method = "remove", at = @At("RETURN"))
    public void remove(final String p_128474_, final CallbackInfoReturnable<Tag> cir)
    {
        markDirty();
    }

    @Inject(method = "merge", at = @At(value = "RETURN"))
    private void onMerge(
      final CompoundTag tag,
      final CallbackInfoReturnable<CompoundTag> cir)
    {
        if ((Object) tag instanceof IParentAwareTag)
        {
            ((IParentAwareTag) (Object) tag).setParent(this);
        }
        markDirty();
    }

    @Inject(method = "equals", at = @At(value = "INVOKE", target = "Ljava/util/Objects;equals(Ljava/lang/Object;Ljava/lang/Object;)Z"), cancellable = true)
    public void befOreComparetest(final Object compound2, final CallbackInfoReturnable<Boolean> cir)
    {
        if (this == compound2)
        {
            cir.setReturnValue(true);
            return;
        }
        else
        {
            if (compound2 instanceof IEqualTag && this.hashCode() == compound2.hashCode())
            {
                if (equalTo == compound2 && ((IEqualTag) compound2).getLastEqual() == this)
                {
                    cir.setReturnValue(true);
                    return;
                }
            }
            else
            {
                cir.setReturnValue(false);
                return;
            }
        }
    }

    @Inject(method = "equals", at = @At(value = "RETURN", target = "Ljava/util/Objects;equals(Ljava/lang/Object;Ljava/lang/Object;)Z"))
    public void afterComparetest(final Object compound2, final CallbackInfoReturnable<Boolean> cir)
    {
        if (this == compound2)
        {
            return;
        }

        if (cir.getReturnValueZ())
        {
            equalTo = (IEqualTag) compound2;
            ((IEqualTag) compound2).setLastEqual(this);
        }
    }

    @Override
    public void setLastEqual(IEqualTag compoundNBT)
    {
        equalTo = compoundNBT;
    }

    @Override
    public void markDirty()
    {
        if (hashCode == -1)
        {
            return;
        }

        hashCode = -1;
        equalTo = null;
        if (parentTag != null && parentTag != this)
        {
            parentTag.markDirty();
            parentTag = null;
        }
    }

    @Override
    public IEqualTag getLastEqual()
    {
        return equalTo;
    }

    @Inject(method = "getCompound", at = @At("RETURN"))
    private void onGetCompound(final String p_128470_, final CallbackInfoReturnable<java.util.Optional<CompoundTag>> cir)
    {
        cir.getReturnValue().ifPresent(tag -> ((IParentAwareTag) (Object) tag).setParent(this));
    }

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
}
