package com.recipeessentials.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * reduces recipe packet by ~15% which reduces the odds of a too big recipe message
 */
@Mixin(ClientboundUpdateRecipesPacket.class)
public class ClientBoundRecipesMixin
{
    @Shadow
    @Final
    @Mutable
    private List<Recipe<?>> recipes;

    @Redirect(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;readList(Lnet/minecraft/network/FriendlyByteBuf$Reader;)Ljava/util/List;"))
    private List on(final FriendlyByteBuf buf, final FriendlyByteBuf.Reader p_236846_)
    {
        int lenght = buf.readVarInt();

        String ids = buf.toString(buf.readerIndex(), lenght, StandardCharsets.UTF_8);
        buf.readerIndex(buf.readerIndex() + lenght);

        String[] idSplit = ids.split(";");

        final List<ResourceLocation> resourceLocations = new ArrayList<>();
        for (final String id : idSplit)
        {
            resourceLocations.add(new ResourceLocation(id));
        }

        recipes = new ArrayList<>();
        for (int i = 0; i < resourceLocations.size() - 1; i += 2)
        {
            final ResourceLocation resourceLocation = resourceLocations.get(i);
            final ResourceLocation resourceLocation1 = resourceLocations.get(i + 1);

            recipes.add(BuiltInRegistries.RECIPE_SERIALIZER.getOptional(resourceLocation).orElseThrow(() -> {
                return new IllegalArgumentException("Unknown recipe serializer " + resourceLocation);
            }).fromNetwork(resourceLocation1, buf));
        }

        return recipes;
    }

    @Overwrite
    public void write(FriendlyByteBuf buf)
    {
        final StringBuilder builder = new StringBuilder();
        for (final Recipe recipe : recipes)
        {
            builder.append(BuiltInRegistries.RECIPE_SERIALIZER.getKey(recipe.getSerializer()).toString());
            builder.append(";");
            builder.append(recipe.getId().toString());
            builder.append(";");
        }

        String total = builder.toString();
        buf.writeUtf(total, total.length());

        for (final Recipe recipe : recipes)
        {
            recipe.getSerializer().toNetwork(buf, recipe);
        }
    }
}
