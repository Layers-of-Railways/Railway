package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.BitSet;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.lighting.WorldLightManager;

public class ChunkPrimerWrapper extends ChunkPrimer {
   private final Chunk chunk;

   public ChunkPrimerWrapper(Chunk p_i49948_1_) {
      super(p_i49948_1_.getPos(), UpgradeData.EMPTY);
      this.chunk = p_i49948_1_;
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos pos) {
      return this.chunk.getTileEntity(pos);
   }

   @Nullable
   public BlockState getBlockState(BlockPos pos) {
      return this.chunk.getBlockState(pos);
   }

   public IFluidState getFluidState(BlockPos pos) {
      return this.chunk.getFluidState(pos);
   }

   public int getMaxLightLevel() {
      return this.chunk.getMaxLightLevel();
   }

   @Nullable
   public BlockState setBlockState(BlockPos pos, BlockState state, boolean isMoving) {
      return null;
   }

   public void addTileEntity(BlockPos pos, TileEntity tileEntityIn) {
   }

   /**
    * Adds an entity to the chunk.
    */
   public void addEntity(Entity entityIn) {
   }

   public void setStatus(ChunkStatus status) {
   }

   /**
    * Returns the ExtendedBlockStorage array for this Chunk.
    */
   public ChunkSection[] getSections() {
      return this.chunk.getSections();
   }

   @Nullable
   public WorldLightManager getWorldLightManager() {
      return this.chunk.getWorldLightManager();
   }

   public void setHeightmap(Heightmap.Type type, long[] data) {
   }

   private Heightmap.Type func_209532_c(Heightmap.Type p_209532_1_) {
      if (p_209532_1_ == Heightmap.Type.WORLD_SURFACE_WG) {
         return Heightmap.Type.WORLD_SURFACE;
      } else {
         return p_209532_1_ == Heightmap.Type.OCEAN_FLOOR_WG ? Heightmap.Type.OCEAN_FLOOR : p_209532_1_;
      }
   }

   public int getTopBlockY(Heightmap.Type heightmapType, int x, int z) {
      return this.chunk.getTopBlockY(this.func_209532_c(heightmapType), x, z);
   }

   /**
    * Gets a {@link ChunkPos} representing the x and z coordinates of this chunk.
    */
   public ChunkPos getPos() {
      return this.chunk.getPos();
   }

   public void setLastSaveTime(long saveTime) {
   }

   @Nullable
   public StructureStart getStructureStart(String stucture) {
      return this.chunk.getStructureStart(stucture);
   }

   public void putStructureStart(String structureIn, StructureStart structureStartIn) {
   }

   public Map<String, StructureStart> getStructureStarts() {
      return this.chunk.getStructureStarts();
   }

   public void setStructureStarts(Map<String, StructureStart> structureStartsIn) {
   }

   public LongSet getStructureReferences(String structureIn) {
      return this.chunk.getStructureReferences(structureIn);
   }

   public void addStructureReference(String strucutre, long reference) {
   }

   public Map<String, LongSet> getStructureReferences() {
      return this.chunk.getStructureReferences();
   }

   public void setStructureReferences(Map<String, LongSet> p_201606_1_) {
   }

   public BiomeContainer getBiomes() {
      return this.chunk.getBiomes();
   }

   public void setModified(boolean modified) {
   }

   public boolean isModified() {
      return false;
   }

   public ChunkStatus getStatus() {
      return this.chunk.getStatus();
   }

   public void removeTileEntity(BlockPos pos) {
   }

   public void markBlockForPostprocessing(BlockPos pos) {
   }

   public void addTileEntity(CompoundNBT nbt) {
   }

   @Nullable
   public CompoundNBT getDeferredTileEntity(BlockPos pos) {
      return this.chunk.getDeferredTileEntity(pos);
   }

   @Nullable
   public CompoundNBT getTileEntityNBT(BlockPos pos) {
      return this.chunk.getTileEntityNBT(pos);
   }

   public void func_225548_a_(BiomeContainer p_225548_1_) {
   }

   public Stream<BlockPos> getLightSources() {
      return this.chunk.getLightSources();
   }

   public ChunkPrimerTickList<Block> getBlocksToBeTicked() {
      return new ChunkPrimerTickList<>((p_209219_0_) -> {
         return p_209219_0_.getDefaultState().isAir();
      }, this.getPos());
   }

   public ChunkPrimerTickList<Fluid> getFluidsToBeTicked() {
      return new ChunkPrimerTickList<>((p_209218_0_) -> {
         return p_209218_0_ == Fluids.EMPTY;
      }, this.getPos());
   }

   public BitSet getCarvingMask(GenerationStage.Carving type) {
      return this.chunk.getCarvingMask(type);
   }

   public Chunk func_217336_u() {
      return this.chunk;
   }

   public boolean hasLight() {
      return this.chunk.hasLight();
   }

   public void setLight(boolean lightCorrectIn) {
      this.chunk.setLight(lightCorrectIn);
   }
}