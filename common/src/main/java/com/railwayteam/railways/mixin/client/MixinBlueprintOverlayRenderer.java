package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.mixin_interfaces.IHasTrackMaterial;
import com.railwayteam.railways.track_api.TrackMaterial;
import com.simibubi.create.content.equipment.blueprint.BlueprintOverlayRenderer;
import com.simibubi.create.content.trains.track.TrackPlacement.PlacementInfo;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlueprintOverlayRenderer.class, remap = false)
public abstract class MixinBlueprintOverlayRenderer { //TODO _track api

  @Unique
  private static PlacementInfo railway$info = null;

  @Inject(
          method = "displayTrackRequirements",
          at = @At(
                  value = "INVOKE",
                  target = "Ljava/util/List;clear()V"
          )
  )
  private static void railway$grabPlacementInfo(PlacementInfo info, ItemStack pavementItem, CallbackInfo ci) {
    railway$info = info;
  }

  @ModifyArg(
          method = "displayTrackRequirements",
          at = @At(
                  value = "INVOKE",
                  target = "Lcom/simibubi/create/foundation/utility/Pair;of(Ljava/lang/Object;Ljava/lang/Object;)Lcom/simibubi/create/foundation/utility/Pair;",
                  ordinal = 0
          ),
          index = 0
  )
  private static Object railway$displayCorrectTrackItem(Object trackStack) {
    TrackMaterial material = ((IHasTrackMaterial) railway$info).getMaterial();
    if (material == TrackMaterial.ANDESITE)
      return trackStack;
    int count = ((ItemStack) trackStack).getCount();
    return material.getTrackBlock().asStack(count);
  }
}
