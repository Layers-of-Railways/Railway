package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CEditBookPacket implements IPacket<IServerPlayNetHandler> {
   private ItemStack stack;
   private boolean updateAll;
   private Hand hand;

   public CEditBookPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CEditBookPacket(ItemStack stackIn, boolean updateAllIn, Hand handIn) {
      this.stack = stackIn.copy();
      this.updateAll = updateAllIn;
      this.hand = handIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.stack = buf.readItemStack();
      this.updateAll = buf.readBoolean();
      this.hand = buf.readEnumValue(Hand.class);
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeItemStack(this.stack);
      buf.writeBoolean(this.updateAll);
      buf.writeEnumValue(this.hand);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IServerPlayNetHandler handler) {
      handler.processEditBook(this);
   }

   /**
    * The client written book stack containing up to date nbt data.
    */
   public ItemStack getStack() {
      return this.stack;
   }

   /**
    * If true it updates author, title and pages. Otherwise just update pages.
    */
   public boolean shouldUpdateAll() {
      return this.updateAll;
   }

   public Hand getHand() {
      return this.hand;
   }
}