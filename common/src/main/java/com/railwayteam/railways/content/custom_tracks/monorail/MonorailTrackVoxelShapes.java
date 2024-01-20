package com.railwayteam.railways.content.custom_tracks.monorail;

import com.simibubi.create.content.trains.track.TrackVoxelShapes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MonorailTrackVoxelShapes extends TrackVoxelShapes {

    public static VoxelShape orthogonal() {
        return Block.box(0, 0, 0, 16, 16, 16);
    }

    public static VoxelShape longOrthogonalX() {
        return Block.box(-3.3, 0,0 , 19.3, 16, 16);
    }

    public static VoxelShape longOrthogonalZ() {
        return Block.box(0, 0, -3.3, 16, 16, 19.3);
    }

    public static VoxelShape longOrthogonalZOffset() {
        return Block.box(0, 0, 0, 16, 16, 24);
    }

    public static VoxelShape ascending() {
        int offset = -9;
        int verticalOffset = 1;
        VoxelShape shape = Block.box(0, verticalOffset, offset, 16, 16+verticalOffset, 16+offset);
        VoxelShape[] shapes = new VoxelShape[6];
        for (int i = 0; i < 6; i++) {
            int off = (i + 1) * 2;
            shapes[i] = Block.box(0, off + verticalOffset, off+offset, 16, 16 + off + verticalOffset, 16 + off + offset);
        }
        return Shapes.or(shape, shapes);
    }

    public static VoxelShape diagonal() {
        VoxelShape shape = Block.box(0, 0, 0, 16, 16, 16);
        final int half_count = 2;
        VoxelShape[] shapes = new VoxelShape[half_count*2];
        int off = 0;

        for (int i = 0; i < half_count; i++) {
            off = (i + 1) * 2;
            shapes[i * 2] = Block.box(off, 0, off, 16 + off, 16, 16 + off);
            shapes[i * 2 + 1] = Block.box(-off, 0, -off, 16 - off, 16, 16 - off);
        }

        shape = Shapes.or(shape, shapes);

        off = 5 * 2;
        shape = Shapes.join(shape, Block.box(off, 0, off, 16 + off, 16, 16 + off), BooleanOp.ONLY_FIRST);
        shape = Shapes.join(shape, Block.box(-off, 0, -off, 16 - off, 16, 16 - off), BooleanOp.ONLY_FIRST);

        if (false) return shape.optimize();

        off = 0;
        shape = Shapes.or(shape, Block.box(off, 0, off, 16 + off, 16, 16 + off));
        shape = Shapes.or(shape, Block.box(-off, 0, -off, 16 - off, 16, 16 - off));

        return shape.optimize();
    }

}
