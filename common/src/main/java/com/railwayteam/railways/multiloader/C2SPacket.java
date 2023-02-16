package com.railwayteam.railways.multiloader;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

/**
 * A Packet that is written on the client and handled on the server.
 */
public interface C2SPacket {
	void write(FriendlyByteBuf buffer);
	void handle(ServerPlayer sender, FriendlyByteBuf buf);
}
