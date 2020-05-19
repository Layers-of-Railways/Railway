package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.item.Item;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SCooldownPacket implements IPacket<IClientPlayNetHandler> {
   private Item item;
   private int ticks;

   public SCooldownPacket() {
   }

   public SCooldownPacket(Item itemIn, int ticksIn) {
      this.item = itemIn;
      this.ticks = ticksIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.item = Item.getItemById(buf.readVarInt());
      this.ticks = buf.readVarInt();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(Item.getIdFromItem(this.item));
      buf.writeVarInt(this.ticks);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleCooldown(this);
   }

   @OnlyIn(Dist.CLIENT)
   public Item getItem() {
      return this.item;
   }

   @OnlyIn(Dist.CLIENT)
   public int getTicks() {
      return this.ticks;
   }
}