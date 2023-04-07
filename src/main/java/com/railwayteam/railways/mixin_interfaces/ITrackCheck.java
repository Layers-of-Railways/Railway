package com.railwayteam.railways.mixin_interfaces;

import com.railwayteam.railways.registry.CRTags;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public interface ITrackCheck { //TODO _track api
  static boolean check(BlockEntry<?> instance, ItemStack itemStack) {
    if (instance.getId().equals(new ResourceLocation("create", "track"))) {
      return CRTags.AllBlockTags.TRACKS.matches(itemStack);
    } else {
      return instance.isIn(itemStack);
    }
  }

  static boolean check(BlockEntry<?> instance, BlockState state) {
    if (instance.getId().equals(new ResourceLocation("create", "track"))) {
      return CRTags.AllBlockTags.TRACKS.matches(state);
    } else {
      return instance.has(state);
    }
  }
}
