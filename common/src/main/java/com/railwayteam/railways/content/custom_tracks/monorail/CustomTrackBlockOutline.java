/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.content.custom_tracks.monorail;

import com.google.common.collect.ImmutableMap;
import com.railwayteam.railways.mixin.client.AccessorTrackBlockOutline;
import com.railwayteam.railways.registry.CRShapes;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.railwayteam.railways.util.Utils;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.trains.track.TrackMaterial;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public class CustomTrackBlockOutline {
	public static final VoxelShape MONORAIL_LONG_CROSS = Shapes.or(MonorailTrackVoxelShapes.longOrthogonalZ(), MonorailTrackVoxelShapes.longOrthogonalX());
	public static final VoxelShape MONORAIL_LONG_ORTHO = MonorailTrackVoxelShapes.longOrthogonalZ();
	public static final VoxelShape MONORAIL_LONG_ORTHO_OFFSET = MonorailTrackVoxelShapes.longOrthogonalZOffset();

	public static final VoxelShape NARROW_LONG_ORTHO = Block.box(-7, 0, -3.3, 16 + 7, 4, 19.3);
	public static final VoxelShape NARROW_LONG_ORTHO_2 = Block.box(-3.3, 0, -7, 19.3, 4, 16 + 7);
	public static final VoxelShape NARROW_LONG_ORTHO_OFFSET = Block.box(-7, 0, 0, 16 + 7, 4, 24);
	public static final VoxelShape NARROW_LONG_CROSS = Shapes.or(NARROW_LONG_ORTHO, NARROW_LONG_ORTHO_2);

	public static final Map<VoxelShape, VoxelShape> TRACK_TO_MONORAIL = Map.of(
		AllShapes.TRACK_ORTHO.get(Direction.EAST), CRShapes.MONORAIL_TRACK_ORTHO.get(Direction.EAST),
		AllShapes.TRACK_ORTHO.get(Direction.SOUTH), CRShapes.MONORAIL_TRACK_ORTHO.get(Direction.SOUTH),
		AllShapes.TRACK_CROSS, CRShapes.MONORAIL_TRACK_CROSS,
		AccessorTrackBlockOutline.getLongOrthoOffset(), MONORAIL_LONG_ORTHO_OFFSET,
		AccessorTrackBlockOutline.getLongOrtho(), MONORAIL_LONG_ORTHO,
		AccessorTrackBlockOutline.getLongCross(), MONORAIL_LONG_CROSS
	);

	public static final Map<VoxelShape, VoxelShape> TRACK_TO_NARROW = ImmutableMap.<VoxelShape, VoxelShape>builder().putAll(Map.of(
		AllShapes.TRACK_ORTHO.get(Direction.EAST), CRShapes.NARROW_TRACK_ORTHO.get(Direction.EAST),
		AllShapes.TRACK_ORTHO.get(Direction.SOUTH), CRShapes.NARROW_TRACK_ORTHO.get(Direction.SOUTH),
		AllShapes.TRACK_CROSS, CRShapes.NARROW_TRACK_CROSS,
		AllShapes.TRACK_DIAG.get(Direction.EAST), CRShapes.NARROW_TRACK_DIAG.get(Direction.EAST),
		AllShapes.TRACK_DIAG.get(Direction.SOUTH), CRShapes.NARROW_TRACK_DIAG.get(Direction.SOUTH))).putAll(Map.of(
		AllShapes.TRACK_CROSS_DIAG, CRShapes.NARROW_TRACK_CROSS_DIAG,
		AccessorTrackBlockOutline.getLongOrthoOffset(), NARROW_LONG_ORTHO_OFFSET,
		AccessorTrackBlockOutline.getLongOrtho(), NARROW_LONG_ORTHO,
		AccessorTrackBlockOutline.getLongCross(), NARROW_LONG_CROSS
	)).build();

	public static VoxelShape convert(Object o, TrackMaterial material) {
		if (o instanceof VoxelShape shape)
			return convert(shape, material);
		throw new IllegalArgumentException("object is not a VoxelShape");
	}

	public static VoxelShape convert(VoxelShape trackShape, TrackMaterial material) {
		if (material == CRTrackMaterials.MONORAIL) {
			if (trackShape.equals(AccessorTrackBlockOutline.getLongOrthoOffset())) {
				return MONORAIL_LONG_ORTHO_OFFSET;
			} else if (trackShape.equals(AccessorTrackBlockOutline.getLongOrtho())) {
				return MONORAIL_LONG_ORTHO;
			} else if (trackShape.equals(AccessorTrackBlockOutline.getLongCross())) {
				return MONORAIL_LONG_CROSS;
			}
            return TRACK_TO_MONORAIL.getOrDefault(trackShape, trackShape);
        }
		if (material.trackType == CRTrackMaterials.CRTrackType.NARROW_GAUGE)
			return TRACK_TO_NARROW.getOrDefault(trackShape, trackShape);
		return trackShape;
	}

	public static boolean skipCustomRendering() {
		return Utils.isDevEnv() && false; // turn on if debugging hitboxes
	}
}
