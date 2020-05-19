package net.minecraft.network.login.client;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.IServerLoginNetHandler;

public class CLoginStartPacket implements IPacket<IServerLoginNetHandler> {
   private GameProfile profile;

   public CLoginStartPacket() {
   }

   public CLoginStartPacket(GameProfile profileIn) {
      this.profile = profileIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.profile = new GameProfile((UUID)null, buf.readString(16));
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeString(this.profile.getName());
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IServerLoginNetHandler handler) {
      handler.processLoginStart(this);
   }

   public GameProfile getProfile() {
      return this.profile;
   }
}