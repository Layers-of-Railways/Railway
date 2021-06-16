package com.railwayteam.railways.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public abstract class RailwaysTags {
    public static final Tags.IOptionalNamedTag<Item> EngineerCaps = itemTag("engineer_caps");
    public static final Tags.IOptionalNamedTag<Item> Tracks = itemTag("tracks");

    static protected <T> Tags.IOptionalNamedTag<T> tag(String name, BiFunction<ResourceLocation, Set<Supplier<T>>, Tags.IOptionalNamedTag<T>> f, T... defaults) {
        Set<Supplier<T>> suppliers = new HashSet<>();
        for(T d : defaults) {
            suppliers.add(() -> d);
        }
        return f.apply(new ResourceLocation("railways", name), suppliers);
    }
    static protected Tags.IOptionalNamedTag<Item> itemTag(String name, Item... defaults) {return tag(name, ItemTags::createOptional, defaults);}
    static protected Tags.IOptionalNamedTag<Block> blockTag(String name, Block... defaults) {return tag(name, BlockTags::createOptional, defaults);}
}
