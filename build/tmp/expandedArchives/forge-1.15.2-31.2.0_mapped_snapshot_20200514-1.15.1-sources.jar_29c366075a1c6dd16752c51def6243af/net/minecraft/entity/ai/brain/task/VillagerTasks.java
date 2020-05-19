package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.village.PointOfInterestType;

public class VillagerTasks {
   public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> core(VillagerProfession profession, float p_220638_1_) {
      return ImmutableList.of(Pair.of(0, new SwimTask(0.4F, 0.8F)), Pair.of(0, new InteractWithDoorTask()), Pair.of(0, new LookTask(45, 90)), Pair.of(0, new PanicTask()), Pair.of(0, new WakeUpTask()), Pair.of(0, new HideFromRaidOnBellRingTask()), Pair.of(0, new BeginRaidTask()), Pair.of(1, new WalkToTargetTask(200)), Pair.of(2, new TradeTask(p_220638_1_)), Pair.of(5, new PickupFoodTask()), Pair.of(10, new GatherPOITask(profession.getPointOfInterest(), MemoryModuleType.JOB_SITE, true)), Pair.of(10, new GatherPOITask(PointOfInterestType.HOME, MemoryModuleType.HOME, false)), Pair.of(10, new GatherPOITask(PointOfInterestType.MEETING, MemoryModuleType.MEETING_POINT, true)), Pair.of(10, new AssignProfessionTask()), Pair.of(10, new ChangeJobTask()));
   }

   public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> work(VillagerProfession profession, float p_220639_1_) {
      return ImmutableList.of(lookAtPlayerOrVillager(), Pair.of(5, new FirstShuffledTask<>(ImmutableList.of(Pair.of(new SpawnGolemTask(), 7), Pair.of(new WorkTask(MemoryModuleType.JOB_SITE, 4), 2), Pair.of(new WalkTowardsPosTask(MemoryModuleType.JOB_SITE, 1, 10), 5), Pair.of(new WalkTowardsRandomSecondaryPosTask(MemoryModuleType.SECONDARY_JOB_SITE, 0.4F, 1, 6, MemoryModuleType.JOB_SITE), 5), Pair.of(new FarmTask(), profession == VillagerProfession.FARMER ? 2 : 5)))), Pair.of(10, new ShowWaresTask(400, 1600)), Pair.of(10, new FindInteractionAndLookTargetTask(EntityType.PLAYER, 4)), Pair.of(2, new StayNearPointTask(MemoryModuleType.JOB_SITE, p_220639_1_, 9, 100, 1200)), Pair.of(3, new GiveHeroGiftsTask(100)), Pair.of(3, new ExpirePOITask(profession.getPointOfInterest(), MemoryModuleType.JOB_SITE)), Pair.of(99, new UpdateActivityTask()));
   }

   public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> play(float walkingSpeed) {
      return ImmutableList.of(Pair.of(0, new WalkToTargetTask(100)), lookAtMany(), Pair.of(5, new WalkToVillagerBabiesTask()), Pair.of(5, new FirstShuffledTask<>(ImmutableMap.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleStatus.VALUE_ABSENT), ImmutableList.of(Pair.of(InteractWithEntityTask.func_220445_a(EntityType.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, walkingSpeed, 2), 2), Pair.of(InteractWithEntityTask.func_220445_a(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, walkingSpeed, 2), 1), Pair.of(new FindWalkTargetTask(walkingSpeed), 1), Pair.of(new WalkTowardsLookTargetTask(walkingSpeed, 2), 1), Pair.of(new JumpOnBedTask(walkingSpeed), 2), Pair.of(new DummyTask(20, 40), 2)))), Pair.of(99, new UpdateActivityTask()));
   }

   public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> rest(VillagerProfession profession, float walkingSpeed) {
      return ImmutableList.of(Pair.of(2, new StayNearPointTask(MemoryModuleType.HOME, walkingSpeed, 1, 150, 1200)), Pair.of(3, new ExpirePOITask(PointOfInterestType.HOME, MemoryModuleType.HOME)), Pair.of(3, new SleepAtHomeTask()), Pair.of(5, new FirstShuffledTask<>(ImmutableMap.of(MemoryModuleType.HOME, MemoryModuleStatus.VALUE_ABSENT), ImmutableList.of(Pair.of(new WalkToHouseTask(walkingSpeed), 1), Pair.of(new WalkRandomlyTask(walkingSpeed), 4), Pair.of(new WalkToPOITask(walkingSpeed, 4), 2), Pair.of(new DummyTask(20, 40), 2)))), lookAtPlayerOrVillager(), Pair.of(99, new UpdateActivityTask()));
   }

   public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> meet(VillagerProfession profession, float p_220637_1_) {
      return ImmutableList.of(Pair.of(2, new FirstShuffledTask<>(ImmutableList.of(Pair.of(new WorkTask(MemoryModuleType.MEETING_POINT, 40), 2), Pair.of(new CongregateTask(), 2)))), Pair.of(10, new ShowWaresTask(400, 1600)), Pair.of(10, new FindInteractionAndLookTargetTask(EntityType.PLAYER, 4)), Pair.of(2, new StayNearPointTask(MemoryModuleType.MEETING_POINT, p_220637_1_, 6, 100, 200)), Pair.of(3, new GiveHeroGiftsTask(100)), Pair.of(3, new ExpirePOITask(PointOfInterestType.MEETING, MemoryModuleType.MEETING_POINT)), Pair.of(3, new MultiTask<>(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET), MultiTask.Ordering.ORDERED, MultiTask.RunType.RUN_ONE, ImmutableList.of(Pair.of(new ShareItemsTask(), 1)))), lookAtMany(), Pair.of(99, new UpdateActivityTask()));
   }

   public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> idle(VillagerProfession profession, float p_220641_1_) {
      return ImmutableList.of(Pair.of(2, new FirstShuffledTask<>(ImmutableList.of(Pair.of(InteractWithEntityTask.func_220445_a(EntityType.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, p_220641_1_, 2), 2), Pair.of(new InteractWithEntityTask<>(EntityType.VILLAGER, 8, VillagerEntity::canBreed, VillagerEntity::canBreed, MemoryModuleType.BREED_TARGET, p_220641_1_, 2), 1), Pair.of(InteractWithEntityTask.func_220445_a(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, p_220641_1_, 2), 1), Pair.of(new FindWalkTargetTask(p_220641_1_), 1), Pair.of(new WalkTowardsLookTargetTask(p_220641_1_, 2), 1), Pair.of(new JumpOnBedTask(p_220641_1_), 1), Pair.of(new DummyTask(30, 60), 1)))), Pair.of(3, new GiveHeroGiftsTask(100)), Pair.of(3, new FindInteractionAndLookTargetTask(EntityType.PLAYER, 4)), Pair.of(3, new ShowWaresTask(400, 1600)), Pair.of(3, new MultiTask<>(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET), MultiTask.Ordering.ORDERED, MultiTask.RunType.RUN_ONE, ImmutableList.of(Pair.of(new ShareItemsTask(), 1)))), Pair.of(3, new MultiTask<>(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.BREED_TARGET), MultiTask.Ordering.ORDERED, MultiTask.RunType.RUN_ONE, ImmutableList.of(Pair.of(new CreateBabyVillagerTask(), 1)))), lookAtMany(), Pair.of(99, new UpdateActivityTask()));
   }

   public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> panic(VillagerProfession profession, float p_220636_1_) {
      float f = p_220636_1_ * 1.5F;
      return ImmutableList.of(Pair.of(0, new ClearHurtTask()), Pair.of(1, new FleeTask(MemoryModuleType.NEAREST_HOSTILE, f)), Pair.of(1, new FleeTask(MemoryModuleType.HURT_BY_ENTITY, f)), Pair.of(3, new FindWalkTargetTask(f, 2, 2)), lookAtPlayerOrVillager());
   }

   public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> preRaid(VillagerProfession profession, float p_220642_1_) {
      return ImmutableList.of(Pair.of(0, new RingBellTask()), Pair.of(0, new FirstShuffledTask<>(ImmutableList.of(Pair.of(new StayNearPointTask(MemoryModuleType.MEETING_POINT, p_220642_1_ * 1.5F, 2, 150, 200), 6), Pair.of(new FindWalkTargetTask(p_220642_1_ * 1.5F), 2)))), lookAtPlayerOrVillager(), Pair.of(99, new ForgetRaidTask()));
   }

   public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> raid(VillagerProfession profession, float p_220640_1_) {
      return ImmutableList.of(Pair.of(0, new FirstShuffledTask<>(ImmutableList.of(Pair.of(new GoOutsideAfterRaidTask(p_220640_1_), 5), Pair.of(new FindWalkTargetAfterRaidVictoryTask(p_220640_1_ * 1.1F), 2)))), Pair.of(0, new CelebrateRaidVictoryTask(600, 600)), Pair.of(2, new FindHidingPlaceDuringRaidTask(24, p_220640_1_ * 1.4F)), lookAtPlayerOrVillager(), Pair.of(99, new ForgetRaidTask()));
   }

   public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> hide(VillagerProfession profession, float p_220644_1_) {
      int i = 2;
      return ImmutableList.of(Pair.of(0, new ExpireHidingTask(15, 2)), Pair.of(1, new FindHidingPlaceTask(32, p_220644_1_ * 1.25F, 2)), lookAtPlayerOrVillager());
   }

   private static Pair<Integer, Task<LivingEntity>> lookAtMany() {
      return Pair.of(5, new FirstShuffledTask<>(ImmutableList.of(Pair.of(new LookAtEntityTask(EntityType.CAT, 8.0F), 8), Pair.of(new LookAtEntityTask(EntityType.VILLAGER, 8.0F), 2), Pair.of(new LookAtEntityTask(EntityType.PLAYER, 8.0F), 2), Pair.of(new LookAtEntityTask(EntityClassification.CREATURE, 8.0F), 1), Pair.of(new LookAtEntityTask(EntityClassification.WATER_CREATURE, 8.0F), 1), Pair.of(new LookAtEntityTask(EntityClassification.MONSTER, 8.0F), 1), Pair.of(new DummyTask(30, 60), 2))));
   }

   private static Pair<Integer, Task<LivingEntity>> lookAtPlayerOrVillager() {
      return Pair.of(5, new FirstShuffledTask<>(ImmutableList.of(Pair.of(new LookAtEntityTask(EntityType.VILLAGER, 8.0F), 2), Pair.of(new LookAtEntityTask(EntityType.PLAYER, 8.0F), 2), Pair.of(new DummyTask(30, 60), 8))));
   }
}