package net.minecraft.block;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.material.Material;
import net.minecraft.block.pattern.BlockMaterialMatcher;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class CarvedPumpkinBlock extends HorizontalBlock {
   public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
   @Nullable
   private BlockPattern snowmanBasePattern;
   @Nullable
   private BlockPattern snowmanPattern;
   @Nullable
   private BlockPattern golemBasePattern;
   @Nullable
   private BlockPattern golemPattern;
   private static final Predicate<BlockState> IS_PUMPKIN = (p_210301_0_) -> {
      return p_210301_0_ != null && (p_210301_0_.getBlock() == Blocks.CARVED_PUMPKIN || p_210301_0_.getBlock() == Blocks.JACK_O_LANTERN);
   };

   protected CarvedPumpkinBlock(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
   }

   public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
      if (oldState.getBlock() != state.getBlock()) {
         this.trySpawnGolem(worldIn, pos);
      }
   }

   public boolean canDispenserPlace(IWorldReader p_196354_1_, BlockPos p_196354_2_) {
      return this.getSnowmanBasePattern().match(p_196354_1_, p_196354_2_) != null || this.getGolemBasePattern().match(p_196354_1_, p_196354_2_) != null;
   }

   private void trySpawnGolem(World p_196358_1_, BlockPos p_196358_2_) {
      BlockPattern.PatternHelper blockpattern$patternhelper = this.getSnowmanPattern().match(p_196358_1_, p_196358_2_);
      if (blockpattern$patternhelper != null) {
         for(int i = 0; i < this.getSnowmanPattern().getThumbLength(); ++i) {
            CachedBlockInfo cachedblockinfo = blockpattern$patternhelper.translateOffset(0, i, 0);
            p_196358_1_.setBlockState(cachedblockinfo.getPos(), Blocks.AIR.getDefaultState(), 2);
            p_196358_1_.playEvent(2001, cachedblockinfo.getPos(), Block.getStateId(cachedblockinfo.getBlockState()));
         }

         SnowGolemEntity snowgolementity = EntityType.SNOW_GOLEM.create(p_196358_1_);
         BlockPos blockpos1 = blockpattern$patternhelper.translateOffset(0, 2, 0).getPos();
         snowgolementity.setLocationAndAngles((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.05D, (double)blockpos1.getZ() + 0.5D, 0.0F, 0.0F);
         p_196358_1_.addEntity(snowgolementity);

         for(ServerPlayerEntity serverplayerentity : p_196358_1_.getEntitiesWithinAABB(ServerPlayerEntity.class, snowgolementity.getBoundingBox().grow(5.0D))) {
            CriteriaTriggers.SUMMONED_ENTITY.trigger(serverplayerentity, snowgolementity);
         }

         for(int l = 0; l < this.getSnowmanPattern().getThumbLength(); ++l) {
            CachedBlockInfo cachedblockinfo3 = blockpattern$patternhelper.translateOffset(0, l, 0);
            p_196358_1_.notifyNeighbors(cachedblockinfo3.getPos(), Blocks.AIR);
         }
      } else {
         blockpattern$patternhelper = this.getGolemPattern().match(p_196358_1_, p_196358_2_);
         if (blockpattern$patternhelper != null) {
            for(int j = 0; j < this.getGolemPattern().getPalmLength(); ++j) {
               for(int k = 0; k < this.getGolemPattern().getThumbLength(); ++k) {
                  CachedBlockInfo cachedblockinfo2 = blockpattern$patternhelper.translateOffset(j, k, 0);
                  p_196358_1_.setBlockState(cachedblockinfo2.getPos(), Blocks.AIR.getDefaultState(), 2);
                  p_196358_1_.playEvent(2001, cachedblockinfo2.getPos(), Block.getStateId(cachedblockinfo2.getBlockState()));
               }
            }

            BlockPos blockpos = blockpattern$patternhelper.translateOffset(1, 2, 0).getPos();
            IronGolemEntity irongolementity = EntityType.IRON_GOLEM.create(p_196358_1_);
            irongolementity.setPlayerCreated(true);
            irongolementity.setLocationAndAngles((double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.05D, (double)blockpos.getZ() + 0.5D, 0.0F, 0.0F);
            p_196358_1_.addEntity(irongolementity);

            for(ServerPlayerEntity serverplayerentity1 : p_196358_1_.getEntitiesWithinAABB(ServerPlayerEntity.class, irongolementity.getBoundingBox().grow(5.0D))) {
               CriteriaTriggers.SUMMONED_ENTITY.trigger(serverplayerentity1, irongolementity);
            }

            for(int i1 = 0; i1 < this.getGolemPattern().getPalmLength(); ++i1) {
               for(int j1 = 0; j1 < this.getGolemPattern().getThumbLength(); ++j1) {
                  CachedBlockInfo cachedblockinfo1 = blockpattern$patternhelper.translateOffset(i1, j1, 0);
                  p_196358_1_.notifyNeighbors(cachedblockinfo1.getPos(), Blocks.AIR);
               }
            }
         }
      }

   }

   public BlockState getStateForPlacement(BlockItemUseContext context) {
      return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(FACING);
   }

   private BlockPattern getSnowmanBasePattern() {
      if (this.snowmanBasePattern == null) {
         this.snowmanBasePattern = BlockPatternBuilder.start().aisle(" ", "#", "#").where('#', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.SNOW_BLOCK))).build();
      }

      return this.snowmanBasePattern;
   }

   private BlockPattern getSnowmanPattern() {
      if (this.snowmanPattern == null) {
         this.snowmanPattern = BlockPatternBuilder.start().aisle("^", "#", "#").where('^', CachedBlockInfo.hasState(IS_PUMPKIN)).where('#', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.SNOW_BLOCK))).build();
      }

      return this.snowmanPattern;
   }

   private BlockPattern getGolemBasePattern() {
      if (this.golemBasePattern == null) {
         this.golemBasePattern = BlockPatternBuilder.start().aisle("~ ~", "###", "~#~").where('#', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.IRON_BLOCK))).where('~', CachedBlockInfo.hasState(BlockMaterialMatcher.forMaterial(Material.AIR))).build();
      }

      return this.golemBasePattern;
   }

   private BlockPattern getGolemPattern() {
      if (this.golemPattern == null) {
         this.golemPattern = BlockPatternBuilder.start().aisle("~^~", "###", "~#~").where('^', CachedBlockInfo.hasState(IS_PUMPKIN)).where('#', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(Blocks.IRON_BLOCK))).where('~', CachedBlockInfo.hasState(BlockMaterialMatcher.forMaterial(Material.AIR))).build();
      }

      return this.golemPattern;
   }

   public boolean canEntitySpawn(BlockState state, IBlockReader worldIn, BlockPos pos, EntityType<?> type) {
      return true;
   }
}