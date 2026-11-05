package com.recipeessentials.mixin;

import com.recipeessentials.component.ICachedComponentMap;
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
public class ItemStackFastComparisonMixin implements ICachedComponentMap
{
    @Shadow
    @Final
    private DataComponentMap prototype;

    @Shadow
    private Reference2ObjectMap<DataComponentType<?>, Optional<?>> patch;

    @Unique
    private int recipeessentials$prototypeHashCode = -1;

    @Unique
    private int recipeessentials$patchHashCode = -1;

    @Unique
    private ICachedComponentMap recipeessentials$lastEquals = null;

    // prototype is immutable
    @Redirect(method = "hashCode", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/component/DataComponentMap;hashCode()I"))
    private int recipeessentials$getPrototypeHashCode(final DataComponentMap prototype)
    {
        if (recipeessentials$prototypeHashCode == -1)
        {
            recipeessentials$prototypeHashCode = prototype.hashCode();
        }
        return recipeessentials$prototypeHashCode;
    }

    // Neoforge patch hash
    @Redirect(method = "hashCode", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Reference2ObjectMap;hashCode()I"))
    private int recipeessentials$getPatchHashCode(final Reference2ObjectMap<DataComponentType<?>, Optional<?>> patch)
    {
        if (recipeessentials$patchHashCode == -1)
        {
            recipeessentials$patchHashCode = patch.hashCode();
        }
        return recipeessentials$patchHashCode;
    }

    // All normal patch mutations pass through this method
    @Inject(method = "ensureMapOwnership", at = @At("HEAD"))
    private void recipeessentials$invalidatePatchHashCode(final CallbackInfo ci)
    {
        recipeessentials$patchHashCode = -1;
        recipeessentials$lastEquals = null;
    }

    /**
     * @author RecipeEssentials
     * @reason Check cached hashes and remembered equality without callback objects,
     * while retaining vanilla's full comparison for previously unverified pairs.
     */
    @Overwrite
    @Override
    public boolean equals(final Object obj)
    {
        final PatchedDataComponentMap self = (PatchedDataComponentMap) (Object) this;
        if (self == obj)
        {
            return true;
        }

        if (!(obj instanceof PatchedDataComponentMap otherMap))
        {
            return false;
        }

        final ICachedComponentMap otherCache = (ICachedComponentMap) (Object) otherMap;
        if (recipeessentials$lastEquals == otherCache && otherCache.recipeessentials$getLastEqual() == this)
        {
            return true;
        }

        if (self.hashCode() != otherMap.hashCode()
            || !prototype.equals(otherCache.recipeessentials$getPrototype())
            || !patch.equals(otherCache.recipeessentials$getPatch()))
        {
            return false;
        }

        recipeessentials$lastEquals = otherCache;
        otherCache.recipeessentials$setLastEqual(this);
        return true;
    }

    @Override
    public DataComponentMap recipeessentials$getPrototype()
    {
        return prototype;
    }

    @Override
    public Reference2ObjectMap<DataComponentType<?>, Optional<?>> recipeessentials$getPatch()
    {
        return patch;
    }

    @Override
    public ICachedComponentMap recipeessentials$getLastEqual()
    {
        return recipeessentials$lastEquals;
    }

    @Override
    public void recipeessentials$setLastEqual(final ICachedComponentMap other)
    {
        recipeessentials$lastEquals = other;
    }
}
