package com.railwayteam.railways.content.Conductor;

import com.railwayteam.railways.Railways;
import com.simibubi.create.AllBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.IItemRenderProperties;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class ConductorCapItem extends ArmorItem {
  public final DyeColor color;
  static Block casing = null; // it'll initialize later

  public ConductorCapItem (Properties props, DyeColor color) {
    super(new ConductorArmorMaterial(), EquipmentSlot.HEAD, props);
    this.color  = color;
    if (casing == null) casing = AllBlocks.ANDESITE_CASING.get();
  }

  static boolean isCasing (Block block) { return block.equals(casing); }
  static boolean isCasing (BlockState state) { return isCasing(state.getBlock()); }
  static boolean isCasing (Level level, BlockPos pos) { return isCasing(level.getBlockState(pos)); }

  @Nonnull
  @Override
  public InteractionResult useOn (UseOnContext ctx) {
    Level level  = ctx.getLevel();
    BlockPos pos = ctx.getClickedPos();
    if (!level.isClientSide && isCasing(level, pos)) {
      level.removeBlock(pos, false);
      ConductorEntity.spawn(level, pos.getX(), pos.getY()+1, pos.getZ(), color);
      if (ctx.getPlayer() != null && !ctx.getPlayer().isCreative()) {
        ctx.getItemInHand().shrink(1);
        return InteractionResult.CONSUME;
      }
      return InteractionResult.SUCCESS;
    }
    return super.useOn(ctx);
  }

  @Override
  public void initializeClient (Consumer<IItemRenderProperties> consumer) {
    consumer.accept(new IItemRenderProperties() {
      @Nonnull
      @Override
      public Model getBaseArmorModel (LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default) {
        EntityModelSet set = Minecraft.getInstance().getEntityModels();
        ConductorCapModel<?> model = new ConductorCapModel<>(set.bakeLayer(ConductorCapModel.LAYER_LOCATION));
        model.setProperties(_default);
        return model;
      }
    });
    super.initializeClient(consumer);
  }

  @Nullable
  @Override
  public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
    return Railways.MODID + ":textures/entity/conductor_cap.png";
  }

  static class ConductorArmorMaterial implements ArmorMaterial {
    @Override
    public int getDurabilityForSlot (EquipmentSlot slot) {
      return 0;
    }

    @Override
    public int getDefenseForSlot (EquipmentSlot slot) {
      return 0;
    }

    @Override
    public int getEnchantmentValue () {
      return 0;
    }

    @Override
    public SoundEvent getEquipSound () {
      return SoundEvents.ARMOR_EQUIP_LEATHER;
    }

    @Override
    public Ingredient getRepairIngredient () {
      return null;
    }

    @Override
    public String getName() {
      return "conductor_cap";
    }

    @Override
    public float getToughness() {
      return 0;
    }

    @Override
    public float getKnockbackResistance() {
      return 0;
    }
  }
}
