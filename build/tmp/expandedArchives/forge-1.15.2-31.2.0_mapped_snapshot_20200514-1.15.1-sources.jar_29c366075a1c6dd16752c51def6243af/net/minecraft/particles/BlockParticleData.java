package net.minecraft.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.arguments.BlockStateParser;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockParticleData implements IParticleData {
   public static final IParticleData.IDeserializer<BlockParticleData> DESERIALIZER = new IParticleData.IDeserializer<BlockParticleData>() {
      public BlockParticleData deserialize(ParticleType<BlockParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
         reader.expect(' ');
         return new BlockParticleData(particleTypeIn, (new BlockStateParser(reader, false)).parse(false).getState());
      }

      public BlockParticleData read(ParticleType<BlockParticleData> particleTypeIn, PacketBuffer buffer) {
         return new BlockParticleData(particleTypeIn, Block.BLOCK_STATE_IDS.getByValue(buffer.readVarInt()));
      }
   };
   private final ParticleType<BlockParticleData> particleType;
   private final BlockState blockState;

   public BlockParticleData(ParticleType<BlockParticleData> particleTypeIn, BlockState blockStateIn) {
      this.particleType = particleTypeIn;
      this.blockState = blockStateIn;
   }

   public void write(PacketBuffer buffer) {
      buffer.writeVarInt(Block.BLOCK_STATE_IDS.get(this.blockState));
   }

   public String getParameters() {
      return Registry.PARTICLE_TYPE.getKey(this.getType()) + " " + BlockStateParser.toString(this.blockState);
   }

   public ParticleType<BlockParticleData> getType() {
      return this.particleType;
   }

   @OnlyIn(Dist.CLIENT)
   public BlockState getBlockState() {
      return this.blockState;
   }

   //FORGE: Add a source pos property, so we can provide models with additional model data
   private net.minecraft.util.math.BlockPos pos;
   public BlockParticleData setPos(net.minecraft.util.math.BlockPos pos) {
      this.pos = pos;
      return this;
   }

   public net.minecraft.util.math.BlockPos getPos() {
      return pos;
   }
}