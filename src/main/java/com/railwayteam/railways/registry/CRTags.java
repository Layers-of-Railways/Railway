package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class CRTags {
    public static class Items {
        public static final Tags.IOptionalNamedTag<Item> EngineerCaps = itemTag("engineer_caps");
        public static final Tags.IOptionalNamedTag<Item> Tracks = itemTag("tracks");
    }

    static protected Tags.IOptionalNamedTag<Item> itemTag(String name, Supplier<Item>... defaults) {
        return ItemTags.createOptional(new ResourceLocation(Railways.MODID, name), Arrays.stream(defaults).collect(Collectors.toSet()));
    }

    static protected Tags.IOptionalNamedTag<Block> blockTag(String name, Supplier<Block>... defaults) {
        return BlockTags.createOptional(new ResourceLocation(Railways.MODID, name), Arrays.stream(defaults).collect(Collectors.toSet()));
    }
}
