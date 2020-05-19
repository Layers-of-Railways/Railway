package net.minecraft.world.dimension;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.biome.provider.CheckerboardBiomeProvider;
import net.minecraft.world.biome.provider.CheckerboardBiomeProviderSettings;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProviderSettings;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.biome.provider.SingleBiomeProviderSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.DebugChunkGenerator;
import net.minecraft.world.gen.DebugGenerationSettings;
import net.minecraft.world.gen.EndChunkGenerator;
import net.minecraft.world.gen.EndGenerationSettings;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.NetherChunkGenerator;
import net.minecraft.world.gen.NetherGenSettings;
import net.minecraft.world.gen.OverworldChunkGenerator;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class OverworldDimension extends Dimension {
   public OverworldDimension(World worldIn, DimensionType typeIn) {
      super(worldIn, typeIn, 0.0F);
   }

   public ChunkGenerator<? extends GenerationSettings> createChunkGenerator() {
      WorldType worldtype = this.world.getWorldInfo().getGenerator();
      ChunkGeneratorType<FlatGenerationSettings, FlatChunkGenerator> chunkgeneratortype = ChunkGeneratorType.FLAT;
      ChunkGeneratorType<DebugGenerationSettings, DebugChunkGenerator> chunkgeneratortype1 = ChunkGeneratorType.DEBUG;
      ChunkGeneratorType<NetherGenSettings, NetherChunkGenerator> chunkgeneratortype2 = ChunkGeneratorType.CAVES;
      ChunkGeneratorType<EndGenerationSettings, EndChunkGenerator> chunkgeneratortype3 = ChunkGeneratorType.FLOATING_ISLANDS;
      ChunkGeneratorType<OverworldGenSettings, OverworldChunkGenerator> chunkgeneratortype4 = ChunkGeneratorType.SURFACE;
      BiomeProviderType<SingleBiomeProviderSettings, SingleBiomeProvider> biomeprovidertype = BiomeProviderType.FIXED;
      BiomeProviderType<OverworldBiomeProviderSettings, OverworldBiomeProvider> biomeprovidertype1 = BiomeProviderType.VANILLA_LAYERED;
      BiomeProviderType<CheckerboardBiomeProviderSettings, CheckerboardBiomeProvider> biomeprovidertype2 = BiomeProviderType.CHECKERBOARD;
      if (worldtype == WorldType.FLAT) {
         FlatGenerationSettings flatgenerationsettings = FlatGenerationSettings.createFlatGenerator(new Dynamic<>(NBTDynamicOps.INSTANCE, this.world.getWorldInfo().getGeneratorOptions()));
         SingleBiomeProviderSettings singlebiomeprovidersettings1 = biomeprovidertype.createSettings(this.world.getWorldInfo()).setBiome(flatgenerationsettings.getBiome());
         return chunkgeneratortype.create(this.world, biomeprovidertype.create(singlebiomeprovidersettings1), flatgenerationsettings);
      } else if (worldtype == WorldType.DEBUG_ALL_BLOCK_STATES) {
         SingleBiomeProviderSettings singlebiomeprovidersettings = biomeprovidertype.createSettings(this.world.getWorldInfo()).setBiome(Biomes.PLAINS);
         return chunkgeneratortype1.create(this.world, biomeprovidertype.create(singlebiomeprovidersettings), chunkgeneratortype1.createSettings());
      } else if (worldtype != WorldType.BUFFET) {
         OverworldGenSettings overworldgensettings = chunkgeneratortype4.createSettings();
         OverworldBiomeProviderSettings overworldbiomeprovidersettings = biomeprovidertype1.createSettings(this.world.getWorldInfo()).setGeneratorSettings(overworldgensettings);
         return chunkgeneratortype4.create(this.world, biomeprovidertype1.create(overworldbiomeprovidersettings), overworldgensettings);
      } else {
         BiomeProvider biomeprovider = null;
         JsonElement jsonelement = Dynamic.convert(NBTDynamicOps.INSTANCE, JsonOps.INSTANCE, this.world.getWorldInfo().getGeneratorOptions());
         JsonObject jsonobject = jsonelement.getAsJsonObject();
         JsonObject jsonobject1 = jsonobject.getAsJsonObject("biome_source");
         if (jsonobject1 != null && jsonobject1.has("type") && jsonobject1.has("options")) {
            BiomeProviderType<?, ?> biomeprovidertype3 = Registry.BIOME_SOURCE_TYPE.getOrDefault(new ResourceLocation(jsonobject1.getAsJsonPrimitive("type").getAsString()));
            JsonObject jsonobject2 = jsonobject1.getAsJsonObject("options");
            Biome[] abiome = new Biome[]{Biomes.OCEAN};
            if (jsonobject2.has("biomes")) {
               JsonArray jsonarray = jsonobject2.getAsJsonArray("biomes");
               abiome = jsonarray.size() > 0 ? new Biome[jsonarray.size()] : new Biome[]{Biomes.OCEAN};

               for(int i = 0; i < jsonarray.size(); ++i) {
                  abiome[i] = Registry.BIOME.getValue(new ResourceLocation(jsonarray.get(i).getAsString())).orElse(Biomes.OCEAN);
               }
            }

            if (BiomeProviderType.FIXED == biomeprovidertype3) {
               SingleBiomeProviderSettings singlebiomeprovidersettings2 = biomeprovidertype.createSettings(this.world.getWorldInfo()).setBiome(abiome[0]);
               biomeprovider = biomeprovidertype.create(singlebiomeprovidersettings2);
            }

            if (BiomeProviderType.CHECKERBOARD == biomeprovidertype3) {
               int j = jsonobject2.has("size") ? jsonobject2.getAsJsonPrimitive("size").getAsInt() : 2;
               CheckerboardBiomeProviderSettings checkerboardbiomeprovidersettings = biomeprovidertype2.createSettings(this.world.getWorldInfo()).setBiomes(abiome).setSize(j);
               biomeprovider = biomeprovidertype2.create(checkerboardbiomeprovidersettings);
            }

            if (BiomeProviderType.VANILLA_LAYERED == biomeprovidertype3) {
               OverworldBiomeProviderSettings overworldbiomeprovidersettings1 = biomeprovidertype1.createSettings(this.world.getWorldInfo());
               biomeprovider = biomeprovidertype1.create(overworldbiomeprovidersettings1);
            }
         }

         if (biomeprovider == null) {
            biomeprovider = biomeprovidertype.create(biomeprovidertype.createSettings(this.world.getWorldInfo()).setBiome(Biomes.OCEAN));
         }

         BlockState blockstate = Blocks.STONE.getDefaultState();
         BlockState blockstate1 = Blocks.WATER.getDefaultState();
         JsonObject jsonobject3 = jsonobject.getAsJsonObject("chunk_generator");
         if (jsonobject3 != null && jsonobject3.has("options")) {
            JsonObject jsonobject4 = jsonobject3.getAsJsonObject("options");
            if (jsonobject4.has("default_block")) {
               String s = jsonobject4.getAsJsonPrimitive("default_block").getAsString();
               blockstate = Registry.BLOCK.getOrDefault(new ResourceLocation(s)).getDefaultState();
            }

            if (jsonobject4.has("default_fluid")) {
               String s1 = jsonobject4.getAsJsonPrimitive("default_fluid").getAsString();
               blockstate1 = Registry.BLOCK.getOrDefault(new ResourceLocation(s1)).getDefaultState();
            }
         }

         if (jsonobject3 != null && jsonobject3.has("type")) {
            ChunkGeneratorType<?, ?> chunkgeneratortype5 = Registry.CHUNK_GENERATOR_TYPE.getOrDefault(new ResourceLocation(jsonobject3.getAsJsonPrimitive("type").getAsString()));
            if (ChunkGeneratorType.CAVES == chunkgeneratortype5) {
               NetherGenSettings nethergensettings = chunkgeneratortype2.createSettings();
               nethergensettings.setDefaultBlock(blockstate);
               nethergensettings.setDefaultFluid(blockstate1);
               return chunkgeneratortype2.create(this.world, biomeprovider, nethergensettings);
            }

            if (ChunkGeneratorType.FLOATING_ISLANDS == chunkgeneratortype5) {
               EndGenerationSettings endgenerationsettings = chunkgeneratortype3.createSettings();
               endgenerationsettings.setSpawnPos(new BlockPos(0, 64, 0));
               endgenerationsettings.setDefaultBlock(blockstate);
               endgenerationsettings.setDefaultFluid(blockstate1);
               return chunkgeneratortype3.create(this.world, biomeprovider, endgenerationsettings);
            }
         }

         OverworldGenSettings overworldgensettings1 = chunkgeneratortype4.createSettings();
         overworldgensettings1.setDefaultBlock(blockstate);
         overworldgensettings1.setDefaultFluid(blockstate1);
         return chunkgeneratortype4.create(this.world, biomeprovider, overworldgensettings1);
      }
   }

   @Nullable
   public BlockPos findSpawn(ChunkPos chunkPosIn, boolean checkValid) {
      for(int i = chunkPosIn.getXStart(); i <= chunkPosIn.getXEnd(); ++i) {
         for(int j = chunkPosIn.getZStart(); j <= chunkPosIn.getZEnd(); ++j) {
            BlockPos blockpos = this.findSpawn(i, j, checkValid);
            if (blockpos != null) {
               return blockpos;
            }
         }
      }

      return null;
   }

   @Nullable
   public BlockPos findSpawn(int posX, int posZ, boolean checkValid) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(posX, 0, posZ);
      Biome biome = this.world.getBiome(blockpos$mutable);
      BlockState blockstate = biome.getSurfaceBuilderConfig().getTop();
      if (checkValid && !blockstate.getBlock().isIn(BlockTags.VALID_SPAWN)) {
         return null;
      } else {
         Chunk chunk = this.world.getChunk(posX >> 4, posZ >> 4);
         int i = chunk.getTopBlockY(Heightmap.Type.MOTION_BLOCKING, posX & 15, posZ & 15);
         if (i < 0) {
            return null;
         } else if (chunk.getTopBlockY(Heightmap.Type.WORLD_SURFACE, posX & 15, posZ & 15) > chunk.getTopBlockY(Heightmap.Type.OCEAN_FLOOR, posX & 15, posZ & 15)) {
            return null;
         } else {
            for(int j = i + 1; j >= 0; --j) {
               blockpos$mutable.setPos(posX, j, posZ);
               BlockState blockstate1 = this.world.getBlockState(blockpos$mutable);
               if (!blockstate1.getFluidState().isEmpty()) {
                  break;
               }

               if (blockstate1.equals(blockstate)) {
                  return blockpos$mutable.up().toImmutable();
               }
            }

            return null;
         }
      }
   }

   /**
    * Calculates the angle of sun and moon in the sky relative to a specified time (usually worldTime)
    */
   public float calculateCelestialAngle(long worldTime, float partialTicks) {
      double d0 = MathHelper.frac((double)worldTime / 24000.0D - 0.25D);
      double d1 = 0.5D - Math.cos(d0 * Math.PI) / 2.0D;
      return (float)(d0 * 2.0D + d1) / 3.0F;
   }

   /**
    * Returns 'true' if in the "main surface world", but 'false' if in the Nether or End dimensions.
    */
   public boolean isSurfaceWorld() {
      return true;
   }

   /**
    * Return Vec3D with biome specific fog color
    */
   @OnlyIn(Dist.CLIENT)
   public Vec3d getFogColor(float celestialAngle, float partialTicks) {
      float f = MathHelper.cos(celestialAngle * ((float)Math.PI * 2F)) * 2.0F + 0.5F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      float f1 = 0.7529412F;
      float f2 = 0.84705883F;
      float f3 = 1.0F;
      f1 = f1 * (f * 0.94F + 0.06F);
      f2 = f2 * (f * 0.94F + 0.06F);
      f3 = f3 * (f * 0.91F + 0.09F);
      return new Vec3d((double)f1, (double)f2, (double)f3);
   }

   /**
    * True if the player can respawn in this dimension (true = overworld, false = nether).
    */
   public boolean canRespawnHere() {
      return true;
   }

   /**
    * Returns true if the given X,Z coordinate should show environmental fog.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean doesXZShowFog(int x, int z) {
      return false;
   }
}