package net.minecraft.network.login.server;

import java.io.IOException;
import java.security.PublicKey;
import net.minecraft.client.network.login.IClientLoginNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.CryptManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SEncryptionRequestPacket implements IPacket<IClientLoginNetHandler> {
   private String hashedServerId;
   private PublicKey publicKey;
   private byte[] verifyToken;

   public SEncryptionRequestPacket() {
   }

   public SEncryptionRequestPacket(String serverIdIn, PublicKey publicKeyIn, byte[] verifyTokenIn) {
      this.hashedServerId = serverIdIn;
      this.publicKey = publicKeyIn;
      this.verifyToken = verifyTokenIn;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.hashedServerId = buf.readString(20);
      this.publicKey = CryptManager.decodePublicKey(buf.readByteArray());
      this.verifyToken = buf.readByteArray();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeString(this.hashedServerId);
      buf.writeByteArray(this.publicKey.getEncoded());
      buf.writeByteArray(this.verifyToken);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientLoginNetHandler handler) {
      handler.handleEncryptionRequest(this);
   }

   @OnlyIn(Dist.CLIENT)
   public String getServerId() {
      return this.hashedServerId;
   }

   @OnlyIn(Dist.CLIENT)
   public PublicKey getPublicKey() {
      return this.publicKey;
   }

   @OnlyIn(Dist.CLIENT)
   public byte[] getVerifyToken() {
      return this.verifyToken;
   }
}