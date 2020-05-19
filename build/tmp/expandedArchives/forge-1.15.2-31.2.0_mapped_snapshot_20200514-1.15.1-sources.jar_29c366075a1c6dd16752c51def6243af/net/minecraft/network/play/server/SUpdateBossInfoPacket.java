package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SUpdateBossInfoPacket implements IPacket<IClientPlayNetHandler> {
   private UUID uniqueId;
   private SUpdateBossInfoPacket.Operation operation;
   private ITextComponent name;
   private float percent;
   private BossInfo.Color color;
   private BossInfo.Overlay overlay;
   private boolean darkenSky;
   private boolean playEndBossMusic;
   private boolean createFog;

   public SUpdateBossInfoPacket() {
   }

   public SUpdateBossInfoPacket(SUpdateBossInfoPacket.Operation operationIn, BossInfo data) {
      this.operation = operationIn;
      this.uniqueId = data.getUniqueId();
      this.name = data.getName();
      this.percent = data.getPercent();
      this.color = data.getColor();
      this.overlay = data.getOverlay();
      this.darkenSky = data.shouldDarkenSky();
      this.playEndBossMusic = data.shouldPlayEndBossMusic();
      this.createFog = data.shouldCreateFog();
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.uniqueId = buf.readUniqueId();
      this.operation = buf.readEnumValue(SUpdateBossInfoPacket.Operation.class);
      switch(this.operation) {
      case ADD:
         this.name = buf.readTextComponent();
         this.percent = buf.readFloat();
         this.color = buf.readEnumValue(BossInfo.Color.class);
         this.overlay = buf.readEnumValue(BossInfo.Overlay.class);
         this.setFlags(buf.readUnsignedByte());
      case REMOVE:
      default:
         break;
      case UPDATE_PCT:
         this.percent = buf.readFloat();
         break;
      case UPDATE_NAME:
         this.name = buf.readTextComponent();
         break;
      case UPDATE_STYLE:
         this.color = buf.readEnumValue(BossInfo.Color.class);
         this.overlay = buf.readEnumValue(BossInfo.Overlay.class);
         break;
      case UPDATE_PROPERTIES:
         this.setFlags(buf.readUnsignedByte());
      }

   }

   private void setFlags(int flags) {
      this.darkenSky = (flags & 1) > 0;
      this.playEndBossMusic = (flags & 2) > 0;
      this.createFog = (flags & 4) > 0;
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeUniqueId(this.uniqueId);
      buf.writeEnumValue(this.operation);
      switch(this.operation) {
      case ADD:
         buf.writeTextComponent(this.name);
         buf.writeFloat(this.percent);
         buf.writeEnumValue(this.color);
         buf.writeEnumValue(this.overlay);
         buf.writeByte(this.getFlags());
      case REMOVE:
      default:
         break;
      case UPDATE_PCT:
         buf.writeFloat(this.percent);
         break;
      case UPDATE_NAME:
         buf.writeTextComponent(this.name);
         break;
      case UPDATE_STYLE:
         buf.writeEnumValue(this.color);
         buf.writeEnumValue(this.overlay);
         break;
      case UPDATE_PROPERTIES:
         buf.writeByte(this.getFlags());
      }

   }

   private int getFlags() {
      int i = 0;
      if (this.darkenSky) {
         i |= 1;
      }

      if (this.playEndBossMusic) {
         i |= 2;
      }

      if (this.createFog) {
         i |= 4;
      }

      return i;
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleUpdateBossInfo(this);
   }

   @OnlyIn(Dist.CLIENT)
   public UUID getUniqueId() {
      return this.uniqueId;
   }

   @OnlyIn(Dist.CLIENT)
   public SUpdateBossInfoPacket.Operation getOperation() {
      return this.operation;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getName() {
      return this.name;
   }

   @OnlyIn(Dist.CLIENT)
   public float getPercent() {
      return this.percent;
   }

   @OnlyIn(Dist.CLIENT)
   public BossInfo.Color getColor() {
      return this.color;
   }

   @OnlyIn(Dist.CLIENT)
   public BossInfo.Overlay getOverlay() {
      return this.overlay;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldDarkenSky() {
      return this.darkenSky;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldPlayEndBossMusic() {
      return this.playEndBossMusic;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldCreateFog() {
      return this.createFog;
   }

   public static enum Operation {
      ADD,
      REMOVE,
      UPDATE_PCT,
      UPDATE_NAME,
      UPDATE_STYLE,
      UPDATE_PROPERTIES;
   }
}