package net.minecraft.world.gen.feature.structure;

import java.util.Random;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;

public abstract class ScatteredStructurePiece extends StructurePiece {
   protected final int width;
   protected final int height;
   protected final int depth;
   protected int hPos = -1;

   protected ScatteredStructurePiece(IStructurePieceType structurePieceTypeIn, Random rand, int xIn, int yIn, int zIn, int widthIn, int heightIn, int depthIn) {
      super(structurePieceTypeIn, 0);
      this.width = widthIn;
      this.height = heightIn;
      this.depth = depthIn;
      this.setCoordBaseMode(Direction.Plane.HORIZONTAL.random(rand));
      if (this.getCoordBaseMode().getAxis() == Direction.Axis.Z) {
         this.boundingBox = new MutableBoundingBox(xIn, yIn, zIn, xIn + widthIn - 1, yIn + heightIn - 1, zIn + depthIn - 1);
      } else {
         this.boundingBox = new MutableBoundingBox(xIn, yIn, zIn, xIn + depthIn - 1, yIn + heightIn - 1, zIn + widthIn - 1);
      }

   }

   protected ScatteredStructurePiece(IStructurePieceType structurePieceTypeIn, CompoundNBT nbt) {
      super(structurePieceTypeIn, nbt);
      this.width = nbt.getInt("Width");
      this.height = nbt.getInt("Height");
      this.depth = nbt.getInt("Depth");
      this.hPos = nbt.getInt("HPos");
   }

   /**
    * (abstract) Helper method to read subclass data from NBT
    */
   protected void readAdditional(CompoundNBT tagCompound) {
      tagCompound.putInt("Width", this.width);
      tagCompound.putInt("Height", this.height);
      tagCompound.putInt("Depth", this.depth);
      tagCompound.putInt("HPos", this.hPos);
   }

   protected boolean isInsideBounds(IWorld worldIn, MutableBoundingBox boundsIn, int heightIn) {
      if (this.hPos >= 0) {
         return true;
      } else {
         int i = 0;
         int j = 0;
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

         for(int k = this.boundingBox.minZ; k <= this.boundingBox.maxZ; ++k) {
            for(int l = this.boundingBox.minX; l <= this.boundingBox.maxX; ++l) {
               blockpos$mutable.setPos(l, 64, k);
               if (boundsIn.isVecInside(blockpos$mutable)) {
                  i += worldIn.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockpos$mutable).getY();
                  ++j;
               }
            }
         }

         if (j == 0) {
            return false;
         } else {
            this.hPos = i / j;
            this.boundingBox.offset(0, this.hPos - this.boundingBox.minY + heightIn, 0);
            return true;
         }
      }
   }
}