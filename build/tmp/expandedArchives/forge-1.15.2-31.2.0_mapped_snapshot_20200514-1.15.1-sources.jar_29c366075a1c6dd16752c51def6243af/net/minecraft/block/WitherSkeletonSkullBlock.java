package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.material.Material;
import net.minecraft.block.pattern.BlockMaterialMatcher;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class WitherSkeletonSkullBlock extends SkullBlock {
   @Nullable
   private static BlockPattern witherPatternFull;
   @Nullable
   private static BlockPattern witherPatternBase;

   protected WitherSkeletonSkullBlock(Block.Properties properties) {
      super(SkullBlock.Types.WITHER_SKELETON, properties);
   }

   /**
    * Called by ItemBlocks after a block is set in the world, to allow post-place logic
    */
   public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof SkullTileEntity) {
         checkWitherSpawn(worldIn, pos, (SkullTileEntity)tileentity);
      }

   }

   public static void checkWitherSpawn(World worldIn, BlockPos pos, SkullTileEntity p_196298_2_) {
      if (!worldIn.isRemote) {
         Block block = p_196298_2_.getBlockState().getBlock();
         boolean flag = block == Blocks.WITHER_SKELETON_SKULL || block == Blocks.WITHER_SKELETON_WALL_SKULL;
         if (flag && pos.getY() >= 2 && worldIn.getDifficulty() != Difficulty.PEACEFUL) {
            BlockPattern blockpattern = getOrCreateWitherFull();
            BlockPattern.PatternHelper blockpattern$patternhelper = blockpattern.match(worldIn, pos);
            if (blockpattern$patternhelper != null) {
               for(int i = 0; i < blockpattern.getPalmLength(); ++i) {
                  for(int j = 0; j < blockpattern.getThumbLength(); ++j) {
                     CachedBlockInfo cachedblockinfo = blockpattern$patternhelper.translateOffset(i, j, 0);
                     worldIn.setBlockState(cachedblockinfo.getPos(), Blocks.AIR.getDefaultState(), 2);
                     worldIn.playEvent(2001, cachedblockinfo.getPos(), Block.getStateId(cachedblockinfo.getBlockState()));
                  }
               }

               WitherEntity witherentity = EntityType.WITHER.create(worldIn);
               BlockPos blockpos = blockpattern$patternhelper.translateOffset(1, 2, 0).getPos();
               witherentity.setLocationAndAngles((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.55D, (double)blockpos.getZ() + 0.5D, blockpattern$patternhelper.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F, 0.0F);
               witherentity.renderYawOffset = blockpattern$patternhelper.getForwards().getAxis() == Direction.Axis.X ? 0.0F : 90.0F;
               witherentity.ignite();

               for(ServerPlayerEntity serverplayerentity : worldIn.getEntitiesWithinAABB(ServerPlayerEntity.class, witherentity.getBoundingBox().grow(50.0D))) {
                  CriteriaTriggers.SUMMONED_ENTITY.trigger(serverplayerentity, witherentity);
               }

               worldIn.addEntity(witherentity);

               for(int k = 0; k < blockpattern.getPalmLength(); ++k) {
                  for(int l = 0; l < blockpattern.getThumbLength(); ++l) {
                     worldIn.notifyNeighbors(blockpattern$patternhelper.translateOffset(k, l, 0).getPos(), Blocks.AIR);
                  }
               }

            }
         }
      }
   }

   public static boolean canSpawnMob(World p_196299_0_, BlockPos p_196299_1_, ItemStack p_196299_2_) {
      if (p_196299_2_.getItem() == Items.WITHER_SKELETON_SKULL && p_196299_1_.getY() >= 2 && p_196299_0_.getDifficulty() != Difficulty.PEACEFUL && !p_196299_0_.isRemote) {
         return getOrCreateWitherBase().match(p_196299_0_, p_196299_1_) != null;
      } else {
         return false;
      }
   }

   private static BlockPattern getOrCreateWitherFull() {
      if (witherPatternFull == null) {
         witherPatternFull = BlockPatternBuilder.start().aisle("^^^", "###", "~#~").where('#', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.SOUL_SAND))).where('^', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.WITHER_SKELETON_SKULL).or(BlockStateMatcher.forBlock(Blocks.WITHER_SKELETON_WALL_SKULL)))).where('~', CachedBlockInfo.hasState(BlockMaterialMatcher.forMaterial(Material.AIR))).build();
      }

      return witherPatternFull;
   }

   private static BlockPattern getOrCreateWitherBase() {
      if (witherPatternBase == null) {
         witherPatternBase = BlockPatternBuilder.start().aisle("   ", "###", "~#~").where('#', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.SOUL_SAND))).where('~', CachedBlockInfo.hasState(BlockMaterialMatcher.forMaterial(Material.AIR))).build();
      }

      return witherPatternBase;
   }
}