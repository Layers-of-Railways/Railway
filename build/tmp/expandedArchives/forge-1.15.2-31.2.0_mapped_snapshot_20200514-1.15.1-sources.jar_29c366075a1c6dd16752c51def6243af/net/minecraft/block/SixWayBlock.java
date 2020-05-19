package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class SixWayBlock extends Block {
   private static final Direction[] FACING_VALUES = Direction.values();
   public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
   public static final BooleanProperty EAST = BlockStateProperties.EAST;
   public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
   public static final BooleanProperty WEST = BlockStateProperties.WEST;
   public static final BooleanProperty UP = BlockStateProperties.UP;
   public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
   public static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = Util.make(Maps.newEnumMap(Direction.class), (p_203421_0_) -> {
      p_203421_0_.put(Direction.NORTH, NORTH);
      p_203421_0_.put(Direction.EAST, EAST);
      p_203421_0_.put(Direction.SOUTH, SOUTH);
      p_203421_0_.put(Direction.WEST, WEST);
      p_203421_0_.put(Direction.UP, UP);
      p_203421_0_.put(Direction.DOWN, DOWN);
   });
   protected final VoxelShape[] shapes;

   protected SixWayBlock(float apothem, Block.Properties properties) {
      super(properties);
      this.shapes = this.makeShapes(apothem);
   }

   private VoxelShape[] makeShapes(float apothem) {
      float f = 0.5F - apothem;
      float f1 = 0.5F + apothem;
      VoxelShape voxelshape = Block.makeCuboidShape((double)(f * 16.0F), (double)(f * 16.0F), (double)(f * 16.0F), (double)(f1 * 16.0F), (double)(f1 * 16.0F), (double)(f1 * 16.0F));
      VoxelShape[] avoxelshape = new VoxelShape[FACING_VALUES.length];

      for(int i = 0; i < FACING_VALUES.length; ++i) {
         Direction direction = FACING_VALUES[i];
         avoxelshape[i] = VoxelShapes.create(0.5D + Math.min((double)(-apothem), (double)direction.getXOffset() * 0.5D), 0.5D + Math.min((double)(-apothem), (double)direction.getYOffset() * 0.5D), 0.5D + Math.min((double)(-apothem), (double)direction.getZOffset() * 0.5D), 0.5D + Math.max((double)apothem, (double)direction.getXOffset() * 0.5D), 0.5D + Math.max((double)apothem, (double)direction.getYOffset() * 0.5D), 0.5D + Math.max((double)apothem, (double)direction.getZOffset() * 0.5D));
      }

      VoxelShape[] avoxelshape1 = new VoxelShape[64];

      for(int k = 0; k < 64; ++k) {
         VoxelShape voxelshape1 = voxelshape;

         for(int j = 0; j < FACING_VALUES.length; ++j) {
            if ((k & 1 << j) != 0) {
               voxelshape1 = VoxelShapes.or(voxelshape1, avoxelshape[j]);
            }
         }

         avoxelshape1[k] = voxelshape1;
      }

      return avoxelshape1;
   }

   public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
      return false;
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return this.shapes[this.getShapeIndex(state)];
   }

   protected int getShapeIndex(BlockState state) {
      int i = 0;

      for(int j = 0; j < FACING_VALUES.length; ++j) {
         if (state.get(FACING_TO_PROPERTY_MAP.get(FACING_VALUES[j]))) {
            i |= 1 << j;
         }
      }

      return i;
   }
}