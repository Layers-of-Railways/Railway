package com.railwayteam.railways.entities.engineer;

import com.railwayteam.railways.ModSetup;
import com.railwayteam.railways.items.EngineerGolemItem;
import com.simibubi.create.AllItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.ArrayList;

public class EngineerGolemEntity extends LivingEntity {
  public static final String name = "engineer_golem";
  public static final String defaultDisplayName = "Engineer Golem"; // huh why isnt he called conductor

  public EngineerGolemEntity(EntityType<? extends LivingEntity> type, World world) {
    super(type, world);
  }

  @Override
  protected void spawnDrops(DamageSource p_213345_1_) {
    entityDropItem(EngineerGolemItem.create(this));
    super.spawnDrops(p_213345_1_);
  }

  @Override
  public boolean getAlwaysRenderNameTagForRender() {
    return false;
  }

  @Override
  public boolean isCustomNameVisible() {
    if(getDisplayName().getUnformattedComponentText().equals(defaultDisplayName)) return false;
    return super.isCustomNameVisible();
  }

  @Override
  public Iterable<ItemStack> getArmorInventoryList() {
    return new ArrayList<ItemStack>();
  }

  @Override
  public ItemStack getItemStackFromSlot(EquipmentSlotType slotType) {
    return ItemStack.EMPTY;
  }

  @Override
  public void setItemStackToSlot(EquipmentSlotType slotType, ItemStack stack) {
  }

  @Override
  public HandSide getPrimaryHand() {
    return null;
  }

  @Override
  public IPacket<?> createSpawnPacket() { return NetworkHooks.getEntitySpawningPacket(this); }

  public static Entity spawn(World world, ItemStack stack, PlayerEntity player, BlockPos pos) {
    return ModSetup.R_ENTITY_ENGINEER.get().spawn(
            world, stack, player, pos, SpawnReason.STRUCTURE, false, false
    );
  }

  @Override
  public boolean processInitialInteract(PlayerEntity plr, Hand hand) {
    ItemStack stack = plr.getHeldItem(hand);
    if(stack.getItem().equals(AllItems.WRENCH.get()) && plr.isCrouching()) {
      remove();
      entityDropItem(EngineerGolemItem.create(this));
      return true;
    }
    return super.processInitialInteract(plr, hand);
  }
}
