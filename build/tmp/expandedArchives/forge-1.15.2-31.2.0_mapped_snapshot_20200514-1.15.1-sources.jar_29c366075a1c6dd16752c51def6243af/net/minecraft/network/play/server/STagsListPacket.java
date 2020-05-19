package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.NetworkTagManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class STagsListPacket implements IPacket<IClientPlayNetHandler> {
   private NetworkTagManager tags;

   public STagsListPacket() {
   }

   public STagsListPacket(NetworkTagManager p_i48211_1_) {
      this.tags = p_i48211_1_;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.tags = NetworkTagManager.read(buf);
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      this.tags.write(buf);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleTags(this);
   }

   @OnlyIn(Dist.CLIENT)
   public NetworkTagManager getTags() {
      return this.tags;
   }
}