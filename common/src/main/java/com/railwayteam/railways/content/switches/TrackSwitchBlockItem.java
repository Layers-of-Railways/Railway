package com.railwayteam.railways.content.switches;

import com.simibubi.create.content.logistics.trains.management.edgePoint.EdgePointType;
import com.simibubi.create.content.logistics.trains.management.edgePoint.TrackTargetingBlockItem;
import com.simibubi.create.content.logistics.trains.track.TrackBlockOutline.BezierPointSelection;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class TrackSwitchBlockItem extends TrackTargetingBlockItem {
  public static <T extends Block> NonNullBiFunction<? super T, Properties, TrackTargetingBlockItem> ofType(
    EdgePointType<?> type) {
    return (b, p) -> new TrackSwitchBlockItem(b, p, type);
  }

  public TrackSwitchBlockItem(Block block, Properties properties, EdgePointType<?> type) {
    super(block, properties, type);
  }

  @Override
  public boolean useOnCurve(BezierPointSelection selection, ItemStack stack) {
    return false;
  }
}
