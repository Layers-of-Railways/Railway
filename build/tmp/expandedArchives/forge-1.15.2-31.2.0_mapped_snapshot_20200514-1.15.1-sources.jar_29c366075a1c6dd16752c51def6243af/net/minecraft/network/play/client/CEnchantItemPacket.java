package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CEnchantItemPacket implements IPacket<IServerPlayNetHandler> {
   private int windowId;
   private int button;

   public CEnchantItemPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CEnchantItemPacket(int windowIdIn, int buttonIn) {
      this.windowId = windowIdIn;
      this.button = buttonIn;
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IServerPlayNetHandler handler) {
      handler.processEnchantItem(this);
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.windowId = buf.readByte();
      this.button = buf.readByte();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByte(this.windowId);
      buf.writeByte(this.button);
   }

   public int getWindowId() {
      return this.windowId;
   }

   public int getButton() {
      return this.button;
   }
}