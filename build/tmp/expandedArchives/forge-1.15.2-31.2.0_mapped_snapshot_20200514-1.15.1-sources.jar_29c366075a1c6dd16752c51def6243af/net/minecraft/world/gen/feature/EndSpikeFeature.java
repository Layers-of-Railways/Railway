package net.minecraft.world.gen.feature;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PaneBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class EndSpikeFeature extends Feature<EndSpikeFeatureConfig> {
   private static final LoadingCache<Long, List<EndSpikeFeature.EndSpike>> LOADING_CACHE = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.MINUTES).build(new EndSpikeFeature.EndSpikeCacheLoader());

   public EndSpikeFeature(Function<Dynamic<?>, ? extends EndSpikeFeatureConfig> p_i51432_1_) {
      super(p_i51432_1_);
   }

   public static List<EndSpikeFeature.EndSpike> generateSpikes(IWorld worldIn) {
      Random random = new Random(worldIn.getSeed());
      long i = random.nextLong() & 65535L;
      return LOADING_CACHE.getUnchecked(i);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, EndSpikeFeatureConfig config) {
      List<EndSpikeFeature.EndSpike> list = config.getSpikes();
      if (list.isEmpty()) {
         list = generateSpikes(worldIn);
      }

      for(EndSpikeFeature.EndSpike endspikefeature$endspike : list) {
         if (endspikefeature$endspike.doesStartInChunk(pos)) {
            this.placeSpike(worldIn, rand, config, endspikefeature$endspike);
         }
      }

      return true;
   }

   /**
    * Places the End Spike in the world. Also generates the obsidian tower.
    */
   private void placeSpike(IWorld worldIn, Random rand, EndSpikeFeatureConfig config, EndSpikeFeature.EndSpike spike) {
      int i = spike.getRadius();

      for(BlockPos blockpos : BlockPos.getAllInBoxMutable(new BlockPos(spike.getCenterX() - i, 0, spike.getCenterZ() - i), new BlockPos(spike.getCenterX() + i, spike.getHeight() + 10, spike.getCenterZ() + i))) {
         if (blockpos.distanceSq((double)spike.getCenterX(), (double)blockpos.getY(), (double)spike.getCenterZ(), false) <= (double)(i * i + 1) && blockpos.getY() < spike.getHeight()) {
            this.setBlockState(worldIn, blockpos, Blocks.OBSIDIAN.getDefaultState());
         } else if (blockpos.getY() > 65) {
            this.setBlockState(worldIn, blockpos, Blocks.AIR.getDefaultState());
         }
      }

      if (spike.isGuarded()) {
         int j1 = -2;
         int k1 = 2;
         int j = 3;
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

         for(int k = -2; k <= 2; ++k) {
            for(int l = -2; l <= 2; ++l) {
               for(int i1 = 0; i1 <= 3; ++i1) {
                  boolean flag = MathHelper.abs(k) == 2;
                  boolean flag1 = MathHelper.abs(l) == 2;
                  boolean flag2 = i1 == 3;
                  if (flag || flag1 || flag2) {
                     boolean flag3 = k == -2 || k == 2 || flag2;
                     boolean flag4 = l == -2 || l == 2 || flag2;
                     BlockState blockstate = Blocks.IRON_BARS.getDefaultState().with(PaneBlock.NORTH, Boolean.valueOf(flag3 && l != -2)).with(PaneBlock.SOUTH, Boolean.valueOf(flag3 && l != 2)).with(PaneBlock.WEST, Boolean.valueOf(flag4 && k != -2)).with(PaneBlock.EAST, Boolean.valueOf(flag4 && k != 2));
                     this.setBlockState(worldIn, blockpos$mutable.setPos(spike.getCenterX() + k, spike.getHeight() + i1, spike.getCenterZ() + l), blockstate);
                  }
               }
            }
         }
      }

      EnderCrystalEntity endercrystalentity = EntityType.END_CRYSTAL.create(worldIn.getWorld());
      endercrystalentity.setBeamTarget(config.getCrystalBeamTarget());
      endercrystalentity.setInvulnerable(config.isCrystalInvulnerable());
      endercrystalentity.setLocationAndAngles((double)((float)spike.getCenterX() + 0.5F), (double)(spike.getHeight() + 1), (double)((float)spike.getCenterZ() + 0.5F), rand.nextFloat() * 360.0F, 0.0F);
      worldIn.addEntity(endercrystalentity);
      this.setBlockState(worldIn, new BlockPos(spike.getCenterX(), spike.getHeight(), spike.getCenterZ()), Blocks.BEDROCK.getDefaultState());
   }

   public static class EndSpike {
      private final int centerX;
      private final int centerZ;
      private final int radius;
      private final int height;
      private final boolean guarded;
      private final AxisAlignedBB topBoundingBox;

      public EndSpike(int centerXIn, int centerZIn, int radiusIn, int heightIn, boolean guardedIn) {
         this.centerX = centerXIn;
         this.centerZ = centerZIn;
         this.radius = radiusIn;
         this.height = heightIn;
         this.guarded = guardedIn;
         this.topBoundingBox = new AxisAlignedBB((double)(centerXIn - radiusIn), 0.0D, (double)(centerZIn - radiusIn), (double)(centerXIn + radiusIn), 256.0D, (double)(centerZIn + radiusIn));
      }

      public boolean doesStartInChunk(BlockPos pos) {
         return pos.getX() >> 4 == this.centerX >> 4 && pos.getZ() >> 4 == this.centerZ >> 4;
      }

      public int getCenterX() {
         return this.centerX;
      }

      public int getCenterZ() {
         return this.centerZ;
      }

      public int getRadius() {
         return this.radius;
      }

      public int getHeight() {
         return this.height;
      }

      public boolean isGuarded() {
         return this.guarded;
      }

      public AxisAlignedBB getTopBoundingBox() {
         return this.topBoundingBox;
      }

      public <T> Dynamic<T> func_214749_a(DynamicOps<T> ops) {
         Builder<T, T> builder = ImmutableMap.builder();
         builder.put(ops.createString("centerX"), ops.createInt(this.centerX));
         builder.put(ops.createString("centerZ"), ops.createInt(this.centerZ));
         builder.put(ops.createString("radius"), ops.createInt(this.radius));
         builder.put(ops.createString("height"), ops.createInt(this.height));
         builder.put(ops.createString("guarded"), ops.createBoolean(this.guarded));
         return new Dynamic<>(ops, ops.createMap(builder.build()));
      }

      public static <T> EndSpikeFeature.EndSpike func_214747_a(Dynamic<T> p_214747_0_) {
         return new EndSpikeFeature.EndSpike(p_214747_0_.get("centerX").asInt(0), p_214747_0_.get("centerZ").asInt(0), p_214747_0_.get("radius").asInt(0), p_214747_0_.get("height").asInt(0), p_214747_0_.get("guarded").asBoolean(false));
      }
   }

   static class EndSpikeCacheLoader extends CacheLoader<Long, List<EndSpikeFeature.EndSpike>> {
      private EndSpikeCacheLoader() {
      }

      public List<EndSpikeFeature.EndSpike> load(Long p_load_1_) {
         List<Integer> list = IntStream.range(0, 10).boxed().collect(Collectors.toList());
         Collections.shuffle(list, new Random(p_load_1_));
         List<EndSpikeFeature.EndSpike> list1 = Lists.newArrayList();

         for(int i = 0; i < 10; ++i) {
            int j = MathHelper.floor(42.0D * Math.cos(2.0D * (-Math.PI + (Math.PI / 10D) * (double)i)));
            int k = MathHelper.floor(42.0D * Math.sin(2.0D * (-Math.PI + (Math.PI / 10D) * (double)i)));
            int l = list.get(i);
            int i1 = 2 + l / 3;
            int j1 = 76 + l * 3;
            boolean flag = l == 1 || l == 2;
            list1.add(new EndSpikeFeature.EndSpike(j, k, i1, j1, flag));
         }

         return list1;
      }
   }
}