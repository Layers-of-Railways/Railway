package net.minecraft.pathfinding;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Region;

public class PathFinder {
   private final PathHeap path = new PathHeap();
   private final Set<PathPoint> closedSet = Sets.newHashSet();
   private final PathPoint[] pathOptions = new PathPoint[32];
   private final int field_215751_d;
   private final NodeProcessor nodeProcessor;

   public PathFinder(NodeProcessor p_i51280_1_, int p_i51280_2_) {
      this.nodeProcessor = p_i51280_1_;
      this.field_215751_d = p_i51280_2_;
   }

   @Nullable
   public Path func_227478_a_(Region p_227478_1_, MobEntity p_227478_2_, Set<BlockPos> p_227478_3_, float p_227478_4_, int p_227478_5_, float p_227478_6_) {
      this.path.clearPath();
      this.nodeProcessor.func_225578_a_(p_227478_1_, p_227478_2_);
      PathPoint pathpoint = this.nodeProcessor.getStart();
      Map<FlaggedPathPoint, BlockPos> map = p_227478_3_.stream().collect(Collectors.toMap((p_224782_1_) -> {
         return this.nodeProcessor.func_224768_a((double)p_224782_1_.getX(), (double)p_224782_1_.getY(), (double)p_224782_1_.getZ());
      }, Function.identity()));
      Path path = this.func_227479_a_(pathpoint, map, p_227478_4_, p_227478_5_, p_227478_6_);
      this.nodeProcessor.postProcess();
      return path;
   }

   @Nullable
   private Path func_227479_a_(PathPoint p_227479_1_, Map<FlaggedPathPoint, BlockPos> p_227479_2_, float p_227479_3_, int p_227479_4_, float p_227479_5_) {
      Set<FlaggedPathPoint> set = p_227479_2_.keySet();
      p_227479_1_.totalPathDistance = 0.0F;
      p_227479_1_.distanceToNext = this.func_224776_a(p_227479_1_, set);
      p_227479_1_.distanceToTarget = p_227479_1_.distanceToNext;
      this.path.clearPath();
      this.closedSet.clear();
      this.path.addPoint(p_227479_1_);
      int i = 0;
      int j = (int)((float)this.field_215751_d * p_227479_5_);

      while(!this.path.isPathEmpty()) {
         ++i;
         if (i >= j) {
            break;
         }

         PathPoint pathpoint = this.path.dequeue();
         pathpoint.visited = true;
         set.stream().filter((p_224781_2_) -> {
            return pathpoint.func_224757_c(p_224781_2_) <= (float)p_227479_4_;
         }).forEach(FlaggedPathPoint::func_224764_e);
         if (set.stream().anyMatch(FlaggedPathPoint::func_224762_f)) {
            break;
         }

         if (!(pathpoint.distanceTo(p_227479_1_) >= p_227479_3_)) {
            int k = this.nodeProcessor.func_222859_a(this.pathOptions, pathpoint);

            for(int l = 0; l < k; ++l) {
               PathPoint pathpoint1 = this.pathOptions[l];
               float f = pathpoint.distanceTo(pathpoint1);
               pathpoint1.field_222861_j = pathpoint.field_222861_j + f;
               float f1 = pathpoint.totalPathDistance + f + pathpoint1.costMalus;
               if (pathpoint1.field_222861_j < p_227479_3_ && (!pathpoint1.isAssigned() || f1 < pathpoint1.totalPathDistance)) {
                  pathpoint1.previous = pathpoint;
                  pathpoint1.totalPathDistance = f1;
                  pathpoint1.distanceToNext = this.func_224776_a(pathpoint1, set) * 1.5F;
                  if (pathpoint1.isAssigned()) {
                     this.path.changeDistance(pathpoint1, pathpoint1.totalPathDistance + pathpoint1.distanceToNext);
                  } else {
                     pathpoint1.distanceToTarget = pathpoint1.totalPathDistance + pathpoint1.distanceToNext;
                     this.path.addPoint(pathpoint1);
                  }
               }
            }
         }
      }

      Stream<Path> stream;
      if (set.stream().anyMatch(FlaggedPathPoint::func_224762_f)) {
         stream = set.stream().filter(FlaggedPathPoint::func_224762_f).map((p_224778_2_) -> {
            return this.func_224780_a(p_224778_2_.func_224763_d(), p_227479_2_.get(p_224778_2_), true);
         }).sorted(Comparator.comparingInt(Path::getCurrentPathLength));
      } else {
         stream = set.stream().map((p_224777_2_) -> {
            return this.func_224780_a(p_224777_2_.func_224763_d(), p_227479_2_.get(p_224777_2_), false);
         }).sorted(Comparator.comparingDouble(Path::func_224769_l).thenComparingInt(Path::getCurrentPathLength));
      }

      Optional<Path> optional = stream.findFirst();
      if (!optional.isPresent()) {
         return null;
      } else {
         Path path = optional.get();
         return path;
      }
   }

   private float func_224776_a(PathPoint p_224776_1_, Set<FlaggedPathPoint> p_224776_2_) {
      float f = Float.MAX_VALUE;

      for(FlaggedPathPoint flaggedpathpoint : p_224776_2_) {
         float f1 = p_224776_1_.distanceTo(flaggedpathpoint);
         flaggedpathpoint.func_224761_a(f1, p_224776_1_);
         f = Math.min(f1, f);
      }

      return f;
   }

   private Path func_224780_a(PathPoint p_224780_1_, BlockPos p_224780_2_, boolean p_224780_3_) {
      List<PathPoint> list = Lists.newArrayList();
      PathPoint pathpoint = p_224780_1_;
      list.add(0, p_224780_1_);

      while(pathpoint.previous != null) {
         pathpoint = pathpoint.previous;
         list.add(0, pathpoint);
      }

      return new Path(list, p_224780_2_, p_224780_3_);
   }
}