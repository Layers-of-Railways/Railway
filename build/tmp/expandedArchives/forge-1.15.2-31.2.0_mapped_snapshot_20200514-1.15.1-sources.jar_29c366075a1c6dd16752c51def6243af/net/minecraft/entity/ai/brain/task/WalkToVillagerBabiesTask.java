package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

public class WalkToVillagerBabiesTask extends Task<CreatureEntity> {
   public WalkToVillagerBabiesTask() {
      super(ImmutableMap.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.INTERACTION_TARGET, MemoryModuleStatus.REGISTERED));
   }

   protected boolean shouldExecute(ServerWorld worldIn, CreatureEntity owner) {
      return worldIn.getRandom().nextInt(10) == 0 && this.func_220501_e(owner);
   }

   protected void startExecuting(ServerWorld worldIn, CreatureEntity entityIn, long gameTimeIn) {
      LivingEntity livingentity = this.func_220500_b(entityIn);
      if (livingentity != null) {
         this.func_220508_a(worldIn, entityIn, livingentity);
      } else {
         Optional<LivingEntity> optional = this.func_220497_b(entityIn);
         if (optional.isPresent()) {
            func_220498_a(entityIn, optional.get());
         } else {
            this.func_220510_a(entityIn).ifPresent((p_220506_1_) -> {
               func_220498_a(entityIn, p_220506_1_);
            });
         }
      }
   }

   private void func_220508_a(ServerWorld p_220508_1_, CreatureEntity p_220508_2_, LivingEntity p_220508_3_) {
      for(int i = 0; i < 10; ++i) {
         Vec3d vec3d = RandomPositionGenerator.getLandPos(p_220508_2_, 20, 8);
         if (vec3d != null && p_220508_1_.isVillage(new BlockPos(vec3d))) {
            p_220508_2_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d, 0.6F, 0));
            return;
         }
      }

   }

   private static void func_220498_a(CreatureEntity p_220498_0_, LivingEntity p_220498_1_) {
      Brain<?> brain = p_220498_0_.getBrain();
      brain.setMemory(MemoryModuleType.INTERACTION_TARGET, p_220498_1_);
      brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(p_220498_1_));
      brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityPosWrapper(p_220498_1_), 0.6F, 1));
   }

   private Optional<LivingEntity> func_220510_a(CreatureEntity p_220510_1_) {
      return this.func_220503_d(p_220510_1_).stream().findAny();
   }

   private Optional<LivingEntity> func_220497_b(CreatureEntity p_220497_1_) {
      Map<LivingEntity, Integer> map = this.func_220505_c(p_220497_1_);
      return map.entrySet().stream().sorted(Comparator.comparingInt(Entry::getValue)).filter((p_220504_0_) -> {
         return p_220504_0_.getValue() > 0 && p_220504_0_.getValue() <= 5;
      }).map(Entry::getKey).findFirst();
   }

   private Map<LivingEntity, Integer> func_220505_c(CreatureEntity p_220505_1_) {
      Map<LivingEntity, Integer> map = Maps.newHashMap();
      this.func_220503_d(p_220505_1_).stream().filter(this::func_220502_c).forEach((p_220509_2_) -> {
         Integer integer = map.compute(this.func_220495_a(p_220509_2_), (p_220511_0_, p_220511_1_) -> {
            return p_220511_1_ == null ? 1 : p_220511_1_ + 1;
         });
      });
      return map;
   }

   private List<LivingEntity> func_220503_d(CreatureEntity p_220503_1_) {
      return p_220503_1_.getBrain().getMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES).get();
   }

   private LivingEntity func_220495_a(LivingEntity p_220495_1_) {
      return p_220495_1_.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).get();
   }

   @Nullable
   private LivingEntity func_220500_b(LivingEntity p_220500_1_) {
      return p_220500_1_.getBrain().getMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES).get().stream().filter((p_220507_2_) -> {
         return this.func_220499_a(p_220500_1_, p_220507_2_);
      }).findAny().orElse((LivingEntity)null);
   }

   private boolean func_220502_c(LivingEntity p_220502_1_) {
      return p_220502_1_.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
   }

   private boolean func_220499_a(LivingEntity p_220499_1_, LivingEntity p_220499_2_) {
      return p_220499_2_.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).filter((p_220496_1_) -> {
         return p_220496_1_ == p_220499_1_;
      }).isPresent();
   }

   private boolean func_220501_e(CreatureEntity p_220501_1_) {
      return p_220501_1_.getBrain().hasMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES);
   }
}