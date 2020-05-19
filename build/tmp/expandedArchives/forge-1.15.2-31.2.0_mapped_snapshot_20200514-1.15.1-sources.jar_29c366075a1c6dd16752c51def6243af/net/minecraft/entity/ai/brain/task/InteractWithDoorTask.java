package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.pathfinding.Path;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.server.ServerWorld;

public class InteractWithDoorTask extends Task<LivingEntity> {
   public InteractWithDoorTask() {
      super(ImmutableMap.of(MemoryModuleType.PATH, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.INTERACTABLE_DOORS, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.field_225462_q, MemoryModuleStatus.REGISTERED));
   }

   protected void startExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn) {
      Brain<?> brain = entityIn.getBrain();
      Path path = brain.getMemory(MemoryModuleType.PATH).get();
      List<GlobalPos> list = brain.getMemory(MemoryModuleType.INTERACTABLE_DOORS).get();
      List<BlockPos> list1 = path.func_215746_d().stream().map((p_220435_0_) -> {
         return new BlockPos(p_220435_0_.x, p_220435_0_.y, p_220435_0_.z);
      }).collect(Collectors.toList());
      Set<BlockPos> set = this.func_220436_a(worldIn, list, list1);
      int i = path.getCurrentPathIndex() - 1;
      this.func_220434_a(worldIn, list1, set, i, entityIn, brain);
   }

   private Set<BlockPos> func_220436_a(ServerWorld p_220436_1_, List<GlobalPos> p_220436_2_, List<BlockPos> p_220436_3_) {
      return p_220436_2_.stream().filter((p_220432_1_) -> {
         return p_220432_1_.getDimension() == p_220436_1_.getDimension().getType();
      }).map(GlobalPos::getPos).filter(p_220436_3_::contains).collect(Collectors.toSet());
   }

   private void func_220434_a(ServerWorld p_220434_1_, List<BlockPos> p_220434_2_, Set<BlockPos> p_220434_3_, int p_220434_4_, LivingEntity p_220434_5_, Brain<?> p_220434_6_) {
      p_220434_3_.forEach((p_225447_4_) -> {
         int i = p_220434_2_.indexOf(p_225447_4_);
         BlockState blockstate = p_220434_1_.getBlockState(p_225447_4_);
         Block block = blockstate.getBlock();
         if (BlockTags.WOODEN_DOORS.contains(block) && block instanceof DoorBlock) {
            boolean flag = i >= p_220434_4_;
            ((DoorBlock)block).toggleDoor(p_220434_1_, p_225447_4_, flag);
            GlobalPos globalpos = GlobalPos.of(p_220434_1_.getDimension().getType(), p_225447_4_);
            if (!p_220434_6_.getMemory(MemoryModuleType.field_225462_q).isPresent() && flag) {
               p_220434_6_.setMemory(MemoryModuleType.field_225462_q, Sets.newHashSet(globalpos));
            } else {
               p_220434_6_.getMemory(MemoryModuleType.field_225462_q).ifPresent((p_225450_2_) -> {
                  if (flag) {
                     p_225450_2_.add(globalpos);
                  } else {
                     p_225450_2_.remove(globalpos);
                  }

               });
            }
         }

      });
      func_225449_a(p_220434_1_, p_220434_2_, p_220434_4_, p_220434_5_, p_220434_6_);
   }

   public static void func_225449_a(ServerWorld p_225449_0_, List<BlockPos> p_225449_1_, int p_225449_2_, LivingEntity p_225449_3_, Brain<?> p_225449_4_) {
      p_225449_4_.getMemory(MemoryModuleType.field_225462_q).ifPresent((p_225451_4_) -> {
         Iterator<GlobalPos> iterator = p_225451_4_.iterator();

         while(iterator.hasNext()) {
            GlobalPos globalpos = iterator.next();
            BlockPos blockpos = globalpos.getPos();
            int i = p_225449_1_.indexOf(blockpos);
            if (p_225449_0_.getDimension().getType() != globalpos.getDimension()) {
               iterator.remove();
            } else {
               BlockState blockstate = p_225449_0_.getBlockState(blockpos);
               Block block = blockstate.getBlock();
               if (BlockTags.WOODEN_DOORS.contains(block) && block instanceof DoorBlock && i < p_225449_2_ && blockpos.withinDistance(p_225449_3_.getPositionVec(), 4.0D)) {
                  ((DoorBlock)block).toggleDoor(p_225449_0_, blockpos, false);
                  iterator.remove();
               }
            }
         }

      });
   }
}