package com.recipeessentials.mixin;

import com.recipeessentials.nbt.IPrototypeHashcode;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PatchedDataComponentMap.class)
public class ItemStackFastComparisonMixin implements IPrototypeHashcode
{
    @Shadow
    private Reference2ObjectMap<DataComponentType<?>, Optional<?>> patch;

    @Unique
    @Final
    @Mutable
    private int prototypeHashCode;

    @Inject(method = "<init>(Lnet/minecraft/core/component/DataComponentMap;Lit/unimi/dsi/fastutil/objects/Reference2ObjectMap;Z)V", at = @At("RETURN"))
    private void onInit(final DataComponentMap dataComponentMap, final Reference2ObjectMap reference2ObjectMap, final boolean bl, final CallbackInfo ci)
    {
        prototypeHashCode = dataComponentMap.hashCode();
    }

    @Overwrite
    public int hashCode()
    {
        return prototypeHashCode + this.patch.hashCode() * 31;
    }

    @Redirect(method = "equals", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/component/DataComponentMap;equals(Ljava/lang/Object;)Z"))
    private boolean onPrototypeCompare(final DataComponentMap ourPrototype, final Object otherPrototype, Object other)
    {
        return prototypeHashCode == ((IPrototypeHashcode) other).protoTypeHashcode() && ourPrototype.equals(otherPrototype);
    }

    @Override
    public int protoTypeHashcode()
    {
        return prototypeHashCode;
    }
}
