package com.railwayteam.railways.mixin;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Ingredient.TagValue.class)
public interface AccessorIngredient_TagValue {
    @Invoker("<init>")
    static Ingredient.TagValue railways$create(TagKey<Item> tag) {
        throw new AssertionError();
    }
}
