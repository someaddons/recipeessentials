package com.recipeessentials.mixin;

import com.recipeessentials.nbt.IPrototypeHashcode;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.PatchedDataComponentMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PatchedDataComponentMap.class)
public class ItemStackFastComparisonMixin implements IPrototypeHashcode
{
    @Unique
    private int prototypeHashCode;

    @Inject(method = "<init>(Lnet/minecraft/core/component/DataComponentMap;Lit/unimi/dsi/fastutil/objects/Reference2ObjectMap;Z)V", at = @At("RETURN"))
    private void recipeessentials$onInit(final DataComponentMap prototype, final Reference2ObjectMap<?, ?> patch,
      final boolean copyOnWrite, final CallbackInfo ci)
    {
        prototypeHashCode = prototype.hashCode();
    }

    // Preserve NeoForge's patch hashing, only cache the immutable prototype's hash.
    @Redirect(method = "hashCode", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/component/DataComponentMap;hashCode()I"))
    private int recipeessentials$prototypeHash(final DataComponentMap prototype)
    {
        return prototypeHashCode;
    }

    @Redirect(method = "equals", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/component/DataComponentMap;equals(Ljava/lang/Object;)Z"))
    private boolean recipeessentials$comparePrototype(final DataComponentMap prototype, final Object otherPrototype, final Object other)
    {
        return prototypeHashCode == ((IPrototypeHashcode) other).protoTypeHashcode() && prototype.equals(otherPrototype);
    }

    @Override
    public int protoTypeHashcode()
    {
        return prototypeHashCode;
    }
}
