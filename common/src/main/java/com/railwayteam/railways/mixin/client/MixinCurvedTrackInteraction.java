/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.content.custom_tracks.casing.SlabUseOnCurvePacket;
import com.railwayteam.railways.content.handcar.HandcarItem;
import com.railwayteam.railways.registry.CRPackets;
import com.railwayteam.railways.registry.CRTags.AllBlockTags;
import com.railwayteam.railways.registry.CRTrackMaterials;
import com.railwayteam.railways.util.AdventureUtils;
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
  private static void railways$encaseCurve(CallbackInfoReturnable<Boolean> cir) {
    LocalPlayer player = Minecraft.getInstance().player;
    if (AdventureUtils.isAdventure(player))
      return;
    ItemStack held = player.getMainHandItem();

    BezierPointSelection result = TrackBlockOutline.result;
    TrackBlockEntity track = result.blockEntity();
    BezierTrackPointLocation location = result.loc();
    BlockPos curveTarget = location.curveTarget();
    Map<BlockPos, BezierConnection> connections = track.getConnections();
    BezierConnection connection = connections == null ? null : connections.get(curveTarget);

    if (held.getItem() instanceof HandcarItem handcar && handcar.useOnCurve(result, held)) {
      player.swing(InteractionHand.MAIN_HAND);
      cir.setReturnValue(true);
      return;
    }

    // allow encasing if no connection or not monorail
    if (connection == null || connection.getMaterial().trackType != CRTrackMaterials.CRTrackType.MONORAIL) {
      // if non-empty, must be a valid slab
      if (!held.isEmpty()) {
        if (!(held.getItem() instanceof BlockItem block))
          return;
        if (!(block.getBlock() instanceof SlabBlock slab))
          return;
        if (AllBlockTags.TRACK_CASING_BLACKLIST.matches(slab))
          return;
      }

      CRPackets.PACKETS.send(new SlabUseOnCurvePacket(track.getBlockPos(), curveTarget, BlockPos.containing(result.vec())));
      player.swing(InteractionHand.MAIN_HAND);
      cir.setReturnValue(true);
    }
  }
}
