package com.recipeessentials.mixin;

import com.recipeessentials.RecipeEssentials;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinConfig implements IMixinConfigPlugin
{
    @Override
    public void onLoad(final String mixinPackage)
    {

    }

    @Override
    public String getRefMapperConfig()
    {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(final String targetClassName, final String mixinClassName)
    {
        if (mixinClassName.contains("ItemStackMixin") || mixinClassName.contains("CompoundNBTMixin") || mixinClassName.contains("ByteArrayTagMixin") || mixinClassName.contains(
          "IntArrayTagMixin") || mixinClassName.contains("ListTagMixin") || mixinClassName.contains("LongArrayTagMixin") || mixinClassName.contains("ItemStackFastComparisonMixin"))
        {
            if (FabricLoader.getInstance().isModLoaded("owo"))
            {
                RecipeEssentials.LOGGER.warn("Disabling fastItemComparison feature, due to the Owo mod's faulty implementation breaking it");
                return false;
            }

            return RecipeEssentials.config.getCommonConfig().fastItemComparisons;
        }

        if (mixinClassName.contains("RecipeManagerMixin"))
        {
            return RecipeEssentials.config.getCommonConfig().cacheRecipes;
        }

        return true;
    }

    @Override
    public void acceptTargets(final Set<String> myTargets, final Set<String> otherTargets)
    {

    }

    @Override
    public List<String> getMixins()
    {
        return null;
    }

    @Override
    public void preApply(final String targetClassName, final ClassNode targetClass, final String mixinClassName, final IMixinInfo mixinInfo)
    {

    }

    @Override
    public void postApply(final String targetClassName, final ClassNode targetClass, final String mixinClassName, final IMixinInfo mixinInfo)
    {

    }
}
