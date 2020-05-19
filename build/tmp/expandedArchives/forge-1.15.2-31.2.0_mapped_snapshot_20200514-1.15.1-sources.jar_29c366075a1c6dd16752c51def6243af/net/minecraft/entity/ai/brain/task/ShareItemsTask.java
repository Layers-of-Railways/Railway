package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.server.ServerWorld;

public class ShareItemsTask extends Task<VillagerEntity> {
   private Set<Item> field_220588_a = ImmutableSet.of();

   public ShareItemsTask() {
      super(ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleStatus.VALUE_PRESENT));
   }

   protected boolean shouldExecute(ServerWorld worldIn, VillagerEntity owner) {
      return BrainUtil.isCorrectVisibleType(owner.getBrain(), MemoryModuleType.INTERACTION_TARGET, EntityType.VILLAGER);
   }

   protected boolean shouldContinueExecuting(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn) {
      return this.shouldExecute(worldIn, entityIn);
   }

   protected void startExecuting(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn) {
      VillagerEntity villagerentity = (VillagerEntity)entityIn.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).get();
      BrainUtil.lookApproachEachOther(entityIn, villagerentity);
      this.field_220588_a = func_220585_a(entityIn, villagerentity);
   }

   protected void updateTask(ServerWorld worldIn, VillagerEntity owner, long gameTime) {
      VillagerEntity villagerentity = (VillagerEntity)owner.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).get();
      if (!(owner.getDistanceSq(villagerentity) > 5.0D)) {
         BrainUtil.lookApproachEachOther(owner, villagerentity);
         owner.func_213746_a(villagerentity, gameTime);
         if (owner.canAbondonItems() && (owner.getVillagerData().getProfession() == VillagerProfession.FARMER || villagerentity.wantsMoreFood())) {
            func_220586_a(owner, VillagerEntity.FOOD_VALUES.keySet(), villagerentity);
         }

         if (!this.field_220588_a.isEmpty() && owner.getVillagerInventory().hasAny(this.field_220588_a)) {
            func_220586_a(owner, this.field_220588_a, villagerentity);
         }

      }
   }

   protected void resetTask(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn) {
      entityIn.getBrain().removeMemory(MemoryModuleType.INTERACTION_TARGET);
   }

   private static Set<Item> func_220585_a(VillagerEntity p_220585_0_, VillagerEntity p_220585_1_) {
      ImmutableSet<Item> immutableset = p_220585_1_.getVillagerData().getProfession().getSpecificItems();
      ImmutableSet<Item> immutableset1 = p_220585_0_.getVillagerData().getProfession().getSpecificItems();
      return immutableset.stream().filter((p_220587_1_) -> {
         return !immutableset1.contains(p_220587_1_);
      }).collect(Collectors.toSet());
   }

   private static void func_220586_a(VillagerEntity p_220586_0_, Set<Item> p_220586_1_, LivingEntity p_220586_2_) {
      Inventory inventory = p_220586_0_.getVillagerInventory();
      ItemStack itemstack = ItemStack.EMPTY;
      int i = 0;

      while(i < inventory.getSizeInventory()) {
         ItemStack itemstack1;
         Item item;
         int j;
         label28: {
            itemstack1 = inventory.getStackInSlot(i);
            if (!itemstack1.isEmpty()) {
               item = itemstack1.getItem();
               if (p_220586_1_.contains(item)) {
                  if (itemstack1.getCount() > itemstack1.getMaxStackSize() / 2) {
                     j = itemstack1.getCount() / 2;
                     break label28;
                  }

                  if (itemstack1.getCount() > 24) {
                     j = itemstack1.getCount() - 24;
                     break label28;
                  }
               }
            }

            ++i;
            continue;
         }

         itemstack1.shrink(j);
         itemstack = new ItemStack(item, j);
         break;
      }

      if (!itemstack.isEmpty()) {
         BrainUtil.throwItemAt(p_220586_0_, itemstack, p_220586_2_);
      }

   }
}