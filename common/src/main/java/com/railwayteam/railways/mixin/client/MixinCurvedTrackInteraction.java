package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.content.custom_tracks.casing.SlabUseOnCurvePacket;
import com.railwayteam.railways.registry.CRPackets;
import com.railwayteam.railways.registry.CRTags.AllBlockTags;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.simibubi.create.content.trains.track.*;
import com.simibubi.create.content.trains.track.TrackBlockOutline.BezierPointSelection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SlabBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(value = CurvedTrackInteraction.class, remap = false)
public abstract class MixinCurvedTrackInteraction {

  @Inject(
          method = "onClickInput",
          at = @At(
                  value = "INVOKE",
                  target = "Lnet/minecraft/client/player/LocalPlayer;getMainHandItem()Lnet/minecraft/world/item/ItemStack;",
                  remap = true
          ),
          cancellable = true
  )
  private static void railway$encaseCurve(CallbackInfoReturnable<Boolean> cir) {
    BezierPointSelection result = TrackBlockOutline.result;
    TrackBlockEntity track = result.blockEntity();
    BezierTrackPointLocation location = result.loc();
    BlockPos curveTarget = location.curveTarget();
    Map<BlockPos, BezierConnection> connections = track.getConnections();
    BezierConnection connection = connections == null ? null : connections.get(curveTarget);

    // allow encasing if no connection or not monorail
    // todo: that doesn't seem right? same as old behavior though
    if (connection == null || connection.getMaterial().trackType != CRTrackMaterials.CRTrackType.MONORAIL) {
      LocalPlayer player = Minecraft.getInstance().player;
      ItemStack held = player.getMainHandItem();

      // if non-empty, must be a valid slab
      if (!held.isEmpty()) {
        if (!(held.getItem() instanceof BlockItem block))
          return;
        if (!(block.getBlock() instanceof SlabBlock slab))
          return;
        if (AllBlockTags.TRACK_CASING_BLACKLIST.matches(slab))
          return;
      }

      // fixme
      CRPackets.PACKETS.send(new SlabUseOnCurvePacket(track.getBlockPos(), curveTarget, new BlockPos(result.vec())));
      player.swing(InteractionHand.MAIN_HAND);
      cir.setReturnValue(true);
    }
  }
}
