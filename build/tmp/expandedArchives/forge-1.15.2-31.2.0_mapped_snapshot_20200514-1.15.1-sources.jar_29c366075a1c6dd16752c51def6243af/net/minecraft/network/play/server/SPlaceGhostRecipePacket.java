package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPlaceGhostRecipePacket implements IPacket<IClientPlayNetHandler> {
   private int windowId;
   private ResourceLocation recipe;

   public SPlaceGhostRecipePacket() {
   }

   public SPlaceGhostRecipePacket(int p_i47615_1_, IRecipe<?> p_i47615_2_) {
      this.windowId = p_i47615_1_;
      this.recipe = p_i47615_2_.getId();
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getRecipeId() {
      return this.recipe;
   }

   @OnlyIn(Dist.CLIENT)
   public int getWindowId() {
      return this.windowId;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.windowId = buf.readByte();
      this.recipe = buf.readResourceLocation();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByte(this.windowId);
      buf.writeResourceLocation(this.recipe);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handlePlaceGhostRecipe(this);
   }
}