package net.minecraft.network.status;

import net.minecraft.network.INetHandler;
import net.minecraft.network.status.client.CPingPacket;
import net.minecraft.network.status.client.CServerQueryPacket;

public interface IServerStatusNetHandler extends INetHandler {
   void processPing(CPingPacket packetIn);

   void processServerQuery(CServerQueryPacket packetIn);
}