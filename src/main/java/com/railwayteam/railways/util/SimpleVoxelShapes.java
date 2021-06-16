package com.railwayteam.railways.util;

import com.mojang.datafixers.util.Function6;
import net.minecraft.block.Block;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;

public class SimpleVoxelShapes {
    public final double posX;
    public final double posY;
    public final double posZ;
    public final double sizeX;
    public final double sizeY;
    public final double sizeZ;

    public SimpleVoxelShapes(double posX, double posY, double posZ, double sizeX, double sizeY, double sizeZ) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }

    public VoxelShape north() {
        return d(Block::makeCuboidShape);
    }

    public VoxelShape east() {
        return d(SimpleVoxelShapes::northToEast);
    }

    public VoxelShape south() {
        return d(SimpleVoxelShapes::northToSouth);
    }

    public VoxelShape west() {
        return d(SimpleVoxelShapes::northToWest);
    }

    public VoxelShape toDir(Direction dir) {
        switch(dir) {
            case WEST: return west();
            case SOUTH: return south();
            case EAST: return east();
            default: return north();
        }
    }

    public VoxelShape d(Function6<Double, Double, Double, Double, Double, Double, VoxelShape> f) {
        return f.apply(posX, posY, posZ, sizeX, sizeY, sizeZ);
    }

    public static VoxelShape northToEast(double posX, double posY, double posZ, double sizeX, double sizeY, double sizeZ) {
        VoxelShape shape;
        return null;
    }

    public static VoxelShape northToSouth(double posX, double posY, double posZ, double sizeX, double sizeY, double sizeZ) {
        return Block.makeCuboidShape(posX, posY, posZ, sizeX * -1, sizeY, sizeZ * -1) ;
    }

    public static VoxelShape northToWest(double posX, double posY, double posZ, double sizeX, double sizeY, double sizeZ) {
        return null;
    }
}
