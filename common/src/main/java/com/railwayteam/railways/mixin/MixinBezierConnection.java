package com.railwayteam.railways.mixin;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.mixin_interfaces.IHasTrackCasing;
import com.railwayteam.railways.registry.CRTags;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.foundation.utility.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = BezierConnection.class, remap = false)
public abstract class MixinBezierConnection implements IHasTrackCasing {
  @Shadow public Couple<BlockPos> tePositions;

  @Shadow public abstract Vec3 getPosition(double t);

  protected SlabBlock trackCasing;
  protected boolean isShiftedDown;

  @Override
  public @Nullable SlabBlock getTrackCasing() {
    return trackCasing;
  }

  @Override
  public void setTrackCasing(@Nullable SlabBlock trackCasing) {
    if (trackCasing != null && CRTags.AllBlockTags.TRACK_CASING_BLACKLIST.matches(trackCasing)) //sanity check
      return;
    this.trackCasing = trackCasing;
  }

  @Override
  public boolean isAlternate() {
    return isShiftedDown;
  }

  @Override
  public void setAlternate(boolean alternate) {
    this.isShiftedDown = alternate;
  }


  @Inject(method = "write(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/nbt/CompoundTag;", at = @At("RETURN"),
      cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, remap = true)
  private void write(BlockPos localTo, CallbackInfoReturnable<CompoundTag> cir, Couple<BlockPos> tePositions, Couple<Vec3> starts, CompoundTag compound) {
    if (getTrackCasing() != null) {
      if (BuiltInRegistries.BLOCK.getKey(getTrackCasing()).toString().equals("minecraft:block")) {
        Railways.LOGGER.error("NBTwrite trackCasing was minecraft:block!!! for BezierConnection: starts=" + starts + ", primary=" + tePositions.getFirst() + ", secondary=" + tePositions.getSecond() + ", casing: " + getTrackCasing());
      } else {
        compound.putString("Casing", BuiltInRegistries.BLOCK.getKey(getTrackCasing()).toString());
      }
    }
    compound.putBoolean("ShiftDown", isAlternate());
    cir.setReturnValue(compound);
  }

  @Inject(method = "write(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("RETURN"), remap = true)
  private void netWrite(FriendlyByteBuf buffer, CallbackInfo ci) {
    buffer.writeBoolean(getTrackCasing() != null);
    if (getTrackCasing() != null) {
      buffer.writeResourceLocation(BuiltInRegistries.BLOCK.getKey(getTrackCasing()));
      buffer.writeBoolean(isAlternate());
    }
  }

  @Inject(method = "<init>(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/BlockPos;)V", at = @At("RETURN"), remap = true)
  private void nbtConstructor(CompoundTag compound, BlockPos localTo, CallbackInfo ci) {
    if (compound.contains("Casing", Tag.TAG_STRING)) {
      if (compound.getString("Casing").equals("minecraft:block")) {
        Railways.LOGGER.error("NBTCtor trackCasing was minecraft:block!!! for BezierConnection: primary="+tePositions.getFirst()+", secondary="+tePositions.getSecond());
      }
      //Railways.LOGGER.warn("NBTCtor: Casing="+compound.getString("Casing"));
      setTrackCasing((SlabBlock) BuiltInRegistries.BLOCK.get(ResourceLocation.of(compound.getString("Casing"), ':')));
    }
    if (compound.contains("ShiftDown", Tag.TAG_BYTE)) {
      setAlternate(compound.getBoolean("ShiftDown"));
    } else {
      setAlternate(false);
    }
  }

  @Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("RETURN"), remap = true)
  private void byteBufConstructor(FriendlyByteBuf buffer, CallbackInfo ci) {
    if (buffer.readBoolean()) {
      setTrackCasing((SlabBlock) BuiltInRegistries.BLOCK.get(buffer.readResourceLocation()));
      setAlternate(buffer.readBoolean());
    } else {
      setTrackCasing(null);
    }
  }

  @Inject(method = "spawnItems", at = @At("TAIL"))
  private void spawnCasing(Level level, CallbackInfo ci) {
    if (this.getTrackCasing() != null) {
      Vec3 origin = Vec3.atLowerCornerOf(tePositions.getFirst());
      Vec3 spawnPos = this.getPosition(0.5);
      ItemEntity entity = new ItemEntity(level, spawnPos.x, spawnPos.y, spawnPos.z, new ItemStack(this.getTrackCasing()));
      entity.setDefaultPickUpDelay();
      level.addFreshEntity(entity);
    }
  }

  @Inject(method = "addItemsToPlayer", at = @At("TAIL"))
  private void addCasingItem(Player player, CallbackInfo ci) {
    if (this.getTrackCasing() != null) {
      Inventory inv = player.getInventory();
      inv.placeItemBackInInventory(new ItemStack(this.getTrackCasing()));
    }
  }
}
