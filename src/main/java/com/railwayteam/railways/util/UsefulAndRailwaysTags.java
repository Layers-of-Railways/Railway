package com.railwayteam.railways.util;

import com.simibubi.create.AllTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UsefulAndRailwaysTags {
    public static final Tag<Item> IronSheet = AllTags.forgeItemTag("plates/iron");
    public static final ResourceLocation EngineerCapsLoc = new ResourceLocation("railways", "engineer_caps");
    public static final Tag<Item> EngineerCaps = ItemTags.getCollection().getOrCreate(EngineerCapsLoc);

    public static Tag<Item> getForgeItemTag(String name) {
        return AllTags.forgeItemTag(name);
    }

    public static Tag<Block> getForgeBlockTag(String name) {
        return AllTags.forgeBlockTag(name);
    }

    public static Tag<Item> getMinecraftItemTag(String name) {
        return ItemTags.getCollection().get(new ResourceLocation(name));
    }

    public static Tag<Block> getMinecraftBlockTag(String name) {
        return BlockTags.getCollection().get(new ResourceLocation(name));
    }

    // none of this works because stupid tags being stupid
//    public static Optional<Item> getWoolByDye(String name, boolean returnWhiteWoolDefault) {
//        Optional<Item> item1 = Optional.empty();
//        Collection<Item> l = ItemTags.WOOL.getAllElements();
//        //            return item.getRegistryName().getPath().equals(name + "_wool");
//        for(Item item : l) {
//            if(item.getRegistryName().getPath().equals(name + "_wool")) {
//                item1 = Optional.of(item);
//            }
//        }
//        return item1;
//    }
//
//    public static Optional<Item> getWoolByDye(DyeColor dye, boolean returnWhiteWoolDefault) {
//        return getWoolByDye(dye.getName(), returnWhiteWoolDefault);
//    }
//
//    public static Optional<Item> getWoolByDye(String name) {
//        return getWoolByDye(name, true);
//    }
//
//    public static Optional<Item> getWoolByDye(DyeColor dye)  {
//        return getWoolByDye(dye.getName());
//    }
}
