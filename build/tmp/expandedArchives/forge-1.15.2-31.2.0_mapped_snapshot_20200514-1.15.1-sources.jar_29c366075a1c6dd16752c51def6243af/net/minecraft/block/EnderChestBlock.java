package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.EnderChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMerger;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EnderChestBlock extends AbstractChestBlock<EnderChestTileEntity> implements IWaterLoggable {
   public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
   public static final TranslationTextComponent field_220115_d = new TranslationTextComponent("container.enderchest");

   protected EnderChestBlock(Block.Properties builder) {
      super(builder, () -> {
         return TileEntityType.ENDER_CHEST;
      });
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(WATERLOGGED, Boolean.valueOf(false)));
   }

   @OnlyIn(Dist.CLIENT)
   public TileEntityMerger.ICallbackWrapper<? extends ChestTileEntity> func_225536_a_(BlockState p_225536_1_, World p_225536_2_, BlockPos p_225536_3_, boolean p_225536_4_) {
      return TileEntityMerger.ICallback::func_225537_b_;
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return SHAPE;
   }

   /**
    * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
    * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
    * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
    */
   public BlockRenderType getRenderType(BlockState state) {
      return BlockRenderType.ENTITYBLOCK_ANIMATED;
   }

   public BlockState getStateForPlacement(BlockItemUseContext context) {
      IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
      return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite()).with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
   }

   public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      EnderChestInventory enderchestinventory = player.getInventoryEnderChest();
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (enderchestinventory != null && tileentity instanceof EnderChestTileEntity) {
         BlockPos blockpos = pos.up();
         if (worldIn.getBlockState(blockpos).isNormalCube(worldIn, blockpos)) {
            return ActionResultType.SUCCESS;
         } else if (worldIn.isRemote) {
            return ActionResultType.SUCCESS;
         } else {
            EnderChestTileEntity enderchesttileentity = (EnderChestTileEntity)tileentity;
            enderchestinventory.setChestTileEntity(enderchesttileentity);
            player.openContainer(new SimpleNamedContainerProvider((p_226928_1_, p_226928_2_, p_226928_3_) -> {
               return ChestContainer.createGeneric9X3(p_226928_1_, p_226928_2_, enderchestinventory);
            }, field_220115_d));
            player.addStat(Stats.OPEN_ENDERCHEST);
            return ActionResultType.SUCCESS;
         }
      } else {
         return ActionResultType.SUCCESS;
      }
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new EnderChestTileEntity();
   }

   /**
    * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
    * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
    * of whether the block can receive random update ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      for(int i = 0; i < 3; ++i) {
         int j = rand.nextInt(2) * 2 - 1;
         int k = rand.nextInt(2) * 2 - 1;
         double d0 = (double)pos.getX() + 0.5D + 0.25D * (double)j;
         double d1 = (double)((float)pos.getY() + rand.nextFloat());
         double d2 = (double)pos.getZ() + 0.5D + 0.25D * (double)k;
         double d3 = (double)(rand.nextFloat() * (float)j);
         double d4 = ((double)rand.nextFloat() - 0.5D) * 0.125D;
         double d5 = (double)(rand.nextFloat() * (float)k);
         worldIn.addParticle(ParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
      }

   }

   /**
    * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
    * fine.
    */
   public BlockState rotate(BlockState state, Rotation rot) {
      return state.with(FACING, rot.rotate(state.get(FACING)));
   }

   /**
    * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
    */
   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.toRotation(state.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(FACING, WATERLOGGED);
   }

   public IFluidState getFluidState(BlockState state) {
      return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (stateIn.get(WATERLOGGED)) {
         worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
      }

      return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }
}