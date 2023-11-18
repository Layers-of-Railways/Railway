package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.multiloader.C2SPacket;
import com.railwayteam.railways.multiloader.PlayerSelection;
import com.railwayteam.railways.registry.CRPackets;
import com.railwayteam.railways.util.DevCapeUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public record ConfigureDevCapeC2SPacket(boolean useDevCape) implements C2SPacket {

    public ConfigureDevCapeC2SPacket(FriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBoolean(useDevCape);
    }

    @Override
    public void handle(ServerPlayer sender) {
        DevCapeUtils.usageStatusServerside.put(sender.getUUID(), useDevCape);

        CRPackets.PACKETS.sendTo(PlayerSelection.all(), new ConfigureDevCapeS2CPacket(sender.getUUID(), useDevCape));
    }
}
