package net.minecraft.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Locale;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RedstoneParticleData implements IParticleData {
   public static final RedstoneParticleData REDSTONE_DUST = new RedstoneParticleData(1.0F, 0.0F, 0.0F, 1.0F);
   public static final IParticleData.IDeserializer<RedstoneParticleData> DESERIALIZER = new IParticleData.IDeserializer<RedstoneParticleData>() {
      public RedstoneParticleData deserialize(ParticleType<RedstoneParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
         reader.expect(' ');
         float f = (float)reader.readDouble();
         reader.expect(' ');
         float f1 = (float)reader.readDouble();
         reader.expect(' ');
         float f2 = (float)reader.readDouble();
         reader.expect(' ');
         float f3 = (float)reader.readDouble();
         return new RedstoneParticleData(f, f1, f2, f3);
      }

      public RedstoneParticleData read(ParticleType<RedstoneParticleData> particleTypeIn, PacketBuffer buffer) {
         return new RedstoneParticleData(buffer.readFloat(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
      }
   };
   private final float red;
   private final float green;
   private final float blue;
   private final float alpha;

   public RedstoneParticleData(float p_i47950_1_, float p_i47950_2_, float p_i47950_3_, float p_i47950_4_) {
      this.red = p_i47950_1_;
      this.green = p_i47950_2_;
      this.blue = p_i47950_3_;
      this.alpha = MathHelper.clamp(p_i47950_4_, 0.01F, 4.0F);
   }

   public void write(PacketBuffer buffer) {
      buffer.writeFloat(this.red);
      buffer.writeFloat(this.green);
      buffer.writeFloat(this.blue);
      buffer.writeFloat(this.alpha);
   }

   public String getParameters() {
      return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f", Registry.PARTICLE_TYPE.getKey(this.getType()), this.red, this.green, this.blue, this.alpha);
   }

   public ParticleType<RedstoneParticleData> getType() {
      return ParticleTypes.DUST;
   }

   @OnlyIn(Dist.CLIENT)
   public float getRed() {
      return this.red;
   }

   @OnlyIn(Dist.CLIENT)
   public float getGreen() {
      return this.green;
   }

   @OnlyIn(Dist.CLIENT)
   public float getBlue() {
      return this.blue;
   }

   @OnlyIn(Dist.CLIENT)
   public float getAlpha() {
      return this.alpha;
   }
}