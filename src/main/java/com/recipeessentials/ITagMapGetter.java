package com.recipeessentials;

import net.minecraft.nbt.CompoundTag;

/**
 * Getter for the map
 */
public interface ITagMapGetter
{
    CompoundTag getLastEqual();

    void setLastEqual(CompoundTag compoundNBT);
}
