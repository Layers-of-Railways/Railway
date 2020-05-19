package net.minecraft.world.server;

import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.network.play.server.SChunkDataPacket;
import net.minecraft.network.play.server.SMultiBlockChangePacket;
import net.minecraft.network.play.server.SUpdateLightPacket;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkPrimerWrapper;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ChunkHolder {
   public static final Either<IChunk, ChunkHolder.IChunkLoadingError> MISSING_CHUNK = Either.right(ChunkHolder.IChunkLoadingError.UNLOADED);
   public static final CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> MISSING_CHUNK_FUTURE = CompletableFuture.completedFuture(MISSING_CHUNK);
   public static final Either<Chunk, ChunkHolder.IChunkLoadingError> UNLOADED_CHUNK = Either.right(ChunkHolder.IChunkLoadingError.UNLOADED);
   private static final CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> UNLOADED_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_CHUNK);
   private static final List<ChunkStatus> CHUNK_STATUS_LIST = ChunkStatus.getAll();
   private static final ChunkHolder.LocationType[] LOCATION_TYPES = ChunkHolder.LocationType.values();
   private final AtomicReferenceArray<CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>>> field_219312_g = new AtomicReferenceArray<>(CHUNK_STATUS_LIST.size());
   /**
    * A future that returns the chunk if it is a border chunk, {@link
    * net.minecraft.world.server.ChunkHolder.IChunkLoadingError#UNLOADED} otherwise.
    */
   private volatile CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> borderFuture = UNLOADED_CHUNK_FUTURE;
   /**
    * A future that returns the chunk if it is a ticking chunk, {@link
    * net.minecraft.world.server.ChunkHolder.IChunkLoadingError#UNLOADED} otherwise.
    */
   private volatile CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> tickingFuture = UNLOADED_CHUNK_FUTURE;
   /**
    * A future that returns the chunk if it is an entity ticking chunk, {@link
    * net.minecraft.world.server.ChunkHolder.IChunkLoadingError#UNLOADED} otherwise.
    */
   private volatile CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> entityTickingFuture = UNLOADED_CHUNK_FUTURE;
   private CompletableFuture<IChunk> field_219315_j = CompletableFuture.completedFuture((IChunk)null);
   private int prevChunkLevel;
   private int chunkLevel;
   private int field_219318_m;
   private final ChunkPos pos;
   private short[] changedBlockPositions = new short[64];
   private int changedBlocks;
   private int blockChangeMask;
   private int boundaryMask;
   private int blockLightChangeMask;
   private int skyLightChangeMask;
   private final WorldLightManager lightManager;
   private final ChunkHolder.IListener field_219327_v;
   private final ChunkHolder.IPlayerProvider playerProvider;
   private boolean accessible;

   public ChunkHolder(ChunkPos p_i50716_1_, int p_i50716_2_, WorldLightManager p_i50716_3_, ChunkHolder.IListener p_i50716_4_, ChunkHolder.IPlayerProvider p_i50716_5_) {
      this.pos = p_i50716_1_;
      this.lightManager = p_i50716_3_;
      this.field_219327_v = p_i50716_4_;
      this.playerProvider = p_i50716_5_;
      this.prevChunkLevel = ChunkManager.MAX_LOADED_LEVEL + 1;
      this.chunkLevel = this.prevChunkLevel;
      this.field_219318_m = this.prevChunkLevel;
      this.setChunkLevel(p_i50716_2_);
   }

   public CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> func_219301_a(ChunkStatus p_219301_1_) {
      CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = this.field_219312_g.get(p_219301_1_.ordinal());
      return completablefuture == null ? MISSING_CHUNK_FUTURE : completablefuture;
   }

   public CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> func_225410_b(ChunkStatus p_225410_1_) {
      return getChunkStatusFromLevel(this.chunkLevel).isAtLeast(p_225410_1_) ? this.func_219301_a(p_225410_1_) : MISSING_CHUNK_FUTURE;
   }

   public CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> getTickingFuture() {
      return this.tickingFuture;
   }

   public CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> getEntityTickingFuture() {
      return this.entityTickingFuture;
   }

   public CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> getBorderFuture() {
      return this.borderFuture;
   }

   @Nullable
   public Chunk getChunkIfComplete() {
      CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> completablefuture = this.getTickingFuture();
      Either<Chunk, ChunkHolder.IChunkLoadingError> either = completablefuture.getNow((Either<Chunk, ChunkHolder.IChunkLoadingError>)null);
      return either == null ? null : either.left().orElse((Chunk)null);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public ChunkStatus func_219285_d() {
      for(int i = CHUNK_STATUS_LIST.size() - 1; i >= 0; --i) {
         ChunkStatus chunkstatus = CHUNK_STATUS_LIST.get(i);
         CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = this.func_219301_a(chunkstatus);
         if (completablefuture.getNow(MISSING_CHUNK).left().isPresent()) {
            return chunkstatus;
         }
      }

      return null;
   }

   @Nullable
   public IChunk func_219287_e() {
      for(int i = CHUNK_STATUS_LIST.size() - 1; i >= 0; --i) {
         ChunkStatus chunkstatus = CHUNK_STATUS_LIST.get(i);
         CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = this.func_219301_a(chunkstatus);
         if (!completablefuture.isCompletedExceptionally()) {
            Optional<IChunk> optional = completablefuture.getNow(MISSING_CHUNK).left();
            if (optional.isPresent()) {
               return optional.get();
            }
         }
      }

      return null;
   }

   public CompletableFuture<IChunk> func_219302_f() {
      return this.field_219315_j;
   }

   public void markBlockChanged(int x, int y, int z) {
      Chunk chunk = this.getChunkIfComplete();
      if (chunk != null) {
         this.blockChangeMask |= 1 << (y >> 4);
         { //Forge; Cache everything, so always run
            short short1 = (short)(x << 12 | z << 8 | y);

            for(int i = 0; i < this.changedBlocks; ++i) {
               if (this.changedBlockPositions[i] == short1) {
                  return;
               }
            }

            if (this.changedBlocks == this.changedBlockPositions.length)
               this.changedBlockPositions = java.util.Arrays.copyOf(this.changedBlockPositions, this.changedBlockPositions.length << 1);
            this.changedBlockPositions[this.changedBlocks++] = short1;
         }

      }
   }

   public void markLightChanged(LightType type, int sectionY) {
      Chunk chunk = this.getChunkIfComplete();
      if (chunk != null) {
         chunk.setModified(true);
         if (type == LightType.SKY) {
            this.skyLightChangeMask |= 1 << sectionY - -1;
         } else {
            this.blockLightChangeMask |= 1 << sectionY - -1;
         }

      }
   }

   public void sendChanges(Chunk chunkIn) {
      if (this.changedBlocks != 0 || this.skyLightChangeMask != 0 || this.blockLightChangeMask != 0) {
         World world = chunkIn.getWorld();
         if (this.changedBlocks >= net.minecraftforge.common.ForgeConfig.SERVER.clumpingThreshold.get()) {
            this.boundaryMask = -1;
         }

         if (this.skyLightChangeMask != 0 || this.blockLightChangeMask != 0) {
            this.sendToTracking(new SUpdateLightPacket(chunkIn.getPos(), this.lightManager, this.skyLightChangeMask & ~this.boundaryMask, this.blockLightChangeMask & ~this.boundaryMask), true);
            int i = this.skyLightChangeMask & this.boundaryMask;
            int j = this.blockLightChangeMask & this.boundaryMask;
            if (i != 0 || j != 0) {
               this.sendToTracking(new SUpdateLightPacket(chunkIn.getPos(), this.lightManager, i, j), false);
            }

            this.skyLightChangeMask = 0;
            this.blockLightChangeMask = 0;
            this.boundaryMask &= ~(this.skyLightChangeMask & this.blockLightChangeMask);
         }

         if (this.changedBlocks == 1) {
            int l = (this.changedBlockPositions[0] >> 12 & 15) + this.pos.x * 16;
            int j1 = this.changedBlockPositions[0] & 255;
            int k = (this.changedBlockPositions[0] >> 8 & 15) + this.pos.z * 16;
            BlockPos blockpos = new BlockPos(l, j1, k);
            this.sendToTracking(new SChangeBlockPacket(world, blockpos), false);
            if (world.getBlockState(blockpos).hasTileEntity()) {
               this.sendTileEntity(world, blockpos);
            }
         } else if (this.changedBlocks >= net.minecraftforge.common.ForgeConfig.SERVER.clumpingThreshold.get()) {
            this.sendToTracking(new SChunkDataPacket(chunkIn, this.blockChangeMask), false);
         } else if (this.changedBlocks != 0) {
            this.sendToTracking(new SMultiBlockChangePacket(this.changedBlocks, this.changedBlockPositions, chunkIn), false);
            for(int i1 = 0; i1 < this.changedBlocks; ++i1) {
               int k1 = (this.changedBlockPositions[i1] >> 12 & 15) + this.pos.x * 16;
               int l1 = this.changedBlockPositions[i1] & 255;
               int i2 = (this.changedBlockPositions[i1] >> 8 & 15) + this.pos.z * 16;
               BlockPos blockpos1 = new BlockPos(k1, l1, i2);
               if (world.getBlockState(blockpos1).hasTileEntity()) {
                  this.sendTileEntity(world, blockpos1);
               }
            }
         }

         this.changedBlocks = 0;
         this.blockChangeMask = 0;
      }
   }

   private void sendTileEntity(World worldIn, BlockPos posIn) {
      TileEntity tileentity = worldIn.getTileEntity(posIn);
      if (tileentity != null) {
         SUpdateTileEntityPacket supdatetileentitypacket = tileentity.getUpdatePacket();
         if (supdatetileentitypacket != null) {
            this.sendToTracking(supdatetileentitypacket, false);
         }
      }

   }

   private void sendToTracking(IPacket<?> packetIn, boolean boundaryOnly) {
      this.playerProvider.getTrackingPlayers(this.pos, boundaryOnly).forEach((p_219304_1_) -> {
         p_219304_1_.connection.sendPacket(packetIn);
      });
   }

   public CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> func_219276_a(ChunkStatus p_219276_1_, ChunkManager p_219276_2_) {
      int i = p_219276_1_.ordinal();
      CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = this.field_219312_g.get(i);
      if (completablefuture != null) {
         Either<IChunk, ChunkHolder.IChunkLoadingError> either = completablefuture.getNow((Either<IChunk, ChunkHolder.IChunkLoadingError>)null);
         if (either == null || either.left().isPresent()) {
            return completablefuture;
         }
      }

      if (getChunkStatusFromLevel(this.chunkLevel).isAtLeast(p_219276_1_)) {
         CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture1 = p_219276_2_.func_219244_a(this, p_219276_1_);
         this.chain(completablefuture1);
         this.field_219312_g.set(i, completablefuture1);
         return completablefuture1;
      } else {
         return completablefuture == null ? MISSING_CHUNK_FUTURE : completablefuture;
      }
   }

   private void chain(CompletableFuture<? extends Either<? extends IChunk, ChunkHolder.IChunkLoadingError>> eitherChunk) {
      this.field_219315_j = this.field_219315_j.thenCombine(eitherChunk, (p_219295_0_, p_219295_1_) -> {
         return p_219295_1_.map((p_219283_0_) -> {
            return p_219283_0_;
         }, (p_219288_1_) -> {
            return p_219295_0_;
         });
      });
   }

   @OnlyIn(Dist.CLIENT)
   public ChunkHolder.LocationType func_219300_g() {
      return getLocationTypeFromLevel(this.chunkLevel);
   }

   public ChunkPos getPosition() {
      return this.pos;
   }

   public int getChunkLevel() {
      return this.chunkLevel;
   }

   public int func_219281_j() {
      return this.field_219318_m;
   }

   private void func_219275_d(int p_219275_1_) {
      this.field_219318_m = p_219275_1_;
   }

   public void setChunkLevel(int level) {
      this.chunkLevel = level;
   }

   /**
    * Updates chunk futures based on current chunk level
    */
   protected void processUpdates(ChunkManager chunkManagerIn) {
      ChunkStatus chunkstatus = getChunkStatusFromLevel(this.prevChunkLevel);
      ChunkStatus chunkstatus1 = getChunkStatusFromLevel(this.chunkLevel);
      boolean flag = this.prevChunkLevel <= ChunkManager.MAX_LOADED_LEVEL;
      boolean flag1 = this.chunkLevel <= ChunkManager.MAX_LOADED_LEVEL;
      ChunkHolder.LocationType chunkholder$locationtype = getLocationTypeFromLevel(this.prevChunkLevel);
      ChunkHolder.LocationType chunkholder$locationtype1 = getLocationTypeFromLevel(this.chunkLevel);
      if (flag) {
         Either<IChunk, ChunkHolder.IChunkLoadingError> either = Either.right(new ChunkHolder.IChunkLoadingError() {
            public String toString() {
               return "Unloaded ticket level " + ChunkHolder.this.pos.toString();
            }
         });

         for(int i = flag1 ? chunkstatus1.ordinal() + 1 : 0; i <= chunkstatus.ordinal(); ++i) {
            CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = this.field_219312_g.get(i);
            if (completablefuture != null) {
               completablefuture.complete(either);
            } else {
               this.field_219312_g.set(i, CompletableFuture.completedFuture(either));
            }
         }
      }

      boolean flag5 = chunkholder$locationtype.isAtLeast(ChunkHolder.LocationType.BORDER);
      boolean flag6 = chunkholder$locationtype1.isAtLeast(ChunkHolder.LocationType.BORDER);
      this.accessible |= flag6;
      if (!flag5 && flag6) {
         this.borderFuture = chunkManagerIn.func_222961_b(this);
         this.chain(this.borderFuture);
      }

      if (flag5 && !flag6) {
         CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> completablefuture1 = this.borderFuture;
         this.borderFuture = UNLOADED_CHUNK_FUTURE;
         this.chain(completablefuture1.thenApply((p_222982_1_) -> {
            return p_222982_1_.ifLeft(chunkManagerIn::func_222973_a);
         }));
      }

      boolean flag7 = chunkholder$locationtype.isAtLeast(ChunkHolder.LocationType.TICKING);
      boolean flag2 = chunkholder$locationtype1.isAtLeast(ChunkHolder.LocationType.TICKING);
      if (!flag7 && flag2) {
         this.tickingFuture = chunkManagerIn.func_219179_a(this);
         this.chain(this.tickingFuture);
      }

      if (flag7 && !flag2) {
         this.tickingFuture.complete(UNLOADED_CHUNK);
         this.tickingFuture = UNLOADED_CHUNK_FUTURE;
      }

      boolean flag3 = chunkholder$locationtype.isAtLeast(ChunkHolder.LocationType.ENTITY_TICKING);
      boolean flag4 = chunkholder$locationtype1.isAtLeast(ChunkHolder.LocationType.ENTITY_TICKING);
      if (!flag3 && flag4) {
         if (this.entityTickingFuture != UNLOADED_CHUNK_FUTURE) {
            throw (IllegalStateException)Util.pauseDevMode(new IllegalStateException());
         }

         this.entityTickingFuture = chunkManagerIn.func_219188_b(this.pos);
         this.chain(this.entityTickingFuture);
      }

      if (flag3 && !flag4) {
         this.entityTickingFuture.complete(UNLOADED_CHUNK);
         this.entityTickingFuture = UNLOADED_CHUNK_FUTURE;
      }

      this.field_219327_v.func_219066_a(this.pos, this::func_219281_j, this.chunkLevel, this::func_219275_d);
      this.prevChunkLevel = this.chunkLevel;
   }

   public static ChunkStatus getChunkStatusFromLevel(int level) {
      return level < 33 ? ChunkStatus.FULL : ChunkStatus.getStatus(level - 33);
   }

   public static ChunkHolder.LocationType getLocationTypeFromLevel(int level) {
      return LOCATION_TYPES[MathHelper.clamp(33 - level + 1, 0, LOCATION_TYPES.length - 1)];
   }

   public boolean isAccessible() {
      return this.accessible;
   }

   public void updateAccessible() {
      this.accessible = getLocationTypeFromLevel(this.chunkLevel).isAtLeast(ChunkHolder.LocationType.BORDER);
   }

   public void func_219294_a(ChunkPrimerWrapper p_219294_1_) {
      for(int i = 0; i < this.field_219312_g.length(); ++i) {
         CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = this.field_219312_g.get(i);
         if (completablefuture != null) {
            Optional<IChunk> optional = completablefuture.getNow(MISSING_CHUNK).left();
            if (optional.isPresent() && optional.get() instanceof ChunkPrimer) {
               this.field_219312_g.set(i, CompletableFuture.completedFuture(Either.left(p_219294_1_)));
            }
         }
      }

      this.chain(CompletableFuture.completedFuture(Either.left(p_219294_1_.func_217336_u())));
   }

   public interface IChunkLoadingError {
      ChunkHolder.IChunkLoadingError UNLOADED = new ChunkHolder.IChunkLoadingError() {
         public String toString() {
            return "UNLOADED";
         }
      };
   }

   public interface IListener {
      void func_219066_a(ChunkPos pos, IntSupplier p_219066_2_, int p_219066_3_, IntConsumer p_219066_4_);
   }

   public interface IPlayerProvider {
      /**
       * Returns the players tracking the given chunk.
       */
      Stream<ServerPlayerEntity> getTrackingPlayers(ChunkPos pos, boolean boundaryOnly);
   }

   public static enum LocationType {
      INACCESSIBLE,
      BORDER,
      TICKING,
      ENTITY_TICKING;

      public boolean isAtLeast(ChunkHolder.LocationType type) {
         return this.ordinal() >= type.ordinal();
      }
   }
}