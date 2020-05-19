package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SSelectAdvancementsTabPacket implements IPacket<IClientPlayNetHandler> {
   @Nullable
   private ResourceLocation tab;

   public SSelectAdvancementsTabPacket() {
   }

   public SSelectAdvancementsTabPacket(@Nullable ResourceLocation p_i47596_1_) {
      this.tab = p_i47596_1_;
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleSelectAdvancementsTab(this);
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      if (buf.readBoolean()) {
         this.tab = buf.readResourceLocation();
      }

   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeBoolean(this.tab != null);
      if (this.tab != null) {
         buf.writeResourceLocation(this.tab);
      }

   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getTab() {
      return this.tab;
   }
}