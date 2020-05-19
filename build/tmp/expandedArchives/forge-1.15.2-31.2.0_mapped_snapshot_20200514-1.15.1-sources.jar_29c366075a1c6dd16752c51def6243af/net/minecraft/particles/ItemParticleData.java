package net.minecraft.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.arguments.ItemInput;
import net.minecraft.command.arguments.ItemParser;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemParticleData implements IParticleData {
   public static final IParticleData.IDeserializer<ItemParticleData> DESERIALIZER = new IParticleData.IDeserializer<ItemParticleData>() {
      public ItemParticleData deserialize(ParticleType<ItemParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
         reader.expect(' ');
         ItemParser itemparser = (new ItemParser(reader, false)).parse();
         ItemStack itemstack = (new ItemInput(itemparser.getItem(), itemparser.getNbt())).createStack(1, false);
         return new ItemParticleData(particleTypeIn, itemstack);
      }

      public ItemParticleData read(ParticleType<ItemParticleData> particleTypeIn, PacketBuffer buffer) {
         return new ItemParticleData(particleTypeIn, buffer.readItemStack());
      }
   };
   private final ParticleType<ItemParticleData> particleType;
   private final ItemStack itemStack;

   public ItemParticleData(ParticleType<ItemParticleData> p_i47952_1_, ItemStack p_i47952_2_) {
      this.particleType = p_i47952_1_;
      this.itemStack = p_i47952_2_.copy(); //Forge: Fix stack updating after the fact causing particle changes.
   }

   public void write(PacketBuffer buffer) {
      buffer.writeItemStack(this.itemStack);
   }

   public String getParameters() {
      return Registry.PARTICLE_TYPE.getKey(this.getType()) + " " + (new ItemInput(this.itemStack.getItem(), this.itemStack.getTag())).serialize();
   }

   public ParticleType<ItemParticleData> getType() {
      return this.particleType;
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getItemStack() {
      return this.itemStack;
   }
}