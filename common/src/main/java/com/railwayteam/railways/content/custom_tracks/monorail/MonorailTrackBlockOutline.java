package com.railwayteam.railways.content.custom_tracks.monorail;

import com.railwayteam.railways.registry.CRShapes;
import com.simibubi.create.AllShapes;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public class MonorailTrackBlockOutline {
	public static final VoxelShape MONORAIL_LONG_CROSS = Shapes.or(MonorailTrackVoxelShapes.longOrthogonalZ(), MonorailTrackVoxelShapes.longOrthogonalX());
	public static final VoxelShape MONORAIL_LONG_ORTHO = MonorailTrackVoxelShapes.longOrthogonalZ();
	public static final VoxelShape MONORAIL_LONG_ORTHO_OFFSET = MonorailTrackVoxelShapes.longOrthogonalZOffset();

	public static final Map<VoxelShape, VoxelShape> TRACK_TO_MONORAIL = Map.of(
			AllShapes.TRACK_ORTHO.get(Direction.EAST), CRShapes.MONORAIL_TRACK_ORTHO.get(Direction.EAST),
			AllShapes.TRACK_ORTHO.get(Direction.SOUTH), CRShapes.MONORAIL_TRACK_ORTHO.get(Direction.SOUTH),
			AllShapes.TRACK_CROSS, CRShapes.MONORAIL_TRACK_CROSS
//			AccessorTrackBlockOutline.getLONG_ORTHO_OFFSET(), MONORAIL_LONG_ORTHO_OFFSET,
//			AccessorTrackBlockOutline.getLONG_ORTHO(), MONORAIL_LONG_ORTHO,
//			AccessorTrackBlockOutline.getLONG_CROSS(), MONORAIL_LONG_CROSS
	);

	public static VoxelShape convert(Object o, boolean monorail) {
		if (o instanceof VoxelShape shape)
			return convert(shape, monorail);
		throw new IllegalArgumentException("object is not a VoxelShape");
	}

	public static VoxelShape convert(VoxelShape trackShape, boolean monorail) {
		return monorail ? TRACK_TO_MONORAIL.getOrDefault(trackShape, trackShape) : trackShape;
	}
}
