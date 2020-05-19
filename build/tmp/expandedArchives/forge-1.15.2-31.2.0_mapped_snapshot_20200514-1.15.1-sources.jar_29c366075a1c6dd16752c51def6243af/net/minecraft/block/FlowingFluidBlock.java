package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FlowingFluidBlock extends Block implements IBucketPickupHandler {
   public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_0_15;
   private final FlowingFluid fluid;
   private final List<IFluidState> field_212565_c;

   // Forge: Use the constructor that takes a supplier
   @Deprecated
   protected FlowingFluidBlock(FlowingFluid fluidIn, Block.Properties builder) {
      super(builder);
      this.fluid = fluidIn;
      this.field_212565_c = Lists.newArrayList();
      this.field_212565_c.add(fluidIn.getStillFluidState(false));

      for(int i = 1; i < 8; ++i) {
         this.field_212565_c.add(fluidIn.getFlowingFluidState(8 - i, false));
      }

      this.field_212565_c.add(fluidIn.getFlowingFluidState(8, true));
      this.setDefaultState(this.stateContainer.getBaseState().with(LEVEL, Integer.valueOf(0)));
      fluidStateCacheInitialized = true;
      supplier = fluidIn.delegate;
   }

   /**
    * @param supplier A fluid supplier such as {@link net.minecraftforge.fml.RegistryObject<Fluid>}
    */
   public FlowingFluidBlock(java.util.function.Supplier<? extends FlowingFluid> supplier, Block.Properties p_i48368_1_) {
      super(p_i48368_1_);
      this.fluid = null;
      this.field_212565_c = Lists.newArrayList();
      this.setDefaultState(this.stateContainer.getBaseState().with(LEVEL, Integer.valueOf(0)));
      this.supplier = supplier;
   }

   /**
    * Performs a random tick on a block.
    */
   public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
      worldIn.getFluidState(pos).randomTick(worldIn, pos, random);
   }

   public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
      return false;
   }

   public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return !this.fluid.isIn(FluidTags.LAVA);
   }

   public IFluidState getFluidState(BlockState state) {
      int i = state.get(LEVEL);
      if (!fluidStateCacheInitialized) initFluidStateCache();
      return this.field_212565_c.get(Math.min(i, 8));
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
      return adjacentBlockState.getFluidState().getFluid().isEquivalentTo(this.fluid);
   }

   /**
    * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
    * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
    * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
    */
   public BlockRenderType getRenderType(BlockState state) {
      return BlockRenderType.INVISIBLE;
   }

   public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
      return Collections.emptyList();
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return VoxelShapes.empty();
   }

   /**
    * How many world ticks before ticking
    */
   public int tickRate(IWorldReader worldIn) {
      return this.fluid.getTickRate(worldIn);
   }

   public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
      if (this.reactWithNeighbors(worldIn, pos, state)) {
         worldIn.getPendingFluidTicks().scheduleTick(pos, state.getFluidState().getFluid(), this.tickRate(worldIn));
      }

   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (stateIn.getFluidState().isSource() || facingState.getFluidState().isSource()) {
         worldIn.getPendingFluidTicks().scheduleTick(currentPos, stateIn.getFluidState().getFluid(), this.tickRate(worldIn));
      }

      return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
      if (this.reactWithNeighbors(worldIn, pos, state)) {
         worldIn.getPendingFluidTicks().scheduleTick(pos, state.getFluidState().getFluid(), this.tickRate(worldIn));
      }

   }

   public boolean reactWithNeighbors(World worldIn, BlockPos pos, BlockState state) {
      if (this.fluid.isIn(FluidTags.LAVA)) {
         boolean flag = false;

         for(Direction direction : Direction.values()) {
            if (direction != Direction.DOWN && worldIn.getFluidState(pos.offset(direction)).isTagged(FluidTags.WATER)) {
               flag = true;
               break;
            }
         }

         if (flag) {
            IFluidState ifluidstate = worldIn.getFluidState(pos);
            if (ifluidstate.isSource()) {
               worldIn.setBlockState(pos, net.minecraftforge.event.ForgeEventFactory.fireFluidPlaceBlockEvent(worldIn, pos, pos, Blocks.OBSIDIAN.getDefaultState()));
               this.triggerMixEffects(worldIn, pos);
               return false;
            }

            if (ifluidstate.getActualHeight(worldIn, pos) >= 0.44444445F) {
               worldIn.setBlockState(pos, net.minecraftforge.event.ForgeEventFactory.fireFluidPlaceBlockEvent(worldIn, pos, pos, Blocks.COBBLESTONE.getDefaultState()));
               this.triggerMixEffects(worldIn, pos);
               return false;
            }
         }
      }

      return true;
   }

   private void triggerMixEffects(IWorld worldIn, BlockPos pos) {
      worldIn.playEvent(1501, pos, 0);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(LEVEL);
   }

   public Fluid pickupFluid(IWorld worldIn, BlockPos pos, BlockState state) {
      if (state.get(LEVEL) == 0) {
         worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
         return this.fluid;
      } else {
         return Fluids.EMPTY;
      }
   }

   public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
      if (this.fluid.isIn(FluidTags.LAVA)) {
         entityIn.setInLava();
      }

   }

   // Forge start
   private final java.util.function.Supplier<? extends Fluid> supplier;
   public FlowingFluid getFluid() {
      return (FlowingFluid)supplier.get();
   }

   private boolean fluidStateCacheInitialized = false;
   protected synchronized void initFluidStateCache() {
      if (fluidStateCacheInitialized == false) {
         this.field_212565_c.add(getFluid().getStillFluidState(false));

         for (int i = 1; i < 8; ++i)
            this.field_212565_c.add(getFluid().getFlowingFluidState(8 - i, false));

         this.field_212565_c.add(getFluid().getFlowingFluidState(8, true));
         fluidStateCacheInitialized = true;
      }
   }
}