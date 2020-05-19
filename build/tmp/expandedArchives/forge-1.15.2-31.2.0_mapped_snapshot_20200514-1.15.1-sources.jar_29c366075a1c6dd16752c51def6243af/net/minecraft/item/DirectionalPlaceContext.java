package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DirectionalPlaceContext extends BlockItemUseContext {
   private final Direction lookDirection;

   public DirectionalPlaceContext(World worldIn, BlockPos pos, Direction lookDirectionIn, ItemStack stackIn, Direction against) {
      super(worldIn, (PlayerEntity)null, Hand.MAIN_HAND, stackIn, new BlockRayTraceResult(new Vec3d((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D), against, pos, false));
      this.lookDirection = lookDirectionIn;
   }

   public BlockPos getPos() {
      return this.rayTraceResult.getPos();
   }

   public boolean canPlace() {
      return this.world.getBlockState(this.rayTraceResult.getPos()).isReplaceable(this);
   }

   public boolean replacingClickedOnBlock() {
      return this.canPlace();
   }

   public Direction getNearestLookingDirection() {
      return Direction.DOWN;
   }

   public Direction[] getNearestLookingDirections() {
      switch(this.lookDirection) {
      case DOWN:
      default:
         return new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP};
      case UP:
         return new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
      case NORTH:
         return new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.SOUTH};
      case SOUTH:
         return new Direction[]{Direction.DOWN, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.NORTH};
      case WEST:
         return new Direction[]{Direction.DOWN, Direction.WEST, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.EAST};
      case EAST:
         return new Direction[]{Direction.DOWN, Direction.EAST, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.WEST};
      }
   }

   public Direction getPlacementHorizontalFacing() {
      return this.lookDirection.getAxis() == Direction.Axis.Y ? Direction.NORTH : this.lookDirection;
   }

   public boolean func_225518_g_() {
      return false;
   }

   public float getPlacementYaw() {
      return (float)(this.lookDirection.getHorizontalIndex() * 90);
   }
}