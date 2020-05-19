package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SDisconnectPacket implements IPacket<IClientPlayNetHandler> {
   private ITextComponent reason;

   public SDisconnectPacket() {
   }

   public SDisconnectPacket(ITextComponent messageIn) {
      this.reason = messageIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.reason = buf.readTextComponent();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeTextComponent(this.reason);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleDisconnect(this);
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getReason() {
      return this.reason;
   }
}