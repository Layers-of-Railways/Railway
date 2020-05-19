package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SUpdateHealthPacket implements IPacket<IClientPlayNetHandler> {
   private float health;
   private int foodLevel;
   private float saturationLevel;

   public SUpdateHealthPacket() {
   }

   public SUpdateHealthPacket(float healthIn, int foodLevelIn, float saturationLevelIn) {
      this.health = healthIn;
      this.foodLevel = foodLevelIn;
      this.saturationLevel = saturationLevelIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.health = buf.readFloat();
      this.foodLevel = buf.readVarInt();
      this.saturationLevel = buf.readFloat();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeFloat(this.health);
      buf.writeVarInt(this.foodLevel);
      buf.writeFloat(this.saturationLevel);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleUpdateHealth(this);
   }

   @OnlyIn(Dist.CLIENT)
   public float getHealth() {
      return this.health;
   }

   @OnlyIn(Dist.CLIENT)
   public int getFoodLevel() {
      return this.foodLevel;
   }

   @OnlyIn(Dist.CLIENT)
   public float getSaturationLevel() {
      return this.saturationLevel;
   }
}