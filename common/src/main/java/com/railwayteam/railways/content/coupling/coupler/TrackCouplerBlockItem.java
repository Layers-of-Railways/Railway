package com.railwayteam.railways.content.coupling.coupler;


import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.track.TrackBlockOutline;
import com.simibubi.create.content.trains.track.TrackTargetingBlockItem;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class TrackCouplerBlockItem extends TrackTargetingBlockItem {

    public static <T extends Block> NonNullBiFunction<? super T, Properties, TrackTargetingBlockItem> ofType(
        EdgePointType<?> type) {
        return (b, p) -> new TrackCouplerBlockItem(b, p, type);
    }

    public TrackCouplerBlockItem(Block pBlock, Properties pProperties, EdgePointType<?> type) {
        super(pBlock, pProperties, type);
    }

    @Override
    public boolean useOnCurve(TrackBlockOutline.BezierPointSelection selection, ItemStack stack) {
        return false;
    }
}
