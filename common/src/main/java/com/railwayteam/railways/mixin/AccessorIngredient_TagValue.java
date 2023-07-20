package com.railwayteam.railways.mixin;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient.TagValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TagValue.class)
public interface AccessorIngredient_TagValue {
	@Invoker("<init>")
	static TagValue railway$create(TagKey<Item> tag) {
		throw new AssertionError();
	}

	@Accessor
	TagKey<Item> getTag();
}
