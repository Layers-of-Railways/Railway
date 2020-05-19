package net.minecraft.world.chunk.storage;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.structure.LegacyStructureDataUtil;
import net.minecraft.world.storage.DimensionSavedDataManager;

public class ChunkLoader implements AutoCloseable {
   private final IOWorker field_227077_a_;
   protected final DataFixer dataFixer;
   @Nullable
   private LegacyStructureDataUtil field_219167_a;

   public ChunkLoader(File regionDirectory, DataFixer dataFixerIn) {
      this.dataFixer = dataFixerIn;
      this.field_227077_a_ = new IOWorker(new RegionFileCache(regionDirectory), "chunk");
   }

   public CompoundNBT updateChunkData(DimensionType p_219166_1_, Supplier<DimensionSavedDataManager> savedDataManager, CompoundNBT chunkData) {
      int i = getDataVersion(chunkData);
      int j = 1493;
      if (i < 1493) {
         chunkData = NBTUtil.update(this.dataFixer, DefaultTypeReferences.CHUNK, chunkData, i, 1493);
         if (chunkData.getCompound("Level").getBoolean("hasLegacyStructureData")) {
            if (this.field_219167_a == null) {
               this.field_219167_a = LegacyStructureDataUtil.func_215130_a(p_219166_1_, savedDataManager.get());
            }

            chunkData = this.field_219167_a.func_212181_a(chunkData);
         }
      }

      chunkData = NBTUtil.update(this.dataFixer, DefaultTypeReferences.CHUNK, chunkData, Math.max(1493, i));
      if (i < SharedConstants.getVersion().getWorldVersion()) {
         chunkData.putInt("DataVersion", SharedConstants.getVersion().getWorldVersion());
      }

      return chunkData;
   }

   public static int getDataVersion(CompoundNBT compound) {
      return compound.contains("DataVersion", 99) ? compound.getInt("DataVersion") : -1;
   }

   @Nullable
   public CompoundNBT readChunk(ChunkPos p_227078_1_) throws IOException {
      return this.field_227077_a_.func_227090_a_(p_227078_1_);
   }

   public void writeChunk(ChunkPos pos, CompoundNBT compound) {
      this.field_227077_a_.func_227093_a_(pos, compound);
      if (this.field_219167_a != null) {
         this.field_219167_a.func_208216_a(pos.asLong());
      }

   }

   public void func_227079_i_() {
      this.field_227077_a_.func_227088_a_().join();
   }

   public void close() throws IOException {
      this.field_227077_a_.close();
   }
}