package com.railwayteam.railways.registry;

import com.railwayteam.railways.content.custom_tracks.monorail.MonorailTrackVoxelShapes;
import com.simibubi.create.AllShapes;
import com.simibubi.create.AllShapes.Builder;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static net.minecraft.core.Direction.*;

public class CRShapes {

    public static final VoxelShaper
        SEMAPHORE = shape(4, 0, 4, 12, 16, 12)
        .forDirectional(NORTH);

    public static final VoxelShaper
        ANDESITE_SWITCH_PROJECTILE = shape(0, 0, 0, 16, 28, 16)
            .forDirectional(NORTH),
        BRASS_SWITCH_PROJECTILE = shape(0, 0, 0, 16, 19, 16)
            .forDirectional(NORTH),
        ANDESITE_SWITCH = shape(0, 0, 0, 16, 5, 16)
                .add(3, 0, 7, 13, 15, 9)
                .add(6, 13, 6, 10, 15, 10)
                .forDirectional(NORTH),
        BRASS_SWITCH = shape(0, 0, 0, 16, 5, 16)
                .forDirectional(NORTH)
    ;

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

    public static VoxelShaper
        HEADSTOCK_LINK_PIN = shape(3, 5, 3, 13, 11, 4)
            .add(5, 5, 4, 11, 11, 7)
            .add(6, 6, 7, 10, 10, 9)
            .add(5, 6, 9, 11, 10, 12)
            .add(4, 5, 12, 12, 11, 13)
            .add(0, 4, 0, 16, 16, 3)
            .forHorizontal(Direction.SOUTH),
        HEADSTOCK_BUFFER = shape(3, 5, 3, 13, 11, 4)
            .add(5, 5, 4, 11, 11, 8)
            .add(6, 6, 8, 10, 10, 12)
            .add(4, 4, 12, 12, 12, 13)
            .add(0, 4, 0, 16, 16, 3)
            .forHorizontal(Direction.SOUTH),
        HEADSTOCK_PLAIN = shape(0, 4, 0, 16, 16, 3)
            .forHorizontal(Direction.SOUTH);

    public static final VoxelShaper LINK_PIN =
        shape(3, 5, 0, 13, 11, 1)
            .add(5, 5, 1, 11, 11, 4)
            .add(6, 6, 4, 10, 10, 6)
            .add(5, 6, 6, 11, 10, 9)
            .add(4, 5, 9, 12, 11, 10)
            .forHorizontal(Direction.SOUTH),
        SMALL_BUFFER = shape(3, 5, 0, 13, 11, 1)
            .add(5, 5, 1, 11, 11, 5)
            .add(6, 6, 5, 10, 10, 9)
            .add(4, 4, 9, 12, 12, 10)
            .forHorizontal(Direction.SOUTH),
        BIG_BUFFER = shape(0, 4, 0, 16, 12, 2)
            .add(4, 4, 2, 12, 12, 8)
            .add(5, 5, 8, 11, 11, 14)
            .add(2, 2, 14, 16, 14, 14)
            .forHorizontal(Direction.SOUTH);

    private static VoxelShape narrowAscending() {
        VoxelShape shape = Block.box(-7, 0, 0, 16 + 7, 4, 4);
        VoxelShape[] shapes = new VoxelShape[6];
        for (int i = 0; i < 6; i++) {
            int off = (i + 1) * 2;
            shapes[i] = Block.box(-7, off, off, 16 + 7, 4 + off, 4 + off);
        }
        return Shapes.or(shape, shapes);
    }

    @SuppressWarnings("ConstantValue")
    public static VoxelShape narrowDiagonal() {
        VoxelShape shape = Block.box(0, 0, 0, 16, 4, 16);
        VoxelShape[] shapes = new VoxelShape[6];
        int off = 0;

        for (int i = 0; i < 3; i++) {
            off = (i + 1) * 2;
            shapes[i * 2] = Block.box(off, 0, off, 16 + off, 4, 16 + off);
            shapes[i * 2 + 1] = Block.box(-off, 0, -off, 16 - off, 4, 16 - off);
        }

        shape = Shapes.or(shape, shapes);

        off = 16;
        shape = Shapes.join(shape, Block.box(off, 0, off, 16 + off, 4, 16 + off), BooleanOp.ONLY_FIRST);
        shape = Shapes.join(shape, Block.box(-off, 0, -off, 16 - off, 4, 16 - off), BooleanOp.ONLY_FIRST);

        off = 4;
        shape = Shapes.or(shape, Block.box(off, 0, off, 16 + off, 4, 16 + off));
        shape = Shapes.or(shape, Block.box(-off, 0, -off, 16 - off, 4, 16 - off));

        return shape.optimize();
    }

    public static final VoxelShaper
        NARROW_TRACK_ORTHO = shape(-7, 0, 0, 16 + 7, 4, 16).forHorizontal(NORTH),
        NARROW_TRACK_ASC = shape(narrowAscending()).forHorizontal(SOUTH),
        NARROW_TRACK_DIAG = shape(narrowDiagonal()).forHorizontal(SOUTH),
        NARROW_TRACK_ORTHO_LONG = shape(-7, 0, 0, 16 + 7, 4, 24).forHorizontal(SOUTH),
        NARROW_TRACK_CROSS_ORTHO_DIAG = shape(NARROW_TRACK_DIAG.get(SOUTH)).add(NARROW_TRACK_ORTHO.get(EAST))
            .forHorizontal(SOUTH),
        NARROW_TRACK_CROSS_DIAG_ORTHO =
            shape(NARROW_TRACK_DIAG.get(SOUTH)).add(NARROW_TRACK_ORTHO.get(SOUTH))
                .forHorizontal(SOUTH);

    public static final VoxelShape
        NARROW_TRACK_CROSS = shape(NARROW_TRACK_ORTHO.get(SOUTH)).add(NARROW_TRACK_ORTHO.get(EAST))
            .build(),
        NARROW_TRACK_CROSS_DIAG = shape(NARROW_TRACK_DIAG.get(SOUTH)).add(NARROW_TRACK_DIAG.get(EAST))
            .build()
        ;

    public static final VoxelShape INVISIBLE_BOGEY = shape(0, 7, 0, 16, 16, 16).build();


    public static final VoxelShape CONDUCTOR_WHISTLE_FLAG = shape(7, 0, 7, 9, 14, 9)
        .add(9, 8, 8 - 0.01, 16, 14, 8 + 0.01)
        .build();

    public static final VoxelShape
        COAL_STACK = shape(4, 0, 4, 12, 16, 12).build(),
        DIESEL_STACK = shape(0, 0, 0, 16, 4, 16).build(),
        OIL_STACK = shape(1, 8, 1, 15, 16, 15)
            .add(2, 0, 2, 14, 8, 14)
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
            .forHorizontalAxis(),
        LONG_STACK = shape(1, 0, 3, 15, 4, 13)
        .forHorizontalAxis();

    public static final VoxelShape
        BLOCK = Shapes.block(),
        BOTTOM_SLAB = shape(0, 0, 0, 16, 8, 16).build(),
        TOP_SLAB = shape(0, 8, 0, 16, 16, 16).build();

    public static final VoxelShape HANDCAR = shape(AllShapes.SEAT_COLLISION)
        .add(-16, 0, 0, 16, 4, 16).build();

    private static Builder shape(VoxelShape shape) {
        return new Builder(shape);
    }

    public static Builder shape(double x1, double y1, double z1, double x2, double y2, double z2) {
        return shape(cuboid(x1, y1, z1, x2, y2, z2));
    }

    private static VoxelShape cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Block.box(x1, y1, z1, x2, y2, z2);
    }
}
