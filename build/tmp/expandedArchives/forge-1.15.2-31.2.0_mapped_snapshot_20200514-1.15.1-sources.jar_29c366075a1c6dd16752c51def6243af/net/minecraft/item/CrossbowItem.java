package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ICrossbowUser;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.FireworkRocketEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CrossbowItem extends ShootableItem {
   /** Set to {@code true} when the crossbow is 20% charged. */
   private boolean isLoadingStart = false;
   /** Set to {@code true} when the crossbow is 50% charged. */
   private boolean isLoadingMiddle = false;

   public CrossbowItem(Item.Properties propertiesIn) {
      super(propertiesIn);
      this.addPropertyOverride(new ResourceLocation("pull"), (p_220022_1_, p_220022_2_, p_220022_3_) -> {
         if (p_220022_3_ != null && p_220022_1_.getItem() == this) {
            return isCharged(p_220022_1_) ? 0.0F : (float)(p_220022_1_.getUseDuration() - p_220022_3_.getItemInUseCount()) / (float)getChargeTime(p_220022_1_);
         } else {
            return 0.0F;
         }
      });
      this.addPropertyOverride(new ResourceLocation("pulling"), (p_220033_0_, p_220033_1_, p_220033_2_) -> {
         return p_220033_2_ != null && p_220033_2_.isHandActive() && p_220033_2_.getActiveItemStack() == p_220033_0_ && !isCharged(p_220033_0_) ? 1.0F : 0.0F;
      });
      this.addPropertyOverride(new ResourceLocation("charged"), (p_220030_0_, p_220030_1_, p_220030_2_) -> {
         return p_220030_2_ != null && isCharged(p_220030_0_) ? 1.0F : 0.0F;
      });
      this.addPropertyOverride(new ResourceLocation("firework"), (p_220020_0_, p_220020_1_, p_220020_2_) -> {
         return p_220020_2_ != null && isCharged(p_220020_0_) && hasChargedProjectile(p_220020_0_, Items.FIREWORK_ROCKET) ? 1.0F : 0.0F;
      });
   }

   public Predicate<ItemStack> getAmmoPredicate() {
      return ARROWS_OR_FIREWORKS;
   }

   /**
    * Get the predicate to match ammunition when searching the player's inventory, not their main/offhand
    */
   public Predicate<ItemStack> getInventoryAmmoPredicate() {
      return ARROWS;
   }

   /**
    * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
    * {@link #onItemUse}.
    */
   public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      if (isCharged(itemstack)) {
         fireProjectiles(worldIn, playerIn, handIn, itemstack, func_220013_l(itemstack), 1.0F);
         setCharged(itemstack, false);
         return ActionResult.resultConsume(itemstack);
      } else if (!playerIn.findAmmo(itemstack).isEmpty()) {
         if (!isCharged(itemstack)) {
            this.isLoadingStart = false;
            this.isLoadingMiddle = false;
            playerIn.setActiveHand(handIn);
         }

         return ActionResult.resultConsume(itemstack);
      } else {
         return ActionResult.resultFail(itemstack);
      }
   }

   /**
    * Called when the player stops using an Item (stops holding the right mouse button).
    */
   public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
      int i = this.getUseDuration(stack) - timeLeft;
      float f = getCharge(i, stack);
      if (f >= 1.0F && !isCharged(stack) && hasAmmo(entityLiving, stack)) {
         setCharged(stack, true);
         SoundCategory soundcategory = entityLiving instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
         worldIn.playSound((PlayerEntity)null, entityLiving.getPosX(), entityLiving.getPosY(), entityLiving.getPosZ(), SoundEvents.ITEM_CROSSBOW_LOADING_END, soundcategory, 1.0F, 1.0F / (random.nextFloat() * 0.5F + 1.0F) + 0.2F);
      }

   }

   private static boolean hasAmmo(LivingEntity entityIn, ItemStack stack) {
      int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.MULTISHOT, stack);
      int j = i == 0 ? 1 : 3;
      boolean flag = entityIn instanceof PlayerEntity && ((PlayerEntity)entityIn).abilities.isCreativeMode;
      ItemStack itemstack = entityIn.findAmmo(stack);
      ItemStack itemstack1 = itemstack.copy();

      for(int k = 0; k < j; ++k) {
         if (k > 0) {
            itemstack = itemstack1.copy();
         }

         if (itemstack.isEmpty() && flag) {
            itemstack = new ItemStack(Items.ARROW);
            itemstack1 = itemstack.copy();
         }

         if (!func_220023_a(entityIn, stack, itemstack, k > 0, flag)) {
            return false;
         }
      }

      return true;
   }

   private static boolean func_220023_a(LivingEntity p_220023_0_, ItemStack p_220023_1_, ItemStack p_220023_2_, boolean p_220023_3_, boolean p_220023_4_) {
      if (p_220023_2_.isEmpty()) {
         return false;
      } else {
         boolean flag = p_220023_4_ && p_220023_2_.getItem() instanceof ArrowItem;
         ItemStack itemstack;
         if (!flag && !p_220023_4_ && !p_220023_3_) {
            itemstack = p_220023_2_.split(1);
            if (p_220023_2_.isEmpty() && p_220023_0_ instanceof PlayerEntity) {
               ((PlayerEntity)p_220023_0_).inventory.deleteStack(p_220023_2_);
            }
         } else {
            itemstack = p_220023_2_.copy();
         }

         addChargedProjectile(p_220023_1_, itemstack);
         return true;
      }
   }

   public static boolean isCharged(ItemStack stack) {
      CompoundNBT compoundnbt = stack.getTag();
      return compoundnbt != null && compoundnbt.getBoolean("Charged");
   }

   public static void setCharged(ItemStack stack, boolean chargedIn) {
      CompoundNBT compoundnbt = stack.getOrCreateTag();
      compoundnbt.putBoolean("Charged", chargedIn);
   }

   private static void addChargedProjectile(ItemStack crossbow, ItemStack projectile) {
      CompoundNBT compoundnbt = crossbow.getOrCreateTag();
      ListNBT listnbt;
      if (compoundnbt.contains("ChargedProjectiles", 9)) {
         listnbt = compoundnbt.getList("ChargedProjectiles", 10);
      } else {
         listnbt = new ListNBT();
      }

      CompoundNBT compoundnbt1 = new CompoundNBT();
      projectile.write(compoundnbt1);
      listnbt.add(compoundnbt1);
      compoundnbt.put("ChargedProjectiles", listnbt);
   }

   private static List<ItemStack> getChargedProjectiles(ItemStack stack) {
      List<ItemStack> list = Lists.newArrayList();
      CompoundNBT compoundnbt = stack.getTag();
      if (compoundnbt != null && compoundnbt.contains("ChargedProjectiles", 9)) {
         ListNBT listnbt = compoundnbt.getList("ChargedProjectiles", 10);
         if (listnbt != null) {
            for(int i = 0; i < listnbt.size(); ++i) {
               CompoundNBT compoundnbt1 = listnbt.getCompound(i);
               list.add(ItemStack.read(compoundnbt1));
            }
         }
      }

      return list;
   }

   private static void clearProjectiles(ItemStack stack) {
      CompoundNBT compoundnbt = stack.getTag();
      if (compoundnbt != null) {
         ListNBT listnbt = compoundnbt.getList("ChargedProjectiles", 9);
         listnbt.clear();
         compoundnbt.put("ChargedProjectiles", listnbt);
      }

   }

   private static boolean hasChargedProjectile(ItemStack stack, Item ammoItem) {
      return getChargedProjectiles(stack).stream().anyMatch((p_220010_1_) -> {
         return p_220010_1_.getItem() == ammoItem;
      });
   }

   private static void fireProjectile(World worldIn, LivingEntity shooter, Hand handIn, ItemStack crossbow, ItemStack projectile, float soundPitch, boolean isCreativeMode, float velocity, float inaccuracy, float projectileAngle) {
      if (!worldIn.isRemote) {
         boolean flag = projectile.getItem() == Items.FIREWORK_ROCKET;
         IProjectile iprojectile;
         if (flag) {
            iprojectile = new FireworkRocketEntity(worldIn, projectile, shooter.getPosX(), shooter.getPosYEye() - (double)0.15F, shooter.getPosZ(), true);
         } else {
            iprojectile = createArrow(worldIn, shooter, crossbow, projectile);
            if (isCreativeMode || projectileAngle != 0.0F) {
               ((AbstractArrowEntity)iprojectile).pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
            }
         }

         if (shooter instanceof ICrossbowUser) {
            ICrossbowUser icrossbowuser = (ICrossbowUser)shooter;
            icrossbowuser.shoot(icrossbowuser.getAttackTarget(), crossbow, iprojectile, projectileAngle);
         } else {
            Vec3d vec3d1 = shooter.getUpVector(1.0F);
            Quaternion quaternion = new Quaternion(new Vector3f(vec3d1), projectileAngle, true);
            Vec3d vec3d = shooter.getLook(1.0F);
            Vector3f vector3f = new Vector3f(vec3d);
            vector3f.transform(quaternion);
            iprojectile.shoot((double)vector3f.getX(), (double)vector3f.getY(), (double)vector3f.getZ(), velocity, inaccuracy);
         }

         crossbow.damageItem(flag ? 3 : 1, shooter, (p_220017_1_) -> {
            p_220017_1_.sendBreakAnimation(handIn);
         });
         worldIn.addEntity((Entity)iprojectile);
         worldIn.playSound((PlayerEntity)null, shooter.getPosX(), shooter.getPosY(), shooter.getPosZ(), SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0F, soundPitch);
      }
   }

   private static AbstractArrowEntity createArrow(World worldIn, LivingEntity shooter, ItemStack crossbow, ItemStack ammo) {
      ArrowItem arrowitem = (ArrowItem)(ammo.getItem() instanceof ArrowItem ? ammo.getItem() : Items.ARROW);
      AbstractArrowEntity abstractarrowentity = arrowitem.createArrow(worldIn, ammo, shooter);
      if (shooter instanceof PlayerEntity) {
         abstractarrowentity.setIsCritical(true);
      }

      abstractarrowentity.setHitSound(SoundEvents.ITEM_CROSSBOW_HIT);
      abstractarrowentity.setShotFromCrossbow(true);
      int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.PIERCING, crossbow);
      if (i > 0) {
         abstractarrowentity.setPierceLevel((byte)i);
      }

      return abstractarrowentity;
   }

   public static void fireProjectiles(World worldIn, LivingEntity shooter, Hand handIn, ItemStack stack, float velocityIn, float inaccuracyIn) {
      List<ItemStack> list = getChargedProjectiles(stack);
      float[] afloat = getRandomSoundPitches(shooter.getRNG());

      for(int i = 0; i < list.size(); ++i) {
         ItemStack itemstack = list.get(i);
         boolean flag = shooter instanceof PlayerEntity && ((PlayerEntity)shooter).abilities.isCreativeMode;
         if (!itemstack.isEmpty()) {
            if (i == 0) {
               fireProjectile(worldIn, shooter, handIn, stack, itemstack, afloat[i], flag, velocityIn, inaccuracyIn, 0.0F);
            } else if (i == 1) {
               fireProjectile(worldIn, shooter, handIn, stack, itemstack, afloat[i], flag, velocityIn, inaccuracyIn, -10.0F);
            } else if (i == 2) {
               fireProjectile(worldIn, shooter, handIn, stack, itemstack, afloat[i], flag, velocityIn, inaccuracyIn, 10.0F);
            }
         }
      }

      fireProjectilesAfter(worldIn, shooter, stack);
   }

   private static float[] getRandomSoundPitches(Random rand) {
      boolean flag = rand.nextBoolean();
      return new float[]{1.0F, getRandomSoundPitch(flag), getRandomSoundPitch(!flag)};
   }

   private static float getRandomSoundPitch(boolean flagIn) {
      float f = flagIn ? 0.63F : 0.43F;
      return 1.0F / (random.nextFloat() * 0.5F + 1.8F) + f;
   }

   /**
    * Called after {@plainlink #fireProjectiles} to clear the charged projectile and to update the player advancements.
    */
   private static void fireProjectilesAfter(World worldIn, LivingEntity shooter, ItemStack stack) {
      if (shooter instanceof ServerPlayerEntity) {
         ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)shooter;
         if (!worldIn.isRemote) {
            CriteriaTriggers.SHOT_CROSSBOW.func_215111_a(serverplayerentity, stack);
         }

         serverplayerentity.addStat(Stats.ITEM_USED.get(stack.getItem()));
      }

      clearProjectiles(stack);
   }

   /**
    * Called as the item is being used by an entity.
    */
   public void onUse(World worldIn, LivingEntity livingEntityIn, ItemStack stack, int count) {
      if (!worldIn.isRemote) {
         int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.QUICK_CHARGE, stack);
         SoundEvent soundevent = this.getSoundEvent(i);
         SoundEvent soundevent1 = i == 0 ? SoundEvents.ITEM_CROSSBOW_LOADING_MIDDLE : null;
         float f = (float)(stack.getUseDuration() - count) / (float)getChargeTime(stack);
         if (f < 0.2F) {
            this.isLoadingStart = false;
            this.isLoadingMiddle = false;
         }

         if (f >= 0.2F && !this.isLoadingStart) {
            this.isLoadingStart = true;
            worldIn.playSound((PlayerEntity)null, livingEntityIn.getPosX(), livingEntityIn.getPosY(), livingEntityIn.getPosZ(), soundevent, SoundCategory.PLAYERS, 0.5F, 1.0F);
         }

         if (f >= 0.5F && soundevent1 != null && !this.isLoadingMiddle) {
            this.isLoadingMiddle = true;
            worldIn.playSound((PlayerEntity)null, livingEntityIn.getPosX(), livingEntityIn.getPosY(), livingEntityIn.getPosZ(), soundevent1, SoundCategory.PLAYERS, 0.5F, 1.0F);
         }
      }

   }

   /**
    * How long it takes to use or consume an item
    */
   public int getUseDuration(ItemStack stack) {
      return getChargeTime(stack) + 3;
   }

   /**
    * The time the crossbow must be used to reload it
    */
   public static int getChargeTime(ItemStack stack) {
      int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.QUICK_CHARGE, stack);
      return i == 0 ? 25 : 25 - 5 * i;
   }

   /**
    * returns the action that specifies what animation to play when the items is being used
    */
   public UseAction getUseAction(ItemStack stack) {
      return UseAction.CROSSBOW;
   }

   private SoundEvent getSoundEvent(int enchantmentLevel) {
      switch(enchantmentLevel) {
      case 1:
         return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_1;
      case 2:
         return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_2;
      case 3:
         return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_3;
      default:
         return SoundEvents.ITEM_CROSSBOW_LOADING_START;
      }
   }

   private static float getCharge(int useTime, ItemStack stack) {
      float f = (float)useTime / (float)getChargeTime(stack);
      if (f > 1.0F) {
         f = 1.0F;
      }

      return f;
   }

   /**
    * allows items to add custom lines of information to the mouseover description
    */
   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      List<ItemStack> list = getChargedProjectiles(stack);
      if (isCharged(stack) && !list.isEmpty()) {
         ItemStack itemstack = list.get(0);
         tooltip.add((new TranslationTextComponent("item.minecraft.crossbow.projectile")).appendText(" ").appendSibling(itemstack.getTextComponent()));
         if (flagIn.isAdvanced() && itemstack.getItem() == Items.FIREWORK_ROCKET) {
            List<ITextComponent> list1 = Lists.newArrayList();
            Items.FIREWORK_ROCKET.addInformation(itemstack, worldIn, list1, flagIn);
            if (!list1.isEmpty()) {
               for(int i = 0; i < list1.size(); ++i) {
                  list1.set(i, (new StringTextComponent("  ")).appendSibling(list1.get(i)).applyTextStyle(TextFormatting.GRAY));
               }

               tooltip.addAll(list1);
            }
         }

      }
   }

   private static float func_220013_l(ItemStack p_220013_0_) {
      return p_220013_0_.getItem() == Items.CROSSBOW && hasChargedProjectile(p_220013_0_, Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
   }
}