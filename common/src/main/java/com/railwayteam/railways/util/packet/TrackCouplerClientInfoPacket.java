package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.content.coupling.coupler.TrackCouplerTileEntity;
import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TrackCouplerClientInfoPacket extends SimplePacketBase {
    final BlockPos blockPos;
    final TrackCouplerTileEntity.ClientInfo info;

    public TrackCouplerClientInfoPacket(TrackCouplerTileEntity te) {
        blockPos = te.getBlockPos();
        info = te.getClientInfo();
    }

    public TrackCouplerClientInfoPacket(FriendlyByteBuf buf) {
        blockPos = buf.readBlockPos();
        info = new TrackCouplerTileEntity.ClientInfo(buf.readNbt());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(blockPos);
        buffer.writeNbt(info.write());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> this.__handle(context)));
        context.get().setPacketHandled(true);
    }

    @Environment(EnvType.CLIENT)
    private void __handle (Supplier<NetworkEvent.Context> supplier) {
        Level level = Minecraft.getInstance().level;
        if (level != null) {
            BlockEntity te = level.getBlockEntity(blockPos);
            if (te instanceof TrackCouplerTileEntity couplerTile)
                couplerTile.setClientInfo(info);
        }
    }
}
