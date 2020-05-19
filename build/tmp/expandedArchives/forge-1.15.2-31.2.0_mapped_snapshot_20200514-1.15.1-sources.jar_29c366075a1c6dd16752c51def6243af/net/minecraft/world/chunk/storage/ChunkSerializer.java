package net.minecraft.world.chunk.storage;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongArrayNBT;
import net.minecraft.nbt.ShortNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.ITickList;
import net.minecraft.world.LightType;
import net.minecraft.world.SerializableTickList;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkPrimerTickList;
import net.minecraft.world.chunk.ChunkPrimerWrapper;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.structure.Structures;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkSerializer {
   private static final Logger LOGGER = LogManager.getLogger();

   public static ChunkPrimer read(ServerWorld worldIn, TemplateManager templateManagerIn, PointOfInterestManager poiManager, ChunkPos pos, CompoundNBT compound) {
      ChunkGenerator<?> chunkgenerator = worldIn.getChunkProvider().getChunkGenerator();
      BiomeProvider biomeprovider = chunkgenerator.getBiomeProvider();
      CompoundNBT compoundnbt = compound.getCompound("Level");
      ChunkPos chunkpos = new ChunkPos(compoundnbt.getInt("xPos"), compoundnbt.getInt("zPos"));
      if (!Objects.equals(pos, chunkpos)) {
         LOGGER.error("Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", pos, pos, chunkpos);
      }

      BiomeContainer biomecontainer = new BiomeContainer(pos, biomeprovider, compoundnbt.contains("Biomes", 11) ? compoundnbt.getIntArray("Biomes") : null);
      UpgradeData upgradedata = compoundnbt.contains("UpgradeData", 10) ? new UpgradeData(compoundnbt.getCompound("UpgradeData")) : UpgradeData.EMPTY;
      ChunkPrimerTickList<Block> chunkprimerticklist = new ChunkPrimerTickList<>((p_222652_0_) -> {
         return p_222652_0_ == null || p_222652_0_.getDefaultState().isAir();
      }, pos, compoundnbt.getList("ToBeTicked", 9));
      ChunkPrimerTickList<Fluid> chunkprimerticklist1 = new ChunkPrimerTickList<>((p_222646_0_) -> {
         return p_222646_0_ == null || p_222646_0_ == Fluids.EMPTY;
      }, pos, compoundnbt.getList("LiquidsToBeTicked", 9));
      boolean flag = compoundnbt.getBoolean("isLightOn");
      ListNBT listnbt = compoundnbt.getList("Sections", 10);
      int i = 16;
      ChunkSection[] achunksection = new ChunkSection[16];
      boolean flag1 = worldIn.getDimension().hasSkyLight();
      AbstractChunkProvider abstractchunkprovider = worldIn.getChunkProvider();
      WorldLightManager worldlightmanager = abstractchunkprovider.getLightManager();
      if (flag) {
         worldlightmanager.retainData(pos, true);
      }

      for(int j = 0; j < listnbt.size(); ++j) {
         CompoundNBT compoundnbt1 = listnbt.getCompound(j);
         int k = compoundnbt1.getByte("Y");
         if (compoundnbt1.contains("Palette", 9) && compoundnbt1.contains("BlockStates", 12)) {
            ChunkSection chunksection = new ChunkSection(k << 4);
            chunksection.getData().readChunkPalette(compoundnbt1.getList("Palette", 10), compoundnbt1.getLongArray("BlockStates"));
            chunksection.recalculateRefCounts();
            if (!chunksection.isEmpty()) {
               achunksection[k] = chunksection;
            }

            poiManager.checkConsistencyWithBlocks(pos, chunksection);
         }

         if (flag) {
            if (compoundnbt1.contains("BlockLight", 7)) {
               worldlightmanager.setData(LightType.BLOCK, SectionPos.from(pos, k), new NibbleArray(compoundnbt1.getByteArray("BlockLight")));
            }

            if (flag1 && compoundnbt1.contains("SkyLight", 7)) {
               worldlightmanager.setData(LightType.SKY, SectionPos.from(pos, k), new NibbleArray(compoundnbt1.getByteArray("SkyLight")));
            }
         }
      }

      long k1 = compoundnbt.getLong("InhabitedTime");
      ChunkStatus.Type chunkstatus$type = getChunkStatus(compound);
      IChunk ichunk;
      if (chunkstatus$type == ChunkStatus.Type.LEVELCHUNK) {
         ITickList<Block> iticklist;
         if (compoundnbt.contains("TileTicks", 9)) {
            iticklist = SerializableTickList.create(compoundnbt.getList("TileTicks", 10), Registry.BLOCK::getKey, Registry.BLOCK::getOrDefault);
         } else {
            iticklist = chunkprimerticklist;
         }

         ITickList<Fluid> iticklist1;
         if (compoundnbt.contains("LiquidTicks", 9)) {
            iticklist1 = SerializableTickList.create(compoundnbt.getList("LiquidTicks", 10), Registry.FLUID::getKey, Registry.FLUID::getOrDefault);
         } else {
            iticklist1 = chunkprimerticklist1;
         }

         ichunk = new Chunk(worldIn.getWorld(), pos, biomecontainer, upgradedata, iticklist, iticklist1, k1, achunksection, (p_222648_1_) -> {
            readEntities(compoundnbt, p_222648_1_);
         });
         if (compoundnbt.contains("ForgeCaps")) ((Chunk)ichunk).readCapsFromNBT(compoundnbt.getCompound("ForgeCaps"));
      } else {
         ChunkPrimer chunkprimer = new ChunkPrimer(pos, upgradedata, achunksection, chunkprimerticklist, chunkprimerticklist1);
         chunkprimer.func_225548_a_(biomecontainer);
         ichunk = chunkprimer;
         chunkprimer.setInhabitedTime(k1);
         chunkprimer.setStatus(ChunkStatus.byName(compoundnbt.getString("Status")));
         if (chunkprimer.getStatus().isAtLeast(ChunkStatus.FEATURES)) {
            chunkprimer.setLightManager(worldlightmanager);
         }

         if (!flag && chunkprimer.getStatus().isAtLeast(ChunkStatus.LIGHT)) {
            for(BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.getXStart(), 0, pos.getZStart(), pos.getXEnd(), 255, pos.getZEnd())) {
               if (ichunk.getBlockState(blockpos).getLightValue(ichunk, blockpos) != 0) {
                  chunkprimer.addLightPosition(blockpos);
               }
            }
         }
      }

      ichunk.setLight(flag);
      CompoundNBT compoundnbt3 = compoundnbt.getCompound("Heightmaps");
      EnumSet<Heightmap.Type> enumset = EnumSet.noneOf(Heightmap.Type.class);

      for(Heightmap.Type heightmap$type : ichunk.getStatus().getHeightMaps()) {
         String s = heightmap$type.getId();
         if (compoundnbt3.contains(s, 12)) {
            ichunk.setHeightmap(heightmap$type, compoundnbt3.getLongArray(s));
         } else {
            enumset.add(heightmap$type);
         }
      }

      Heightmap.updateChunkHeightmaps(ichunk, enumset);
      CompoundNBT compoundnbt4 = compoundnbt.getCompound("Structures");
      ichunk.setStructureStarts(unpackStructureStart(chunkgenerator, templateManagerIn, compoundnbt4));
      ichunk.setStructureReferences(unpackStructureReferences(pos, compoundnbt4));
      if (compoundnbt.getBoolean("shouldSave")) {
         ichunk.setModified(true);
      }

      ListNBT listnbt3 = compoundnbt.getList("PostProcessing", 9);

      for(int l1 = 0; l1 < listnbt3.size(); ++l1) {
         ListNBT listnbt1 = listnbt3.getList(l1);

         for(int l = 0; l < listnbt1.size(); ++l) {
            ichunk.func_201636_b(listnbt1.getShort(l), l1);
         }
      }

      if (chunkstatus$type == ChunkStatus.Type.LEVELCHUNK) {
         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkDataEvent.Load(ichunk, compoundnbt, chunkstatus$type));
         return new ChunkPrimerWrapper((Chunk)ichunk);
      } else {
         ChunkPrimer chunkprimer1 = (ChunkPrimer)ichunk;
         ListNBT listnbt4 = compoundnbt.getList("Entities", 10);

         for(int i2 = 0; i2 < listnbt4.size(); ++i2) {
            chunkprimer1.addEntity(listnbt4.getCompound(i2));
         }

         ListNBT listnbt5 = compoundnbt.getList("TileEntities", 10);

         for(int i1 = 0; i1 < listnbt5.size(); ++i1) {
            CompoundNBT compoundnbt2 = listnbt5.getCompound(i1);
            ichunk.addTileEntity(compoundnbt2);
         }

         ListNBT listnbt6 = compoundnbt.getList("Lights", 9);

         for(int j2 = 0; j2 < listnbt6.size(); ++j2) {
            ListNBT listnbt2 = listnbt6.getList(j2);

            for(int j1 = 0; j1 < listnbt2.size(); ++j1) {
               chunkprimer1.addLightValue(listnbt2.getShort(j1), j2);
            }
         }

         CompoundNBT compoundnbt5 = compoundnbt.getCompound("CarvingMasks");

         for(String s1 : compoundnbt5.keySet()) {
            GenerationStage.Carving generationstage$carving = GenerationStage.Carving.valueOf(s1);
            chunkprimer1.setCarvingMask(generationstage$carving, BitSet.valueOf(compoundnbt5.getByteArray(s1)));
         }

         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkDataEvent.Load(ichunk, compoundnbt, chunkstatus$type));

         return chunkprimer1;
      }
   }

   public static CompoundNBT write(ServerWorld worldIn, IChunk chunkIn) {
      ChunkPos chunkpos = chunkIn.getPos();
      CompoundNBT compoundnbt = new CompoundNBT();
      CompoundNBT compoundnbt1 = new CompoundNBT();
      compoundnbt.putInt("DataVersion", SharedConstants.getVersion().getWorldVersion());
      compoundnbt.put("Level", compoundnbt1);
      compoundnbt1.putInt("xPos", chunkpos.x);
      compoundnbt1.putInt("zPos", chunkpos.z);
      compoundnbt1.putLong("LastUpdate", worldIn.getGameTime());
      compoundnbt1.putLong("InhabitedTime", chunkIn.getInhabitedTime());
      compoundnbt1.putString("Status", chunkIn.getStatus().getName());
      UpgradeData upgradedata = chunkIn.getUpgradeData();
      if (!upgradedata.isEmpty()) {
         compoundnbt1.put("UpgradeData", upgradedata.write());
      }

      ChunkSection[] achunksection = chunkIn.getSections();
      ListNBT listnbt = new ListNBT();
      WorldLightManager worldlightmanager = worldIn.getChunkProvider().getLightManager();
      boolean flag = chunkIn.hasLight();

      for(int i = -1; i < 17; ++i) {
         int j = i;
         ChunkSection chunksection = Arrays.stream(achunksection).filter((p_222657_1_) -> {
            return p_222657_1_ != null && p_222657_1_.getYLocation() >> 4 == j;
         }).findFirst().orElse(Chunk.EMPTY_SECTION);
         NibbleArray nibblearray = worldlightmanager.getLightEngine(LightType.BLOCK).getData(SectionPos.from(chunkpos, j));
         NibbleArray nibblearray1 = worldlightmanager.getLightEngine(LightType.SKY).getData(SectionPos.from(chunkpos, j));
         if (chunksection != Chunk.EMPTY_SECTION || nibblearray != null || nibblearray1 != null) {
            CompoundNBT compoundnbt2 = new CompoundNBT();
            compoundnbt2.putByte("Y", (byte)(j & 255));
            if (chunksection != Chunk.EMPTY_SECTION) {
               chunksection.getData().writeChunkPalette(compoundnbt2, "Palette", "BlockStates");
            }

            if (nibblearray != null && !nibblearray.isEmpty()) {
               compoundnbt2.putByteArray("BlockLight", nibblearray.getData());
            }

            if (nibblearray1 != null && !nibblearray1.isEmpty()) {
               compoundnbt2.putByteArray("SkyLight", nibblearray1.getData());
            }

            listnbt.add(compoundnbt2);
         }
      }

      compoundnbt1.put("Sections", listnbt);
      if (flag) {
         compoundnbt1.putBoolean("isLightOn", true);
      }

      BiomeContainer biomecontainer = chunkIn.getBiomes();
      if (biomecontainer != null) {
         compoundnbt1.putIntArray("Biomes", biomecontainer.getBiomeIds());
      }

      ListNBT listnbt1 = new ListNBT();

      for(BlockPos blockpos : chunkIn.getTileEntitiesPos()) {
         CompoundNBT compoundnbt4 = chunkIn.getTileEntityNBT(blockpos);
         if (compoundnbt4 != null) {
            listnbt1.add(compoundnbt4);
         }
      }

      compoundnbt1.put("TileEntities", listnbt1);
      ListNBT listnbt2 = new ListNBT();
      if (chunkIn.getStatus().getType() == ChunkStatus.Type.LEVELCHUNK) {
         Chunk chunk = (Chunk)chunkIn;
         chunk.setHasEntities(false);

         for(int k = 0; k < chunk.getEntityLists().length; ++k) {
            for(Entity entity : chunk.getEntityLists()[k]) {
               CompoundNBT compoundnbt3 = new CompoundNBT();
               try {
               if (entity.writeUnlessPassenger(compoundnbt3)) {
                  chunk.setHasEntities(true);
                  listnbt2.add(compoundnbt3);
               }
               } catch (Exception e) {
                  LogManager.getLogger().error("An Entity type {} has thrown an exception trying to write state. It will not persist. Report this to the mod author", entity.getType(), e);
               }
            }
         }
         try {
             final CompoundNBT capTag = chunk.writeCapsToNBT();
             if (capTag != null) compoundnbt1.put("ForgeCaps", capTag);
         } catch (Exception exception) {
             LogManager.getLogger().error("A capability provider has thrown an exception trying to write state. It will not persist. Report this to the mod author", exception);
         }
      } else {
         ChunkPrimer chunkprimer = (ChunkPrimer)chunkIn;
         listnbt2.addAll(chunkprimer.getEntities());
         compoundnbt1.put("Lights", toNbt(chunkprimer.getPackedLightPositions()));
         CompoundNBT compoundnbt5 = new CompoundNBT();

         for(GenerationStage.Carving generationstage$carving : GenerationStage.Carving.values()) {
            compoundnbt5.putByteArray(generationstage$carving.toString(), chunkIn.getCarvingMask(generationstage$carving).toByteArray());
         }

         compoundnbt1.put("CarvingMasks", compoundnbt5);
      }

      compoundnbt1.put("Entities", listnbt2);
      ITickList<Block> iticklist = chunkIn.getBlocksToBeTicked();
      if (iticklist instanceof ChunkPrimerTickList) {
         compoundnbt1.put("ToBeTicked", ((ChunkPrimerTickList)iticklist).write());
      } else if (iticklist instanceof SerializableTickList) {
         compoundnbt1.put("TileTicks", ((SerializableTickList)iticklist).save(worldIn.getGameTime()));
      } else {
         compoundnbt1.put("TileTicks", worldIn.getPendingBlockTicks().func_219503_a(chunkpos));
      }

      ITickList<Fluid> iticklist1 = chunkIn.getFluidsToBeTicked();
      if (iticklist1 instanceof ChunkPrimerTickList) {
         compoundnbt1.put("LiquidsToBeTicked", ((ChunkPrimerTickList)iticklist1).write());
      } else if (iticklist1 instanceof SerializableTickList) {
         compoundnbt1.put("LiquidTicks", ((SerializableTickList)iticklist1).save(worldIn.getGameTime()));
      } else {
         compoundnbt1.put("LiquidTicks", worldIn.getPendingFluidTicks().func_219503_a(chunkpos));
      }

      compoundnbt1.put("PostProcessing", toNbt(chunkIn.getPackedPositions()));
      CompoundNBT compoundnbt6 = new CompoundNBT();

      for(Entry<Heightmap.Type, Heightmap> entry : chunkIn.getHeightmaps()) {
         if (chunkIn.getStatus().getHeightMaps().contains(entry.getKey())) {
            compoundnbt6.put(entry.getKey().getId(), new LongArrayNBT(entry.getValue().getDataArray()));
         }
      }

      compoundnbt1.put("Heightmaps", compoundnbt6);
      compoundnbt1.put("Structures", writeStructures(chunkpos, chunkIn.getStructureStarts(), chunkIn.getStructureReferences()));
      return compoundnbt;
   }

   public static ChunkStatus.Type getChunkStatus(@Nullable CompoundNBT chunkNBT) {
      if (chunkNBT != null) {
         ChunkStatus chunkstatus = ChunkStatus.byName(chunkNBT.getCompound("Level").getString("Status"));
         if (chunkstatus != null) {
            return chunkstatus.getType();
         }
      }

      return ChunkStatus.Type.PROTOCHUNK;
   }

   private static void readEntities(CompoundNBT compound, Chunk chunkIn) {
      ListNBT listnbt = compound.getList("Entities", 10);
      World world = chunkIn.getWorld();

      for(int i = 0; i < listnbt.size(); ++i) {
         CompoundNBT compoundnbt = listnbt.getCompound(i);
         EntityType.func_220335_a(compoundnbt, world, (p_222655_1_) -> {
            chunkIn.addEntity(p_222655_1_);
            return p_222655_1_;
         });
         chunkIn.setHasEntities(true);
      }

      ListNBT listnbt1 = compound.getList("TileEntities", 10);

      for(int j = 0; j < listnbt1.size(); ++j) {
         CompoundNBT compoundnbt1 = listnbt1.getCompound(j);
         boolean flag = compoundnbt1.getBoolean("keepPacked");
         if (flag) {
            chunkIn.addTileEntity(compoundnbt1);
         } else {
            TileEntity tileentity = TileEntity.create(compoundnbt1);
            if (tileentity != null) {
               chunkIn.addTileEntity(tileentity);
            }
         }
      }

   }

   private static CompoundNBT writeStructures(ChunkPos pos, Map<String, StructureStart> p_222649_1_, Map<String, LongSet> p_222649_2_) {
      CompoundNBT compoundnbt = new CompoundNBT();
      CompoundNBT compoundnbt1 = new CompoundNBT();

      for(Entry<String, StructureStart> entry : p_222649_1_.entrySet()) {
         compoundnbt1.put(entry.getKey(), entry.getValue().write(pos.x, pos.z));
      }

      compoundnbt.put("Starts", compoundnbt1);
      CompoundNBT compoundnbt2 = new CompoundNBT();

      for(Entry<String, LongSet> entry1 : p_222649_2_.entrySet()) {
         compoundnbt2.put(entry1.getKey(), new LongArrayNBT(entry1.getValue()));
      }

      compoundnbt.put("References", compoundnbt2);
      return compoundnbt;
   }

   private static Map<String, StructureStart> unpackStructureStart(ChunkGenerator<?> p_227076_0_, TemplateManager p_227076_1_, CompoundNBT p_227076_2_) {
      Map<String, StructureStart> map = Maps.newHashMap();
      CompoundNBT compoundnbt = p_227076_2_.getCompound("Starts");

      for(String s : compoundnbt.keySet()) {
         map.put(s, Structures.func_227456_a_(p_227076_0_, p_227076_1_, compoundnbt.getCompound(s)));
      }

      return map;
   }

   private static Map<String, LongSet> unpackStructureReferences(ChunkPos p_227075_0_, CompoundNBT p_227075_1_) {
      Map<String, LongSet> map = Maps.newHashMap();
      CompoundNBT compoundnbt = p_227075_1_.getCompound("References");

      for(String s : compoundnbt.keySet()) {
         map.put(s, new LongOpenHashSet(Arrays.stream(compoundnbt.getLongArray(s)).filter((p_227074_2_) -> {
            ChunkPos chunkpos = new ChunkPos(p_227074_2_);
            if (chunkpos.getChessboardDistance(p_227075_0_) > 8) {
               LOGGER.warn("Found invalid structure reference [ {} @ {} ] for chunk {}.", s, chunkpos, p_227075_0_);
               return false;
            } else {
               return true;
            }
         }).toArray()));
      }

      return map;
   }

   public static ListNBT toNbt(ShortList[] list) {
      ListNBT listnbt = new ListNBT();

      for(ShortList shortlist : list) {
         ListNBT listnbt1 = new ListNBT();
         if (shortlist != null) {
            for(Short oshort : shortlist) {
               listnbt1.add(ShortNBT.valueOf(oshort));
            }
         }

         listnbt.add(listnbt1);
      }

      return listnbt;
   }
}