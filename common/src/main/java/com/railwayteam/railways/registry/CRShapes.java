/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
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

package com.railwayteam.railways.registry;

import com.railwayteam.railways.content.custom_tracks.monorail.MonorailTrackVoxelShapes;
import com.simibubi.create.AllShapes;
import com.simibubi.create.AllShapes.Builder;
import net.createmod.catnip.math.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import static net.minecraft.core.Direction.EAST;
import static net.minecraft.core.Direction.NORTH;
import static net.minecraft.core.Direction.SOUTH;

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
            .forHorizontal(Direction.SOUTH),
        HEADSTOCK_KNUCKLE = shape(3, 5, 3, 13, 11, 4)
            .add(4, 5, 4, 12, 11, 7)
            .add(6, 6, 7, 10, 10, 9)
            .add(4, 5, 9, 12, 11, 12)
            .add(4, 5, 12, 6, 11, 15)
            .add(5, 5.5, 14, 9, 10.5, 16)
            .add(0, 4, 0, 16, 16, 3)
            .forHorizontal(Direction.SOUTH),
        HEADSTOCK_SCREWLINK = shape(4, 6, 3, 12, 10, 4)
            .add(5, 6, 4, 11, 10, 6)
            .add(7, 7, 6, 9, 9, 8)
            .add(7, 6.5, 7, 9, 9.5, 11)
            .add(0, 4, 0, 16, 16, 3)
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
            .forHorizontal(Direction.SOUTH),
        KNUCKLE = shape(3, 5, 0, 13, 11, 1)
            .add(4, 5, 1, 12, 11, 4)
            .add(6, 6, 4, 10, 10, 6)
            .add(4, 5, 6, 12, 11, 9)
            .add(4, 5, 9, 6, 11, 12)
            .add(5, 5.5, 11, 9, 10.5, 13)
            .forHorizontal(Direction.SOUTH),
        SCREWLINK = shape(4, 6, 0, 12, 10, 1)
            .add(5, 6, 1, 11, 10, 3)
            .add(7, 7, 3, 9, 9, 5)
            .add(7, 6.5, 4, 9, 9.5, 8)
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
        int off;

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

    public static VoxelShape boiler(double offset) {
        VoxelShape shape = Shapes.empty();

        for (double i = 0; i < 10; i++) {
            shape = Shapes.or(shape, Block.box(0, -8 + offset + i, 1 - i, 16, 24 + offset - i, 15 + i));
        }

        return shape.optimize();
    }

    public static final VoxelShaper BOILER = shape(boiler(0)).forHorizontal(EAST);
    public static final VoxelShaper BOILER_RAISED = shape(boiler(8)).forHorizontal(EAST);

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
        COAL_STACK = shape(4, 0, 4, 12, 16, 12)
            .add(3, 12, 3, 13, 15, 13)
            .build(),
        OIL_STACK = shape(2, 0, 2, 14, 16, 14)
            .add(0, 11, 0, 16, 14, 16)
            .build(),
        WOOD_STACK = shape(4, 0, 4, 12, 4, 12)
            .add(2 ,4, 2, 14, 9, 14)
            .add(0 ,9, 0, 16, 14, 16)
            .add(2 ,14, 2, 14, 16, 14)
            .build();
    public static final VoxelShaper
        CABOOSE_STACK = shape(4, 0, 4, 12, 2, 12)
            .add(5, 2, 5, 11, 3, 11)
            .add(6, 3, 6, 10, 10, 10)
            .add(5, 10, 4, 11, 13, 12)
            .forHorizontalAxis(),
        LONG_STACK = shape(1, 0, 3, 15, 5, 13)
            .add(0, 2, 2, 16, 4, 14)
            .forHorizontal(Direction.WEST),
        STREAMLINED_STACK = shape(1, 0, 3, 15, 2, 13)
            .forHorizontal(Direction.EAST),
        DIESEL_STACK = shape(0, 0, 0, 16, 4, 16)
            .forDirectional(Direction.UP);

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
