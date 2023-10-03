package com.recipeessentials.nbt;

import org.spongepowered.asm.mixin.Unique;

public interface IParentAwareTag
{
    @Unique
    void setParent(IParentTag tag);

    @Unique
    IParentTag parent();
}
