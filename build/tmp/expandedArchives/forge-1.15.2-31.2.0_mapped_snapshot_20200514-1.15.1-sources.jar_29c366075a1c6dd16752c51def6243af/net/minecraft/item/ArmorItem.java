package net.minecraft.item;

import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ArmorItem extends Item {
   private static final UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
   public static final IDispenseItemBehavior DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior() {
      /**
       * Dispense the specified stack, play the dispense sound and spawn particles.
       */
      protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
         return ArmorItem.func_226626_a_(source, stack) ? stack : super.dispenseStack(source, stack);
      }
   };
   protected final EquipmentSlotType slot;
   protected final int damageReduceAmount;
   protected final float toughness;
   protected final IArmorMaterial material;

   public static boolean func_226626_a_(IBlockSource p_226626_0_, ItemStack p_226626_1_) {
      BlockPos blockpos = p_226626_0_.getBlockPos().offset(p_226626_0_.getBlockState().get(DispenserBlock.FACING));
      List<LivingEntity> list = p_226626_0_.getWorld().getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(blockpos), EntityPredicates.NOT_SPECTATING.and(new EntityPredicates.ArmoredMob(p_226626_1_)));
      if (list.isEmpty()) {
         return false;
      } else {
         LivingEntity livingentity = list.get(0);
         EquipmentSlotType equipmentslottype = MobEntity.getSlotForItemStack(p_226626_1_);
         ItemStack itemstack = p_226626_1_.split(1);
         livingentity.setItemStackToSlot(equipmentslottype, itemstack);
         if (livingentity instanceof MobEntity) {
            ((MobEntity)livingentity).setDropChance(equipmentslottype, 2.0F);
            ((MobEntity)livingentity).enablePersistence();
         }

         return true;
      }
   }

   public ArmorItem(IArmorMaterial materialIn, EquipmentSlotType slot, Item.Properties builder) {
      super(builder.defaultMaxDamage(materialIn.getDurability(slot)));
      this.material = materialIn;
      this.slot = slot;
      this.damageReduceAmount = materialIn.getDamageReductionAmount(slot);
      this.toughness = materialIn.getToughness();
      DispenserBlock.registerDispenseBehavior(this, DISPENSER_BEHAVIOR);
   }

   /**
    * Gets the equipment slot of this armor piece (formerly known as armor type)
    */
   public EquipmentSlotType getEquipmentSlot() {
      return this.slot;
   }

   /**
    * Return the enchantability factor of the item, most of the time is based on material.
    */
   public int getItemEnchantability() {
      return this.material.getEnchantability();
   }

   public IArmorMaterial getArmorMaterial() {
      return this.material;
   }

   /**
    * Return whether this item is repairable in an anvil.
    */
   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      return this.material.getRepairMaterial().test(repair) || super.getIsRepairable(toRepair, repair);
   }

   /**
    * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
    * {@link #onItemUse}.
    */
   public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      EquipmentSlotType equipmentslottype = MobEntity.getSlotForItemStack(itemstack);
      ItemStack itemstack1 = playerIn.getItemStackFromSlot(equipmentslottype);
      if (itemstack1.isEmpty()) {
         playerIn.setItemStackToSlot(equipmentslottype, itemstack.copy());
         itemstack.setCount(0);
         return ActionResult.resultSuccess(itemstack);
      } else {
         return ActionResult.resultFail(itemstack);
      }
   }

   /**
    * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
    */
   public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
      Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot);
      if (equipmentSlot == this.slot) {
         multimap.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor modifier", (double)this.damageReduceAmount, AttributeModifier.Operation.ADDITION));
         multimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor toughness", (double)this.toughness, AttributeModifier.Operation.ADDITION));
      }

      return multimap;
   }

   public int getDamageReduceAmount() {
      return this.damageReduceAmount;
   }

   public float getToughness() {
      return this.toughness;
   }
}