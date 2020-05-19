package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ITickList;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.TickPriority;
import net.minecraft.world.chunk.storage.ChunkSerializer;

public class ChunkPrimerTickList<T> implements ITickList<T> {
   protected final Predicate<T> filter;
   private final ChunkPos pos;
   private final ShortList[] packedPositions = new ShortList[16];

   public ChunkPrimerTickList(Predicate<T> p_i51495_1_, ChunkPos p_i51495_2_) {
      this(p_i51495_1_, p_i51495_2_, new ListNBT());
   }

   public ChunkPrimerTickList(Predicate<T> p_i51496_1_, ChunkPos p_i51496_2_, ListNBT p_i51496_3_) {
      this.filter = p_i51496_1_;
      this.pos = p_i51496_2_;

      for(int i = 0; i < p_i51496_3_.size(); ++i) {
         ListNBT listnbt = p_i51496_3_.getList(i);

         for(int j = 0; j < listnbt.size(); ++j) {
            IChunk.getList(this.packedPositions, i).add(listnbt.getShort(j));
         }
      }

   }

   public ListNBT write() {
      return ChunkSerializer.toNbt(this.packedPositions);
   }

   public void postProcess(ITickList<T> tickList, Function<BlockPos, T> func) {
      for(int i = 0; i < this.packedPositions.length; ++i) {
         if (this.packedPositions[i] != null) {
            for(Short oshort : this.packedPositions[i]) {
               BlockPos blockpos = ChunkPrimer.unpackToWorld(oshort, i, this.pos);
               tickList.scheduleTick(blockpos, func.apply(blockpos), 0);
            }

            this.packedPositions[i].clear();
         }
      }

   }

   public boolean isTickScheduled(BlockPos pos, T itemIn) {
      return false;
   }

   public void scheduleTick(BlockPos pos, T itemIn, int scheduledTime, TickPriority priority) {
      IChunk.getList(this.packedPositions, pos.getY() >> 4).add(ChunkPrimer.packToLocal(pos));
   }

   /**
    * Checks if this position/item is scheduled to be updated this tick
    */
   public boolean isTickPending(BlockPos pos, T obj) {
      return false;
   }

   public void addAll(Stream<NextTickListEntry<T>> p_219497_1_) {
      p_219497_1_.forEach((p_219506_1_) -> {
         this.scheduleTick(p_219506_1_.position, p_219506_1_.getTarget(), 0, p_219506_1_.priority);
      });
   }
}