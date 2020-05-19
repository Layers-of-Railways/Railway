package net.minecraft.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.crash.ReportedException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.chunk.storage.ChunkLoader;
import net.minecraft.world.chunk.storage.RegionFile;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldOptimizer {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ThreadFactory THREAD_FACTORY = (new ThreadFactoryBuilder()).setDaemon(true).build();
   private final String worldName;
   private final boolean field_219957_d;
   private final SaveHandler worldStorage;
   private final Thread thread;
   private final File folder;
   private volatile boolean active = true;
   private volatile boolean done;
   private volatile float totalProgress;
   private volatile int totalChunks;
   private volatile int converted;
   private volatile int skipped;
   private final Object2FloatMap<DimensionType> progress = Object2FloatMaps.synchronize(new Object2FloatOpenCustomHashMap<>(Util.identityHashStrategy()));
   private volatile ITextComponent statusText = new TranslationTextComponent("optimizeWorld.stage.counting");
   private static final Pattern REGION_FILE_PATTERN = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
   private final DimensionSavedDataManager savedDataManager;

   public WorldOptimizer(String p_i50400_1_, SaveFormat p_i50400_2_, WorldInfo p_i50400_3_, boolean p_i50400_4_) {
      this.worldName = p_i50400_3_.getWorldName();
      this.field_219957_d = p_i50400_4_;
      this.worldStorage = p_i50400_2_.getSaveLoader(p_i50400_1_, (MinecraftServer)null);
      this.worldStorage.saveWorldInfo(p_i50400_3_);
      this.savedDataManager = new DimensionSavedDataManager(new File(DimensionType.OVERWORLD.getDirectory(this.worldStorage.getWorldDirectory()), "data"), this.worldStorage.getFixer());
      this.folder = this.worldStorage.getWorldDirectory();
      this.thread = THREAD_FACTORY.newThread(this::optimize);
      this.thread.setUncaughtExceptionHandler((p_219956_1_, p_219956_2_) -> {
         LOGGER.error("Error upgrading world", p_219956_2_);
         this.statusText = new TranslationTextComponent("optimizeWorld.stage.failed");
         this.done = true;
      });
      this.thread.start();
   }

   public void cancel() {
      this.active = false;

      try {
         this.thread.join();
      } catch (InterruptedException var2) {
         ;
      }

   }

   private void optimize() {
      File file1 = this.worldStorage.getWorldDirectory();
      this.totalChunks = 0;
      Builder<DimensionType, ListIterator<ChunkPos>> builder = ImmutableMap.builder();

      for(DimensionType dimensiontype : DimensionType.getAll()) {
         List<ChunkPos> list = this.func_219953_b(dimensiontype);
         builder.put(dimensiontype, list.listIterator());
         this.totalChunks += list.size();
      }

      if (this.totalChunks == 0) {
         this.done = true;
      } else {
         float f1 = (float)this.totalChunks;
         ImmutableMap<DimensionType, ListIterator<ChunkPos>> immutablemap = builder.build();
         Builder<DimensionType, ChunkLoader> builder1 = ImmutableMap.builder();

         for(DimensionType dimensiontype1 : DimensionType.getAll()) {
            File file2 = dimensiontype1.getDirectory(file1);
            builder1.put(dimensiontype1, new ChunkLoader(new File(file2, "region"), this.worldStorage.getFixer()));
         }

         ImmutableMap<DimensionType, ChunkLoader> immutablemap1 = builder1.build();
         long i = Util.milliTime();
         this.statusText = new TranslationTextComponent("optimizeWorld.stage.upgrading");

         while(this.active) {
            boolean flag = false;
            float f = 0.0F;

            for(DimensionType dimensiontype2 : DimensionType.getAll()) {
               ListIterator<ChunkPos> listiterator = immutablemap.get(dimensiontype2);
               ChunkLoader chunkloader = immutablemap1.get(dimensiontype2);
               if (listiterator.hasNext()) {
                  ChunkPos chunkpos = listiterator.next();
                  boolean flag1 = false;

                  try {
                     CompoundNBT compoundnbt = chunkloader.readChunk(chunkpos);
                     if (compoundnbt != null) {
                        int j = ChunkLoader.getDataVersion(compoundnbt);
                        CompoundNBT compoundnbt1 = chunkloader.updateChunkData(dimensiontype2, () -> {
                           return this.savedDataManager;
                        }, compoundnbt);
                        CompoundNBT compoundnbt2 = compoundnbt1.getCompound("Level");
                        ChunkPos chunkpos1 = new ChunkPos(compoundnbt2.getInt("xPos"), compoundnbt2.getInt("zPos"));
                        if (!chunkpos1.equals(chunkpos)) {
                           LOGGER.warn("Chunk {} has invalid position {}", chunkpos, chunkpos1);
                        }

                        boolean flag2 = j < SharedConstants.getVersion().getWorldVersion();
                        if (this.field_219957_d) {
                           flag2 = flag2 || compoundnbt2.contains("Heightmaps");
                           compoundnbt2.remove("Heightmaps");
                           flag2 = flag2 || compoundnbt2.contains("isLightOn");
                           compoundnbt2.remove("isLightOn");
                        }

                        if (flag2) {
                           chunkloader.writeChunk(chunkpos, compoundnbt1);
                           flag1 = true;
                        }
                     }
                  } catch (ReportedException reportedexception) {
                     Throwable throwable = reportedexception.getCause();
                     if (!(throwable instanceof IOException)) {
                        throw reportedexception;
                     }

                     LOGGER.error("Error upgrading chunk {}", chunkpos, throwable);
                  } catch (IOException ioexception1) {
                     LOGGER.error("Error upgrading chunk {}", chunkpos, ioexception1);
                  }

                  if (flag1) {
                     ++this.converted;
                  } else {
                     ++this.skipped;
                  }

                  flag = true;
               }

               float f2 = (float)listiterator.nextIndex() / f1;
               this.progress.put(dimensiontype2, f2);
               f += f2;
            }

            this.totalProgress = f;
            if (!flag) {
               this.active = false;
            }
         }

         this.statusText = new TranslationTextComponent("optimizeWorld.stage.finished");

         for(ChunkLoader chunkloader1 : immutablemap1.values()) {
            try {
               chunkloader1.close();
            } catch (IOException ioexception) {
               LOGGER.error("Error upgrading chunk", (Throwable)ioexception);
            }
         }

         this.savedDataManager.save();
         i = Util.milliTime() - i;
         LOGGER.info("World optimizaton finished after {} ms", (long)i);
         this.done = true;
      }
   }

   private List<ChunkPos> func_219953_b(DimensionType p_219953_1_) {
      File file1 = p_219953_1_.getDirectory(this.folder);
      File file2 = new File(file1, "region");
      File[] afile = file2.listFiles((p_219954_0_, p_219954_1_) -> {
         return p_219954_1_.endsWith(".mca");
      });
      if (afile == null) {
         return ImmutableList.of();
      } else {
         List<ChunkPos> list = Lists.newArrayList();

         for(File file3 : afile) {
            Matcher matcher = REGION_FILE_PATTERN.matcher(file3.getName());
            if (matcher.matches()) {
               int i = Integer.parseInt(matcher.group(1)) << 5;
               int j = Integer.parseInt(matcher.group(2)) << 5;

               try (RegionFile regionfile = new RegionFile(file3, file2)) {
                  for(int k = 0; k < 32; ++k) {
                     for(int l = 0; l < 32; ++l) {
                        ChunkPos chunkpos = new ChunkPos(k + i, l + j);
                        if (regionfile.func_222662_b(chunkpos)) {
                           list.add(chunkpos);
                        }
                     }
                  }
               } catch (Throwable var28) {
                  ;
               }
            }
         }

         return list;
      }
   }

   public boolean isFinished() {
      return this.done;
   }

   @OnlyIn(Dist.CLIENT)
   public float getProgress(DimensionType p_212543_1_) {
      return this.progress.getFloat(p_212543_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public float getTotalProgress() {
      return this.totalProgress;
   }

   public int getTotalChunks() {
      return this.totalChunks;
   }

   public int getConverted() {
      return this.converted;
   }

   public int getSkipped() {
      return this.skipped;
   }

   public ITextComponent getStatusText() {
      return this.statusText;
   }
}