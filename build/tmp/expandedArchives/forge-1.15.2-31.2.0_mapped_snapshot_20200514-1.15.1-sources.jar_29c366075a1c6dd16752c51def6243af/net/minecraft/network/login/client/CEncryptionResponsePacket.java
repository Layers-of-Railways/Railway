package net.minecraft.network.login.client;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.IServerLoginNetHandler;
import net.minecraft.util.CryptManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CEncryptionResponsePacket implements IPacket<IServerLoginNetHandler> {
   private byte[] secretKeyEncrypted = new byte[0];
   private byte[] verifyTokenEncrypted = new byte[0];

   public CEncryptionResponsePacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CEncryptionResponsePacket(SecretKey secret, PublicKey key, byte[] verifyToken) {
      this.secretKeyEncrypted = CryptManager.encryptData(key, secret.getEncoded());
      this.verifyTokenEncrypted = CryptManager.encryptData(key, verifyToken);
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.secretKeyEncrypted = buf.readByteArray();
      this.verifyTokenEncrypted = buf.readByteArray();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByteArray(this.secretKeyEncrypted);
      buf.writeByteArray(this.verifyTokenEncrypted);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IServerLoginNetHandler handler) {
      handler.processEncryptionResponse(this);
   }

   public SecretKey getSecretKey(PrivateKey key) {
      return CryptManager.decryptSharedKey(key, this.secretKeyEncrypted);
   }

   public byte[] getVerifyToken(PrivateKey key) {
      return key == null ? this.verifyTokenEncrypted : CryptManager.decryptData(key, this.verifyTokenEncrypted);
   }
}