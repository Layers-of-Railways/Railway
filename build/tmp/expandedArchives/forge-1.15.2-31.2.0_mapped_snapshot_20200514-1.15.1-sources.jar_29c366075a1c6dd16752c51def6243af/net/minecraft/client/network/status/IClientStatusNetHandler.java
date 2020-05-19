package net.minecraft.client.network.status;

import net.minecraft.network.INetHandler;
import net.minecraft.network.status.server.SPongPacket;
import net.minecraft.network.status.server.SServerInfoPacket;

public interface IClientStatusNetHandler extends INetHandler {
   void handleServerInfo(SServerInfoPacket packetIn);

   void handlePong(SPongPacket packetIn);
}