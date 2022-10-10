package com.railwayteam.railways.mixin;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.custom_tracks.TrackMaterial;
import com.railwayteam.railways.mixin_interfaces.IHasTrackMaterial;
import com.simibubi.create.content.logistics.trains.BezierConnection;
import com.simibubi.create.content.logistics.trains.track.TrackBlock;
import com.simibubi.create.foundation.utility.Couple;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = BezierConnection.class, remap = false)
public abstract class MixinBezierConnection implements IHasTrackMaterial {
  protected TrackMaterial trackMaterial;

  @Override
  public TrackMaterial getMaterial() {
    if (trackMaterial == null) {
      BezierConnection this_ = (BezierConnection) (Object) this;
      Railways.LOGGER.error("trackMaterial was null!!! for BezierConnection: starts="+this_.starts+", primary="+this_.primary+", tePositions="+this_.tePositions);
      trackMaterial = TrackMaterial.ANDESITE;
    }
    return trackMaterial;
  }

  @Override
  public void setMaterial(TrackMaterial trackMaterial) {
    this.trackMaterial = trackMaterial;
  }

  public BezierConnection withMaterial(TrackMaterial trackMaterial) {
    setMaterial(trackMaterial);
    return (BezierConnection) (Object) this;
  }

  @Inject(method = "secondary", at = @At("RETURN"), cancellable = true, remap = false)
  private void secondary(CallbackInfoReturnable<BezierConnection> cir) {
    ((IHasTrackMaterial) cir.getReturnValue()).setMaterial(getMaterial());
    cir.setReturnValue(cir.getReturnValue());
  }

  @Inject(method = "write(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/nbt/CompoundTag;", at = @At("RETURN"),
      cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
  private void write(BlockPos localTo, CallbackInfoReturnable<CompoundTag> cir, Couple<BlockPos> tePositions, Couple<Vec3> starts, CompoundTag compound) {
    compound.putString("Material", getMaterial().resName()); // this is long term storage, so it should be protected against enum reordering (that's why we don't use ordinals)
    cir.setReturnValue(compound);
  }

  @Inject(method = "write(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("RETURN"), remap = false)
  private void netWrite(FriendlyByteBuf buffer, CallbackInfo ci) {
    buffer.writeEnum(getMaterial()); // this is for net code, which is short-lived, and should be space efficient, so we write the ordinal
  }

  @Inject(method = "<init>(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/BlockPos;)V", at = @At("RETURN"), remap = false)
  private void nbtConstructor(CompoundTag compound, BlockPos localTo, CallbackInfo ci) {
    setMaterial(TrackMaterial.ANDESITE);
    if (compound.contains("Material", Tag.TAG_STRING)) {
      setMaterial(TrackMaterial.deserialize(compound.getString("Material")));
    }
  }

  @Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("RETURN"), remap = false)
  private void byteBufConstructor(FriendlyByteBuf buffer, CallbackInfo ci) {
    setMaterial(buffer.readEnum(TrackMaterial.class));
  }

  @SuppressWarnings("unchecked")
  @Redirect(method = {"spawnItems", "spawnDestroyParticles", "addItemsToPlayer"},
      at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lcom/simibubi/create/AllBlocks;TRACK:Lcom/tterrag/registrate/util/entry/BlockEntry;"), remap = false)
  private BlockEntry<TrackBlock> redirectTrackSpawn() {
    return (BlockEntry<TrackBlock>) getMaterial().getTrackBlock();
  }
}
