package com.railwayteam.railways.entities.conductor;

import com.railwayteam.railways.ModSetup;
import com.railwayteam.railways.Util;
import com.railwayteam.railways.goals.WalkToAndSitInNearestMinecart;
import com.railwayteam.railways.items.ConductorItem;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = "railways", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConductorEntity extends CreatureEntity implements Util.Animatable, Util.WrenchableEntity {
  public static final String name = "conductor";
  public int color = getDefaultColor().getId();
  public static final String defaultDisplayName = "Conductor"; // huh why isnt he called conductor

  public ConductorEntity(EntityType<? extends CreatureEntity> p_i48575_1_, World p_i48575_2_) {
    super(p_i48575_1_, p_i48575_2_);
  }

  @SubscribeEvent
  public static void createEntityAttributes(EntityAttributeCreationEvent event) {
    event.put(ModSetup.R_ENTITY_CONDUCTOR.get(), createLivingAttributes().add(Attributes.GENERIC_FOLLOW_RANGE, 16).build());
  }

  public static DyeColor getDefaultColor() {
    return DyeColor.BLUE;
  }

  public boolean isInMinecart() {
    return this.getRidingEntity() instanceof MinecartEntity;
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
    goalSelector.addGoal(10, new WalkToAndSitInNearestMinecart(this, 0.4 /* Move to the minecart slightly faster than normal */, 5, 2));
  }

  @Override
  protected void spawnDrops(DamageSource p_213345_1_) {
    entityDropItem(ConductorItem.create(this));
    super.spawnDrops(p_213345_1_);
  }

  @Nullable
  @Override
  public ILivingEntityData onInitialSpawn(IServerWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
    updateCap();

    return super.onInitialSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
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

  public static int getColorId(ConductorEntity entity) {
    CompoundNBT nbt = entity.serializeNBT();
    return nbt.getInt("CapColor");
  }

  public static DyeColor getColor(ConductorEntity entity) {
    return DyeColor.byId(getColorId(entity));
  }

  public int getColorId() {
   return getColorId(this);
  }

  public DyeColor getColor() {
    return getColor(this);
  }

  public ItemStack getHatByColor(int id) {
    return getHatByColor(DyeColor.byId(id));
  }

  public ItemStack getHatByColor(DyeColor color) {
//    return new ItemStack(Items.IRON_HELMET);
    return ModSetup.ENGINEERS_CAPS.get(color).asStack();
  }

//  @Override
//  public IPacket<?> createSpawnPacket() { return NetworkHooks.getEntitySpawningPacket(this); }

  public static ConductorEntity spawn(World world, int x, int y, int z, DyeColor color) {
    ConductorEntity entity = new ConductorEntity(ModSetup.R_ENTITY_CONDUCTOR.get(), world);
    entity.setPosition(x, y, z);

    world.addEntity(entity);
    entity.setColor(color);
    entity.updateCap();
    return entity;
  }

  public static ConductorEntity spawn(World world, BlockPos pos, DyeColor color) {
    return spawn(world, pos.getX(), pos.getY(), pos.getZ(), color);
  }

  public ItemStack setCap(ItemStack stack) {
    setItemStackToSlot(EquipmentSlotType.HEAD, stack);
    return stack;
  }

  public ItemStack setCap(Item item) {
    return setCap(new ItemStack(item));
  }

  public ItemStack setCap(DyeColor color) {
    return setCap(getHatByColor(color));
  }

  public ItemStack updateCap() {
    if(color == 0) {
      setColor(getDefaultColor());
    }

    return setCap(getColor());
  }

  public static void setColor(ConductorEntity entity, int color) {
    CompoundNBT nbt = entity.serializeNBT();
    nbt.putInt("CapColor", color);
//    System.out.println(entity.getBlockPos().toShortString() + ": " + color);
    entity.deserializeNBT(nbt);
  }

  public static void setColor(ConductorEntity entity, DyeColor color) {
    setColor(entity, color.getId());
  }

  public void setColor(int color) {
    setColor(this, color);
  }

  public void setColor(DyeColor color) {
    setColor(this, color);
  }

//  @Override
//  public CompoundNBT serializeNBT() {
//    CompoundNBT nbt =  super.serializeNBT();
//    nbt.putInt("color", color);
//    return nbt;
//  }

  @Override
  public void read(CompoundNBT nbt) {
    color = nbt.getInt("CapColor");
    super.read(nbt);
  }

  @Override
  public void writeAdditional(CompoundNBT nbt) {
    nbt.putInt("CapColor", color);
  }

  @Override
  public ActionResultType applyPlayerInteraction(PlayerEntity plr, Vector3d vector3d, Hand hand) {
    return onWrenched(plr, hand, this);
  }

  @Override
  public void afterWrenched(PlayerEntity plr, Hand hand) {
    entityDropItem(ConductorItem.create(this));
  }

  private AnimationFactory factory = new AnimationFactory(this);

  @Override
  public <E extends IAnimatable> AnimationBuilder getAnimation(AnimationEvent<E> event) {
    if(this.getRidingEntity() != null) {
      if(isInMinecart()) {
        return anim("minecart");
      }
      return anim("sit");
    }
    if (!(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) {
      return anim("walk");
    }
    return anim("idle");
  }

  @Override
  public AnimationFactory getFactory() {
    return this.factory;
  }

  @Override
  public String getAnimationPrefix() {
    return "conductor_";
  }
}
