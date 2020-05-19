package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.GrindstoneContainer;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class GrindstoneBlock extends HorizontalFaceBlock {
   public static final VoxelShape field_220238_a = Block.makeCuboidShape(2.0D, 0.0D, 6.0D, 4.0D, 7.0D, 10.0D);
   public static final VoxelShape field_220239_b = Block.makeCuboidShape(12.0D, 0.0D, 6.0D, 14.0D, 7.0D, 10.0D);
   public static final VoxelShape field_220240_c = Block.makeCuboidShape(2.0D, 7.0D, 5.0D, 4.0D, 13.0D, 11.0D);
   public static final VoxelShape field_220241_d = Block.makeCuboidShape(12.0D, 7.0D, 5.0D, 14.0D, 13.0D, 11.0D);
   public static final VoxelShape field_220242_e = VoxelShapes.or(field_220238_a, field_220240_c);
   public static final VoxelShape field_220243_f = VoxelShapes.or(field_220239_b, field_220241_d);
   public static final VoxelShape field_220244_g = VoxelShapes.or(field_220242_e, field_220243_f);
   public static final VoxelShape field_220245_h = VoxelShapes.or(field_220244_g, Block.makeCuboidShape(4.0D, 4.0D, 2.0D, 12.0D, 16.0D, 14.0D));
   public static final VoxelShape field_220246_i = Block.makeCuboidShape(6.0D, 0.0D, 2.0D, 10.0D, 7.0D, 4.0D);
   public static final VoxelShape field_220247_j = Block.makeCuboidShape(6.0D, 0.0D, 12.0D, 10.0D, 7.0D, 14.0D);
   public static final VoxelShape field_220248_k = Block.makeCuboidShape(5.0D, 7.0D, 2.0D, 11.0D, 13.0D, 4.0D);
   public static final VoxelShape field_220249_w = Block.makeCuboidShape(5.0D, 7.0D, 12.0D, 11.0D, 13.0D, 14.0D);
   public static final VoxelShape field_220250_x = VoxelShapes.or(field_220246_i, field_220248_k);
   public static final VoxelShape field_220251_y = VoxelShapes.or(field_220247_j, field_220249_w);
   public static final VoxelShape field_220252_z = VoxelShapes.or(field_220250_x, field_220251_y);
   public static final VoxelShape field_220213_A = VoxelShapes.or(field_220252_z, Block.makeCuboidShape(2.0D, 4.0D, 4.0D, 14.0D, 16.0D, 12.0D));
   public static final VoxelShape field_220214_B = Block.makeCuboidShape(2.0D, 6.0D, 0.0D, 4.0D, 10.0D, 7.0D);
   public static final VoxelShape field_220215_D = Block.makeCuboidShape(12.0D, 6.0D, 0.0D, 14.0D, 10.0D, 7.0D);
   public static final VoxelShape field_220216_E = Block.makeCuboidShape(2.0D, 5.0D, 7.0D, 4.0D, 11.0D, 13.0D);
   public static final VoxelShape field_220217_F = Block.makeCuboidShape(12.0D, 5.0D, 7.0D, 14.0D, 11.0D, 13.0D);
   public static final VoxelShape field_220218_G = VoxelShapes.or(field_220214_B, field_220216_E);
   public static final VoxelShape field_220219_H = VoxelShapes.or(field_220215_D, field_220217_F);
   public static final VoxelShape field_220220_I = VoxelShapes.or(field_220218_G, field_220219_H);
   public static final VoxelShape SHAPE_WALL_SOUTH = VoxelShapes.or(field_220220_I, Block.makeCuboidShape(4.0D, 2.0D, 4.0D, 12.0D, 14.0D, 16.0D));
   public static final VoxelShape field_220222_K = Block.makeCuboidShape(2.0D, 6.0D, 7.0D, 4.0D, 10.0D, 16.0D);
   public static final VoxelShape field_220223_L = Block.makeCuboidShape(12.0D, 6.0D, 7.0D, 14.0D, 10.0D, 16.0D);
   public static final VoxelShape field_220224_M = Block.makeCuboidShape(2.0D, 5.0D, 3.0D, 4.0D, 11.0D, 9.0D);
   public static final VoxelShape field_220225_N = Block.makeCuboidShape(12.0D, 5.0D, 3.0D, 14.0D, 11.0D, 9.0D);
   public static final VoxelShape field_220226_O = VoxelShapes.or(field_220222_K, field_220224_M);
   public static final VoxelShape field_220227_P = VoxelShapes.or(field_220223_L, field_220225_N);
   public static final VoxelShape field_220228_Q = VoxelShapes.or(field_220226_O, field_220227_P);
   public static final VoxelShape SHAPE_WALL_NORTH = VoxelShapes.or(field_220228_Q, Block.makeCuboidShape(4.0D, 2.0D, 0.0D, 12.0D, 14.0D, 12.0D));
   public static final VoxelShape field_220230_S = Block.makeCuboidShape(7.0D, 6.0D, 2.0D, 16.0D, 10.0D, 4.0D);
   public static final VoxelShape field_220231_T = Block.makeCuboidShape(7.0D, 6.0D, 12.0D, 16.0D, 10.0D, 14.0D);
   public static final VoxelShape field_220232_U = Block.makeCuboidShape(3.0D, 5.0D, 2.0D, 9.0D, 11.0D, 4.0D);
   public static final VoxelShape field_220233_V = Block.makeCuboidShape(3.0D, 5.0D, 12.0D, 9.0D, 11.0D, 14.0D);
   public static final VoxelShape field_220234_W = VoxelShapes.or(field_220230_S, field_220232_U);
   public static final VoxelShape field_220235_X = VoxelShapes.or(field_220231_T, field_220233_V);
   public static final VoxelShape field_220236_Y = VoxelShapes.or(field_220234_W, field_220235_X);
   public static final VoxelShape field_220237_Z = VoxelShapes.or(field_220236_Y, Block.makeCuboidShape(0.0D, 2.0D, 4.0D, 12.0D, 14.0D, 12.0D));
   public static final VoxelShape field_220188_aa = Block.makeCuboidShape(0.0D, 6.0D, 2.0D, 9.0D, 10.0D, 4.0D);
   public static final VoxelShape field_220189_ab = Block.makeCuboidShape(0.0D, 6.0D, 12.0D, 9.0D, 10.0D, 14.0D);
   public static final VoxelShape field_220190_ac = Block.makeCuboidShape(7.0D, 5.0D, 2.0D, 13.0D, 11.0D, 4.0D);
   public static final VoxelShape field_220191_ad = Block.makeCuboidShape(7.0D, 5.0D, 12.0D, 13.0D, 11.0D, 14.0D);
   public static final VoxelShape field_220192_ae = VoxelShapes.or(field_220188_aa, field_220190_ac);
   public static final VoxelShape field_220193_af = VoxelShapes.or(field_220189_ab, field_220191_ad);
   public static final VoxelShape field_220194_ag = VoxelShapes.or(field_220192_ae, field_220193_af);
   public static final VoxelShape field_220195_ah = VoxelShapes.or(field_220194_ag, Block.makeCuboidShape(4.0D, 2.0D, 4.0D, 16.0D, 14.0D, 12.0D));
   public static final VoxelShape field_220196_ai = Block.makeCuboidShape(2.0D, 9.0D, 6.0D, 4.0D, 16.0D, 10.0D);
   public static final VoxelShape field_220197_aj = Block.makeCuboidShape(12.0D, 9.0D, 6.0D, 14.0D, 16.0D, 10.0D);
   public static final VoxelShape field_220198_ak = Block.makeCuboidShape(2.0D, 3.0D, 5.0D, 4.0D, 9.0D, 11.0D);
   public static final VoxelShape field_220199_al = Block.makeCuboidShape(12.0D, 3.0D, 5.0D, 14.0D, 9.0D, 11.0D);
   public static final VoxelShape field_220200_am = VoxelShapes.or(field_220196_ai, field_220198_ak);
   public static final VoxelShape field_220201_an = VoxelShapes.or(field_220197_aj, field_220199_al);
   public static final VoxelShape field_220202_ao = VoxelShapes.or(field_220200_am, field_220201_an);
   public static final VoxelShape SHAPE_CEILING_NORTH_OR_SOUTH = VoxelShapes.or(field_220202_ao, Block.makeCuboidShape(4.0D, 0.0D, 2.0D, 12.0D, 12.0D, 14.0D));
   public static final VoxelShape field_220204_aq = Block.makeCuboidShape(6.0D, 9.0D, 2.0D, 10.0D, 16.0D, 4.0D);
   public static final VoxelShape field_220205_ar = Block.makeCuboidShape(6.0D, 9.0D, 12.0D, 10.0D, 16.0D, 14.0D);
   public static final VoxelShape field_220206_as = Block.makeCuboidShape(5.0D, 3.0D, 2.0D, 11.0D, 9.0D, 4.0D);
   public static final VoxelShape field_220207_at = Block.makeCuboidShape(5.0D, 3.0D, 12.0D, 11.0D, 9.0D, 14.0D);
   public static final VoxelShape field_220208_au = VoxelShapes.or(field_220204_aq, field_220206_as);
   public static final VoxelShape field_220209_av = VoxelShapes.or(field_220205_ar, field_220207_at);
   public static final VoxelShape field_220210_aw = VoxelShapes.or(field_220208_au, field_220209_av);
   public static final VoxelShape SHAPE_CEILING_EAST_OR_WEST = VoxelShapes.or(field_220210_aw, Block.makeCuboidShape(2.0D, 0.0D, 4.0D, 14.0D, 12.0D, 12.0D));
   private static final TranslationTextComponent CONTAINER_NAME = new TranslationTextComponent("container.grindstone_title");

   protected GrindstoneBlock(Block.Properties propertiesIn) {
      super(propertiesIn);
      this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, Direction.NORTH).with(FACE, AttachFace.WALL));
   }

   /**
    * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
    * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
    * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
    */
   public BlockRenderType getRenderType(BlockState state) {
      return BlockRenderType.MODEL;
   }

   private VoxelShape getShapeFromState(BlockState state) {
      Direction direction = state.get(HORIZONTAL_FACING);
      switch((AttachFace)state.get(FACE)) {
      case FLOOR:
         if (direction != Direction.NORTH && direction != Direction.SOUTH) {
            return field_220213_A;
         }

         return field_220245_h;
      case WALL:
         if (direction == Direction.NORTH) {
            return SHAPE_WALL_NORTH;
         } else if (direction == Direction.SOUTH) {
            return SHAPE_WALL_SOUTH;
         } else {
            if (direction == Direction.EAST) {
               return field_220195_ah;
            }

            return field_220237_Z;
         }
      case CEILING:
         if (direction != Direction.NORTH && direction != Direction.SOUTH) {
            return SHAPE_CEILING_EAST_OR_WEST;
         }

         return SHAPE_CEILING_NORTH_OR_SOUTH;
      default:
         return field_220213_A;
      }
   }

   public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return this.getShapeFromState(state);
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return this.getShapeFromState(state);
   }

   public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
      return true;
   }

   public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      if (worldIn.isRemote) {
         return ActionResultType.SUCCESS;
      } else {
         player.openContainer(state.getContainer(worldIn, pos));
         player.addStat(Stats.INTERACT_WITH_GRINDSTONE);
         return ActionResultType.SUCCESS;
      }
   }

   public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
      return new SimpleNamedContainerProvider((p_220187_2_, p_220187_3_, p_220187_4_) -> {
         return new GrindstoneContainer(p_220187_2_, p_220187_3_, IWorldPosCallable.of(worldIn, pos));
      }, CONTAINER_NAME);
   }

   /**
    * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
    * fine.
    */
   public BlockState rotate(BlockState state, Rotation rot) {
      return state.with(HORIZONTAL_FACING, rot.rotate(state.get(HORIZONTAL_FACING)));
   }

   /**
    * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
    */
   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.toRotation(state.get(HORIZONTAL_FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(HORIZONTAL_FACING, FACE);
   }

   public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }
}