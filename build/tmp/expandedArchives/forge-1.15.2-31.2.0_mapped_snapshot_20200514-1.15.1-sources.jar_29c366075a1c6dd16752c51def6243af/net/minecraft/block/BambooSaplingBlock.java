package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.state.properties.BambooLeaves;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class BambooSaplingBlock extends Block implements IGrowable {
   protected static final VoxelShape field_220088_a = Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 12.0D, 12.0D);

   public BambooSaplingBlock(Block.Properties properties) {
      super(properties);
   }

   /**
    * Get the OffsetType for this Block. Determines if the model is rendered slightly offset.
    */
   public Block.OffsetType getOffsetType() {
      return Block.OffsetType.XZ;
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      Vec3d vec3d = state.getOffset(worldIn, pos);
      return field_220088_a.withOffset(vec3d.x, vec3d.y, vec3d.z);
   }

   public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
      if (rand.nextInt(3) == 0 && worldIn.isAirBlock(pos.up()) && worldIn.getLightSubtracted(pos.up(), 0) >= 9) {
         this.func_220087_a(worldIn, pos);
      }

   }

   public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.down()).isIn(BlockTags.BAMBOO_PLANTABLE_ON);
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (!stateIn.isValidPosition(worldIn, currentPos)) {
         return Blocks.AIR.getDefaultState();
      } else {
         if (facing == Direction.UP && facingState.getBlock() == Blocks.BAMBOO) {
            worldIn.setBlockState(currentPos, Blocks.BAMBOO.getDefaultState(), 2);
         }

         return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
      }
   }

   public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
      return new ItemStack(Items.BAMBOO);
   }

   /**
    * Whether this IGrowable can grow
    */
   public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
      return worldIn.getBlockState(pos.up()).isAir();
   }

   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
      return true;
   }

   public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
      this.func_220087_a(worldIn, pos);
   }

   /**
    * Get the hardness of this Block relative to the ability of the given player
    * @deprecated call via {@link IBlockState#getPlayerRelativeBlockHardness(EntityPlayer,World,BlockPos)} whenever
    * possible. Implementing/overriding is fine.
    */
   public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos) {
      return player.getHeldItemMainhand().getItem() instanceof SwordItem ? 1.0F : super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
   }

   protected void func_220087_a(World p_220087_1_, BlockPos p_220087_2_) {
      p_220087_1_.setBlockState(p_220087_2_.up(), Blocks.BAMBOO.getDefaultState().with(BambooBlock.PROPERTY_BAMBOO_LEAVES, BambooLeaves.SMALL), 3);
   }
}