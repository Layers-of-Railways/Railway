package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SChatPacket implements IPacket<IClientPlayNetHandler> {
   private ITextComponent chatComponent;
   private ChatType type;

   public SChatPacket() {
   }

   public SChatPacket(ITextComponent componentIn) {
      this(componentIn, ChatType.SYSTEM);
   }

   public SChatPacket(ITextComponent message, ChatType type) {
      this.chatComponent = message;
      this.type = type;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.chatComponent = buf.readTextComponent();
      this.type = ChatType.byId(buf.readByte());
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeTextComponent(this.chatComponent);
      buf.writeByte(this.type.getId());
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleChat(this);
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getChatComponent() {
      return this.chatComponent;
   }

   /**
    * This method returns true if the type is SYSTEM or ABOVE_HOTBAR, and false if CHAT
    */
   public boolean isSystem() {
      return this.type == ChatType.SYSTEM || this.type == ChatType.GAME_INFO;
   }

   public ChatType getType() {
      return this.type;
   }

   public boolean shouldSkipErrors() {
      return true;
   }
}