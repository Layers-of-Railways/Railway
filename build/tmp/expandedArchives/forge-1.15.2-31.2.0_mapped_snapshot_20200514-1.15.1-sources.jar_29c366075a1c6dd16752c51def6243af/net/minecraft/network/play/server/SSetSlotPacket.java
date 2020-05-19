package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SSetSlotPacket implements IPacket<IClientPlayNetHandler> {
   private int windowId;
   private int slot;
   private ItemStack item = ItemStack.EMPTY;

   public SSetSlotPacket() {
   }

   public SSetSlotPacket(int windowIdIn, int slotIn, ItemStack itemIn) {
      this.windowId = windowIdIn;
      this.slot = slotIn;
      this.item = itemIn.copy();
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleSetSlot(this);
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.windowId = buf.readByte();
      this.slot = buf.readShort();
      this.item = buf.readItemStack();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByte(this.windowId);
      buf.writeShort(this.slot);
      buf.writeItemStack(this.item);
   }

   @OnlyIn(Dist.CLIENT)
   public int getWindowId() {
      return this.windowId;
   }

   @OnlyIn(Dist.CLIENT)
   public int getSlot() {
      return this.slot;
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getStack() {
      return this.item;
   }
}