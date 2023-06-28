package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.content.conductor.ConductorPossessionController;
import com.railwayteam.railways.multiloader.C2SPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class SpyConductorInteractPacket implements C2SPacket {
    final BlockPos pos;

    public SpyConductorInteractPacket(BlockPos pos) {
        this.pos = pos;
    }

    public SpyConductorInteractPacket(FriendlyByteBuf buf) {
        pos = buf.readBlockPos();
    }


    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
    }

    @Override
    public void handle(ServerPlayer sender) {
        ConductorEntity conductor;
        if ((conductor = ConductorPossessionController.getPossessingConductor(sender)) != null) {
            conductor.onSpyInteract(pos);
        }
    }
}
