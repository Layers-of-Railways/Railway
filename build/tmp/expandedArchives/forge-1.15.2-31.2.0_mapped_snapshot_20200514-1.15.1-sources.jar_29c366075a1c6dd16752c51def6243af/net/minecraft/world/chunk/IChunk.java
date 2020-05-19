package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IStructureReader;
import net.minecraft.world.ITickList;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructureStart;
import org.apache.logging.log4j.LogManager;

public interface IChunk extends IBlockReader, IStructureReader {
   @Nullable
   BlockState setBlockState(BlockPos pos, BlockState state, boolean isMoving);

   void addTileEntity(BlockPos pos, TileEntity tileEntityIn);

   /**
    * Adds an entity to the chunk.
    */
   void addEntity(Entity entityIn);

   @Nullable
   default ChunkSection getLastExtendedBlockStorage() {
      ChunkSection[] achunksection = this.getSections();

      for(int i = achunksection.length - 1; i >= 0; --i) {
         ChunkSection chunksection = achunksection[i];
         if (!ChunkSection.isEmpty(chunksection)) {
            return chunksection;
         }
      }

      return null;
   }

   /**
    * Returns the topmost ExtendedBlockStorage instance for this Chunk that actually contains a block.
    */
   default int getTopFilledSegment() {
      ChunkSection chunksection = this.getLastExtendedBlockStorage();
      return chunksection == null ? 0 : chunksection.getYLocation();
   }

   Set<BlockPos> getTileEntitiesPos();

   /**
    * Returns the ExtendedBlockStorage array for this Chunk.
    */
   ChunkSection[] getSections();

   Collection<Entry<Heightmap.Type, Heightmap>> getHeightmaps();

   void setHeightmap(Heightmap.Type type, long[] data);

   Heightmap getHeightmap(Heightmap.Type typeIn);

   int getTopBlockY(Heightmap.Type heightmapType, int x, int z);

   /**
    * Gets a {@link ChunkPos} representing the x and z coordinates of this chunk.
    */
   ChunkPos getPos();

   void setLastSaveTime(long saveTime);

   Map<String, StructureStart> getStructureStarts();

   void setStructureStarts(Map<String, StructureStart> structureStartsIn);

   /**
    * Returns whether the ExtendedBlockStorages containing levels (in blocks) from arg 1 to arg 2 are fully empty (true)
    * or not (false).
    */
   default boolean isEmptyBetween(int startY, int endY) {
      if (startY < 0) {
         startY = 0;
      }

      if (endY >= 256) {
         endY = 255;
      }

      for(int i = startY; i <= endY; i += 16) {
         if (!ChunkSection.isEmpty(this.getSections()[i >> 4])) {
            return false;
         }
      }

      return true;
   }

   @Nullable
   BiomeContainer getBiomes();

   void setModified(boolean modified);

   boolean isModified();

   ChunkStatus getStatus();

   void removeTileEntity(BlockPos pos);

   default void markBlockForPostprocessing(BlockPos pos) {
      LogManager.getLogger().warn("Trying to mark a block for PostProcessing @ {}, but this operation is not supported.", (Object)pos);
   }

   ShortList[] getPackedPositions();

   default void func_201636_b(short packedPosition, int index) {
      getList(this.getPackedPositions(), index).add(packedPosition);
   }

   default void addTileEntity(CompoundNBT nbt) {
      LogManager.getLogger().warn("Trying to set a BlockEntity, but this operation is not supported.");
   }

   @Nullable
   CompoundNBT getDeferredTileEntity(BlockPos pos);

   @Nullable
   CompoundNBT getTileEntityNBT(BlockPos pos);

   Stream<BlockPos> getLightSources();

   ITickList<Block> getBlocksToBeTicked();

   ITickList<Fluid> getFluidsToBeTicked();

   default BitSet getCarvingMask(GenerationStage.Carving type) {
      throw (RuntimeException)Util.pauseDevMode(new RuntimeException("Meaningless in this context"));
   }

   UpgradeData getUpgradeData();

   void setInhabitedTime(long newInhabitedTime);

   long getInhabitedTime();

   static ShortList getList(ShortList[] p_217308_0_, int p_217308_1_) {
      if (p_217308_0_[p_217308_1_] == null) {
         p_217308_0_[p_217308_1_] = new ShortArrayList();
      }

      return p_217308_0_[p_217308_1_];
   }

   boolean hasLight();

   void setLight(boolean lightCorrectIn);

   @Nullable
   default net.minecraft.world.IWorld getWorldForge() {
      return null;
   }
}