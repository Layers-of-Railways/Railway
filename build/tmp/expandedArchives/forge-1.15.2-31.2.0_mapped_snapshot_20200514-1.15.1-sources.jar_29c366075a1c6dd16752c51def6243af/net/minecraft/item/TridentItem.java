package net.minecraft.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TridentItem extends Item {
   public TridentItem(Item.Properties builder) {
      super(builder);
      this.addPropertyOverride(new ResourceLocation("throwing"), (p_210315_0_, p_210315_1_, p_210315_2_) -> {
         return p_210315_2_ != null && p_210315_2_.isHandActive() && p_210315_2_.getActiveItemStack() == p_210315_0_ ? 1.0F : 0.0F;
      });
   }

   public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
      return !player.isCreative();
   }

   /**
    * returns the action that specifies what animation to play when the items is being used
    */
   public UseAction getUseAction(ItemStack stack) {
      return UseAction.SPEAR;
   }

   /**
    * How long it takes to use or consume an item
    */
   public int getUseDuration(ItemStack stack) {
      return 72000;
   }

   /**
    * Called when the player stops using an Item (stops holding the right mouse button).
    */
   public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
      if (entityLiving instanceof PlayerEntity) {
         PlayerEntity playerentity = (PlayerEntity)entityLiving;
         int i = this.getUseDuration(stack) - timeLeft;
         if (i >= 10) {
            int j = EnchantmentHelper.getRiptideModifier(stack);
            if (j <= 0 || playerentity.isWet()) {
               if (!worldIn.isRemote) {
                  stack.damageItem(1, playerentity, (p_220047_1_) -> {
                     p_220047_1_.sendBreakAnimation(entityLiving.getActiveHand());
                  });
                  if (j == 0) {
                     TridentEntity tridententity = new TridentEntity(worldIn, playerentity, stack);
                     tridententity.shoot(playerentity, playerentity.rotationPitch, playerentity.rotationYaw, 0.0F, 2.5F + (float)j * 0.5F, 1.0F);
                     if (playerentity.abilities.isCreativeMode) {
                        tridententity.pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
                     }

                     worldIn.addEntity(tridententity);
                     worldIn.playMovingSound((PlayerEntity)null, tridententity, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
                     if (!playerentity.abilities.isCreativeMode) {
                        playerentity.inventory.deleteStack(stack);
                     }
                  }
               }

               playerentity.addStat(Stats.ITEM_USED.get(this));
               if (j > 0) {
                  float f7 = playerentity.rotationYaw;
                  float f = playerentity.rotationPitch;
                  float f1 = -MathHelper.sin(f7 * ((float)Math.PI / 180F)) * MathHelper.cos(f * ((float)Math.PI / 180F));
                  float f2 = -MathHelper.sin(f * ((float)Math.PI / 180F));
                  float f3 = MathHelper.cos(f7 * ((float)Math.PI / 180F)) * MathHelper.cos(f * ((float)Math.PI / 180F));
                  float f4 = MathHelper.sqrt(f1 * f1 + f2 * f2 + f3 * f3);
                  float f5 = 3.0F * ((1.0F + (float)j) / 4.0F);
                  f1 = f1 * (f5 / f4);
                  f2 = f2 * (f5 / f4);
                  f3 = f3 * (f5 / f4);
                  playerentity.addVelocity((double)f1, (double)f2, (double)f3);
                  playerentity.startSpinAttack(20);
                  if (playerentity.onGround) {
                     float f6 = 1.1999999F;
                     playerentity.move(MoverType.SELF, new Vec3d(0.0D, (double)1.1999999F, 0.0D));
                  }

                  SoundEvent soundevent;
                  if (j >= 3) {
                     soundevent = SoundEvents.ITEM_TRIDENT_RIPTIDE_3;
                  } else if (j == 2) {
                     soundevent = SoundEvents.ITEM_TRIDENT_RIPTIDE_2;
                  } else {
                     soundevent = SoundEvents.ITEM_TRIDENT_RIPTIDE_1;
                  }

                  worldIn.playMovingSound((PlayerEntity)null, playerentity, soundevent, SoundCategory.PLAYERS, 1.0F, 1.0F);
               }

            }
         }
      }
   }

   /**
    * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
    * {@link #onItemUse}.
    */
   public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      if (itemstack.getDamage() >= itemstack.getMaxDamage() - 1) {
         return ActionResult.resultFail(itemstack);
      } else if (EnchantmentHelper.getRiptideModifier(itemstack) > 0 && !playerIn.isWet()) {
         return ActionResult.resultFail(itemstack);
      } else {
         playerIn.setActiveHand(handIn);
         return ActionResult.resultConsume(itemstack);
      }
   }

   /**
    * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
    * the damage on the stack.
    */
   public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      stack.damageItem(1, attacker, (p_220048_0_) -> {
         p_220048_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND);
      });
      return true;
   }

   /**
    * Called when a Block is destroyed using this Item. Return true to trigger the "Use Item" statistic.
    */
   public boolean onBlockDestroyed(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
      if ((double)state.getBlockHardness(worldIn, pos) != 0.0D) {
         stack.damageItem(2, entityLiving, (p_220046_0_) -> {
            p_220046_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND);
         });
      }

      return true;
   }

   /**
    * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
    */
   public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
      Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot);
      if (equipmentSlot == EquipmentSlotType.MAINHAND) {
         multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", 8.0D, AttributeModifier.Operation.ADDITION));
         multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", (double)-2.9F, AttributeModifier.Operation.ADDITION));
      }

      return multimap;
   }

   /**
    * Return the enchantability factor of the item, most of the time is based on material.
    */
   public int getItemEnchantability() {
      return 1;
   }
}