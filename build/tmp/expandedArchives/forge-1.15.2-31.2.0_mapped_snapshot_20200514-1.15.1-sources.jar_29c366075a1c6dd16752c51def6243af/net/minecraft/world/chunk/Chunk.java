package net.minecraft.world.chunk;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.EmptyTickList;
import net.minecraft.world.ITickList;
import net.minecraft.world.SerializableTickList;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.gen.DebugChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chunk extends net.minecraftforge.common.capabilities.CapabilityProvider<Chunk> implements IChunk, net.minecraftforge.common.extensions.IForgeChunk {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ChunkSection EMPTY_SECTION = null;
   private final ChunkSection[] sections = new ChunkSection[16];
   private BiomeContainer blockBiomeArray;
   private final Map<BlockPos, CompoundNBT> deferredTileEntities = Maps.newHashMap();
   private boolean loaded;
   private final World world;
   private final Map<Heightmap.Type, Heightmap> heightMap = Maps.newEnumMap(Heightmap.Type.class);
   private final UpgradeData upgradeData;
   /** A Map of ChunkPositions to TileEntities in this chunk */
   private final Map<BlockPos, TileEntity> tileEntities = Maps.newHashMap();
   private final ClassInheritanceMultiMap<Entity>[] entityLists;
   private final Map<String, StructureStart> structureStarts = Maps.newHashMap();
   private final Map<String, LongSet> structureReferences = Maps.newHashMap();
   private final ShortList[] packedBlockPositions = new ShortList[16];
   private ITickList<Block> blocksToBeTicked;
   private ITickList<Fluid> fluidsToBeTicked;
   private boolean hasEntities;
   private long lastSaveTime;
   private volatile boolean dirty;
   /** the cumulative number of ticks players have been in this chunk */
   private long inhabitedTime;
   @Nullable
   private Supplier<ChunkHolder.LocationType> locationType;
   @Nullable
   private Consumer<Chunk> postLoadConsumer;
   private final ChunkPos pos;
   private volatile boolean lightCorrect;

   public Chunk(World worldIn, ChunkPos chunkPosIn, BiomeContainer biomeContainerIn) {
      this(worldIn, chunkPosIn, biomeContainerIn, UpgradeData.EMPTY, EmptyTickList.get(), EmptyTickList.get(), 0L, (ChunkSection[])null, (Consumer<Chunk>)null);
   }

   public Chunk(World worldIn, ChunkPos chunkPosIn, BiomeContainer biomeContainerIn, UpgradeData upgradeDataIn, ITickList<Block> tickBlocksIn, ITickList<Fluid> tickFluidsIn, long inhabitedTimeIn, @Nullable ChunkSection[] sectionsIn, @Nullable Consumer<Chunk> postLoadConsumerIn) {
      super(Chunk.class);
      this.entityLists = new ClassInheritanceMultiMap[16];
      this.world = worldIn;
      this.pos = chunkPosIn;
      this.upgradeData = upgradeDataIn;

      for(Heightmap.Type heightmap$type : Heightmap.Type.values()) {
         if (ChunkStatus.FULL.getHeightMaps().contains(heightmap$type)) {
            this.heightMap.put(heightmap$type, new Heightmap(this, heightmap$type));
         }
      }

      for(int i = 0; i < this.entityLists.length; ++i) {
         this.entityLists[i] = new ClassInheritanceMultiMap<>(Entity.class);
      }

      this.blockBiomeArray = biomeContainerIn;
      this.blocksToBeTicked = tickBlocksIn;
      this.fluidsToBeTicked = tickFluidsIn;
      this.inhabitedTime = inhabitedTimeIn;
      this.postLoadConsumer = postLoadConsumerIn;
      if (sectionsIn != null) {
         if (this.sections.length == sectionsIn.length) {
            System.arraycopy(sectionsIn, 0, this.sections, 0, this.sections.length);
         } else {
            LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", sectionsIn.length, this.sections.length);
         }
      }
      this.gatherCapabilities();
   }

   public Chunk(World worldIn, ChunkPrimer p_i49947_2_) {
      this(worldIn, p_i49947_2_.getPos(), p_i49947_2_.getBiomes(), p_i49947_2_.getUpgradeData(), p_i49947_2_.getBlocksToBeTicked(), p_i49947_2_.getFluidsToBeTicked(), p_i49947_2_.getInhabitedTime(), p_i49947_2_.getSections(), (Consumer<Chunk>)null);

      for(CompoundNBT compoundnbt : p_i49947_2_.getEntities()) {
         EntityType.func_220335_a(compoundnbt, worldIn, (p_217325_1_) -> {
            this.addEntity(p_217325_1_);
            return p_217325_1_;
         });
      }

      for(TileEntity tileentity : p_i49947_2_.getTileEntities().values()) {
         this.addTileEntity(tileentity);
      }

      this.deferredTileEntities.putAll(p_i49947_2_.getDeferredTileEntities());

      for(int i = 0; i < p_i49947_2_.getPackedPositions().length; ++i) {
         this.packedBlockPositions[i] = p_i49947_2_.getPackedPositions()[i];
      }

      this.setStructureStarts(p_i49947_2_.getStructureStarts());
      this.setStructureReferences(p_i49947_2_.getStructureReferences());

      for(Entry<Heightmap.Type, Heightmap> entry : p_i49947_2_.getHeightmaps()) {
         if (ChunkStatus.FULL.getHeightMaps().contains(entry.getKey())) {
            this.getHeightmap(entry.getKey()).setDataArray(entry.getValue().getDataArray());
         }
      }

      this.setLight(p_i49947_2_.hasLight());
      this.dirty = true;
   }

   public Heightmap getHeightmap(Heightmap.Type typeIn) {
      return this.heightMap.computeIfAbsent(typeIn, (p_217319_1_) -> {
         return new Heightmap(this, p_217319_1_);
      });
   }

   public Set<BlockPos> getTileEntitiesPos() {
      Set<BlockPos> set = Sets.newHashSet(this.deferredTileEntities.keySet());
      set.addAll(this.tileEntities.keySet());
      return set;
   }

   /**
    * Returns the ExtendedBlockStorage array for this Chunk.
    */
   public ChunkSection[] getSections() {
      return this.sections;
   }

   public BlockState getBlockState(BlockPos pos) {
      int i = pos.getX();
      int j = pos.getY();
      int k = pos.getZ();
      if (this.world.getWorldType() == WorldType.DEBUG_ALL_BLOCK_STATES) {
         BlockState blockstate = null;
         if (j == 60) {
            blockstate = Blocks.BARRIER.getDefaultState();
         }

         if (j == 70) {
            blockstate = DebugChunkGenerator.getBlockStateFor(i, k);
         }

         return blockstate == null ? Blocks.AIR.getDefaultState() : blockstate;
      } else {
         try {
            if (j >= 0 && j >> 4 < this.sections.length) {
               ChunkSection chunksection = this.sections[j >> 4];
               if (!ChunkSection.isEmpty(chunksection)) {
                  return chunksection.getBlockState(i & 15, j & 15, k & 15);
               }
            }

            return Blocks.AIR.getDefaultState();
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Getting block state");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being got");
            crashreportcategory.addDetail("Location", () -> {
               return CrashReportCategory.getCoordinateInfo(i, j, k);
            });
            throw new ReportedException(crashreport);
         }
      }
   }

   public IFluidState getFluidState(BlockPos pos) {
      return this.getFluidState(pos.getX(), pos.getY(), pos.getZ());
   }

   public IFluidState getFluidState(int bx, int by, int bz) {
      try {
         if (by >= 0 && by >> 4 < this.sections.length) {
            ChunkSection chunksection = this.sections[by >> 4];
            if (!ChunkSection.isEmpty(chunksection)) {
               return chunksection.getFluidState(bx & 15, by & 15, bz & 15);
            }
         }

         return Fluids.EMPTY.getDefaultState();
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Getting fluid state");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being got");
         crashreportcategory.addDetail("Location", () -> {
            return CrashReportCategory.getCoordinateInfo(bx, by, bz);
         });
         throw new ReportedException(crashreport);
      }
   }

   @Nullable
   public BlockState setBlockState(BlockPos pos, BlockState state, boolean isMoving) {
      int i = pos.getX() & 15;
      int j = pos.getY();
      int k = pos.getZ() & 15;
      ChunkSection chunksection = this.sections[j >> 4];
      if (chunksection == EMPTY_SECTION) {
         if (state.isAir()) {
            return null;
         }

         chunksection = new ChunkSection(j >> 4 << 4);
         this.sections[j >> 4] = chunksection;
      }

      boolean flag = chunksection.isEmpty();
      BlockState blockstate = chunksection.setBlockState(i, j & 15, k, state);
      if (blockstate == state) {
         return null;
      } else {
         Block block = state.getBlock();
         Block block1 = blockstate.getBlock();
         this.heightMap.get(Heightmap.Type.MOTION_BLOCKING).update(i, j, k, state);
         this.heightMap.get(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES).update(i, j, k, state);
         this.heightMap.get(Heightmap.Type.OCEAN_FLOOR).update(i, j, k, state);
         this.heightMap.get(Heightmap.Type.WORLD_SURFACE).update(i, j, k, state);
         boolean flag1 = chunksection.isEmpty();
         if (flag != flag1) {
            this.world.getChunkProvider().getLightManager().func_215567_a(pos, flag1);
         }

         if (!this.world.isRemote) {
            blockstate.onReplaced(this.world, pos, state, isMoving);
         } else if ((block1 != block || !state.hasTileEntity()) && blockstate.hasTileEntity()) {
            this.world.removeTileEntity(pos);
         }

         if (chunksection.getBlockState(i, j & 15, k).getBlock() != block) {
            return null;
         } else {
            if (blockstate.hasTileEntity()) {
               TileEntity tileentity = this.getTileEntity(pos, Chunk.CreateEntityType.CHECK);
               if (tileentity != null) {
                  tileentity.updateContainingBlockInfo();
               }
            }

            if (!this.world.isRemote) {
               state.onBlockAdded(this.world, pos, blockstate, isMoving);
            }

            if (state.hasTileEntity()) {
               TileEntity tileentity1 = this.getTileEntity(pos, Chunk.CreateEntityType.CHECK);
               if (tileentity1 == null) {
                  tileentity1 = state.createTileEntity(this.world);
                  this.world.setTileEntity(pos, tileentity1);
               } else {
                  tileentity1.updateContainingBlockInfo();
               }
            }

            this.dirty = true;
            return blockstate;
         }
      }
   }

   @Nullable
   public WorldLightManager getWorldLightManager() {
      return this.world.getChunkProvider().getLightManager();
   }

   /**
    * Adds an entity to the chunk.
    */
   public void addEntity(Entity entityIn) {
      this.hasEntities = true;
      int i = MathHelper.floor(entityIn.getPosX() / 16.0D);
      int j = MathHelper.floor(entityIn.getPosZ() / 16.0D);
      if (i != this.pos.x || j != this.pos.z) {
         LOGGER.warn("Wrong location! ({}, {}) should be ({}, {}), {}", i, j, this.pos.x, this.pos.z, entityIn);
         entityIn.removed = true;
      }

      int k = MathHelper.floor(entityIn.getPosY() / 16.0D);
      if (k < 0) {
         k = 0;
      }

      if (k >= this.entityLists.length) {
         k = this.entityLists.length - 1;
      }

      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityEvent.EnteringChunk(entityIn, this.pos.x, this.pos.z, entityIn.chunkCoordX, entityIn.chunkCoordZ));
      entityIn.addedToChunk = true;
      entityIn.chunkCoordX = this.pos.x;
      entityIn.chunkCoordY = k;
      entityIn.chunkCoordZ = this.pos.z;
      this.entityLists[k].add(entityIn);
      this.markDirty(); // Forge - ensure chunks are marked to save after an entity add
   }

   public void setHeightmap(Heightmap.Type type, long[] data) {
      this.heightMap.get(type).setDataArray(data);
   }

   /**
    * removes entity using its y chunk coordinate as its index
    */
   public void removeEntity(Entity entityIn) {
      this.removeEntityAtIndex(entityIn, entityIn.chunkCoordY);
   }

   /**
    * Removes entity at the specified index from the entity array.
    */
   public void removeEntityAtIndex(Entity entityIn, int index) {
      if (index < 0) {
         index = 0;
      }

      if (index >= this.entityLists.length) {
         index = this.entityLists.length - 1;
      }

      this.entityLists[index].remove(entityIn);
      this.markDirty(); // Forge - ensure chunks are marked to save after entity removals
   }

   public int getTopBlockY(Heightmap.Type heightmapType, int x, int z) {
      return this.heightMap.get(heightmapType).getHeight(x & 15, z & 15) - 1;
   }

   @Nullable
   private TileEntity createNewTileEntity(BlockPos pos) {
      BlockState blockstate = this.getBlockState(pos);
      Block block = blockstate.getBlock();
      return !blockstate.hasTileEntity() ? null : blockstate.createTileEntity(this.world);
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos pos) {
      return this.getTileEntity(pos, Chunk.CreateEntityType.CHECK);
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos pos, Chunk.CreateEntityType creationMode) {
      TileEntity tileentity = this.tileEntities.get(pos);
      if (tileentity != null && tileentity.isRemoved()) {
         tileEntities.remove(pos);
         tileentity = null;
      }
      if (tileentity == null) {
         CompoundNBT compoundnbt = this.deferredTileEntities.remove(pos);
         if (compoundnbt != null) {
            TileEntity tileentity1 = this.setDeferredTileEntity(pos, compoundnbt);
            if (tileentity1 != null) {
               return tileentity1;
            }
         }
      }

      if (tileentity == null) {
         if (creationMode == Chunk.CreateEntityType.IMMEDIATE) {
            tileentity = this.createNewTileEntity(pos);
            this.world.setTileEntity(pos, tileentity);
         }
      }

      return tileentity;
   }

   public void addTileEntity(TileEntity tileEntityIn) {
      this.addTileEntity(tileEntityIn.getPos(), tileEntityIn);
      if (this.loaded || this.world.isRemote()) {
         this.world.setTileEntity(tileEntityIn.getPos(), tileEntityIn);
      }

   }

   public void addTileEntity(BlockPos pos, TileEntity tileEntityIn) {
      if (this.getBlockState(pos).hasTileEntity()) {
         tileEntityIn.setWorldAndPos(this.world, pos);
         tileEntityIn.validate();
         TileEntity tileentity = this.tileEntities.put(pos.toImmutable(), tileEntityIn);
         if (tileentity != null && tileentity != tileEntityIn) {
            tileentity.remove();
         }

      }
   }

   public void addTileEntity(CompoundNBT nbt) {
      this.deferredTileEntities.put(new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z")), nbt);
   }

   @Nullable
   public CompoundNBT getTileEntityNBT(BlockPos pos) {
      TileEntity tileentity = this.getTileEntity(pos);
      if (tileentity != null && !tileentity.isRemoved()) {
         try {
         CompoundNBT compoundnbt1 = tileentity.write(new CompoundNBT());
         compoundnbt1.putBoolean("keepPacked", false);
         return compoundnbt1;
         } catch (Exception e) {
            LogManager.getLogger().error("A TileEntity type {} has thrown an exception trying to write state. It will not persist, Report this to the mod author", tileentity.getClass().getName(), e);
            return null;
         }
      } else {
         CompoundNBT compoundnbt = this.deferredTileEntities.get(pos);
         if (compoundnbt != null) {
            compoundnbt = compoundnbt.copy();
            compoundnbt.putBoolean("keepPacked", true);
         }

         return compoundnbt;
      }
   }

   public void removeTileEntity(BlockPos pos) {
      if (this.loaded || this.world.isRemote()) {
         TileEntity tileentity = this.tileEntities.remove(pos);
         if (tileentity != null) {
            tileentity.remove();
         }
      }

   }

   public void postLoad() {
      if (this.postLoadConsumer != null) {
         this.postLoadConsumer.accept(this);
         this.postLoadConsumer = null;
      }

   }

   /**
    * Sets the isModified flag for this Chunk
    */
   public void markDirty() {
      this.dirty = true;
   }

   /**
    * Fills the given list of all entities that intersect within the given bounding box that aren't the passed entity.
    */
   public void getEntitiesWithinAABBForEntity(@Nullable Entity entityIn, AxisAlignedBB aabb, List<Entity> listToFill, @Nullable Predicate<? super Entity> filter) {
      int i = MathHelper.floor((aabb.minY - this.world.getMaxEntityRadius()) / 16.0D);
      int j = MathHelper.floor((aabb.maxY + this.world.getMaxEntityRadius()) / 16.0D);
      i = MathHelper.clamp(i, 0, this.entityLists.length - 1);
      j = MathHelper.clamp(j, 0, this.entityLists.length - 1);

      for(int k = i; k <= j; ++k) {
         if (!this.entityLists[k].isEmpty()) {
            for(Entity entity : this.entityLists[k]) {
               if (entity.getBoundingBox().intersects(aabb) && entity != entityIn) {
                  if (filter == null || filter.test(entity)) {
                     listToFill.add(entity);
                  }

                  if (entity instanceof EnderDragonEntity) {
                     for(EnderDragonPartEntity enderdragonpartentity : ((EnderDragonEntity)entity).getDragonParts()) {
                        if (enderdragonpartentity != entityIn && enderdragonpartentity.getBoundingBox().intersects(aabb) && (filter == null || filter.test(enderdragonpartentity))) {
                           listToFill.add(enderdragonpartentity);
                        }
                     }
                  }
               }
            }
         }
      }

   }

   /**
    * Fills the given list of all entities that are specific EntityType and intersects within the given bounding box.
    */
   public <T extends Entity> void getEntitiesWithinAABBForList(@Nullable EntityType<?> entitytypeIn, AxisAlignedBB aabb, List<? super T> list, Predicate<? super T> filter) {
      int i = MathHelper.floor((aabb.minY - this.world.getMaxEntityRadius()) / 16.0D);
      int j = MathHelper.floor((aabb.maxY + this.world.getMaxEntityRadius()) / 16.0D);
      i = MathHelper.clamp(i, 0, this.entityLists.length - 1);
      j = MathHelper.clamp(j, 0, this.entityLists.length - 1);

      for(int k = i; k <= j; ++k) {
         for(Entity entity : this.entityLists[k].getByClass(Entity.class)) {
            if ((entitytypeIn == null || entity.getType() == entitytypeIn) && entity.getBoundingBox().intersects(aabb) && filter.test((T)entity)) {
               list.add((T)entity);
            }
         }
      }

   }

   /**
    * Gets all entities that can be assigned to the specified class.
    */
   public <T extends Entity> void getEntitiesOfTypeWithinAABB(Class<? extends T> entityClass, AxisAlignedBB aabb, List<T> listToFill, @Nullable Predicate<? super T> filter) {
      int i = MathHelper.floor((aabb.minY - this.world.getMaxEntityRadius()) / 16.0D);
      int j = MathHelper.floor((aabb.maxY + this.world.getMaxEntityRadius()) / 16.0D);
      i = MathHelper.clamp(i, 0, this.entityLists.length - 1);
      j = MathHelper.clamp(j, 0, this.entityLists.length - 1);

      for(int k = i; k <= j; ++k) {
         for(T t : this.entityLists[k].getByClass(entityClass)) {
            if (t.getBoundingBox().intersects(aabb) && (filter == null || filter.test(t))) {
               listToFill.add(t);
            }
         }
      }

   }

   public boolean isEmpty() {
      return false;
   }

   /**
    * Gets a {@link ChunkPos} representing the x and z coordinates of this chunk.
    */
   public ChunkPos getPos() {
      return this.pos;
   }

   @OnlyIn(Dist.CLIENT)
   public void read(@Nullable BiomeContainer biomeContainerIn, PacketBuffer packetBufferIn, CompoundNBT nbtIn, int availableSections) {
      boolean flag = biomeContainerIn != null;
      Predicate<BlockPos> predicate = flag ? (p_217315_0_) -> {
         return true;
      } : (p_217323_1_) -> {
         return (availableSections & 1 << (p_217323_1_.getY() >> 4)) != 0;
      };
      Sets.newHashSet(this.tileEntities.keySet()).stream().filter(predicate).forEach(this.world::removeTileEntity);

      for (TileEntity tileEntity : tileEntities.values()) {
         tileEntity.updateContainingBlockInfo();
         tileEntity.getBlockState();
      }

      for(int i = 0; i < this.sections.length; ++i) {
         ChunkSection chunksection = this.sections[i];
         if ((availableSections & 1 << i) == 0) {
            if (flag && chunksection != EMPTY_SECTION) {
               this.sections[i] = EMPTY_SECTION;
            }
         } else {
            if (chunksection == EMPTY_SECTION) {
               chunksection = new ChunkSection(i << 4);
               this.sections[i] = chunksection;
            }

            chunksection.read(packetBufferIn);
         }
      }

      if (biomeContainerIn != null) {
         this.blockBiomeArray = biomeContainerIn;
      }

      for(Heightmap.Type heightmap$type : Heightmap.Type.values()) {
         String s = heightmap$type.getId();
         if (nbtIn.contains(s, 12)) {
            this.setHeightmap(heightmap$type, nbtIn.getLongArray(s));
         }
      }

      for(TileEntity tileentity : this.tileEntities.values()) {
         tileentity.updateContainingBlockInfo();
      }

   }

   public BiomeContainer getBiomes() {
      return this.blockBiomeArray;
   }

   public void setLoaded(boolean loaded) {
      this.loaded = loaded;
   }

   public World getWorld() {
      return this.world;
   }

   public Collection<Entry<Heightmap.Type, Heightmap>> getHeightmaps() {
      return Collections.unmodifiableSet(this.heightMap.entrySet());
   }

   public Map<BlockPos, TileEntity> getTileEntityMap() {
      return this.tileEntities;
   }

   public ClassInheritanceMultiMap<Entity>[] getEntityLists() {
      return this.entityLists;
   }

   public CompoundNBT getDeferredTileEntity(BlockPos pos) {
      return this.deferredTileEntities.get(pos);
   }

   public Stream<BlockPos> getLightSources() {
      return StreamSupport.stream(BlockPos.getAllInBoxMutable(this.pos.getXStart(), 0, this.pos.getZStart(), this.pos.getXEnd(), 255, this.pos.getZEnd()).spliterator(), false).filter((p_217312_1_) -> {
         return this.getBlockState(p_217312_1_).getLightValue(getWorld(), p_217312_1_) != 0;
      });
   }

   public ITickList<Block> getBlocksToBeTicked() {
      return this.blocksToBeTicked;
   }

   public ITickList<Fluid> getFluidsToBeTicked() {
      return this.fluidsToBeTicked;
   }

   public void setModified(boolean modified) {
      this.dirty = modified;
   }

   public boolean isModified() {
      return this.dirty || this.hasEntities && this.world.getGameTime() != this.lastSaveTime;
   }

   public void setHasEntities(boolean hasEntitiesIn) {
      this.hasEntities = hasEntitiesIn;
   }

   public void setLastSaveTime(long saveTime) {
      this.lastSaveTime = saveTime;
   }

   @Nullable
   public StructureStart getStructureStart(String stucture) {
      return this.structureStarts.get(stucture);
   }

   public void putStructureStart(String structureIn, StructureStart structureStartIn) {
      this.structureStarts.put(structureIn, structureStartIn);
   }

   public Map<String, StructureStart> getStructureStarts() {
      return this.structureStarts;
   }

   public void setStructureStarts(Map<String, StructureStart> structureStartsIn) {
      this.structureStarts.clear();
      this.structureStarts.putAll(structureStartsIn);
   }

   public LongSet getStructureReferences(String structureIn) {
      return this.structureReferences.computeIfAbsent(structureIn, (p_201603_0_) -> {
         return new LongOpenHashSet();
      });
   }

   public void addStructureReference(String strucutre, long reference) {
      this.structureReferences.computeIfAbsent(strucutre, (p_201598_0_) -> {
         return new LongOpenHashSet();
      }).add(reference);
   }

   public Map<String, LongSet> getStructureReferences() {
      return this.structureReferences;
   }

   public void setStructureReferences(Map<String, LongSet> p_201606_1_) {
      this.structureReferences.clear();
      this.structureReferences.putAll(p_201606_1_);
   }

   public long getInhabitedTime() {
      return this.inhabitedTime;
   }

   public void setInhabitedTime(long newInhabitedTime) {
      this.inhabitedTime = newInhabitedTime;
   }

   public void postProcess() {
      ChunkPos chunkpos = this.getPos();

      for(int i = 0; i < this.packedBlockPositions.length; ++i) {
         if (this.packedBlockPositions[i] != null) {
            for(Short oshort : this.packedBlockPositions[i]) {
               BlockPos blockpos = ChunkPrimer.unpackToWorld(oshort, i, chunkpos);
               BlockState blockstate = this.getBlockState(blockpos);
               BlockState blockstate1 = Block.getValidBlockForPosition(blockstate, this.world, blockpos);
               this.world.setBlockState(blockpos, blockstate1, 20);
            }

            this.packedBlockPositions[i].clear();
         }
      }

      this.rescheduleTicks();

      for(BlockPos blockpos1 : Sets.newHashSet(this.deferredTileEntities.keySet())) {
         this.getTileEntity(blockpos1);
      }

      this.deferredTileEntities.clear();
      this.upgradeData.postProcessChunk(this);
   }

   /**
    * Sets up or deserializes a {@link TileEntity} at the desired location from the given compound. If the compound's
    * TileEntity id is {@code "DUMMY"}, the TileEntity may be created by the {@link ITileProvider} instance if the
    * {@link Block} at the given position is in fact a provider. Otherwise, the TileEntity is deserialized at the given
    * position.
    */
   @Nullable
   private TileEntity setDeferredTileEntity(BlockPos pos, CompoundNBT compound) {
      TileEntity tileentity;
      if ("DUMMY".equals(compound.getString("id"))) {
         BlockState state = this.getBlockState(pos);
         if (state.hasTileEntity()) {
            tileentity = state.createTileEntity(this.world);
         } else {
            tileentity = null;
            LOGGER.warn("Tried to load a DUMMY block entity @ {} but found not block entity block {} at location", pos, this.getBlockState(pos));
         }
      } else {
         tileentity = TileEntity.create(compound);
      }

      if (tileentity != null) {
         tileentity.setWorldAndPos(this.world, pos);
         this.addTileEntity(tileentity);
      } else {
         LOGGER.warn("Tried to load a block entity for block {} but failed at location {}", this.getBlockState(pos), pos);
      }

      return tileentity;
   }

   public UpgradeData getUpgradeData() {
      return this.upgradeData;
   }

   public ShortList[] getPackedPositions() {
      return this.packedBlockPositions;
   }

   /**
    * Reschedule all serialized scheduled ticks this chunk had
    */
   public void rescheduleTicks() {
      if (this.blocksToBeTicked instanceof ChunkPrimerTickList) {
         ((ChunkPrimerTickList<Block>)this.blocksToBeTicked).postProcess(this.world.getPendingBlockTicks(), (p_222881_1_) -> {
            return this.getBlockState(p_222881_1_).getBlock();
         });
         this.blocksToBeTicked = EmptyTickList.get();
      } else if (this.blocksToBeTicked instanceof SerializableTickList) {
         this.world.getPendingBlockTicks().addAll(((SerializableTickList)this.blocksToBeTicked).ticks());
         this.blocksToBeTicked = EmptyTickList.get();
      }

      if (this.fluidsToBeTicked instanceof ChunkPrimerTickList) {
         ((ChunkPrimerTickList<Fluid>)this.fluidsToBeTicked).postProcess(this.world.getPendingFluidTicks(), (p_222878_1_) -> {
            return this.getFluidState(p_222878_1_).getFluid();
         });
         this.fluidsToBeTicked = EmptyTickList.get();
      } else if (this.fluidsToBeTicked instanceof SerializableTickList) {
         this.world.getPendingFluidTicks().addAll(((SerializableTickList)this.fluidsToBeTicked).ticks());
         this.fluidsToBeTicked = EmptyTickList.get();
      }

   }

   /**
    * Remove scheduled ticks belonging to this chunk from the world and keep it locally for incoming serialization
    */
   public void saveScheduledTicks(ServerWorld serverWorldIn) {
      if (this.blocksToBeTicked == EmptyTickList.<Block>get()) {
         this.blocksToBeTicked = new SerializableTickList<>(Registry.BLOCK::getKey, serverWorldIn.getPendingBlockTicks().getPending(this.pos, true, false));
         this.setModified(true);
      }

      if (this.fluidsToBeTicked == EmptyTickList.<Fluid>get()) {
         this.fluidsToBeTicked = new SerializableTickList<>(Registry.FLUID::getKey, serverWorldIn.getPendingFluidTicks().getPending(this.pos, true, false));
         this.setModified(true);
      }

   }

   public ChunkStatus getStatus() {
      return ChunkStatus.FULL;
   }

   public ChunkHolder.LocationType getLocationType() {
      return this.locationType == null ? ChunkHolder.LocationType.BORDER : this.locationType.get();
   }

   public void setLocationType(Supplier<ChunkHolder.LocationType> locationTypeIn) {
      this.locationType = locationTypeIn;
   }

   public boolean hasLight() {
      return this.lightCorrect;
   }

   public void setLight(boolean lightCorrectIn) {
      this.lightCorrect = lightCorrectIn;
      this.setModified(true);
   }

   public static enum CreateEntityType {
      IMMEDIATE,
      QUEUED,
      CHECK;
   }

   /**
    * <strong>FOR INTERNAL USE ONLY</strong>
    * <p>
    * Only public for use in {@link AnvilChunkLoader}.
    */
   @java.lang.Deprecated
   @javax.annotation.Nullable
   public final CompoundNBT writeCapsToNBT() {
      return this.serializeCaps();
   }

   /**
    * <strong>FOR INTERNAL USE ONLY</strong>
    * <p>
    * Only public for use in {@link AnvilChunkLoader}.
    */
   @java.lang.Deprecated
   public final void readCapsFromNBT(CompoundNBT tag) {
      this.deserializeCaps(tag);
   }

   @Override
   public World getWorldForge() {
      return getWorld();
   }
}