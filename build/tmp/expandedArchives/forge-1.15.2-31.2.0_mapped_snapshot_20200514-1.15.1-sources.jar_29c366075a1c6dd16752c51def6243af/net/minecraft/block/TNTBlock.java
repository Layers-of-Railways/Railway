package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class TNTBlock extends Block {
   public static final BooleanProperty UNSTABLE = BlockStateProperties.UNSTABLE;

   public TNTBlock(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.getDefaultState().with(UNSTABLE, Boolean.valueOf(false)));
   }

   public void catchFire(BlockState state, World world, BlockPos pos, @Nullable net.minecraft.util.Direction face, @Nullable LivingEntity igniter) {
      explode(world, pos, igniter);
   }

   public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
      if (oldState.getBlock() != state.getBlock()) {
         if (worldIn.isBlockPowered(pos)) {
            catchFire(state, worldIn, pos, null, null);
            worldIn.removeBlock(pos, false);
         }

      }
   }

   public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
      if (worldIn.isBlockPowered(pos)) {
         catchFire(state, worldIn, pos, null, null);
         worldIn.removeBlock(pos, false);
      }

   }

   /**
    * Called before the Block is set to air in the world. Called regardless of if the player's tool can actually collect
    * this block
    */
   public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
      if (!worldIn.isRemote() && !player.isCreative() && state.get(UNSTABLE)) {
         catchFire(state, worldIn, pos, null, null);
      }

      super.onBlockHarvested(worldIn, pos, state, player);
   }

   /**
    * Called when this Block is destroyed by an Explosion
    */
   public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {
      if (!worldIn.isRemote) {
         TNTEntity tntentity = new TNTEntity(worldIn, (double)((float)pos.getX() + 0.5F), (double)pos.getY(), (double)((float)pos.getZ() + 0.5F), explosionIn.getExplosivePlacedBy());
         tntentity.setFuse((short)(worldIn.rand.nextInt(tntentity.getFuse() / 4) + tntentity.getFuse() / 8));
         worldIn.addEntity(tntentity);
      }
   }

   @Deprecated //Forge: Prefer using IForgeBlock#catchFire
   public static void explode(World p_196534_0_, BlockPos worldIn) {
      explode(p_196534_0_, worldIn, (LivingEntity)null);
   }

   @Deprecated //Forge: Prefer using IForgeBlock#catchFire
   private static void explode(World worldIn, BlockPos pos, @Nullable LivingEntity entityIn) {
      if (!worldIn.isRemote) {
         TNTEntity tntentity = new TNTEntity(worldIn, (double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, entityIn);
         worldIn.addEntity(tntentity);
         worldIn.playSound((PlayerEntity)null, tntentity.getPosX(), tntentity.getPosY(), tntentity.getPosZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
      }
   }

   public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      ItemStack itemstack = player.getHeldItem(handIn);
      Item item = itemstack.getItem();
      if (item != Items.FLINT_AND_STEEL && item != Items.FIRE_CHARGE) {
         return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
      } else {
         catchFire(state, worldIn, pos, hit.getFace(), player);
         worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);
         if (!player.isCreative()) {
            if (item == Items.FLINT_AND_STEEL) {
               itemstack.damageItem(1, player, (p_220287_1_) -> {
                  p_220287_1_.sendBreakAnimation(handIn);
               });
            } else {
               itemstack.shrink(1);
            }
         }

         return ActionResultType.SUCCESS;
      }
   }

   public void onProjectileCollision(World worldIn, BlockState state, BlockRayTraceResult hit, Entity projectile) {
      if (!worldIn.isRemote && projectile instanceof AbstractArrowEntity) {
         AbstractArrowEntity abstractarrowentity = (AbstractArrowEntity)projectile;
         Entity entity = abstractarrowentity.getShooter();
         if (abstractarrowentity.isBurning()) {
            BlockPos blockpos = hit.getPos();
            catchFire(state, worldIn, blockpos, null, entity instanceof LivingEntity ? (LivingEntity)entity : null);
            worldIn.removeBlock(blockpos, false);
         }
      }

   }

   /**
    * Return whether this block can drop from an explosion.
    */
   public boolean canDropFromExplosion(Explosion explosionIn) {
      return false;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(UNSTABLE);
   }
}