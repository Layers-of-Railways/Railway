package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.BrewingStandTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BrewingStandBlock extends ContainerBlock {
   public static final BooleanProperty[] HAS_BOTTLE = new BooleanProperty[]{BlockStateProperties.HAS_BOTTLE_0, BlockStateProperties.HAS_BOTTLE_1, BlockStateProperties.HAS_BOTTLE_2};
   protected static final VoxelShape SHAPE = VoxelShapes.or(Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 2.0D, 15.0D), Block.makeCuboidShape(7.0D, 0.0D, 7.0D, 9.0D, 14.0D, 9.0D));

   public BrewingStandBlock(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(HAS_BOTTLE[0], Boolean.valueOf(false)).with(HAS_BOTTLE[1], Boolean.valueOf(false)).with(HAS_BOTTLE[2], Boolean.valueOf(false)));
   }

   /**
    * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
    * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
    * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
    */
   public BlockRenderType getRenderType(BlockState state) {
      return BlockRenderType.MODEL;
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new BrewingStandTileEntity();
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return SHAPE;
   }

   public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      if (worldIn.isRemote) {
         return ActionResultType.SUCCESS;
      } else {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof BrewingStandTileEntity) {
            player.openContainer((BrewingStandTileEntity)tileentity);
            player.addStat(Stats.INTERACT_WITH_BREWINGSTAND);
         }

         return ActionResultType.SUCCESS;
      }
   }

   /**
    * Called by ItemBlocks after a block is set in the world, to allow post-place logic
    */
   public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
      if (stack.hasDisplayName()) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof BrewingStandTileEntity) {
            ((BrewingStandTileEntity)tileentity).setCustomName(stack.getDisplayName());
         }
      }

   }

   /**
    * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
    * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
    * of whether the block can receive random update ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      double d0 = (double)pos.getX() + 0.4D + (double)rand.nextFloat() * 0.2D;
      double d1 = (double)pos.getY() + 0.7D + (double)rand.nextFloat() * 0.3D;
      double d2 = (double)pos.getZ() + 0.4D + (double)rand.nextFloat() * 0.2D;
      worldIn.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
   }

   public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof BrewingStandTileEntity) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (BrewingStandTileEntity)tileentity);
         }

         super.onReplaced(state, worldIn, pos, newState, isMoving);
      }
   }

   /**
    * @deprecated call via {@link IBlockState#hasComparatorInputOverride()} whenever possible. Implementing/overriding
    * is fine.
    */
   public boolean hasComparatorInputOverride(BlockState state) {
      return true;
   }

   /**
    * @deprecated call via {@link IBlockState#getComparatorInputOverride(World,BlockPos)} whenever possible.
    * Implementing/overriding is fine.
    */
   public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
      return Container.calcRedstone(worldIn.getTileEntity(pos));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(HAS_BOTTLE[0], HAS_BOTTLE[1], HAS_BOTTLE[2]);
   }

   public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }
}