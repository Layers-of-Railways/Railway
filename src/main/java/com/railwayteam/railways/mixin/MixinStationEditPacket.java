package com.railwayteam.railways.mixin;

import com.railwayteam.railways.mixin_interfaces.ILimited;
import com.simibubi.create.Create;
import com.simibubi.create.content.logistics.trains.GraphLocation;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.GlobalStation;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.StationEditPacket;
import com.simibubi.create.content.logistics.trains.management.edgePoint.station.StationTileEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = StationEditPacket.class, remap = false)
public abstract class MixinStationEditPacket implements ILimited {
    private Boolean limitEnabled;

    @Override
    public void setLimitEnabled(boolean limitEnabled) {
        this.limitEnabled = limitEnabled;
    }

    @Override
    public boolean isLimitEnabled() {
        return limitEnabled;
    }

    @Inject(method = "writeSettings", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;writeBoolean(Z)Lio/netty/buffer/ByteBuf;", ordinal = 3, remap = true), cancellable = true)
    private void writeLimitEnabled(FriendlyByteBuf buffer, CallbackInfo ci) {
        buffer.writeBoolean(limitEnabled != null);
        if (limitEnabled != null) {
            buffer.writeBoolean(limitEnabled);
            ci.cancel();
        }
    }

    @Inject(method = "readSettings", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;readBoolean()Z", ordinal = 3, remap = true), cancellable = true)
    private void readLimitEnabled(FriendlyByteBuf buffer, CallbackInfo ci) {
        if (buffer.readBoolean()) {
            limitEnabled = buffer.readBoolean();
            ci.cancel();
        }
    }

    @Inject(method = "applySettings(Lnet/minecraft/server/level/ServerPlayer;Lcom/simibubi/create/content/logistics/trains/management/edgePoint/station/StationTileEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;"))
    private void applyLimit(ServerPlayer player, StationTileEntity te, CallbackInfo ci) {
        if (limitEnabled != null) {
            GlobalStation station = te.getStation();
            GraphLocation graphLocation = te.edgePoint.determineGraphLocation();
            if (station != null && graphLocation != null) {
                ((ILimited) station).setLimitEnabled(limitEnabled);
                Create.RAILWAYS.sync.pointAdded(graphLocation.graph, station);
                Create.RAILWAYS.markTracksDirty();
            }
        }
    }
}
