package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.player.ChatVisibility;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.HandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CClientSettingsPacket implements IPacket<IServerPlayNetHandler> {
   private String lang;
   private int view;
   private ChatVisibility chatVisibility;
   private boolean enableColors;
   private int modelPartFlags;
   private HandSide mainHand;

   public CClientSettingsPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CClientSettingsPacket(String p_i50761_1_, int p_i50761_2_, ChatVisibility p_i50761_3_, boolean p_i50761_4_, int p_i50761_5_, HandSide p_i50761_6_) {
      this.lang = p_i50761_1_;
      this.view = p_i50761_2_;
      this.chatVisibility = p_i50761_3_;
      this.enableColors = p_i50761_4_;
      this.modelPartFlags = p_i50761_5_;
      this.mainHand = p_i50761_6_;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.lang = buf.readString(16);
      this.view = buf.readByte();
      this.chatVisibility = buf.readEnumValue(ChatVisibility.class);
      this.enableColors = buf.readBoolean();
      this.modelPartFlags = buf.readUnsignedByte();
      this.mainHand = buf.readEnumValue(HandSide.class);
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeString(this.lang);
      buf.writeByte(this.view);
      buf.writeEnumValue(this.chatVisibility);
      buf.writeBoolean(this.enableColors);
      buf.writeByte(this.modelPartFlags);
      buf.writeEnumValue(this.mainHand);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IServerPlayNetHandler handler) {
      handler.processClientSettings(this);
   }

   public String getLang() {
      return this.lang;
   }

   public ChatVisibility getChatVisibility() {
      return this.chatVisibility;
   }

   public boolean isColorsEnabled() {
      return this.enableColors;
   }

   public int getModelPartFlags() {
      return this.modelPartFlags;
   }

   public HandSide getMainHand() {
      return this.mainHand;
   }
}