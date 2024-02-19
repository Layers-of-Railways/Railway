package com.railwayteam.railways.mixin;

import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.observer.TrackObserver;
import com.simibubi.create.content.trains.signal.SingleBlockEntityEdgePoint;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TrackObserver.class, priority = 1023) // random priority to ensure consistent injection order
public abstract class MixinTrackObserver extends SingleBlockEntityEdgePoint {
    @Inject(method = "write(Lnet/minecraft/network/FriendlyByteBuf;Lcom/simibubi/create/content/trains/graph/DimensionPalette;)V", at = @At("RETURN"))
    private void writePositions(FriendlyByteBuf buffer, DimensionPalette dimensions, CallbackInfo ci) {
        buffer.writeBoolean(blockEntityPos != null);
        if (blockEntityPos != null) {
            buffer.writeBlockPos(blockEntityPos);
        }
    }

    @Inject(method = "read(Lnet/minecraft/network/FriendlyByteBuf;Lcom/simibubi/create/content/trains/graph/DimensionPalette;)V", at = @At("RETURN"))
    private void readPositions(FriendlyByteBuf buffer, DimensionPalette dimensions, CallbackInfo ci) {
        if (buffer.readBoolean()) {
            blockEntityPos = buffer.readBlockPos();
        }
    }
}
