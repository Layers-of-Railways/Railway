package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.world.server.ServerWorld;

public class ShowWaresTask extends Task<VillagerEntity> {
   @Nullable
   private ItemStack field_220559_a;
   private final List<ItemStack> field_220560_b = Lists.newArrayList();
   private int field_220561_c;
   private int field_220562_d;
   private int field_220563_e;

   public ShowWaresTask(int durationMinIn, int durationMaxIn) {
      super(ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, MemoryModuleStatus.VALUE_PRESENT), durationMinIn, durationMaxIn);
   }

   public boolean shouldExecute(ServerWorld worldIn, VillagerEntity owner) {
      Brain<?> brain = owner.getBrain();
      if (!brain.getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent()) {
         return false;
      } else {
         LivingEntity livingentity = brain.getMemory(MemoryModuleType.INTERACTION_TARGET).get();
         return livingentity.getType() == EntityType.PLAYER && owner.isAlive() && livingentity.isAlive() && !owner.isChild() && owner.getDistanceSq(livingentity) <= 17.0D;
      }
   }

   public boolean shouldContinueExecuting(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn) {
      return this.shouldExecute(worldIn, entityIn) && this.field_220563_e > 0 && entityIn.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
   }

   public void startExecuting(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn) {
      super.startExecuting(worldIn, entityIn, gameTimeIn);
      this.func_220557_c(entityIn);
      this.field_220561_c = 0;
      this.field_220562_d = 0;
      this.field_220563_e = 40;
   }

   public void updateTask(ServerWorld worldIn, VillagerEntity owner, long gameTime) {
      LivingEntity livingentity = this.func_220557_c(owner);
      this.func_220556_a(livingentity, owner);
      if (!this.field_220560_b.isEmpty()) {
         this.func_220553_d(owner);
      } else {
         owner.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
         this.field_220563_e = Math.min(this.field_220563_e, 40);
      }

      --this.field_220563_e;
   }

   public void resetTask(ServerWorld worldIn, VillagerEntity entityIn, long gameTimeIn) {
      super.resetTask(worldIn, entityIn, gameTimeIn);
      entityIn.getBrain().removeMemory(MemoryModuleType.INTERACTION_TARGET);
      entityIn.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
      this.field_220559_a = null;
   }

   private void func_220556_a(LivingEntity p_220556_1_, VillagerEntity p_220556_2_) {
      boolean flag = false;
      ItemStack itemstack = p_220556_1_.getHeldItemMainhand();
      if (this.field_220559_a == null || !ItemStack.areItemsEqual(this.field_220559_a, itemstack)) {
         this.field_220559_a = itemstack;
         flag = true;
         this.field_220560_b.clear();
      }

      if (flag && !this.field_220559_a.isEmpty()) {
         this.func_220555_b(p_220556_2_);
         if (!this.field_220560_b.isEmpty()) {
            this.field_220563_e = 900;
            this.func_220558_a(p_220556_2_);
         }
      }

   }

   private void func_220558_a(VillagerEntity p_220558_1_) {
      p_220558_1_.setItemStackToSlot(EquipmentSlotType.MAINHAND, this.field_220560_b.get(0));
   }

   private void func_220555_b(VillagerEntity p_220555_1_) {
      for(MerchantOffer merchantoffer : p_220555_1_.getOffers()) {
         if (!merchantoffer.hasNoUsesLeft() && this.func_220554_a(merchantoffer)) {
            this.field_220560_b.add(merchantoffer.getSellingStack());
         }
      }

   }

   private boolean func_220554_a(MerchantOffer p_220554_1_) {
      return ItemStack.areItemsEqual(this.field_220559_a, p_220554_1_.func_222205_b()) || ItemStack.areItemsEqual(this.field_220559_a, p_220554_1_.getBuyingStackSecond());
   }

   private LivingEntity func_220557_c(VillagerEntity p_220557_1_) {
      Brain<?> brain = p_220557_1_.getBrain();
      LivingEntity livingentity = brain.getMemory(MemoryModuleType.INTERACTION_TARGET).get();
      brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(livingentity));
      return livingentity;
   }

   private void func_220553_d(VillagerEntity p_220553_1_) {
      if (this.field_220560_b.size() >= 2 && ++this.field_220561_c >= 40) {
         ++this.field_220562_d;
         this.field_220561_c = 0;
         if (this.field_220562_d > this.field_220560_b.size() - 1) {
            this.field_220562_d = 0;
         }

         p_220553_1_.setItemStackToSlot(EquipmentSlotType.MAINHAND, this.field_220560_b.get(this.field_220562_d));
      }

   }
}