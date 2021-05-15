package com.railwayteam.railways.util;

import com.simibubi.create.AllTags;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public abstract class TagUtils {
    public static final ResourceLocation EngineerCapsLoc = new ResourceLocation("railways", "engineer_caps");
    public static final Tags.IOptionalNamedTag<Item> EngineerCaps = ItemTags.createOptional(EngineerCapsLoc);

    public static ITag<Block> getMinecraftBlockTag(String name) {
        return BlockTags.getCollection().get(new ResourceLocation(name));
    }

    public static ITag.INamedTag<Item> getForgeItemTag(String name) {
        return AllTags.forgeItemTag(name);
    }

    public static ITag.INamedTag<Block> getForgeBlockTag(String name) {
        return AllTags.forgeBlockTag(name);
    }

    public static ITag<Item> getMinecraftItemTag(String name) {
        return ItemTags.getCollection().get(new ResourceLocation(name));
    }
}
