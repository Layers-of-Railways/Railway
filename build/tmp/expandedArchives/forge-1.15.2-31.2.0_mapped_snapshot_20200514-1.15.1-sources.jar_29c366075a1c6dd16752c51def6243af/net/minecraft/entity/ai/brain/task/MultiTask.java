package net.minecraft.entity.ai.brain.task;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.WeightedList;
import net.minecraft.world.server.ServerWorld;

public class MultiTask<E extends LivingEntity> extends Task<E> {
   private final Set<MemoryModuleType<?>> field_220416_b;
   private final MultiTask.Ordering field_220417_c;
   private final MultiTask.RunType field_220418_d;
   private final WeightedList<Task<? super E>> field_220419_e = new WeightedList<>();

   public MultiTask(Map<MemoryModuleType<?>, MemoryModuleStatus> p_i51503_1_, Set<MemoryModuleType<?>> p_i51503_2_, MultiTask.Ordering p_i51503_3_, MultiTask.RunType p_i51503_4_, List<Pair<Task<? super E>, Integer>> p_i51503_5_) {
      super(p_i51503_1_);
      this.field_220416_b = p_i51503_2_;
      this.field_220417_c = p_i51503_3_;
      this.field_220418_d = p_i51503_4_;
      p_i51503_5_.forEach((p_220411_1_) -> {
         this.field_220419_e.func_226313_a_(p_220411_1_.getFirst(), p_220411_1_.getSecond());
      });
   }

   protected boolean shouldContinueExecuting(ServerWorld worldIn, E entityIn, long gameTimeIn) {
      return this.field_220419_e.func_220655_b().filter((p_220414_0_) -> {
         return p_220414_0_.getStatus() == Task.Status.RUNNING;
      }).anyMatch((p_220413_4_) -> {
         return p_220413_4_.shouldContinueExecuting(worldIn, entityIn, gameTimeIn);
      });
   }

   protected boolean isTimedOut(long gameTime) {
      return false;
   }

   protected void startExecuting(ServerWorld worldIn, E entityIn, long gameTimeIn) {
      this.field_220417_c.func_220628_a(this.field_220419_e);
      this.field_220418_d.func_220630_a(this.field_220419_e, worldIn, entityIn, gameTimeIn);
   }

   protected void updateTask(ServerWorld worldIn, E owner, long gameTime) {
      this.field_220419_e.func_220655_b().filter((p_220408_0_) -> {
         return p_220408_0_.getStatus() == Task.Status.RUNNING;
      }).forEach((p_220409_4_) -> {
         p_220409_4_.tick(worldIn, owner, gameTime);
      });
   }

   protected void resetTask(ServerWorld worldIn, E entityIn, long gameTimeIn) {
      this.field_220419_e.func_220655_b().filter((p_220407_0_) -> {
         return p_220407_0_.getStatus() == Task.Status.RUNNING;
      }).forEach((p_220412_4_) -> {
         p_220412_4_.stop(worldIn, entityIn, gameTimeIn);
      });
      this.field_220416_b.forEach(entityIn.getBrain()::removeMemory);
   }

   public String toString() {
      Set<? extends Task<? super E>> set = this.field_220419_e.func_220655_b().filter((p_220410_0_) -> {
         return p_220410_0_.getStatus() == Task.Status.RUNNING;
      }).collect(Collectors.toSet());
      return "(" + this.getClass().getSimpleName() + "): " + set;
   }

   static enum Ordering {
      ORDERED((p_220627_0_) -> {
      }),
      SHUFFLED(WeightedList::func_226309_a_);

      private final Consumer<WeightedList<?>> field_220629_c;

      private Ordering(Consumer<WeightedList<?>> p_i50849_3_) {
         this.field_220629_c = p_i50849_3_;
      }

      public void func_220628_a(WeightedList<?> p_220628_1_) {
         this.field_220629_c.accept(p_220628_1_);
      }
   }

   static enum RunType {
      RUN_ONE {
         public <E extends LivingEntity> void func_220630_a(WeightedList<Task<? super E>> p_220630_1_, ServerWorld p_220630_2_, E p_220630_3_, long p_220630_4_) {
            p_220630_1_.func_220655_b().filter((p_220634_0_) -> {
               return p_220634_0_.getStatus() == Task.Status.STOPPED;
            }).filter((p_220633_4_) -> {
               return p_220633_4_.start(p_220630_2_, p_220630_3_, p_220630_4_);
            }).findFirst();
         }
      },
      TRY_ALL {
         public <E extends LivingEntity> void func_220630_a(WeightedList<Task<? super E>> p_220630_1_, ServerWorld p_220630_2_, E p_220630_3_, long p_220630_4_) {
            p_220630_1_.func_220655_b().filter((p_220632_0_) -> {
               return p_220632_0_.getStatus() == Task.Status.STOPPED;
            }).forEach((p_220631_4_) -> {
               p_220631_4_.start(p_220630_2_, p_220630_3_, p_220630_4_);
            });
         }
      };

      private RunType() {
      }

      public abstract <E extends LivingEntity> void func_220630_a(WeightedList<Task<? super E>> p_220630_1_, ServerWorld p_220630_2_, E p_220630_3_, long p_220630_4_);
   }
}