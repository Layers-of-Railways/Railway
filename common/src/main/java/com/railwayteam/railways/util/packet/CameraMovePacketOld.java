package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.multiloader.C2SPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

public class CameraMovePacketOld implements C2SPacket {
    final int id;
    final float yRot;
    final float xRot;

    public CameraMovePacketOld(ConductorEntity entity, float yRot, float xRot) {
        this.id = entity.getId();
        this.yRot = yRot;
        this.xRot = xRot;
    }

    public CameraMovePacketOld(FriendlyByteBuf buf) {
        this.id = buf.readVarInt();
        this.yRot = buf.readFloat();
        this.xRot = buf.readFloat();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(id);
        buffer.writeFloat(yRot);
        buffer.writeFloat(xRot);
    }

    @Override
    public void handle(ServerPlayer sender) {
        if (sender.level.getEntity(id) instanceof ConductorEntity conductor && sender.getCamera() == conductor) {
            conductor.setYRot(yRot % 360.0f);
            conductor.setXRot(Mth.clamp(xRot, -90.0f, 90.0f) % 360.0f);
        }
    }
}
