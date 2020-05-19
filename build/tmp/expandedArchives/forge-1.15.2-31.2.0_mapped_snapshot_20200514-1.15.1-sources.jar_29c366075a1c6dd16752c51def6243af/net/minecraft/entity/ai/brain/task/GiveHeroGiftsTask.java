package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTables;

public class GiveHeroGiftsTask extends Task<VillagerEntity> {
   private static final Map<VillagerProfession, ResourceLocation> GIFTS = Util.make(Maps.newHashMap(), (p_220395_0_) -> {
      p_220395_0_.put(VillagerProfession.ARMORER, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_ARMORER_GIFT);
      p_220395_0_.put(VillagerProfession.BUTCHER, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_BUTCHER_GIFT);
      p_220395_0_.put(VillagerProfession.CARTOGRAPHER, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_CARTOGRAPHER_GIFT);
      p_220395_0_.put(VillagerProfession.CLERIC, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_CLERIC_GIFT);
      p_220395_0_.put(VillagerProfession.FARMER, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_FARMER_GIFT);
      p_220395_0_.put(VillagerProfession.FISHERMAN, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_FISHERMAN_GIFT);
      p_220395_0_.put(VillagerProfession.FLETCHER, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_FLETCHER_GIFT);
      p_220395_0_.put(VillagerProfession.LEATHERWORKER, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_LEATHERWORKER_GIFT);
      p_220395_0_.put(VillagerProfession.LIBRARIAN, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_LIBRARIAN_GIFT);
      p_220395_0_.put(VillagerProfession.MASON, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_MASON_GIFT);
      p_220395_0_.put(VillagerProfession.SHEPHERD, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_SHEPHERD_GIFT);
      p_220395_0_.put(VillagerProfession.TOOLSMITH, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_TOOLSMITH_GIFT);
      p_220395_0_.put(VillagerProfession.WEAPONSMITH, LootTables.GAMEPLAY_HERO_OF_THE_VILLAGE_WEAPONSMITH_GIFT);
   });
   private int cooldown = 600;
   private boolean done;
   private long startTime;

   public GiveHeroGiftsTask(int p_i50366_1_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.INTERACTION_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleStatus.VALUE_PRESENT), p_i50366_1_);
   }

   protected boolean shouldExecute(ServerWorld worldIn, VillagerEntity owner) {
      if (!this.hasNearestPlayer(owner)) {
         return false;
      } else if (this.cooldown > 0) {
         --this.cooldown;
         return false;
      } else {
         return true;
      }
   }

   protected void startExecuting(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn) {
      this.done = false;
      this.startTime = gameTimeIn;
      PlayerEntity playerentity = this.getNearestPlayer(entityIn).get();
      entityIn.getBrain().setMemory(MemoryModuleType.INTERACTION_TARGET, playerentity);
      BrainUtil.lookAt(entityIn, playerentity);
   }

   protected boolean shouldContinueExecuting(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn) {
      return this.hasNearestPlayer(entityIn) && !this.done;
   }

   protected void updateTask(ServerWorld worldIn, VillagerEntity owner, long gameTime) {
      PlayerEntity playerentity = this.getNearestPlayer(owner).get();
      BrainUtil.lookAt(owner, playerentity);
      if (this.isCloseEnough(owner, playerentity)) {
         if (gameTime - this.startTime > 20L) {
            this.giveGifts(owner, playerentity);
            this.done = true;
         }
      } else {
         BrainUtil.approach(owner, playerentity, 5);
      }

   }

   protected void resetTask(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn) {
      this.cooldown = getNextCooldown(worldIn);
      entityIn.getBrain().removeMemory(MemoryModuleType.INTERACTION_TARGET);
      entityIn.getBrain().removeMemory(MemoryModuleType.WALK_TARGET);
      entityIn.getBrain().removeMemory(MemoryModuleType.LOOK_TARGET);
   }

   private void giveGifts(VillagerEntity p_220398_1_, LivingEntity p_220398_2_) {
      for(ItemStack itemstack : this.getGifts(p_220398_1_)) {
         BrainUtil.throwItemAt(p_220398_1_, itemstack, p_220398_2_);
      }

   }

   private List<ItemStack> getGifts(VillagerEntity p_220399_1_) {
      if (p_220399_1_.isChild()) {
         return ImmutableList.of(new ItemStack(Items.POPPY));
      } else {
         VillagerProfession villagerprofession = p_220399_1_.getVillagerData().getProfession();
         if (GIFTS.containsKey(villagerprofession)) {
            LootTable loottable = p_220399_1_.world.getServer().getLootTableManager().getLootTableFromLocation(GIFTS.get(villagerprofession));
            LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerWorld)p_220399_1_.world)).withParameter(LootParameters.POSITION, new BlockPos(p_220399_1_)).withParameter(LootParameters.THIS_ENTITY, p_220399_1_).withRandom(p_220399_1_.getRNG());
            return loottable.generate(lootcontext$builder.build(LootParameterSets.GIFT));
         } else {
            return ImmutableList.of(new ItemStack(Items.WHEAT_SEEDS));
         }
      }
   }

   private boolean hasNearestPlayer(VillagerEntity p_220396_1_) {
      return this.getNearestPlayer(p_220396_1_).isPresent();
   }

   private Optional<PlayerEntity> getNearestPlayer(VillagerEntity p_220400_1_) {
      return p_220400_1_.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER).filter(this::isHero);
   }

   private boolean isHero(PlayerEntity p_220402_1_) {
      return p_220402_1_.isPotionActive(Effects.HERO_OF_THE_VILLAGE);
   }

   private boolean isCloseEnough(VillagerEntity p_220401_1_, PlayerEntity p_220401_2_) {
      BlockPos blockpos = new BlockPos(p_220401_2_);
      BlockPos blockpos1 = new BlockPos(p_220401_1_);
      return blockpos1.withinDistance(blockpos, 5.0D);
   }

   private static int getNextCooldown(ServerWorld p_220397_0_) {
      return 600 + p_220397_0_.rand.nextInt(6001);
   }
}