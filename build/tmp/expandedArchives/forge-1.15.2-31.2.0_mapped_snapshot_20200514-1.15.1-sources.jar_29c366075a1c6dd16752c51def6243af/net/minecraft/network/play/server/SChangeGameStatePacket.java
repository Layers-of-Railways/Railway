package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SChangeGameStatePacket implements IPacket<IClientPlayNetHandler> {
   public static final String[] MESSAGE_NAMES = new String[]{"block.minecraft.bed.not_valid"};
   private int state;
   private float value;

   public SChangeGameStatePacket() {
   }

   public SChangeGameStatePacket(int stateIn, float valueIn) {
      this.state = stateIn;
      this.value = valueIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.state = buf.readUnsignedByte();
      this.value = buf.readFloat();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByte(this.state);
      buf.writeFloat(this.value);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleChangeGameState(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getGameState() {
      return this.state;
   }

   @OnlyIn(Dist.CLIENT)
   public float getValue() {
      return this.value;
   }
}