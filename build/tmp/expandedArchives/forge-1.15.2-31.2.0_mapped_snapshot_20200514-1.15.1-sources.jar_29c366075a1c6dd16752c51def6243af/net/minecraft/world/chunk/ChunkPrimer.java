package net.minecraft.world.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.lighting.WorldLightManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkPrimer implements IChunk {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ChunkPos pos;
   private volatile boolean modified;
   @Nullable
   private BiomeContainer biomes;
   @Nullable
   private volatile WorldLightManager field_217334_e;
   private final Map<Heightmap.Type, Heightmap> heightmaps = Maps.newEnumMap(Heightmap.Type.class);
   private volatile ChunkStatus status = ChunkStatus.EMPTY;
   private final Map<BlockPos, TileEntity> tileEntities = Maps.newHashMap();
   private final Map<BlockPos, CompoundNBT> deferredTileEntities = Maps.newHashMap();
   private final ChunkSection[] sections = new ChunkSection[16];
   private final List<CompoundNBT> entities = Lists.newArrayList();
   private final List<BlockPos> lightPositions = Lists.newArrayList();
   private final ShortList[] packedPositions = new ShortList[16];
   private final Map<String, StructureStart> structureStartMap = Maps.newHashMap();
   private final Map<String, LongSet> structureReferenceMap = Maps.newHashMap();
   private final UpgradeData upgradeData;
   private final ChunkPrimerTickList<Block> pendingBlockTicks;
   private final ChunkPrimerTickList<Fluid> pendingFluidTicks;
   private long inhabitedTime;
   private final Map<GenerationStage.Carving, BitSet> carvingMasks = Maps.newHashMap();
   private volatile boolean hasLight;

   public ChunkPrimer(ChunkPos p_i48700_1_, UpgradeData data) {
      this(p_i48700_1_, data, (ChunkSection[])null, new ChunkPrimerTickList<>((p_205332_0_) -> {
         return p_205332_0_ == null || p_205332_0_.getDefaultState().isAir();
      }, p_i48700_1_), new ChunkPrimerTickList<>((p_205766_0_) -> {
         return p_205766_0_ == null || p_205766_0_ == Fluids.EMPTY;
      }, p_i48700_1_));
   }

   public ChunkPrimer(ChunkPos p_i49941_1_, UpgradeData p_i49941_2_, @Nullable ChunkSection[] p_i49941_3_, ChunkPrimerTickList<Block> p_i49941_4_, ChunkPrimerTickList<Fluid> p_i49941_5_) {
      this.pos = p_i49941_1_;
      this.upgradeData = p_i49941_2_;
      this.pendingBlockTicks = p_i49941_4_;
      this.pendingFluidTicks = p_i49941_5_;
      if (p_i49941_3_ != null) {
         if (this.sections.length == p_i49941_3_.length) {
            System.arraycopy(p_i49941_3_, 0, this.sections, 0, this.sections.length);
         } else {
            LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", p_i49941_3_.length, this.sections.length);
         }
      }

   }

   public BlockState getBlockState(BlockPos pos) {
      int i = pos.getY();
      if (World.isYOutOfBounds(i)) {
         return Blocks.VOID_AIR.getDefaultState();
      } else {
         ChunkSection chunksection = this.getSections()[i >> 4];
         return ChunkSection.isEmpty(chunksection) ? Blocks.AIR.getDefaultState() : chunksection.getBlockState(pos.getX() & 15, i & 15, pos.getZ() & 15);
      }
   }

   public IFluidState getFluidState(BlockPos pos) {
      int i = pos.getY();
      if (World.isYOutOfBounds(i)) {
         return Fluids.EMPTY.getDefaultState();
      } else {
         ChunkSection chunksection = this.getSections()[i >> 4];
         return ChunkSection.isEmpty(chunksection) ? Fluids.EMPTY.getDefaultState() : chunksection.getFluidState(pos.getX() & 15, i & 15, pos.getZ() & 15);
      }
   }

   public Stream<BlockPos> getLightSources() {
      return this.lightPositions.stream();
   }

   public ShortList[] getPackedLightPositions() {
      ShortList[] ashortlist = new ShortList[16];

      for(BlockPos blockpos : this.lightPositions) {
         IChunk.getList(ashortlist, blockpos.getY() >> 4).add(packToLocal(blockpos));
      }

      return ashortlist;
   }

   public void addLightValue(short packedPosition, int lightValue) {
      this.addLightPosition(unpackToWorld(packedPosition, lightValue, this.pos));
   }

   public void addLightPosition(BlockPos lightPos) {
      this.lightPositions.add(lightPos.toImmutable());
   }

   @Nullable
   public BlockState setBlockState(BlockPos pos, BlockState state, boolean isMoving) {
      int i = pos.getX();
      int j = pos.getY();
      int k = pos.getZ();
      if (j >= 0 && j < 256) {
         if (this.sections[j >> 4] == Chunk.EMPTY_SECTION && state.getBlock() == Blocks.AIR) {
            return state;
         } else {
            if (state.getLightValue(this, pos) > 0) {
               this.lightPositions.add(new BlockPos((i & 15) + this.getPos().getXStart(), j, (k & 15) + this.getPos().getZStart()));
            }

            ChunkSection chunksection = this.getSection(j >> 4);
            BlockState blockstate = chunksection.setBlockState(i & 15, j & 15, k & 15, state);
            if (this.status.isAtLeast(ChunkStatus.FEATURES) && state != blockstate && (state.getOpacity(this, pos) != blockstate.getOpacity(this, pos) || state.getLightValue(this, pos) != blockstate.getLightValue(this, pos) || state.isTransparent() || blockstate.isTransparent())) {
               WorldLightManager worldlightmanager = this.getWorldLightManager();
               worldlightmanager.checkBlock(pos);
            }

            EnumSet<Heightmap.Type> enumset1 = this.getStatus().getHeightMaps();
            EnumSet<Heightmap.Type> enumset = null;

            for(Heightmap.Type heightmap$type : enumset1) {
               Heightmap heightmap = this.heightmaps.get(heightmap$type);
               if (heightmap == null) {
                  if (enumset == null) {
                     enumset = EnumSet.noneOf(Heightmap.Type.class);
                  }

                  enumset.add(heightmap$type);
               }
            }

            if (enumset != null) {
               Heightmap.updateChunkHeightmaps(this, enumset);
            }

            for(Heightmap.Type heightmap$type1 : enumset1) {
               this.heightmaps.get(heightmap$type1).update(i & 15, j, k & 15, state);
            }

            return blockstate;
         }
      } else {
         return Blocks.VOID_AIR.getDefaultState();
      }
   }

   public ChunkSection getSection(int p_217332_1_) {
      if (this.sections[p_217332_1_] == Chunk.EMPTY_SECTION) {
         this.sections[p_217332_1_] = new ChunkSection(p_217332_1_ << 4);
      }

      return this.sections[p_217332_1_];
   }

   public void addTileEntity(BlockPos pos, TileEntity tileEntityIn) {
      tileEntityIn.setPos(pos);
      this.tileEntities.put(pos, tileEntityIn);
   }

   public Set<BlockPos> getTileEntitiesPos() {
      Set<BlockPos> set = Sets.newHashSet(this.deferredTileEntities.keySet());
      set.addAll(this.tileEntities.keySet());
      return set;
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos pos) {
      return this.tileEntities.get(pos);
   }

   public Map<BlockPos, TileEntity> getTileEntities() {
      return this.tileEntities;
   }

   public void addEntity(CompoundNBT entityCompound) {
      this.entities.add(entityCompound);
   }

   /**
    * Adds an entity to the chunk.
    */
   public void addEntity(Entity entityIn) {
      CompoundNBT compoundnbt = new CompoundNBT();
      entityIn.writeUnlessPassenger(compoundnbt);
      this.addEntity(compoundnbt);
   }

   public List<CompoundNBT> getEntities() {
      return this.entities;
   }

   public void func_225548_a_(BiomeContainer p_225548_1_) {
      this.biomes = p_225548_1_;
   }

   @Nullable
   public BiomeContainer getBiomes() {
      return this.biomes;
   }

   public void setModified(boolean modified) {
      this.modified = modified;
   }

   public boolean isModified() {
      return this.modified;
   }

   public ChunkStatus getStatus() {
      return this.status;
   }

   public void setStatus(ChunkStatus status) {
      this.status = status;
      this.setModified(true);
   }

   /**
    * Returns the ExtendedBlockStorage array for this Chunk.
    */
   public ChunkSection[] getSections() {
      return this.sections;
   }

   @Nullable
   public WorldLightManager getWorldLightManager() {
      return this.field_217334_e;
   }

   public Collection<Entry<Heightmap.Type, Heightmap>> getHeightmaps() {
      return Collections.unmodifiableSet(this.heightmaps.entrySet());
   }

   public void setHeightmap(Heightmap.Type type, long[] data) {
      this.getHeightmap(type).setDataArray(data);
   }

   public Heightmap getHeightmap(Heightmap.Type typeIn) {
      return this.heightmaps.computeIfAbsent(typeIn, (p_217333_1_) -> {
         return new Heightmap(this, p_217333_1_);
      });
   }

   public int getTopBlockY(Heightmap.Type heightmapType, int x, int z) {
      Heightmap heightmap = this.heightmaps.get(heightmapType);
      if (heightmap == null) {
         Heightmap.updateChunkHeightmaps(this, EnumSet.of(heightmapType));
         heightmap = this.heightmaps.get(heightmapType);
      }

      return heightmap.getHeight(x & 15, z & 15) - 1;
   }

   /**
    * Gets a {@link ChunkPos} representing the x and z coordinates of this chunk.
    */
   public ChunkPos getPos() {
      return this.pos;
   }

   public void setLastSaveTime(long saveTime) {
   }

   @Nullable
   public StructureStart getStructureStart(String stucture) {
      return this.structureStartMap.get(stucture);
   }

   public void putStructureStart(String structureIn, StructureStart structureStartIn) {
      this.structureStartMap.put(structureIn, structureStartIn);
      this.modified = true;
   }

   public Map<String, StructureStart> getStructureStarts() {
      return Collections.unmodifiableMap(this.structureStartMap);
   }

   public void setStructureStarts(Map<String, StructureStart> structureStartsIn) {
      this.structureStartMap.clear();
      this.structureStartMap.putAll(structureStartsIn);
      this.modified = true;
   }

   public LongSet getStructureReferences(String structureIn) {
      return this.structureReferenceMap.computeIfAbsent(structureIn, (p_208302_0_) -> {
         return new LongOpenHashSet();
      });
   }

   public void addStructureReference(String strucutre, long reference) {
      this.structureReferenceMap.computeIfAbsent(strucutre, (p_201628_0_) -> {
         return new LongOpenHashSet();
      }).add(reference);
      this.modified = true;
   }

   public Map<String, LongSet> getStructureReferences() {
      return Collections.unmodifiableMap(this.structureReferenceMap);
   }

   public void setStructureReferences(Map<String, LongSet> p_201606_1_) {
      this.structureReferenceMap.clear();
      this.structureReferenceMap.putAll(p_201606_1_);
      this.modified = true;
   }

   public static short packToLocal(BlockPos p_201651_0_) {
      int i = p_201651_0_.getX();
      int j = p_201651_0_.getY();
      int k = p_201651_0_.getZ();
      int l = i & 15;
      int i1 = j & 15;
      int j1 = k & 15;
      return (short)(l | i1 << 4 | j1 << 8);
   }

   public static BlockPos unpackToWorld(short packedPos, int yOffset, ChunkPos chunkPosIn) {
      int i = (packedPos & 15) + (chunkPosIn.x << 4);
      int j = (packedPos >>> 4 & 15) + (yOffset << 4);
      int k = (packedPos >>> 8 & 15) + (chunkPosIn.z << 4);
      return new BlockPos(i, j, k);
   }

   public void markBlockForPostprocessing(BlockPos pos) {
      if (!World.isOutsideBuildHeight(pos)) {
         IChunk.getList(this.packedPositions, pos.getY() >> 4).add(packToLocal(pos));
      }

   }

   public ShortList[] getPackedPositions() {
      return this.packedPositions;
   }

   public void func_201636_b(short packedPosition, int index) {
      IChunk.getList(this.packedPositions, index).add(packedPosition);
   }

   public ChunkPrimerTickList<Block> getBlocksToBeTicked() {
      return this.pendingBlockTicks;
   }

   public ChunkPrimerTickList<Fluid> getFluidsToBeTicked() {
      return this.pendingFluidTicks;
   }

   public UpgradeData getUpgradeData() {
      return this.upgradeData;
   }

   public void setInhabitedTime(long newInhabitedTime) {
      this.inhabitedTime = newInhabitedTime;
   }

   public long getInhabitedTime() {
      return this.inhabitedTime;
   }

   public void addTileEntity(CompoundNBT nbt) {
      this.deferredTileEntities.put(new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z")), nbt);
   }

   public Map<BlockPos, CompoundNBT> getDeferredTileEntities() {
      return Collections.unmodifiableMap(this.deferredTileEntities);
   }

   public CompoundNBT getDeferredTileEntity(BlockPos pos) {
      return this.deferredTileEntities.get(pos);
   }

   @Nullable
   public CompoundNBT getTileEntityNBT(BlockPos pos) {
      TileEntity tileentity = this.getTileEntity(pos);
      return tileentity != null ? tileentity.write(new CompoundNBT()) : this.deferredTileEntities.get(pos);
   }

   public void removeTileEntity(BlockPos pos) {
      this.tileEntities.remove(pos);
      this.deferredTileEntities.remove(pos);
   }

   public BitSet getCarvingMask(GenerationStage.Carving type) {
      return this.carvingMasks.computeIfAbsent(type, (p_205761_0_) -> {
         return new BitSet(65536);
      });
   }

   public void setCarvingMask(GenerationStage.Carving type, BitSet mask) {
      this.carvingMasks.put(type, mask);
   }

   public void setLightManager(WorldLightManager p_217306_1_) {
      this.field_217334_e = p_217306_1_;
   }

   public boolean hasLight() {
      return this.hasLight;
   }

   public void setLight(boolean lightCorrectIn) {
      this.hasLight = lightCorrectIn;
      this.setModified(true);
   }
}