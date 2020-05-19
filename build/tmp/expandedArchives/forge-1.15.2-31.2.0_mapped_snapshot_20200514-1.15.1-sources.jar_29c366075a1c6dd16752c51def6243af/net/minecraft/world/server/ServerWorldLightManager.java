package net.minecraft.world.server;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntSupplier;
import javax.annotation.Nullable;
import net.minecraft.util.Util;
import net.minecraft.util.concurrent.DelegatedTaskExecutor;
import net.minecraft.util.concurrent.ITaskExecutor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkTaskPriorityQueueSorter;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.lighting.WorldLightManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerWorldLightManager extends WorldLightManager implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final DelegatedTaskExecutor<Runnable> field_215605_b;
   private final ObjectList<Pair<ServerWorldLightManager.Phase, Runnable>> field_215606_c = new ObjectArrayList<>();
   private final ChunkManager chunkManager;
   private final ITaskExecutor<ChunkTaskPriorityQueueSorter.FunctionEntry<Runnable>> field_215608_e;
   private volatile int field_215609_f = 5;
   private final AtomicBoolean field_215610_g = new AtomicBoolean();

   public ServerWorldLightManager(IChunkLightProvider provider, ChunkManager chunkManagerIn, boolean hasSkyLight, DelegatedTaskExecutor<Runnable> p_i50701_4_, ITaskExecutor<ChunkTaskPriorityQueueSorter.FunctionEntry<Runnable>> p_i50701_5_) {
      super(provider, true, hasSkyLight);
      this.chunkManager = chunkManagerIn;
      this.field_215608_e = p_i50701_5_;
      this.field_215605_b = p_i50701_4_;
   }

   public void close() {
   }

   public int tick(int toUpdateCount, boolean updateSkyLight, boolean updateBlockLight) {
      throw (UnsupportedOperationException)Util.pauseDevMode(new UnsupportedOperationException("Ran authomatically on a different thread!"));
   }

   public void onBlockEmissionIncrease(BlockPos blockPosIn, int p_215573_2_) {
      throw (UnsupportedOperationException)Util.pauseDevMode(new UnsupportedOperationException("Ran authomatically on a different thread!"));
   }

   public void checkBlock(BlockPos blockPosIn) {
      BlockPos blockpos = blockPosIn.toImmutable();
      this.func_215586_a(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4, ServerWorldLightManager.Phase.POST_UPDATE, Util.namedRunnable(() -> {
         super.checkBlock(blockpos);
      }, () -> {
         return "checkBlock " + blockpos;
      }));
   }

   protected void updateChunkStatus(ChunkPos p_215581_1_) {
      this.func_215600_a(p_215581_1_.x, p_215581_1_.z, () -> {
         return 0;
      }, ServerWorldLightManager.Phase.PRE_UPDATE, Util.namedRunnable(() -> {
         super.retainData(p_215581_1_, false);
         super.enableLightSources(p_215581_1_, false);

         for(int i = -1; i < 17; ++i) {
            super.setData(LightType.BLOCK, SectionPos.from(p_215581_1_, i), (NibbleArray)null);
            super.setData(LightType.SKY, SectionPos.from(p_215581_1_, i), (NibbleArray)null);
         }

         for(int j = 0; j < 16; ++j) {
            super.updateSectionStatus(SectionPos.from(p_215581_1_, j), true);
         }

      }, () -> {
         return "updateChunkStatus " + p_215581_1_ + " " + true;
      }));
   }

   public void updateSectionStatus(SectionPos pos, boolean isEmpty) {
      this.func_215600_a(pos.getSectionX(), pos.getSectionZ(), () -> {
         return 0;
      }, ServerWorldLightManager.Phase.PRE_UPDATE, Util.namedRunnable(() -> {
         super.updateSectionStatus(pos, isEmpty);
      }, () -> {
         return "updateSectionStatus " + pos + " " + isEmpty;
      }));
   }

   public void enableLightSources(ChunkPos p_215571_1_, boolean p_215571_2_) {
      this.func_215586_a(p_215571_1_.x, p_215571_1_.z, ServerWorldLightManager.Phase.PRE_UPDATE, Util.namedRunnable(() -> {
         super.enableLightSources(p_215571_1_, p_215571_2_);
      }, () -> {
         return "enableLight " + p_215571_1_ + " " + p_215571_2_;
      }));
   }

   public void setData(LightType type, SectionPos pos, @Nullable NibbleArray array) {
      this.func_215600_a(pos.getSectionX(), pos.getSectionZ(), () -> {
         return 0;
      }, ServerWorldLightManager.Phase.PRE_UPDATE, Util.namedRunnable(() -> {
         super.setData(type, pos, array);
      }, () -> {
         return "queueData " + pos;
      }));
   }

   private void func_215586_a(int chunkX, int chunkZ, ServerWorldLightManager.Phase p_215586_3_, Runnable p_215586_4_) {
      this.func_215600_a(chunkX, chunkZ, this.chunkManager.func_219191_c(ChunkPos.asLong(chunkX, chunkZ)), p_215586_3_, p_215586_4_);
   }

   private void func_215600_a(int chunkX, int chunkZ, IntSupplier p_215600_3_, ServerWorldLightManager.Phase p_215600_4_, Runnable p_215600_5_) {
      this.field_215608_e.enqueue(ChunkTaskPriorityQueueSorter.func_219069_a(() -> {
         this.field_215606_c.add(Pair.of(p_215600_4_, p_215600_5_));
         if (this.field_215606_c.size() >= this.field_215609_f) {
            this.func_215603_b();
         }

      }, ChunkPos.asLong(chunkX, chunkZ), p_215600_3_));
   }

   public void retainData(ChunkPos pos, boolean retain) {
      this.func_215600_a(pos.x, pos.z, () -> {
         return 0;
      }, ServerWorldLightManager.Phase.PRE_UPDATE, Util.namedRunnable(() -> {
         super.retainData(pos, retain);
      }, () -> {
         return "retainData " + pos;
      }));
   }

   public CompletableFuture<IChunk> lightChunk(IChunk p_215593_1_, boolean p_215593_2_) {
      ChunkPos chunkpos = p_215593_1_.getPos();
      p_215593_1_.setLight(false);
      this.func_215586_a(chunkpos.x, chunkpos.z, ServerWorldLightManager.Phase.PRE_UPDATE, Util.namedRunnable(() -> {
         ChunkSection[] achunksection = p_215593_1_.getSections();

         for(int i = 0; i < 16; ++i) {
            ChunkSection chunksection = achunksection[i];
            if (!ChunkSection.isEmpty(chunksection)) {
               super.updateSectionStatus(SectionPos.from(chunkpos, i), false);
            }
         }

         super.enableLightSources(chunkpos, true);
         if (!p_215593_2_) {
            p_215593_1_.getLightSources().forEach((p_215579_2_) -> {
               super.onBlockEmissionIncrease(p_215579_2_, p_215593_1_.getLightValue(p_215579_2_));
            });
         }

         this.chunkManager.func_219209_c(chunkpos);
      }, () -> {
         return "lightChunk " + chunkpos + " " + p_215593_2_;
      }));
      return CompletableFuture.supplyAsync(() -> {
         p_215593_1_.setLight(true);
         super.retainData(chunkpos, false);
         return p_215593_1_;
      }, (p_215597_2_) -> {
         this.func_215586_a(chunkpos.x, chunkpos.z, ServerWorldLightManager.Phase.POST_UPDATE, p_215597_2_);
      });
   }

   public void func_215588_z_() {
      if ((!this.field_215606_c.isEmpty() || super.hasLightWork()) && this.field_215610_g.compareAndSet(false, true)) {
         this.field_215605_b.enqueue(() -> {
            this.func_215603_b();
            this.field_215610_g.set(false);
         });
      }

   }

   private void func_215603_b() {
      int i = Math.min(this.field_215606_c.size(), this.field_215609_f);
      ObjectListIterator<Pair<ServerWorldLightManager.Phase, Runnable>> objectlistiterator = this.field_215606_c.iterator();

      int j;
      for(j = 0; objectlistiterator.hasNext() && j < i; ++j) {
         Pair<ServerWorldLightManager.Phase, Runnable> pair = objectlistiterator.next();
         if (pair.getFirst() == ServerWorldLightManager.Phase.PRE_UPDATE) {
            pair.getSecond().run();
         }
      }

      objectlistiterator.back(j);
      super.tick(Integer.MAX_VALUE, true, true);

      for(int k = 0; objectlistiterator.hasNext() && k < i; ++k) {
         Pair<ServerWorldLightManager.Phase, Runnable> pair1 = objectlistiterator.next();
         if (pair1.getFirst() == ServerWorldLightManager.Phase.POST_UPDATE) {
            pair1.getSecond().run();
         }

         objectlistiterator.remove();
      }

   }

   public void func_215598_a(int p_215598_1_) {
      this.field_215609_f = p_215598_1_;
   }

   static enum Phase {
      PRE_UPDATE,
      POST_UPDATE;
   }
}