package net.minecraft.entity.ai.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.schedule.Schedule;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;

public class Brain<E extends LivingEntity> implements IDynamicSerializable {
   private final Map<MemoryModuleType<?>, Optional<?>> memories = Maps.newHashMap();
   private final Map<SensorType<? extends Sensor<? super E>>, Sensor<? super E>> sensors = Maps.newLinkedHashMap();
   private final Map<Integer, Map<Activity, Set<Task<? super E>>>> field_218232_c = Maps.newTreeMap();
   private Schedule schedule = Schedule.EMPTY;
   private final Map<Activity, Set<Pair<MemoryModuleType<?>, MemoryModuleStatus>>> requiredMemoryStates = Maps.newHashMap();
   private Set<Activity> defaultActivities = Sets.newHashSet();
   private final Set<Activity> activities = Sets.newHashSet();
   private Activity fallbackActivity = Activity.IDLE;
   private long lastGameTime = -9999L;

   public <T> Brain(Collection<MemoryModuleType<?>> memoryModules, Collection<SensorType<? extends Sensor<? super E>>> sensorTypes, Dynamic<T> dynamicIn) {
      memoryModules.forEach((p_218228_1_) -> {
         Optional optional = this.memories.put(p_218228_1_, Optional.empty());
      });
      sensorTypes.forEach((p_218204_1_) -> {
         Sensor sensor = this.sensors.put(p_218204_1_, p_218204_1_.func_220995_a());
      });
      this.sensors.values().forEach((p_218225_1_) -> {
         for(MemoryModuleType<?> memorymoduletype : p_218225_1_.getUsedMemories()) {
            this.memories.put(memorymoduletype, Optional.empty());
         }

      });

      for(Entry<Dynamic<T>, Dynamic<T>> entry : dynamicIn.get("memories").asMap(Function.identity(), Function.identity()).entrySet()) {
         this.setMemory(Registry.MEMORY_MODULE_TYPE.getOrDefault(new ResourceLocation(entry.getKey().asString(""))), entry.getValue());
      }

   }

   public boolean hasMemory(MemoryModuleType<?> typeIn) {
      return this.hasMemory(typeIn, MemoryModuleStatus.VALUE_PRESENT);
   }

   private <T, U> void setMemory(MemoryModuleType<U> typeIn, Dynamic<T> dynamicIn) {
      this.setMemory(typeIn, typeIn.getDeserializer().orElseThrow(RuntimeException::new).apply(dynamicIn));
   }

   public <U> void removeMemory(MemoryModuleType<U> type) {
      this.setMemory(type, Optional.empty());
   }

   public <U> void setMemory(MemoryModuleType<U> memoryType, @Nullable U p_218205_2_) {
      this.setMemory(memoryType, Optional.ofNullable(p_218205_2_));
   }

   public <U> void setMemory(MemoryModuleType<U> memoryType, Optional<U> p_218226_2_) {
      if (this.memories.containsKey(memoryType)) {
         if (p_218226_2_.isPresent() && this.isEmptyCollection(p_218226_2_.get())) {
            this.removeMemory(memoryType);
         } else {
            this.memories.put(memoryType, p_218226_2_);
         }
      }

   }

   public <U> Optional<U> getMemory(MemoryModuleType<U> type) {
      return (Optional<U>) this.memories.get(type);
   }

   public boolean hasMemory(MemoryModuleType<?> memoryTypeIn, MemoryModuleStatus memoryStatusIn) {
      Optional<?> optional = this.memories.get(memoryTypeIn);
      if (optional == null) {
         return false;
      } else {
         return memoryStatusIn == MemoryModuleStatus.REGISTERED || memoryStatusIn == MemoryModuleStatus.VALUE_PRESENT && optional.isPresent() || memoryStatusIn == MemoryModuleStatus.VALUE_ABSENT && !optional.isPresent();
      }
   }

   public Schedule getSchedule() {
      return this.schedule;
   }

   public void setSchedule(Schedule newSchedule) {
      this.schedule = newSchedule;
   }

   public void setDefaultActivities(Set<Activity> newActivities) {
      this.defaultActivities = newActivities;
   }

   @Deprecated
   public Stream<Task<? super E>> getRunningTasks() {
      return this.field_218232_c.values().stream().flatMap((p_218221_0_) -> {
         return p_218221_0_.values().stream();
      }).flatMap(Collection::stream).filter((p_218187_0_) -> {
         return p_218187_0_.getStatus() == Task.Status.RUNNING;
      });
   }

   public void switchTo(Activity activityIn) {
      this.activities.clear();
      this.activities.addAll(this.defaultActivities);
      boolean flag = this.requiredMemoryStates.keySet().contains(activityIn) && this.hasRequiredMemories(activityIn);
      this.activities.add(flag ? activityIn : this.fallbackActivity);
   }

   public void updateActivity(long dayTime, long gameTime) {
      if (gameTime - this.lastGameTime > 20L) {
         this.lastGameTime = gameTime;
         Activity activity = this.getSchedule().getScheduledActivity((int)(dayTime % 24000L));
         if (!this.activities.contains(activity)) {
            this.switchTo(activity);
         }
      }

   }

   public void setFallbackActivity(Activity newFallbackActivity) {
      this.fallbackActivity = newFallbackActivity;
   }

   public void registerActivity(Activity activityIn, ImmutableList<Pair<Integer, ? extends Task<? super E>>> p_218208_2_) {
      this.registerActivity(activityIn, p_218208_2_, ImmutableSet.of());
   }

   public void registerActivity(Activity activityIn, ImmutableList<Pair<Integer, ? extends Task<? super E>>> p_218224_2_, Set<Pair<MemoryModuleType<?>, MemoryModuleStatus>> p_218224_3_) {
      this.requiredMemoryStates.put(activityIn, p_218224_3_);
      p_218224_2_.forEach((p_218223_2_) -> {
         this.field_218232_c.computeIfAbsent(p_218223_2_.getFirst(), (p_218212_0_) -> {
            return Maps.newHashMap();
         }).computeIfAbsent(activityIn, (p_218195_0_) -> {
            return Sets.newLinkedHashSet();
         }).add(p_218223_2_.getSecond());
      });
   }

   public boolean hasActivity(Activity activityIn) {
      return this.activities.contains(activityIn);
   }

   public Brain<E> copy() {
      Brain<E> brain = new Brain<>(this.memories.keySet(), this.sensors.keySet(), new Dynamic<>(NBTDynamicOps.INSTANCE, new CompoundNBT()));
      this.memories.forEach((p_218188_1_, p_218188_2_) -> {
         p_218188_2_.ifPresent((p_218209_2_) -> {
            Optional optional = brain.memories.put(p_218188_1_, Optional.of(p_218209_2_));
         });
      });
      return brain;
   }

   public void tick(ServerWorld worldIn, E entityIn) {
      this.updateSensors(worldIn, entityIn);
      this.startTasks(worldIn, entityIn);
      this.tickTasks(worldIn, entityIn);
   }

   public void stopAllTasks(ServerWorld worldIn, E owner) {
      long i = owner.world.getGameTime();
      this.getRunningTasks().forEach((p_218206_4_) -> {
         p_218206_4_.stop(worldIn, owner, i);
      });
   }

   public <T> T serialize(DynamicOps<T> p_218175_1_) {
      T t = p_218175_1_.createMap(this.memories.entrySet().stream().filter((p_218197_0_) -> {
         return p_218197_0_.getKey().getDeserializer().isPresent() && p_218197_0_.getValue().isPresent();
      }).map((p_218186_1_) -> {
         return Pair.of(p_218175_1_.createString(Registry.MEMORY_MODULE_TYPE.getKey(p_218186_1_.getKey()).toString()), ((IDynamicSerializable)p_218186_1_.getValue().get()).serialize(p_218175_1_));
      }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
      return p_218175_1_.createMap(ImmutableMap.of(p_218175_1_.createString("memories"), t));
   }

   private void updateSensors(ServerWorld worldIn, E entityIn) {
      this.sensors.values().forEach((p_218201_2_) -> {
         p_218201_2_.tick(worldIn, entityIn);
      });
   }

   private void startTasks(ServerWorld worldIn, E entityIn) {
      long i = worldIn.getGameTime();
      this.field_218232_c.values().stream().flatMap((p_218219_0_) -> {
         return p_218219_0_.entrySet().stream();
      }).filter((p_218215_1_) -> {
         return this.activities.contains(p_218215_1_.getKey());
      }).map(Entry::getValue).flatMap(Collection::stream).filter((p_218194_0_) -> {
         return p_218194_0_.getStatus() == Task.Status.STOPPED;
      }).forEach((p_218192_4_) -> {
         p_218192_4_.start(worldIn, entityIn, i);
      });
   }

   private void tickTasks(ServerWorld worldIn, E entityIn) {
      long i = worldIn.getGameTime();
      this.getRunningTasks().forEach((p_218220_4_) -> {
         p_218220_4_.tick(worldIn, entityIn, i);
      });
   }

   private boolean hasRequiredMemories(Activity activityIn) {
      return this.requiredMemoryStates.get(activityIn).stream().allMatch((p_218190_1_) -> {
         MemoryModuleType<?> memorymoduletype = p_218190_1_.getFirst();
         MemoryModuleStatus memorymodulestatus = p_218190_1_.getSecond();
         return this.hasMemory(memorymoduletype, memorymodulestatus);
      });
   }

   private boolean isEmptyCollection(Object p_218213_1_) {
      return p_218213_1_ instanceof Collection && ((Collection)p_218213_1_).isEmpty();
   }
}