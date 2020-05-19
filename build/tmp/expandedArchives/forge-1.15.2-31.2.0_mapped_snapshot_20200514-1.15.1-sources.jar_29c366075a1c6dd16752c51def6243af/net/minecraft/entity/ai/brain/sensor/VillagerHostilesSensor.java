package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;

public class VillagerHostilesSensor extends Sensor<LivingEntity> {
   private static final ImmutableMap<EntityType<?>, Float> field_220991_b = ImmutableMap.<EntityType<?>, Float>builder().put(EntityType.DROWNED, 8.0F).put(EntityType.EVOKER, 12.0F).put(EntityType.HUSK, 8.0F).put(EntityType.ILLUSIONER, 12.0F).put(EntityType.PILLAGER, 15.0F).put(EntityType.RAVAGER, 12.0F).put(EntityType.VEX, 8.0F).put(EntityType.VINDICATOR, 10.0F).put(EntityType.ZOMBIE, 8.0F).put(EntityType.ZOMBIE_VILLAGER, 8.0F).build();

   public Set<MemoryModuleType<?>> getUsedMemories() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_HOSTILE);
   }

   protected void update(ServerWorld worldIn, LivingEntity entityIn) {
      entityIn.getBrain().setMemory(MemoryModuleType.NEAREST_HOSTILE, this.func_220989_a(entityIn));
   }

   private Optional<LivingEntity> func_220989_a(LivingEntity p_220989_1_) {
      return this.func_220990_b(p_220989_1_).flatMap((p_220984_2_) -> {
         return p_220984_2_.stream().filter(this::func_220988_c).filter((p_220985_2_) -> {
            return this.func_220987_a(p_220989_1_, p_220985_2_);
         }).min((p_220986_2_, p_220986_3_) -> {
            return this.func_220983_a(p_220989_1_, p_220986_2_, p_220986_3_);
         });
      });
   }

   private Optional<List<LivingEntity>> func_220990_b(LivingEntity p_220990_1_) {
      return p_220990_1_.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS);
   }

   private int func_220983_a(LivingEntity p_220983_1_, LivingEntity p_220983_2_, LivingEntity p_220983_3_) {
      return MathHelper.floor(p_220983_2_.getDistanceSq(p_220983_1_) - p_220983_3_.getDistanceSq(p_220983_1_));
   }

   private boolean func_220987_a(LivingEntity p_220987_1_, LivingEntity p_220987_2_) {
      float f = field_220991_b.get(p_220987_2_.getType());
      return p_220987_2_.getDistanceSq(p_220987_1_) <= (double)(f * f);
   }

   private boolean func_220988_c(LivingEntity p_220988_1_) {
      return field_220991_b.containsKey(p_220988_1_.getType());
   }
}