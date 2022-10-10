package com.railwayteam.railways.util;

import com.simibubi.create.AllBlocks;
import com.tterrag.registrate.util.entry.BlockEntry;

//So that mixin doesn't load all blocks
public interface AllBlocksWrapper {
  static BlockEntry<?> metalGirder() {
    return AllBlocks.METAL_GIRDER;
  }
  static BlockEntry<?> track() {
    return AllBlocks.TRACK;
  }
}
