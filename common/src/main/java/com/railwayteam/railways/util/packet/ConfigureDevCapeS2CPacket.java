package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.multiloader.S2CPacket;
import com.railwayteam.railways.util.DevCapeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public record ConfigureDevCapeS2CPacket(UUID uuid, boolean useDevCape) implements S2CPacket {

    public ConfigureDevCapeS2CPacket(FriendlyByteBuf buf) {
        this(buf.readUUID(), buf.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(uuid);
        buffer.writeBoolean(useDevCape);
    }

    @Override
    public void handle(Minecraft mc) {
        DevCapeUtils.usageStatusClientside.put(uuid, useDevCape);
    }
}
