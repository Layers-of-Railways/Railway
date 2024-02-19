package com.railwayteam.railways.mixin;

import com.railwayteam.railways.registry.CREdgePointTypes;
import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.EdgePointType;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Iterate;
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
