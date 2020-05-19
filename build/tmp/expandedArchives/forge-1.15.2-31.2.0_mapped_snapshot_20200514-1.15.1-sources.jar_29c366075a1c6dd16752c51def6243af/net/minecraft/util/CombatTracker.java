package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class CombatTracker {
   private final List<CombatEntry> combatEntries = Lists.newArrayList();
   private final LivingEntity fighter;
   private int lastDamageTime;
   private int combatStartTime;
   private int combatEndTime;
   private boolean inCombat;
   private boolean takingDamage;
   private String fallSuffix;

   public CombatTracker(LivingEntity fighterIn) {
      this.fighter = fighterIn;
   }

   public void calculateFallSuffix() {
      this.resetFallSuffix();
      if (this.fighter.isOnLadder()) {
         Block block = this.fighter.world.getBlockState(new BlockPos(this.fighter)).getBlock();
         if (block == Blocks.LADDER) {
            this.fallSuffix = "ladder";
         } else if (block == Blocks.VINE) {
            this.fallSuffix = "vines";
         }
      } else if (this.fighter.isInWater()) {
         this.fallSuffix = "water";
      }

   }

   /**
    * Adds an entry for the combat tracker
    */
   public void trackDamage(DamageSource damageSrc, float healthIn, float damageAmount) {
      this.reset();
      this.calculateFallSuffix();
      CombatEntry combatentry = new CombatEntry(damageSrc, this.fighter.ticksExisted, healthIn, damageAmount, this.fallSuffix, this.fighter.fallDistance);
      this.combatEntries.add(combatentry);
      this.lastDamageTime = this.fighter.ticksExisted;
      this.takingDamage = true;
      if (combatentry.isLivingDamageSrc() && !this.inCombat && this.fighter.isAlive()) {
         this.inCombat = true;
         this.combatStartTime = this.fighter.ticksExisted;
         this.combatEndTime = this.combatStartTime;
         this.fighter.sendEnterCombat();
      }

   }

   public ITextComponent getDeathMessage() {
      if (this.combatEntries.isEmpty()) {
         return new TranslationTextComponent("death.attack.generic", this.fighter.getDisplayName());
      } else {
         CombatEntry combatentry = this.getBestCombatEntry();
         CombatEntry combatentry1 = this.combatEntries.get(this.combatEntries.size() - 1);
         ITextComponent itextcomponent1 = combatentry1.getDamageSrcDisplayName();
         Entity entity = combatentry1.getDamageSrc().getTrueSource();
         ITextComponent itextcomponent;
         if (combatentry != null && combatentry1.getDamageSrc() == DamageSource.FALL) {
            ITextComponent itextcomponent2 = combatentry.getDamageSrcDisplayName();
            if (combatentry.getDamageSrc() != DamageSource.FALL && combatentry.getDamageSrc() != DamageSource.OUT_OF_WORLD) {
               if (itextcomponent2 != null && (itextcomponent1 == null || !itextcomponent2.equals(itextcomponent1))) {
                  Entity entity1 = combatentry.getDamageSrc().getTrueSource();
                  ItemStack itemstack1 = entity1 instanceof LivingEntity ? ((LivingEntity)entity1).getHeldItemMainhand() : ItemStack.EMPTY;
                  if (!itemstack1.isEmpty() && itemstack1.hasDisplayName()) {
                     itextcomponent = new TranslationTextComponent("death.fell.assist.item", this.fighter.getDisplayName(), itextcomponent2, itemstack1.getTextComponent());
                  } else {
                     itextcomponent = new TranslationTextComponent("death.fell.assist", this.fighter.getDisplayName(), itextcomponent2);
                  }
               } else if (itextcomponent1 != null) {
                  ItemStack itemstack = entity instanceof LivingEntity ? ((LivingEntity)entity).getHeldItemMainhand() : ItemStack.EMPTY;
                  if (!itemstack.isEmpty() && itemstack.hasDisplayName()) {
                     itextcomponent = new TranslationTextComponent("death.fell.finish.item", this.fighter.getDisplayName(), itextcomponent1, itemstack.getTextComponent());
                  } else {
                     itextcomponent = new TranslationTextComponent("death.fell.finish", this.fighter.getDisplayName(), itextcomponent1);
                  }
               } else {
                  itextcomponent = new TranslationTextComponent("death.fell.killer", this.fighter.getDisplayName());
               }
            } else {
               itextcomponent = new TranslationTextComponent("death.fell.accident." + this.getFallSuffix(combatentry), this.fighter.getDisplayName());
            }
         } else {
            itextcomponent = combatentry1.getDamageSrc().getDeathMessage(this.fighter);
         }

         return itextcomponent;
      }
   }

   @Nullable
   public LivingEntity getBestAttacker() {
      LivingEntity livingentity = null;
      PlayerEntity playerentity = null;
      float f = 0.0F;
      float f1 = 0.0F;

      for(CombatEntry combatentry : this.combatEntries) {
         if (combatentry.getDamageSrc().getTrueSource() instanceof PlayerEntity && (playerentity == null || combatentry.getDamage() > f1)) {
            f1 = combatentry.getDamage();
            playerentity = (PlayerEntity)combatentry.getDamageSrc().getTrueSource();
         }

         if (combatentry.getDamageSrc().getTrueSource() instanceof LivingEntity && (livingentity == null || combatentry.getDamage() > f)) {
            f = combatentry.getDamage();
            livingentity = (LivingEntity)combatentry.getDamageSrc().getTrueSource();
         }
      }

      return (LivingEntity)(playerentity != null && f1 >= f / 3.0F ? playerentity : livingentity);
   }

   @Nullable
   private CombatEntry getBestCombatEntry() {
      CombatEntry combatentry = null;
      CombatEntry combatentry1 = null;
      float f = 0.0F;
      float f1 = 0.0F;

      for(int i = 0; i < this.combatEntries.size(); ++i) {
         CombatEntry combatentry2 = this.combatEntries.get(i);
         CombatEntry combatentry3 = i > 0 ? this.combatEntries.get(i - 1) : null;
         if ((combatentry2.getDamageSrc() == DamageSource.FALL || combatentry2.getDamageSrc() == DamageSource.OUT_OF_WORLD) && combatentry2.getDamageAmount() > 0.0F && (combatentry == null || combatentry2.getDamageAmount() > f1)) {
            if (i > 0) {
               combatentry = combatentry3;
            } else {
               combatentry = combatentry2;
            }

            f1 = combatentry2.getDamageAmount();
         }

         if (combatentry2.getFallSuffix() != null && (combatentry1 == null || combatentry2.getDamage() > f)) {
            combatentry1 = combatentry2;
            f = combatentry2.getDamage();
         }
      }

      if (f1 > 5.0F && combatentry != null) {
         return combatentry;
      } else {
         return f > 5.0F && combatentry1 != null ? combatentry1 : null;
      }
   }

   private String getFallSuffix(CombatEntry entry) {
      return entry.getFallSuffix() == null ? "generic" : entry.getFallSuffix();
   }

   public int getCombatDuration() {
      return this.inCombat ? this.fighter.ticksExisted - this.combatStartTime : this.combatEndTime - this.combatStartTime;
   }

   private void resetFallSuffix() {
      this.fallSuffix = null;
   }

   /**
    * Resets this trackers list of combat entries
    */
   public void reset() {
      int i = this.inCombat ? 300 : 100;
      if (this.takingDamage && (!this.fighter.isAlive() || this.fighter.ticksExisted - this.lastDamageTime > i)) {
         boolean flag = this.inCombat;
         this.takingDamage = false;
         this.inCombat = false;
         this.combatEndTime = this.fighter.ticksExisted;
         if (flag) {
            this.fighter.sendEndCombat();
         }

         this.combatEntries.clear();
      }

   }

   /**
    * Returns EntityLivingBase assigned for this CombatTracker
    */
   public LivingEntity getFighter() {
      return this.fighter;
   }
}