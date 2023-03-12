package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.track_api.TrackMaterial;
import com.railwayteam.railways.mixin_interfaces.IHasTrackMaterial;
import com.simibubi.create.content.curiosities.tools.BlueprintOverlayRenderer;
import com.simibubi.create.content.logistics.trains.track.TrackBlock;
import com.simibubi.create.content.logistics.trains.track.TrackPlacement;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlueprintOverlayRenderer.class, remap = false)
public abstract class MixinBlueprintOverlayRenderer {
  @Nullable
  private static TrackPlacement.PlacementInfo placementInfo; //TODO track api

  @Inject(method = "displayTrackRequirements", at = @At("HEAD"))
  private static void storeInfo(TrackPlacement.PlacementInfo info, ItemStack pavementItem, CallbackInfo ci) { //TODO track api
    placementInfo = info;
  }

  @Inject(method = "displayTrackRequirements", at = @At("RETURN"))
  private static void clearInfo(TrackPlacement.PlacementInfo info, ItemStack pavementItem, CallbackInfo ci) { //TODO track api
    placementInfo = null;
  }

  @SuppressWarnings("unchecked")
  @Redirect(method = "displayTrackRequirements", at = @At(value = "FIELD", opcode = Opcodes.GETSTATIC, target = "Lcom/simibubi/create/AllBlocks;TRACK:Lcom/tterrag/registrate/util/entry/BlockEntry;")) //TODO track api
  private static BlockEntry<TrackBlock> replaceTracks() {
    if (placementInfo != null) {
      return (BlockEntry<TrackBlock>) ((IHasTrackMaterial) placementInfo).getMaterial().getTrackBlock();
    }
    return (BlockEntry<TrackBlock>) TrackMaterial.ANDESITE.getTrackBlock(); //Avoid importing AllBlocks - that makes the datagen fail
  }
}
