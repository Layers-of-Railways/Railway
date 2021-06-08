package com.railwayteam.railways.entities.conductor;

import com.railwayteam.railways.ModSetup;
import com.railwayteam.railways.goals.WalkToAndSitInNearestMinecartGoal;
import com.railwayteam.railways.goals.WalkToNearestPlayerWithCapGoal;
import com.railwayteam.railways.items.ConductorItem;
import com.railwayteam.railways.util.WrenchableEntity;
import net.minecraft.client.renderer.entity.layers.SheepWoolLayer;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

public class ConductorEntity extends CreatureEntity implements WrenchableEntity {
  public static final String name = "conductor";
//  public int color = getDefaultColor().getId();
  public static final String defaultDisplayName = "Conductor"; // huh why isnt he called conductor

  public ConductorEntity(EntityType<? extends CreatureEntity> p_i48575_1_, World p_i48575_2_) {
    super(p_i48575_1_, p_i48575_2_);
  }

  public static final DataParameter<Integer> COLOR = EntityDataManager.createKey(ConductorEntity.class, DataSerializers.VARINT);

  @Override
  protected void registerData() {
    super.registerData();
    EntityDataManager dataManager = getDataManager();
    dataManager.register(COLOR, getDefaultColor().getId());
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
    goalSelector.addGoal(3, new LookAtGoal(this, PlayerEntity.class, 8));
    goalSelector.addGoal(3, new WaterAvoidingRandomWalkingGoal(this, 0.2));
    goalSelector.addGoal(2, new LookAtWithoutMovingGoal(this, PlayerEntity.class, 5, 5));
    goalSelector.addGoal(2, new LookRandomlyGoal(this));
    goalSelector.addGoal(5, new SwimGoal(this));
    goalSelector.addGoal(5, new RandomSwimmingGoal(this, 0.2, 8));
    goalSelector.addGoal(1, new WalkToAndSitInNearestMinecartGoal(this, 0.4 /* Move to the minecart slightly faster than normal */, 5, 2));
    goalSelector.addGoal(0, new WalkToNearestPlayerWithCapGoal(this, 0.4, 5, 2, 1));
  }

  @Override
  protected void spawnDrops(DamageSource p_213345_1_) {
    entityDropItem(ConductorItem.g(getColor()).create(this));
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

  public int getColorId() {
    return getDataManager().get(COLOR);
//    CompoundNBT nbt = entity.serializeNBT();
//    return nbt.getInt("CapColor");
  }

  public DyeColor getColor() {
    return DyeColor.byId(getColorId());
  }

  public ItemStack createHatByColor(int id) {
    return new ItemStack(getHatByColor(id));
  }

  public ItemStack createHatByColor(DyeColor color) {
//    return new ItemStack(Items.IRON_HELMET);
    return new ItemStack(getHatByColor(color));
  }

  public Item getHatByColor(int id) {
    return getHatByColor(DyeColor.byId(id));
  }

  public Item getHatByColor(DyeColor color) {
    return (Item) ModSetup.ENGINEERS_CAPS.get(color).get();
  }

//  @Override
//  public IPacket<?> createSpawnPacket() { return NetworkHooks.getEntitySpawningPacket(this); }

  public static ConductorEntity spawn(World world, double x, double y, double z, DyeColor color) {
    ConductorEntity entity = new ConductorEntity(ModSetup.R_ENTITY_CONDUCTOR.get(), world);
    entity.setPosition(x, y, z);

    world.addEntity(entity);
    entity.setColor(color);
    entity.updateCap();
    return entity;
  }

  public static ConductorEntity spawn(World world, Vector3d pos, DyeColor color) {
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
    return setCap(createHatByColor(color));
  }

  public ItemStack updateCap() {
    return setCap(getColor());
  }

  public void setColor(int color) {
//    CompoundNBT nbt = entity.serializeNBT();
//    nbt.putInt("CapColor", color);
//    System.out.println(entity.getBlockPos().toShortString() + ": " + color);
//    entity.deserializeNBT(nbt);
    getDataManager().set(COLOR, color);
  }

  public void setColor(DyeColor color) {
    setColor(color.getId());
  }

//  @Override
//  public CompoundNBT serializeNBT() {
//    CompoundNBT nbt =  super.serializeNBT();
//    nbt.putInt("color", color);
//    return nbt;
//  }

//  @Override
//  public void writeAdditional(CompoundNBT nbt) {
//    nbt.putInt("CapColor", color);
//  }

  @Override
  public ActionResultType applyPlayerInteraction(PlayerEntity plr, Vector3d vector3d, Hand hand) {
    ItemStack stack = plr.getHeldItem(hand);
    Item item = stack.getItem();

    if(item instanceof DyeItem) {
      DyeColor color = ((DyeItem) item).getDyeColor();
      if(!color.equals(getColor())) {
        setColor(color);
        if(!plr.isCreative()) {
          stack.shrink(1);
        }
      }
      return ActionResultType.SUCCESS;
    }

    return onWrenched(plr, hand, this);
  }

  @Override
  public void afterWrenched(PlayerEntity plr, Hand hand) {
    entityDropItem(ConductorItem.g(getColor()).create(this));
  }

  @Override
  public void read(CompoundNBT nbt) {
    super.read(nbt);

    setColor(nbt.getInt("CapColor"));
  }

  @Override
  public void writeAdditional(CompoundNBT nbt) {
    nbt.putInt("CapColor", getColorId());

    super.writeAdditional(nbt);
  }

  @Override
  public ItemStack getPickedResult(RayTraceResult target) {
    return ConductorItem.g(getColor()).create(this);
  }

  @Override
  public float getStandingEyeHeight(Pose pose, EntitySize size) {
    return size.height * 0.90F;
  }

  public boolean shouldBeRainbow() {
    return hasCustomName() && "jeb_".equals(getName().getUnformattedComponentText());
  }
}
