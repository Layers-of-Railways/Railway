package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SScoreboardObjectivePacket implements IPacket<IClientPlayNetHandler> {
   private String objectiveName;
   private ITextComponent displayName;
   private ScoreCriteria.RenderType renderType;
   private int action;

   public SScoreboardObjectivePacket() {
   }

   public SScoreboardObjectivePacket(ScoreObjective objective, int actionIn) {
      this.objectiveName = objective.getName();
      this.displayName = objective.getDisplayName();
      this.renderType = objective.getRenderType();
      this.action = actionIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.objectiveName = buf.readString(16);
      this.action = buf.readByte();
      if (this.action == 0 || this.action == 2) {
         this.displayName = buf.readTextComponent();
         this.renderType = buf.readEnumValue(ScoreCriteria.RenderType.class);
      }

   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeString(this.objectiveName);
      buf.writeByte(this.action);
      if (this.action == 0 || this.action == 2) {
         buf.writeTextComponent(this.displayName);
         buf.writeEnumValue(this.renderType);
      }

   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleScoreboardObjective(this);
   }

   @OnlyIn(Dist.CLIENT)
   public String getObjectiveName() {
      return this.objectiveName;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getDisplayName() {
      return this.displayName;
   }

   @OnlyIn(Dist.CLIENT)
   public int getAction() {
      return this.action;
   }

   @OnlyIn(Dist.CLIENT)
   public ScoreCriteria.RenderType getRenderType() {
      return this.renderType;
   }
}