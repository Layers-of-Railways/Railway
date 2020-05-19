package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class TurtleEggBlock extends Block {
   private static final VoxelShape ONE_EGG_SHAPE = Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 12.0D, 7.0D, 12.0D);
   private static final VoxelShape MULTI_EGG_SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 7.0D, 15.0D);
   public static final IntegerProperty HATCH = BlockStateProperties.HATCH_0_2;
   public static final IntegerProperty EGGS = BlockStateProperties.EGGS_1_4;

   public TurtleEggBlock(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(HATCH, Integer.valueOf(0)).with(EGGS, Integer.valueOf(1)));
   }

   /**
    * Called when the given entity walks on this Block
    */
   public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
      this.tryTrample(worldIn, pos, entityIn, 100);
      super.onEntityWalk(worldIn, pos, entityIn);
   }

   /**
    * Block's chance to react to a living entity falling on it.
    */
   public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
      if (!(entityIn instanceof ZombieEntity)) {
         this.tryTrample(worldIn, pos, entityIn, 3);
      }

      super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
   }

   private void tryTrample(World worldIn, BlockPos pos, Entity trampler, int p_203167_4_) {
      if (!this.canTrample(worldIn, trampler)) {
         super.onEntityWalk(worldIn, pos, trampler);
      } else {
         if (!worldIn.isRemote && worldIn.rand.nextInt(p_203167_4_) == 0) {
            this.removeOneEgg(worldIn, pos, worldIn.getBlockState(pos));
         }

      }
   }

   private void removeOneEgg(World worldIn, BlockPos pos, BlockState state) {
      worldIn.playSound((PlayerEntity)null, pos, SoundEvents.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + worldIn.rand.nextFloat() * 0.2F);
      int i = state.get(EGGS);
      if (i <= 1) {
         worldIn.destroyBlock(pos, false);
      } else {
         worldIn.setBlockState(pos, state.with(EGGS, Integer.valueOf(i - 1)), 2);
         worldIn.playEvent(2001, pos, Block.getStateId(state));
      }

   }

   public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
      if (this.canGrow(worldIn) && this.hasProperHabitat(worldIn, pos)) {
         int i = state.get(HATCH);
         if (i < 2) {
            worldIn.playSound((PlayerEntity)null, pos, SoundEvents.ENTITY_TURTLE_EGG_CRACK, SoundCategory.BLOCKS, 0.7F, 0.9F + rand.nextFloat() * 0.2F);
            worldIn.setBlockState(pos, state.with(HATCH, Integer.valueOf(i + 1)), 2);
         } else {
            worldIn.playSound((PlayerEntity)null, pos, SoundEvents.ENTITY_TURTLE_EGG_HATCH, SoundCategory.BLOCKS, 0.7F, 0.9F + rand.nextFloat() * 0.2F);
            worldIn.removeBlock(pos, false);

            for(int j = 0; j < state.get(EGGS); ++j) {
               worldIn.playEvent(2001, pos, Block.getStateId(state));
               TurtleEntity turtleentity = EntityType.TURTLE.create(worldIn);
               turtleentity.setGrowingAge(-24000);
               turtleentity.setHome(pos);
               turtleentity.setLocationAndAngles((double)pos.getX() + 0.3D + (double)j * 0.2D, (double)pos.getY(), (double)pos.getZ() + 0.3D, 0.0F, 0.0F);
               worldIn.addEntity(turtleentity);
            }
         }
      }

   }

   private boolean hasProperHabitat(IBlockReader p_203168_1_, BlockPos p_203168_2_) {
      return p_203168_1_.getBlockState(p_203168_2_.down()).getBlock() == Blocks.SAND;
   }

   public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
      if (this.hasProperHabitat(worldIn, pos) && !worldIn.isRemote) {
         worldIn.playEvent(2005, pos, 0);
      }

   }

   private boolean canGrow(World worldIn) {
      float f = worldIn.getCelestialAngle(1.0F);
      if ((double)f < 0.69D && (double)f > 0.65D) {
         return true;
      } else {
         return worldIn.rand.nextInt(500) == 0;
      }
   }

   /**
    * Spawns the block's drops in the world. By the time this is called the Block has possibly been set to air via
    * Block.removedByPlayer
    */
   public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
      super.harvestBlock(worldIn, player, pos, state, te, stack);
      this.removeOneEgg(worldIn, pos, state);
   }

   public boolean isReplaceable(BlockState state, BlockItemUseContext useContext) {
      return useContext.getItem().getItem() == this.asItem() && state.get(EGGS) < 4 ? true : super.isReplaceable(state, useContext);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext context) {
      BlockState blockstate = context.getWorld().getBlockState(context.getPos());
      return blockstate.getBlock() == this ? blockstate.with(EGGS, Integer.valueOf(Math.min(4, blockstate.get(EGGS) + 1))) : super.getStateForPlacement(context);
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return state.get(EGGS) > 1 ? MULTI_EGG_SHAPE : ONE_EGG_SHAPE;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(HATCH, EGGS);
   }

   private boolean canTrample(World worldIn, Entity trampler) {
      if (trampler instanceof TurtleEntity) {
         return false;
      } else {
         return trampler instanceof LivingEntity && !(trampler instanceof PlayerEntity) ? net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(worldIn, trampler) : true;
      }
   }
}