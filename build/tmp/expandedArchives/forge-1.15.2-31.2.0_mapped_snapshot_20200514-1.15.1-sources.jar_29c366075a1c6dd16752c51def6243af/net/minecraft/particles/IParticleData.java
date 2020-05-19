package net.minecraft.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketBuffer;

public interface IParticleData {
   ParticleType<?> getType();

   void write(PacketBuffer buffer);

   String getParameters();

   public interface IDeserializer<T extends IParticleData> {
      T deserialize(ParticleType<T> particleTypeIn, StringReader reader) throws CommandSyntaxException;

      T read(ParticleType<T> particleTypeIn, PacketBuffer buffer);
   }
}