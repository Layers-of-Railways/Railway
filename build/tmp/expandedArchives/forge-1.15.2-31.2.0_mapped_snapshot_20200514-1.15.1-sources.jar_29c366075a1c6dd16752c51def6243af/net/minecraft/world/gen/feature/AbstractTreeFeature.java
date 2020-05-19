package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.shapes.BitSetVoxelShapePart;
import net.minecraft.util.math.shapes.VoxelShapePart;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.IWorldGenerationBaseReader;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.template.Template;

public abstract class AbstractTreeFeature<T extends BaseTreeFeatureConfig> extends Feature<T> {
   public AbstractTreeFeature(Function<Dynamic<?>, ? extends T> p_i225797_1_) {
      super(p_i225797_1_);
   }

   protected static boolean canBeReplacedByLogs(IWorldGenerationBaseReader p_214587_0_, BlockPos p_214587_1_) {
      if (p_214587_0_ instanceof net.minecraft.world.IWorldReader) // FORGE: Redirect to state method when possible
         return p_214587_0_.hasBlockState(p_214587_1_, state -> state.canBeReplacedByLogs((net.minecraft.world.IWorldReader)p_214587_0_, p_214587_1_));
      return p_214587_0_.hasBlockState(p_214587_1_, (p_214573_0_) -> {
         Block block = p_214573_0_.getBlock();
         return p_214573_0_.isAir() || p_214573_0_.isIn(BlockTags.LEAVES) || isDirt(block) || block.isIn(BlockTags.LOGS) || block.isIn(BlockTags.SAPLINGS) || block == Blocks.VINE;
      });
   }

   public static boolean isAir(IWorldGenerationBaseReader worldIn, BlockPos pos) {
      if (worldIn instanceof net.minecraft.world.IBlockReader) // FORGE: Redirect to state method when possible
        return worldIn.hasBlockState(pos, state -> state.isAir((net.minecraft.world.IBlockReader)worldIn, pos));
      return worldIn.hasBlockState(pos, BlockState::isAir);
   }

   protected static boolean isDirt(IWorldGenerationBaseReader worldIn, BlockPos pos) {
      return worldIn.hasBlockState(pos, (p_214590_0_) -> {
         Block block = p_214590_0_.getBlock();
         return isDirt(block) && block != Blocks.GRASS_BLOCK && block != Blocks.MYCELIUM;
      });
   }

   protected static boolean isVine(IWorldGenerationBaseReader p_227222_0_, BlockPos p_227222_1_) {
      return p_227222_0_.hasBlockState(p_227222_1_, (p_227224_0_) -> {
         return p_227224_0_.getBlock() == Blocks.VINE;
      });
   }

   public static boolean isWater(IWorldGenerationBaseReader worldIn, BlockPos pos) {
      return worldIn.hasBlockState(pos, (p_214583_0_) -> {
         return p_214583_0_.getBlock() == Blocks.WATER;
      });
   }

   public static boolean isAirOrLeaves(IWorldGenerationBaseReader worldIn, BlockPos pos) {
      if (worldIn instanceof net.minecraft.world.IWorldReader) // FORGE: Redirect to state method when possible
         return worldIn.hasBlockState(pos, state -> state.canBeReplacedByLeaves((net.minecraft.world.IWorldReader)worldIn, pos));
      return worldIn.hasBlockState(pos, (p_227223_0_) -> {
         return p_227223_0_.isAir() || p_227223_0_.isIn(BlockTags.LEAVES);
      });
   }

   @Deprecated //Forge: moved to isSoil
   public static boolean isDirtOrGrassBlock(IWorldGenerationBaseReader worldIn, BlockPos pos) {
      return worldIn.hasBlockState(pos, (p_227221_0_) -> {
         return isDirt(p_227221_0_.getBlock());
      });
   }

   protected static boolean isSoil(IWorldGenerationBaseReader reader, BlockPos pos, net.minecraftforge.common.IPlantable sapling) {
      if (!(reader instanceof net.minecraft.world.IBlockReader) || sapling == null)
         return isDirtOrGrassBlock(reader, pos);
      return reader.hasBlockState(pos, state -> state.canSustainPlant((net.minecraft.world.IBlockReader)reader, pos, Direction.UP, sapling));
   }

   @Deprecated //Forge: moved to isSoilOrFarm
   protected static boolean isDirtOrGrassBlockOrFarmland(IWorldGenerationBaseReader worldIn, BlockPos pos) {
      return worldIn.hasBlockState(pos, (p_227220_0_) -> {
         Block block = p_227220_0_.getBlock();
         return isDirt(block) || block == Blocks.FARMLAND;
      });
   }

   protected static boolean isSoilOrFarm(IWorldGenerationBaseReader reader, BlockPos pos, net.minecraftforge.common.IPlantable sapling) {
      if (!(reader instanceof net.minecraft.world.IBlockReader) || sapling == null)
         return isDirtOrGrassBlockOrFarmland(reader, pos);
      return reader.hasBlockState(pos, state -> state.canSustainPlant((net.minecraft.world.IBlockReader)reader, pos, Direction.UP, sapling));
   }

   public static boolean isTallPlants(IWorldGenerationBaseReader p_214576_0_, BlockPos p_214576_1_) {
      return p_214576_0_.hasBlockState(p_214576_1_, (p_227218_0_) -> {
         Material material = p_227218_0_.getMaterial();
         return material == Material.TALL_PLANTS;
      });
   }

   @Deprecated //Forge: moved to setDirtAt
   protected void setDirt(IWorldGenerationReader p_214584_1_, BlockPos p_214584_2_) {
      if (!isDirt(p_214584_1_, p_214584_2_)) {
         this.setBlockState(p_214584_1_, p_214584_2_, Blocks.DIRT.getDefaultState());
      }

   }

   protected boolean func_227216_a_(IWorldGenerationReader p_227216_1_, Random p_227216_2_, BlockPos p_227216_3_, Set<BlockPos> p_227216_4_, MutableBoundingBox p_227216_5_, BaseTreeFeatureConfig p_227216_6_) {
      if (!isAirOrLeaves(p_227216_1_, p_227216_3_) && !isTallPlants(p_227216_1_, p_227216_3_) && !isWater(p_227216_1_, p_227216_3_)) {
         return false;
      } else {
         this.func_227217_a_(p_227216_1_, p_227216_3_, p_227216_6_.trunkProvider.getBlockState(p_227216_2_, p_227216_3_), p_227216_5_);
         p_227216_4_.add(p_227216_3_.toImmutable());
         return true;
      }
   }

   protected boolean func_227219_b_(IWorldGenerationReader p_227219_1_, Random p_227219_2_, BlockPos p_227219_3_, Set<BlockPos> p_227219_4_, MutableBoundingBox p_227219_5_, BaseTreeFeatureConfig p_227219_6_) {
      if (!isAirOrLeaves(p_227219_1_, p_227219_3_) && !isTallPlants(p_227219_1_, p_227219_3_) && !isWater(p_227219_1_, p_227219_3_)) {
         return false;
      } else {
         this.func_227217_a_(p_227219_1_, p_227219_3_, p_227219_6_.leavesProvider.getBlockState(p_227219_2_, p_227219_3_), p_227219_5_);
         p_227219_4_.add(p_227219_3_.toImmutable());
         return true;
      }
   }

   protected void setDirtAt(IWorldGenerationReader reader, BlockPos pos, BlockPos origin) {
      if (!(reader instanceof IWorld)) {
         setDirt(reader, pos);
         return;
      }
      ((IWorld)reader).getBlockState(pos).onPlantGrow((IWorld)reader, pos, origin);
   }
   protected void setBlockState(IWorldWriter worldIn, BlockPos pos, BlockState state) {
      this.func_208521_b(worldIn, pos, state);
   }

   protected final void func_227217_a_(IWorldWriter p_227217_1_, BlockPos p_227217_2_, BlockState p_227217_3_, MutableBoundingBox p_227217_4_) {
      this.func_208521_b(p_227217_1_, p_227217_2_, p_227217_3_);
      p_227217_4_.expandTo(new MutableBoundingBox(p_227217_2_, p_227217_2_));
   }

   private void func_208521_b(IWorldWriter p_208521_1_, BlockPos p_208521_2_, BlockState p_208521_3_) {
      p_208521_1_.setBlockState(p_208521_2_, p_208521_3_, 19);
   }

   public final boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, T config) {
      Set<BlockPos> set = Sets.newHashSet();
      Set<BlockPos> set1 = Sets.newHashSet();
      Set<BlockPos> set2 = Sets.newHashSet();
      MutableBoundingBox mutableboundingbox = MutableBoundingBox.getNewBoundingBox();
      boolean flag = this.place(worldIn, rand, pos, set, set1, mutableboundingbox, config);
      if (mutableboundingbox.minX <= mutableboundingbox.maxX && flag && !set.isEmpty()) {
         if (!config.decorators.isEmpty()) {
            List<BlockPos> list = Lists.newArrayList(set);
            List<BlockPos> list1 = Lists.newArrayList(set1);
            list.sort(Comparator.comparingInt(Vec3i::getY));
            list1.sort(Comparator.comparingInt(Vec3i::getY));
            config.decorators.forEach((p_227215_6_) -> {
               p_227215_6_.func_225576_a_(worldIn, rand, list, list1, set2, mutableboundingbox);
            });
         }

         VoxelShapePart voxelshapepart = this.func_227214_a_(worldIn, mutableboundingbox, set, set2);
         Template.func_222857_a(worldIn, 3, voxelshapepart, mutableboundingbox.minX, mutableboundingbox.minY, mutableboundingbox.minZ);
         return true;
      } else {
         return false;
      }
   }

   private VoxelShapePart func_227214_a_(IWorld p_227214_1_, MutableBoundingBox p_227214_2_, Set<BlockPos> p_227214_3_, Set<BlockPos> p_227214_4_) {
      List<Set<BlockPos>> list = Lists.newArrayList();
      VoxelShapePart voxelshapepart = new BitSetVoxelShapePart(p_227214_2_.getXSize(), p_227214_2_.getYSize(), p_227214_2_.getZSize());
      int i = 6;

      for(int j = 0; j < 6; ++j) {
         list.add(Sets.newHashSet());
      }

      try (BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.retain()) {
         for(BlockPos blockpos : Lists.newArrayList(p_227214_4_)) {
            if (p_227214_2_.isVecInside(blockpos)) {
               voxelshapepart.setFilled(blockpos.getX() - p_227214_2_.minX, blockpos.getY() - p_227214_2_.minY, blockpos.getZ() - p_227214_2_.minZ, true, true);
            }
         }

         for(BlockPos blockpos1 : Lists.newArrayList(p_227214_3_)) {
            if (p_227214_2_.isVecInside(blockpos1)) {
               voxelshapepart.setFilled(blockpos1.getX() - p_227214_2_.minX, blockpos1.getY() - p_227214_2_.minY, blockpos1.getZ() - p_227214_2_.minZ, true, true);
            }

            for(Direction direction : Direction.values()) {
               blockpos$pooledmutable.setPos(blockpos1).move(direction);
               if (!p_227214_3_.contains(blockpos$pooledmutable)) {
                  BlockState blockstate = p_227214_1_.getBlockState(blockpos$pooledmutable);
                  if (blockstate.has(BlockStateProperties.DISTANCE_1_7)) {
                     list.get(0).add(blockpos$pooledmutable.toImmutable());
                     this.func_208521_b(p_227214_1_, blockpos$pooledmutable, blockstate.with(BlockStateProperties.DISTANCE_1_7, Integer.valueOf(1)));
                     if (p_227214_2_.isVecInside(blockpos$pooledmutable)) {
                        voxelshapepart.setFilled(blockpos$pooledmutable.getX() - p_227214_2_.minX, blockpos$pooledmutable.getY() - p_227214_2_.minY, blockpos$pooledmutable.getZ() - p_227214_2_.minZ, true, true);
                     }
                  }
               }
            }
         }

         for(int l = 1; l < 6; ++l) {
            Set<BlockPos> set = list.get(l - 1);
            Set<BlockPos> set1 = list.get(l);

            for(BlockPos blockpos2 : set) {
               if (p_227214_2_.isVecInside(blockpos2)) {
                  voxelshapepart.setFilled(blockpos2.getX() - p_227214_2_.minX, blockpos2.getY() - p_227214_2_.minY, blockpos2.getZ() - p_227214_2_.minZ, true, true);
               }

               for(Direction direction1 : Direction.values()) {
                  blockpos$pooledmutable.setPos(blockpos2).move(direction1);
                  if (!set.contains(blockpos$pooledmutable) && !set1.contains(blockpos$pooledmutable)) {
                     BlockState blockstate1 = p_227214_1_.getBlockState(blockpos$pooledmutable);
                     if (blockstate1.has(BlockStateProperties.DISTANCE_1_7)) {
                        int k = blockstate1.get(BlockStateProperties.DISTANCE_1_7);
                        if (k > l + 1) {
                           BlockState blockstate2 = blockstate1.with(BlockStateProperties.DISTANCE_1_7, Integer.valueOf(l + 1));
                           this.func_208521_b(p_227214_1_, blockpos$pooledmutable, blockstate2);
                           if (p_227214_2_.isVecInside(blockpos$pooledmutable)) {
                              voxelshapepart.setFilled(blockpos$pooledmutable.getX() - p_227214_2_.minX, blockpos$pooledmutable.getY() - p_227214_2_.minY, blockpos$pooledmutable.getZ() - p_227214_2_.minZ, true, true);
                           }

                           set1.add(blockpos$pooledmutable.toImmutable());
                        }
                     }
                  }
               }
            }
         }
      }

      return voxelshapepart;
   }

   /**
    * Called when placing the tree feature.
    */
   protected abstract boolean place(IWorldGenerationReader generationReader, Random rand, BlockPos positionIn, Set<BlockPos> p_225557_4_, Set<BlockPos> p_225557_5_, MutableBoundingBox boundingBoxIn, T configIn);
}