package net.minecraft.world;

import com.google.common.collect.Streams;
import java.util.Collections;
import java.util.Set;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.CubeCoordinateIterator;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.border.WorldBorder;

public interface ICollisionReader extends IBlockReader {
   WorldBorder getWorldBorder();

   @Nullable
   IBlockReader getBlockReader(int chunkX, int chunkZ);

   default boolean checkNoEntityCollision(@Nullable Entity entityIn, VoxelShape shape) {
      return true;
   }

   default boolean func_226663_a_(BlockState p_226663_1_, BlockPos p_226663_2_, ISelectionContext p_226663_3_) {
      VoxelShape voxelshape = p_226663_1_.getCollisionShape(this, p_226663_2_, p_226663_3_);
      return voxelshape.isEmpty() || this.checkNoEntityCollision((Entity)null, voxelshape.withOffset((double)p_226663_2_.getX(), (double)p_226663_2_.getY(), (double)p_226663_2_.getZ()));
   }

   default boolean checkNoEntityCollision(Entity p_226668_1_) {
      return this.checkNoEntityCollision(p_226668_1_, VoxelShapes.create(p_226668_1_.getBoundingBox()));
   }

   default boolean hasNoCollisions(AxisAlignedBB p_226664_1_) {
      return this.hasNoCollisions((Entity)null, p_226664_1_, Collections.emptySet());
   }

   default boolean hasNoCollisions(Entity p_226669_1_) {
      return this.hasNoCollisions(p_226669_1_, p_226669_1_.getBoundingBox(), Collections.emptySet());
   }

   default boolean hasNoCollisions(Entity p_226665_1_, AxisAlignedBB p_226665_2_) {
      return this.hasNoCollisions(p_226665_1_, p_226665_2_, Collections.emptySet());
   }

   default boolean hasNoCollisions(@Nullable Entity p_226662_1_, AxisAlignedBB p_226662_2_, Set<Entity> p_226662_3_) {
      return this.getCollisionShapes(p_226662_1_, p_226662_2_, p_226662_3_).allMatch(VoxelShape::isEmpty);
   }

   default Stream<VoxelShape> getEmptyCollisionShapes(@Nullable Entity entityIn, AxisAlignedBB aabb, Set<Entity> entitiesToIgnore) {
      return Stream.empty();
   }

   default Stream<VoxelShape> getCollisionShapes(@Nullable Entity p_226667_1_, AxisAlignedBB p_226667_2_, Set<Entity> p_226667_3_) {
      return Streams.concat(this.getCollisionShapes(p_226667_1_, p_226667_2_), this.getEmptyCollisionShapes(p_226667_1_, p_226667_2_, p_226667_3_));
   }

   default Stream<VoxelShape> getCollisionShapes(@Nullable final Entity p_226666_1_, AxisAlignedBB p_226666_2_) {
      int i = MathHelper.floor(p_226666_2_.minX - 1.0E-7D) - 1;
      int j = MathHelper.floor(p_226666_2_.maxX + 1.0E-7D) + 1;
      int k = MathHelper.floor(p_226666_2_.minY - 1.0E-7D) - 1;
      int l = MathHelper.floor(p_226666_2_.maxY + 1.0E-7D) + 1;
      int i1 = MathHelper.floor(p_226666_2_.minZ - 1.0E-7D) - 1;
      int j1 = MathHelper.floor(p_226666_2_.maxZ + 1.0E-7D) + 1;
      final ISelectionContext iselectioncontext = p_226666_1_ == null ? ISelectionContext.dummy() : ISelectionContext.forEntity(p_226666_1_);
      final CubeCoordinateIterator cubecoordinateiterator = new CubeCoordinateIterator(i, k, i1, j, l, j1);
      final BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
      final VoxelShape voxelshape = VoxelShapes.create(p_226666_2_);
      return StreamSupport.stream(new AbstractSpliterator<VoxelShape>(Long.MAX_VALUE, 1280) {
         boolean field_226670_a_ = p_226666_1_ == null;

         public boolean tryAdvance(Consumer<? super VoxelShape> p_tryAdvance_1_) {
            if (!this.field_226670_a_) {
               this.field_226670_a_ = true;
               VoxelShape voxelshape1 = ICollisionReader.this.getWorldBorder().getShape();
               boolean flag = VoxelShapes.compare(voxelshape1, VoxelShapes.create(p_226666_1_.getBoundingBox().shrink(1.0E-7D)), IBooleanFunction.AND);
               boolean flag1 = VoxelShapes.compare(voxelshape1, VoxelShapes.create(p_226666_1_.getBoundingBox().grow(1.0E-7D)), IBooleanFunction.AND);
               if (!flag && flag1) {
                  p_tryAdvance_1_.accept(voxelshape1);
                  return true;
               }
            }

            VoxelShape voxelshape3;
            while(true) {
               if (!cubecoordinateiterator.hasNext()) {
                  return false;
               }

               int j2 = cubecoordinateiterator.getX();
               int k2 = cubecoordinateiterator.getY();
               int l2 = cubecoordinateiterator.getZ();
               int k1 = cubecoordinateiterator.numBoundariesTouched();
               if (k1 != 3) {
                  int l1 = j2 >> 4;
                  int i2 = l2 >> 4;
                  IBlockReader iblockreader = ICollisionReader.this.getBlockReader(l1, i2);
                  if (iblockreader != null) {
                     blockpos$mutable.setPos(j2, k2, l2);
                     BlockState blockstate = iblockreader.getBlockState(blockpos$mutable);
                     if ((k1 != 1 || blockstate.isCollisionShapeLargerThanFullBlock()) && (k1 != 2 || blockstate.getBlock() == Blocks.MOVING_PISTON)) {
                        VoxelShape voxelshape2 = blockstate.getCollisionShape(ICollisionReader.this, blockpos$mutable, iselectioncontext);
                        voxelshape3 = voxelshape2.withOffset((double)j2, (double)k2, (double)l2);
                        if (VoxelShapes.compare(voxelshape, voxelshape3, IBooleanFunction.AND)) {
                           break;
                        }
                     }
                  }
               }
            }

            p_tryAdvance_1_.accept(voxelshape3);
            return true;
         }
      }, false);
   }
}