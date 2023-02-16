package com.railwayteam.railways.multiloader;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

/**
 * A Packet that is written on the server and handled on the client.
 */
public interface S2CPacket {
	void write(FriendlyByteBuf buffer);
	@Environment(EnvType.CLIENT)
	void handle(Minecraft mc, FriendlyByteBuf buffer);
}
