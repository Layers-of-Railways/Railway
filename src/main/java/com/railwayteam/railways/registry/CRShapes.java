package com.railwayteam.railways.registry;

import com.railwayteam.railways.content.custom_tracks.monorail.MonorailTrackVoxelShapes;
import com.simibubi.create.content.logistics.trains.track.TrackVoxelShapes;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.simibubi.create.AllShapes.Builder;

import static net.minecraft.core.Direction.UP;
import static net.minecraft.core.Direction.DOWN;
import static net.minecraft.core.Direction.NORTH;
import static net.minecraft.core.Direction.SOUTH;
import static net.minecraft.core.Direction.EAST;
import static net.minecraft.core.Direction.WEST;

public class CRShapes {

    public static final VoxelShaper
        SEMAPHORE = shape(4, 0, 4, 12, 16, 12)
        .forDirectional(NORTH);

    public static final VoxelShape
        MONORAIL_COLLISION = shape(0, 0, 0, 16, 15, 16).build(); //This HAS to be less than a full block, or else monorails try to go diagonal


    public static final VoxelShaper
        MONORAIL_TRACK_ORTHO = shape(MonorailTrackVoxelShapes.orthogonal()).forHorizontal(NORTH),
        MONORAIL_TRACK_ASC = shape(MonorailTrackVoxelShapes.ascending()).forHorizontal(SOUTH),
        MONORAIL_TRACK_DIAG = shape(MonorailTrackVoxelShapes.diagonal()).forHorizontal(SOUTH),
        MONORAIL_TRACK_ORTHO_LONG = shape(MonorailTrackVoxelShapes.longOrthogonalZOffset()).forHorizontal(SOUTH),
        MONORAIL_TRACK_CROSS_ORTHO_DIAG = shape(MONORAIL_TRACK_DIAG.get(SOUTH)).add(MONORAIL_TRACK_ORTHO.get(EAST))
            .forHorizontal(SOUTH),
        MONORAIL_TRACK_CROSS_DIAG_ORTHO =
            shape(MONORAIL_TRACK_DIAG.get(SOUTH)).add(MONORAIL_TRACK_ORTHO.get(SOUTH))
                .forHorizontal(SOUTH);

    public static final VoxelShape
        MONORAIL_TRACK_CROSS = shape(MONORAIL_TRACK_ORTHO.get(SOUTH)).add(MONORAIL_TRACK_ORTHO.get(EAST)).build(),
        MONORAIL_TRACK_CROSS_DIAG = shape(MONORAIL_TRACK_DIAG.get(SOUTH)).add(MONORAIL_TRACK_DIAG.get(EAST)).build(),
        MONORAIL_TRACK_FALLBACK = shape(0, 0, 0, 16, 16, 16).build();


    public static final VoxelShape CONDUCTOR_WHISTLE_FLAG = shape(7, 0, 7, 9, 14, 9)
        .add(9, 8, 8 - 0.01, 16, 14, 8 + 0.01)
        .build();

    public static final VoxelShape
        COAL_STACK = shape(10, 0, 6, 11, 12, 11)
            .add(6, 0, 5, 11, 12, 6)
            .add(5, 0, 10, 10, 12, 11)
            .add(5, 0, 5, 6, 12, 10)
            .add(4, 12, 4, 10, 16, 6)
            .add(6, 12, 10, 12, 16, 12)
            .add(10, 12, 4, 12, 16, 10)
            .add(4, 12, 6, 6, 16, 12)
            .build(),
        DIESEL_STACK = shape(0, 0, 0, 16, 4, 16).build(),
        OIL_STACK = shape(2, 0, 1, 15, 8, 2)
            .add(1, 0, 14, 14, 8, 15)
            .add(1, 0, 1, 2, 8, 14)
            .add(14, 0, 2, 15, 8, 15)
            .add(0, 8, 2, 2, 10, 16)
            .add(14, 8, 0, 16, 10, 14)
            .add(0, 8, 0, 14, 10, 2)
            .add(2, 8, 14, 16, 10, 16)
            .build(),
        STREAMLINED_STACK = shape(0, 0, 2, 2, 4, 16)
            .add(14, 0, 0, 16, 4, 14)
            .add(0, 0, 0, 14, 4, 2)
            .add(2, 0, 14, 16, 4, 16)
            .build(),
        WOOD_STACK = shape(4, 0, 4, 12, 14, 12)
            .add(0, 6, 0, 16, 14, 16)
            .build();
    public static final VoxelShaper
        CABOOSE_STACK = shape(7, 0, 7, 9, 12, 9)
            .add(5, 8, 6, 11, 12, 10)
            .forHorizontalAxis();

    private static Builder shape(VoxelShape shape) {
        return new Builder(shape);
    }

    private static Builder shape(double x1, double y1, double z1, double x2, double y2, double z2) {
        return shape(cuboid(x1, y1, z1, x2, y2, z2));
    }

    private static VoxelShape cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Block.box(x1, y1, z1, x2, y2, z2);
    }
}
