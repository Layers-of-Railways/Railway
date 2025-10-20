/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
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

package com.railwayteam.railways.mixin;

import com.railwayteam.railways.registry.CREdgePointTypes;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(value = SignalBoundary.class, priority = 1023) // random priority to ensure consistent injection order
public class MixinSignalBoundary {
    @Shadow public Couple<Map<BlockPos, Boolean>> blockEntities;

    @Inject(method = "canCoexistWith", at = @At("RETURN"), cancellable = true, remap = false)
    private void railways$switchOrCouplerCanCoexist(EdgePointType<?> otherType, boolean front, CallbackInfoReturnable<Boolean> cir) {
        if (otherType == CREdgePointTypes.COUPLER || otherType == CREdgePointTypes.SWITCH)
            cir.setReturnValue(true);
    }

    @Inject(method = "write(Lnet/minecraft/network/FriendlyByteBuf;Lcom/simibubi/create/content/trains/graph/DimensionPalette;)V", at = @At("RETURN"))
    private void writePositions(FriendlyByteBuf buffer, DimensionPalette dimensions, CallbackInfo ci) {
        for (boolean front : Iterate.trueAndFalse) {
            Map<BlockPos, Boolean> map = blockEntities.get(front);
            buffer.writeVarInt(map.size());
            for (Map.Entry<BlockPos, Boolean> entry : map.entrySet()) {
                buffer.writeBlockPos(entry.getKey());
                buffer.writeBoolean(entry.getValue());
            }
        }
    }

    @Inject(method = "read(Lnet/minecraft/network/FriendlyByteBuf;Lcom/simibubi/create/content/trains/graph/DimensionPalette;)V", at = @At("RETURN"))
    private void readPositions(FriendlyByteBuf buffer, DimensionPalette dimensions, CallbackInfo ci) {
        for (boolean front : Iterate.trueAndFalse) {
            Map<BlockPos, Boolean> map = blockEntities.get(front);
            map.clear();
            int size = buffer.readVarInt();
            for (int i = 0; i < size; i++) {
                map.put(buffer.readBlockPos(), buffer.readBoolean());
            }
        }
    }
}
