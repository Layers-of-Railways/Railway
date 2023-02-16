package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.content.custom_tracks.TrackMaterial;
import com.railwayteam.railways.content.custom_tracks.casing.SlabUseOnCurvePacket;
import com.railwayteam.railways.mixin_interfaces.IHasTrackMaterial;
import com.railwayteam.railways.registry.CRPackets;
import com.railwayteam.railways.registry.CRTags;
import com.simibubi.create.content.logistics.trains.track.CurvedTrackInteraction;
import com.simibubi.create.content.logistics.trains.track.TrackBlockOutline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraftforge.client.event.InputEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CurvedTrackInteraction.class)
public abstract class MixinCurvedTrackInteraction {
  @Inject(method = "onClickInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"), locals = LocalCapture.CAPTURE_FAILHARD,
      cancellable = true)
  private static void encaseTrackSend(InputEvent.ClickInputEvent event, CallbackInfoReturnable<Boolean> cir,
                                      TrackBlockOutline.BezierPointSelection result, Minecraft mc, LocalPlayer player,
                                      ClientLevel level, ItemStack heldItem) {
    if (result.te().getConnections() == null || !result.te().getConnections().containsKey(result.loc().curveTarget()) ||
        ((IHasTrackMaterial) result.te().getConnections().get(result.loc().curveTarget())).getMaterial().trackType != TrackMaterial.TrackType.MONORAIL) {
      if (heldItem.isEmpty() || heldItem.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof SlabBlock slabBlock &&
          !CRTags.AllBlockTags.TRACK_CASING_BLACKLIST.matches(slabBlock)) {
        CRPackets.PACKETS.send(new SlabUseOnCurvePacket(result.te()
            .getBlockPos(),
            result.loc()
                .curveTarget(),
            new BlockPos(result.vec())
        ));
        player.swing(InteractionHand.MAIN_HAND);
        cir.setReturnValue(true);
      }
    }
  }
}
