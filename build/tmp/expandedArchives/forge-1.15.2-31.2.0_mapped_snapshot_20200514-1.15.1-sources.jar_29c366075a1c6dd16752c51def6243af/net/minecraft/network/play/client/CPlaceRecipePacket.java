package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPlaceRecipePacket implements IPacket<IServerPlayNetHandler> {
   private int windowId;
   private ResourceLocation recipeId;
   private boolean placeAll;

   public CPlaceRecipePacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPlaceRecipePacket(int p_i47614_1_, IRecipe<?> p_i47614_2_, boolean p_i47614_3_) {
      this.windowId = p_i47614_1_;
      this.recipeId = p_i47614_2_.getId();
      this.placeAll = p_i47614_3_;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.windowId = buf.readByte();
      this.recipeId = buf.readResourceLocation();
      this.placeAll = buf.readBoolean();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByte(this.windowId);
      buf.writeResourceLocation(this.recipeId);
      buf.writeBoolean(this.placeAll);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IServerPlayNetHandler handler) {
      handler.processPlaceRecipe(this);
   }

   public int getWindowId() {
      return this.windowId;
   }

   public ResourceLocation getRecipeId() {
      return this.recipeId;
   }

   public boolean shouldPlaceAll() {
      return this.placeAll;
   }
}