package com.recipeessentials.component;

import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;

import java.util.Optional;

public interface ICachedComponentMap
{
    DataComponentMap recipeessentials$getPrototype();

    Reference2ObjectMap<DataComponentType<?>, Optional<?>> recipeessentials$getPatch();

    ICachedComponentMap recipeessentials$getLastEqual();

    void recipeessentials$setLastEqual(ICachedComponentMap other);
}
