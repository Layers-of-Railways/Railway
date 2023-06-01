package com.railwayteam.railways.util;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * For de-hardcoding checks for {@link AllBlocks#TRACK}.
 */
public class CustomTrackChecks {
  private static final ItemStack trackStack = AllBlocks.TRACK.asStack();
  private static final BlockState trackState = AllBlocks.TRACK.getDefaultState();

  /**
   * For use in replacing the argument of a comparison. Given a stack, returns either the original if not a track,
   * or a stack of the default track if it is. Be careful, the returned stack should not be modified.
   */
  public static ItemStack check(ItemStack stack) {
    if (AllTags.AllBlockTags.TRACKS.matches(stack)) {
      return trackStack;
    }
    return stack;
  }

  public static BlockState check(BlockState state) {
    if (AllTags.AllBlockTags.TRACKS.matches(state)) {
      return trackState;
    }
    return state;
  }
}
