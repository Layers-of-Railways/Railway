package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.multiloader.C2SPacket;
import com.railwayteam.railways.registry.CRPackets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public class DismountCameraPacket implements C2SPacket {
    public DismountCameraPacket() {}
    public DismountCameraPacket(FriendlyByteBuf buf) {}
    @Override
    public void write(FriendlyByteBuf buffer) {}

    @Override
    public void handle(ServerPlayer sender) {
        if (sender.getCamera() instanceof ConductorEntity conductor) {
            conductor.stopViewing(sender);
        } else {
            CRPackets.PACKETS.sendTo(sender, new SetCameraViewPacket(sender));
        }
    }
}
