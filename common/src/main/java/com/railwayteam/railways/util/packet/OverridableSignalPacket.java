package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.content.distant_signals.IOverridableSignal;
import com.railwayteam.railways.multiloader.S2CPacket;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class OverridableSignalPacket implements S2CPacket {

    final BlockPos blockPos;
    @Nullable
    final BlockPos signalPos;
    final SignalBlockEntity.SignalState signalState;
    final int ticks;
    final boolean distantSignal;

    public OverridableSignalPacket(BlockPos displayPos, @Nullable BlockPos signalPos,
                                   SignalBlockEntity.SignalState signalState, int ticks, boolean distantSignal) {
        blockPos = displayPos;
        this.signalPos = signalPos;
        this.signalState = signalState;
        this.ticks = ticks;
        this.distantSignal = distantSignal;
    }

    public OverridableSignalPacket(FriendlyByteBuf buf) {
        blockPos = buf.readBlockPos();
        if (buf.readBoolean()) {
            signalPos = buf.readBlockPos();
        } else {
            signalPos = null;
        }
        signalState = SignalBlockEntity.SignalState.values()[buf.readInt()];
        ticks = buf.readInt();
        distantSignal = buf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(blockPos);
        buffer.writeBoolean(signalPos != null);
        if (signalPos != null)
            buffer.writeBlockPos(signalPos);
        buffer.writeInt(signalState.ordinal());
        buffer.writeInt(ticks);
        buffer.writeBoolean(distantSignal);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void handle(Minecraft mc) {
        Level level = mc.level;
        if (level != null) {
            BlockEntity te = level.getBlockEntity(blockPos);
            if (te instanceof IOverridableSignal overridableSignal) {
                SignalBlockEntity signalBE = null;
                if (signalPos != null && level.getBlockEntity(signalPos) instanceof SignalBlockEntity signal)
                    signalBE = signal;
                overridableSignal.refresh(signalBE, signalState, ticks, distantSignal);
            }
        }
    }
}
