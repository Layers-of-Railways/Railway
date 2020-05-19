package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SSetExperiencePacket implements IPacket<IClientPlayNetHandler> {
   private float experienceBar;
   private int totalExperience;
   private int level;

   public SSetExperiencePacket() {
   }

   public SSetExperiencePacket(float experienceBarIn, int totalExperienceIn, int levelIn) {
      this.experienceBar = experienceBarIn;
      this.totalExperience = totalExperienceIn;
      this.level = levelIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.experienceBar = buf.readFloat();
      this.level = buf.readVarInt();
      this.totalExperience = buf.readVarInt();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeFloat(this.experienceBar);
      buf.writeVarInt(this.level);
      buf.writeVarInt(this.totalExperience);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleSetExperience(this);
   }

   @OnlyIn(Dist.CLIENT)
   public float getExperienceBar() {
      return this.experienceBar;
   }

   @OnlyIn(Dist.CLIENT)
   public int getTotalExperience() {
      return this.totalExperience;
   }

   @OnlyIn(Dist.CLIENT)
   public int getLevel() {
      return this.level;
   }
}