package net.minecraft.entity.ai.brain.sensor;

import java.util.Random;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;

public abstract class Sensor<E extends LivingEntity> {
   private static final Random RANDOM = new Random();
   private final int interval;
   private long counter;

   public Sensor(int p_i50301_1_) {
      this.interval = p_i50301_1_;
      this.counter = (long)RANDOM.nextInt(p_i50301_1_);
   }

   public Sensor() {
      this(20);
   }

   public final void tick(ServerWorld worldIn, E entityIn) {
      if (--this.counter <= 0L) {
         this.counter = (long)this.interval;
         this.update(worldIn, entityIn);
      }

   }

   protected abstract void update(ServerWorld worldIn, E entityIn);

   public abstract Set<MemoryModuleType<?>> getUsedMemories();
}