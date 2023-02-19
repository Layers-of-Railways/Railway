package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.content.custom_tracks.TrackMaterial.TrackType;
import com.railwayteam.railways.content.custom_tracks.casing.SlabUseOnCurvePacket;
import com.railwayteam.railways.mixin_interfaces.IHasTrackMaterial;
import com.railwayteam.railways.registry.CRPackets;
import com.railwayteam.railways.registry.CRTags.AllBlockTags;
import com.railwayteam.railways.util.CustomTrackChecks;
import com.simibubi.create.content.logistics.trains.BezierConnection;
import com.simibubi.create.content.logistics.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.logistics.trains.track.CurvedTrackInteraction;
import com.simibubi.create.content.logistics.trains.track.TrackBlockOutline;
import com.simibubi.create.content.logistics.trains.track.TrackBlockOutline.BezierPointSelection;
import com.simibubi.create.content.logistics.trains.track.TrackTileEntity;
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
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(value = CurvedTrackInteraction.class, remap = false)
public abstract class MixinCurvedTrackInteraction {
  @ModifyArg(
          method = "onClickInput",
          at = @At(
                  value = "INVOKE",
                  target = "Lcom/tterrag/registrate/util/entry/BlockEntry;isIn(Lnet/minecraft/world/item/ItemStack;)Z",
                  remap = true
          )
  )
  private static ItemStack railway$allowCustomTracks(ItemStack held) {
    return CustomTrackChecks.check(held);
  }

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
    TrackTileEntity track = result.te();
    BezierTrackPointLocation location = result.loc();
    BlockPos curveTarget = location.curveTarget();
    Map<BlockPos, BezierConnection> connections = track.getConnections();
    BezierConnection connection = connections == null ? null : connections.get(curveTarget);

    // allow encasing if no connection or not monorail
    // todo: that doesn't seem right? same as old behavior though
    if (connection == null || ((IHasTrackMaterial) connection).getMaterial().trackType != TrackType.MONORAIL) {
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

      CRPackets.PACKETS.send(new SlabUseOnCurvePacket(track.getBlockPos(), curveTarget, new BlockPos(result.vec())));
      player.swing(InteractionHand.MAIN_HAND);
      cir.setReturnValue(true);
    }
  }
}
