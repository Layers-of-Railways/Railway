package com.railwayteam.railways.entities.conductor;

import com.railwayteam.railways.ModSetup;
import com.railwayteam.railways.items.ConductorItem;
import com.simibubi.create.AllItems;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkHooks;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = "railways", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConductorEntity extends CreatureEntity implements IAnimatable {
  public static final String name = "conductor";
  public static final String defaultDisplayName = "Conductor"; // huh why isnt he called conductor

  public ConductorEntity(EntityType<? extends CreatureEntity> type, World world) {
    super(type, world);
  }

  @SubscribeEvent
  public static void createEntityAttributes(EntityAttributeCreationEvent event) {
    event.put(ModSetup.R_ENTITY_CONDUCTOR.get(), createLivingAttributes().add(Attributes.GENERIC_FOLLOW_RANGE, 16).build());
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
  public void setHeadRotation(float p_208000_1_, int p_208000_2_) {
    super.setHeadRotation(p_208000_1_, p_208000_2_);
  }

  @Override
  protected void spawnDrops(DamageSource p_213345_1_) {
    entityDropItem(ConductorItem.create(this));
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

  public static ConductorEntity spawn(World world, int x, int y, int z) {
    ConductorEntity entity = new ConductorEntity(ModSetup.R_ENTITY_CONDUCTOR.get(), world);
    entity.setPosition(x, y, z);

    world.addEntity(entity);
    return entity;
  }

  public static ConductorEntity spawn(World world, BlockPos pos) {
    return spawn(world, pos.getX(), pos.getY(), pos.getZ());
  }


  @Override
  public ActionResultType applyPlayerInteraction(PlayerEntity plr, Vector3d vector3d, Hand hand) {
    ItemStack stack = plr.getHeldItem(hand);
    if(stack.getItem().equals(AllItems.WRENCH.get()) && plr.isSneaking()) {
      remove();
      entityDropItem(ConductorItem.create(this));
      return ActionResultType.SUCCESS;
    }
    return super.applyPlayerInteraction(plr, vector3d, hand);
  }

  private AnimationFactory factory = new AnimationFactory(this);

  private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
    if (!(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) {
      event.getController().setAnimation(new AnimationBuilder().addAnimation("conductor_walk", true));
    } else {
      event.getController().setAnimation(new AnimationBuilder().addAnimation("conductor_idle", true));
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
