package com.railwayteam.railways.entities.engineer;

import com.railwayteam.railways.ModSetup;
import com.railwayteam.railways.items.EngineerGolemItem;
import com.simibubi.create.AllItems;
import net.minecraft.client.renderer.entity.model.VillagerModel;
import net.minecraft.client.renderer.entity.model.WolfModel;
import net.minecraft.client.renderer.entity.model.ZombieModel;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.ArrayList;

public class EngineerGolemEntity extends CreatureEntity implements IAnimatable {
  public static final String name = "engineer_golem";
  public static final String defaultDisplayName = "Engineer Golem"; // huh why isnt he called conductor

  public EngineerGolemEntity(EntityType<? extends CreatureEntity> type, World world) {
    super(type, world);
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    goalSelector.addGoal(1, new LookAtGoal(this, PlayerEntity.class, 8));
    goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.2));
    goalSelector.addGoal(2, new LookAtWithoutMovingGoal(this, PlayerEntity.class, 5, 5));
    goalSelector.addGoal(1, new LookRandomlyGoal(this));
    goalSelector.addGoal(2, new SwimGoal(this));
    goalSelector.addGoal(3, new RandomSwimmingGoal(this, 0.2, 8));
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
    return new ArrayList<>();
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

  public static EngineerGolemEntity spawn(ServerWorld world, ItemStack stack, PlayerEntity player, BlockPos pos, SpawnReason spawnReason) {
    return (EngineerGolemEntity) ModSetup.R_ENTITY_ENGINEER.get().spawn(
            world, stack, player, pos, spawnReason, false, false
    );
  }

  public static EngineerGolemEntity spawn(World world, int x, int y, int z) {
    EngineerGolemEntity entity = new EngineerGolemEntity(ModSetup.R_ENTITY_ENGINEER.get(), world);
    entity.setPosition(x, y, z);

    world.addEntity(entity);
    return entity;
  }

  public static EngineerGolemEntity spawn(World world, BlockPos pos) {
    return spawn(world, pos.getX(), pos.getY(), pos.getZ());
  }

//  @Override
//  protected boolean processInteract(PlayerEntity plr, Hand hand) {
//    ItemStack stack = plr.getHeldItem(hand);
//    if(stack.getItem().equals(AllItems.WRENCH.get()) && plr.isCrouching()) {
//      remove();
//      entityDropItem(EngineerGolemItem.create(this));
//      return true;
//    }
//    return super.processInteract(plr, hand);
//  }


  @Override
  protected ActionResultType interactMob(PlayerEntity plr, Hand hand) {
    ItemStack stack = plr.getHeldItem(hand);
    if(stack.getItem().equals(AllItems.WRENCH.get()) && plr.isCrouching()) {
      remove();
      entityDropItem(EngineerGolemItem.create(this));
      return ActionResultType.SUCCESS;
    }
    return super.interactMob(plr, hand);
  }

  private AnimationFactory factory = new AnimationFactory(this);

  private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
    if (!(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) {
      event.getController().setAnimation(new AnimationBuilder().addAnimation("golem_walk", true));
    } else {
      event.getController().setAnimation(new AnimationBuilder().addAnimation("golem_idle", true));
    }
    return PlayState.CONTINUE;
  }

  @Override
  public void registerControllers(AnimationData data) {
    data.addAnimationController(new AnimationController(this, "controller", 0, this::predicate));
  }

  @Override
  public AnimationFactory getFactory() {
    return this.factory;
  }
}
