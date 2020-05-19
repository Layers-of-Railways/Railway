package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;

public class SSpawnMovingSoundEffectPacket implements IPacket<IClientPlayNetHandler> {
   private SoundEvent field_218765_a;
   private SoundCategory field_218766_b;
   private int field_218767_c;
   private float field_218768_d;
   private float field_218769_e;

   public SSpawnMovingSoundEffectPacket() {
   }

   public SSpawnMovingSoundEffectPacket(SoundEvent p_i50763_1_, SoundCategory p_i50763_2_, Entity p_i50763_3_, float p_i50763_4_, float p_i50763_5_) {
      Validate.notNull(p_i50763_1_, "sound");
      this.field_218765_a = p_i50763_1_;
      this.field_218766_b = p_i50763_2_;
      this.field_218767_c = p_i50763_3_.getEntityId();
      this.field_218768_d = p_i50763_4_;
      this.field_218769_e = p_i50763_5_;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.field_218765_a = Registry.SOUND_EVENT.getByValue(buf.readVarInt());
      this.field_218766_b = buf.readEnumValue(SoundCategory.class);
      this.field_218767_c = buf.readVarInt();
      this.field_218768_d = buf.readFloat();
      this.field_218769_e = buf.readFloat();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(Registry.SOUND_EVENT.getId(this.field_218765_a));
      buf.writeEnumValue(this.field_218766_b);
      buf.writeVarInt(this.field_218767_c);
      buf.writeFloat(this.field_218768_d);
      buf.writeFloat(this.field_218769_e);
   }

   @OnlyIn(Dist.CLIENT)
   public SoundEvent func_218763_b() {
      return this.field_218765_a;
   }

   @OnlyIn(Dist.CLIENT)
   public SoundCategory func_218760_c() {
      return this.field_218766_b;
   }

   @OnlyIn(Dist.CLIENT)
   public int func_218762_d() {
      return this.field_218767_c;
   }

   @OnlyIn(Dist.CLIENT)
   public float func_218764_e() {
      return this.field_218768_d;
   }

   @OnlyIn(Dist.CLIENT)
   public float func_218761_f() {
      return this.field_218769_e;
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleSpawnMovingSoundEffect(this);
   }
}