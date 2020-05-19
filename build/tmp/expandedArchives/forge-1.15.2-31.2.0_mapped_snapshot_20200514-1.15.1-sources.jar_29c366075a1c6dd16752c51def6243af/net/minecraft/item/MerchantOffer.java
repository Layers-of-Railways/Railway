package net.minecraft.item;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.MathHelper;

public class MerchantOffer {
   /** The first input for this offer. */
   private final ItemStack buyingStackFirst;
   /** The second input for this offer. */
   private final ItemStack buyingStackSecond;
   /** The output of this offer. */
   private final ItemStack sellingStack;
   private int uses;
   private final int maxUses;
   private boolean doesRewardEXP = true;
   private int specialPrice;
   private int demand;
   private float priceMultiplier;
   private int givenEXP = 1;

   public MerchantOffer(CompoundNBT dataTag) {
      this.buyingStackFirst = ItemStack.read(dataTag.getCompound("buy"));
      this.buyingStackSecond = ItemStack.read(dataTag.getCompound("buyB"));
      this.sellingStack = ItemStack.read(dataTag.getCompound("sell"));
      this.uses = dataTag.getInt("uses");
      if (dataTag.contains("maxUses", 99)) {
         this.maxUses = dataTag.getInt("maxUses");
      } else {
         this.maxUses = 4;
      }

      if (dataTag.contains("rewardExp", 1)) {
         this.doesRewardEXP = dataTag.getBoolean("rewardExp");
      }

      if (dataTag.contains("xp", 3)) {
         this.givenEXP = dataTag.getInt("xp");
      }

      if (dataTag.contains("priceMultiplier", 5)) {
         this.priceMultiplier = dataTag.getFloat("priceMultiplier");
      }

      this.specialPrice = dataTag.getInt("specialPrice");
      this.demand = dataTag.getInt("demand");
   }

   public MerchantOffer(ItemStack buyingStackFirstIn, ItemStack sellingStackIn, int maxUsesIn, int givenEXPIn, float priceMultiplierIn) {
      this(buyingStackFirstIn, ItemStack.EMPTY, sellingStackIn, maxUsesIn, givenEXPIn, priceMultiplierIn);
   }

   public MerchantOffer(ItemStack buyingStackFirstIn, ItemStack buyingStackSecondIn, ItemStack sellingStackIn, int maxUsesIn, int givenEXPIn, float priceMultiplierIn) {
      this(buyingStackFirstIn, buyingStackSecondIn, sellingStackIn, 0, maxUsesIn, givenEXPIn, priceMultiplierIn);
   }

   public MerchantOffer(ItemStack buyingStackFirstIn, ItemStack buyingStackSecondIn, ItemStack sellingStackIn, int usesIn, int maxUsesIn, int givenEXPIn, float priceMultiplierIn) {
      this(buyingStackFirstIn, buyingStackSecondIn, sellingStackIn, usesIn, maxUsesIn, givenEXPIn, priceMultiplierIn, 0);
   }

   public MerchantOffer(ItemStack buyingStackFirstIn, ItemStack buyingStackSecondIn, ItemStack sellingStackIn, int usesIn, int maxUsesIn, int givenEXPIn, float priceMultiplierIn, int demandIn) {
      this.buyingStackFirst = buyingStackFirstIn;
      this.buyingStackSecond = buyingStackSecondIn;
      this.sellingStack = sellingStackIn;
      this.uses = usesIn;
      this.maxUses = maxUsesIn;
      this.givenEXP = givenEXPIn;
      this.priceMultiplier = priceMultiplierIn;
      this.demand = demandIn;
   }

   public ItemStack getBuyingStackFirst() {
      return this.buyingStackFirst;
   }

   public ItemStack func_222205_b() {
      int i = this.buyingStackFirst.getCount();
      ItemStack itemstack = this.buyingStackFirst.copy();
      int j = Math.max(0, MathHelper.floor((float)(i * this.demand) * this.priceMultiplier));
      itemstack.setCount(MathHelper.clamp(i + j + this.specialPrice, 1, this.buyingStackFirst.getItem().getMaxStackSize()));
      return itemstack;
   }

   public ItemStack getBuyingStackSecond() {
      return this.buyingStackSecond;
   }

   public ItemStack getSellingStack() {
      return this.sellingStack;
   }

   /**
    * Calculates the demand with following formula: demand = demand + uses - maxUses - uses
    */
   public void calculateDemand() {
      this.demand = this.demand + this.uses - (this.maxUses - this.uses);
   }

   public ItemStack getCopyOfSellingStack() {
      return this.sellingStack.copy();
   }

   public int getUses() {
      return this.uses;
   }

   public void resetUses() {
      this.uses = 0;
   }

   public int func_222214_i() {
      return this.maxUses;
   }

   public void increaseUses() {
      ++this.uses;
   }

   public int getDemand() {
      return this.demand;
   }

   public void increaseSpecialPrice(int add) {
      this.specialPrice += add;
   }

   public void resetSpecialPrice() {
      this.specialPrice = 0;
   }

   public int getSpecialPrice() {
      return this.specialPrice;
   }

   public void setSpecialPrice(int price) {
      this.specialPrice = price;
   }

   public float getPriceMultiplier() {
      return this.priceMultiplier;
   }

   public int getGivenExp() {
      return this.givenEXP;
   }

   public boolean hasNoUsesLeft() {
      return this.uses >= this.maxUses;
   }

   public void getMaxUses() {
      this.uses = this.maxUses;
   }

   public boolean hasBeenUsed() {
      return this.uses > 0;
   }

   public boolean getDoesRewardExp() {
      return this.doesRewardEXP;
   }

   public CompoundNBT write() {
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.put("buy", this.buyingStackFirst.write(new CompoundNBT()));
      compoundnbt.put("sell", this.sellingStack.write(new CompoundNBT()));
      compoundnbt.put("buyB", this.buyingStackSecond.write(new CompoundNBT()));
      compoundnbt.putInt("uses", this.uses);
      compoundnbt.putInt("maxUses", this.maxUses);
      compoundnbt.putBoolean("rewardExp", this.doesRewardEXP);
      compoundnbt.putInt("xp", this.givenEXP);
      compoundnbt.putFloat("priceMultiplier", this.priceMultiplier);
      compoundnbt.putInt("specialPrice", this.specialPrice);
      compoundnbt.putInt("demand", this.demand);
      return compoundnbt;
   }

   public boolean matches(ItemStack p_222204_1_, ItemStack p_222204_2_) {
      return this.equalIgnoringDamage(p_222204_1_, this.func_222205_b()) && p_222204_1_.getCount() >= this.func_222205_b().getCount() && this.equalIgnoringDamage(p_222204_2_, this.buyingStackSecond) && p_222204_2_.getCount() >= this.buyingStackSecond.getCount();
   }

   private boolean equalIgnoringDamage(ItemStack left, ItemStack right) {
      if (right.isEmpty() && left.isEmpty()) {
         return true;
      } else {
         ItemStack itemstack = left.copy();
         if (itemstack.getItem().isDamageable()) {
            itemstack.setDamage(itemstack.getDamage());
         }

         return ItemStack.areItemsEqual(itemstack, right) && (!right.hasTag() || itemstack.hasTag() && NBTUtil.areNBTEquals(right.getTag(), itemstack.getTag(), false));
      }
   }

   public boolean doTransaction(ItemStack p_222215_1_, ItemStack p_222215_2_) {
      if (!this.matches(p_222215_1_, p_222215_2_)) {
         return false;
      } else {
         p_222215_1_.shrink(this.func_222205_b().getCount());
         if (!this.getBuyingStackSecond().isEmpty()) {
            p_222215_2_.shrink(this.getBuyingStackSecond().getCount());
         }

         return true;
      }
   }
}