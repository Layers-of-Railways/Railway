package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.MerchantInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class NPCMerchant implements IMerchant {
   private final MerchantInventory merchantInventory;
   private final PlayerEntity customer;
   private MerchantOffers offers = new MerchantOffers();
   private int xp;

   public NPCMerchant(PlayerEntity p_i50184_1_) {
      this.customer = p_i50184_1_;
      this.merchantInventory = new MerchantInventory(this);
   }

   @Nullable
   public PlayerEntity getCustomer() {
      return this.customer;
   }

   public void setCustomer(@Nullable PlayerEntity player) {
   }

   public MerchantOffers getOffers() {
      return this.offers;
   }

   @OnlyIn(Dist.CLIENT)
   public void setClientSideOffers(@Nullable MerchantOffers offers) {
      this.offers = offers;
   }

   public void onTrade(MerchantOffer offer) {
      offer.increaseUses();
   }

   /**
    * Notifies the merchant of a possible merchantrecipe being fulfilled or not. Usually, this is just a sound byte
    * being played depending if the suggested itemstack is not null.
    */
   public void verifySellingItem(ItemStack stack) {
   }

   public World getWorld() {
      return this.customer.world;
   }

   public int getXp() {
      return this.xp;
   }

   public void setXP(int xpIn) {
      this.xp = xpIn;
   }

   public boolean func_213705_dZ() {
      return true;
   }

   public SoundEvent getYesSound() {
      return SoundEvents.ENTITY_VILLAGER_YES;
   }
}