package com.recipeessentials.mixin;

import com.recipeessentials.ITagMapGetter;
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
public class CompoundNBTMixin implements ITagMapGetter
{
    @Shadow
    @Final
    private Map<String, Tag> tags;

    @Unique
    private int         hashCode   = 0;
    @Unique
    private boolean     needRehash = true;
    @Unique
    private CompoundTag equalTo    = null;
    @Unique
    private CompoundTag self       = (CompoundTag) (Object) this;

    @Overwrite
    public int hashCode()
    {
        if (needRehash)
        {
            needRehash = false;
            hashCode = tags.hashCode();
        }

        return hashCode;
    }

    @Redirect(method = {"put", "putByte", "putShort", "putInt", "putLong", "putUUID", "putFloat", "putDouble", "putString", "putByteArray", "putIntArray", "putLongArray",
      "putBoolean"}, at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object onPut(final Map map, final Object key, final Object value)
    {
        needRehash = true;
        equalTo = null;
        return map.put(key, value);
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
            if (compound2 instanceof CompoundTag && this.hashCode() == compound2.hashCode())
            {
                if (equalTo == compound2 && ((ITagMapGetter) compound2).getLastEqual() == self)
                {
                    cir.setReturnValue(true);
                }
            }
            else
            {
                cir.setReturnValue(false);
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
            equalTo = (CompoundTag) compound2;
            ((ITagMapGetter) compound2).setLastEqual(self);
        }
    }

    @Override
    public void setLastEqual(CompoundTag compoundNBT)
    {
        equalTo = compoundNBT;
    }

    @Override
    public CompoundTag getLastEqual()
    {
        return equalTo;
    }
}
