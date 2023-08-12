package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.events.CommonEvents;
import com.railwayteam.railways.multiloader.C2SPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public record JourneymapConfigurePacket(boolean wantPackets) implements C2SPacket {
    public JourneymapConfigurePacket(FriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBoolean(wantPackets);
    }

    @Override
    public void handle(ServerPlayer sender) {
        if (wantPackets) {
            CommonEvents.journeymapUsers.add(sender.getUUID());
        } else {
            CommonEvents.journeymapUsers.remove(sender.getUUID());
        }
    }
}
