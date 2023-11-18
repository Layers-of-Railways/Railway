package com.railwayteam.railways.content.conductor;

import com.railwayteam.railways.Railways;
import com.simibubi.create.AllBlocks;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Locale;

public abstract class ConductorCapItem extends ArmorItem {
  public final DyeColor color;
  public final ResourceLocation textureId;
  public final String textureStr;

  protected ConductorCapItem(Properties props, DyeColor color) {
    super(new ConductorArmorMaterial(), EquipmentSlot.HEAD, props);
    this.color  = color;
    String colorName = color.getName().toLowerCase(Locale.ROOT);
    this.textureId = Railways.asResource("textures/entity/caps/%s_conductor_cap.png".formatted(colorName));
    this.textureStr = textureId.toString();
  }

  @ExpectPlatform
  public static ConductorCapItem create(Properties props, DyeColor color) {
    throw new AssertionError();
  }

  static boolean isCasing (Block block) { return block.equals( AllBlocks.ANDESITE_CASING.get()); }
  static boolean isCasing (BlockState state) { return isCasing(state.getBlock()); }
  static boolean isCasing (Level level, BlockPos pos) { return isCasing(level.getBlockState(pos)); }

  @Nonnull
  @Override
  public InteractionResult useOn (UseOnContext ctx) {
    Level level  = ctx.getLevel();
    BlockPos pos = ctx.getClickedPos();
    if (isCasing(level, pos)) {
      if (level.isClientSide)
        return InteractionResult.SUCCESS;
      level.removeBlock(pos, false);
      ConductorEntity.spawn(level, pos, ctx.getItemInHand().copy());
      if (ctx.getPlayer() != null && !ctx.getPlayer().isCreative()) {
        ctx.getItemInHand().shrink(1);
        return InteractionResult.CONSUME;
      }
      return InteractionResult.SUCCESS;
    }
    return super.useOn(ctx);
  }

  static class ConductorArmorMaterial implements ArmorMaterial {
    @Override
    public int getDurabilityForSlot (@NotNull EquipmentSlot slot) {
      return 0;
    }

    @Override
    public int getDefenseForSlot (@NotNull EquipmentSlot slot) {
      return 0;
    }

    @Override
    public int getEnchantmentValue () {
      return 0;
    }

    @Override
    public @NotNull SoundEvent getEquipSound() {
      return SoundEvents.ARMOR_EQUIP_LEATHER;
    }

    @Override
    public @NotNull Ingredient getRepairIngredient() {
      return Ingredient.EMPTY;
    }

    @Override
    public @NotNull String getName() {
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
