package net.minecraft.world.lighting;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.mutable.MutableInt;

public abstract class LightEngine<M extends LightDataMap<M>, S extends SectionLightStorage<M>> extends LevelBasedGraph implements IWorldLightListener {
   private static final Direction[] DIRECTIONS = Direction.values();
   protected final IChunkLightProvider chunkProvider;
   protected final LightType type;
   protected final S storage;
   private boolean field_215629_e;
   protected final BlockPos.Mutable scratchPos = new BlockPos.Mutable();
   private final long[] recentPositions = new long[2];
   private final IBlockReader[] recentChunks = new IBlockReader[2];

   public LightEngine(IChunkLightProvider chunkLightProvider, LightType lightTypeIn, S storageIn) {
      super(16, 256, 8192);
      this.chunkProvider = chunkLightProvider;
      this.type = lightTypeIn;
      this.storage = storageIn;
      this.invalidateCaches();
   }

   protected void scheduleUpdate(long worldPos) {
      this.storage.processAllLevelUpdates();
      if (this.storage.hasSection(SectionPos.worldToSection(worldPos))) {
         super.scheduleUpdate(worldPos);
      }

   }

   @Nullable
   private IBlockReader getChunkReader(int chunkX, int chunkZ) {
      long i = ChunkPos.asLong(chunkX, chunkZ);

      for(int j = 0; j < 2; ++j) {
         if (i == this.recentPositions[j]) {
            return this.recentChunks[j];
         }
      }

      IBlockReader iblockreader = this.chunkProvider.getChunkForLight(chunkX, chunkZ);

      for(int k = 1; k > 0; --k) {
         this.recentPositions[k] = this.recentPositions[k - 1];
         this.recentChunks[k] = this.recentChunks[k - 1];
      }

      this.recentPositions[0] = i;
      this.recentChunks[0] = iblockreader;
      return iblockreader;
   }

   private void invalidateCaches() {
      Arrays.fill(this.recentPositions, ChunkPos.SENTINEL);
      Arrays.fill(this.recentChunks, (Object)null);
   }

   protected BlockState func_227468_a_(long p_227468_1_, @Nullable MutableInt p_227468_3_) {
      if (p_227468_1_ == Long.MAX_VALUE) {
         if (p_227468_3_ != null) {
            p_227468_3_.setValue(0);
         }

         return Blocks.AIR.getDefaultState();
      } else {
         int i = SectionPos.toChunk(BlockPos.unpackX(p_227468_1_));
         int j = SectionPos.toChunk(BlockPos.unpackZ(p_227468_1_));
         IBlockReader iblockreader = this.getChunkReader(i, j);
         if (iblockreader == null) {
            if (p_227468_3_ != null) {
               p_227468_3_.setValue(16);
            }

            return Blocks.BEDROCK.getDefaultState();
         } else {
            this.scratchPos.setPos(p_227468_1_);
            BlockState blockstate = iblockreader.getBlockState(this.scratchPos);
            boolean flag = blockstate.isSolid() && blockstate.isTransparent();
            if (p_227468_3_ != null) {
               p_227468_3_.setValue(blockstate.getOpacity(this.chunkProvider.getWorld(), this.scratchPos));
            }

            return flag ? blockstate : Blocks.AIR.getDefaultState();
         }
      }
   }

   protected VoxelShape getVoxelShape(BlockState blockStateIn, long worldPos, Direction directionIn) {
      return blockStateIn.isSolid() ? blockStateIn.getFaceOcclusionShape(this.chunkProvider.getWorld(), this.scratchPos.setPos(worldPos), directionIn) : VoxelShapes.empty();
   }

   public static int func_215613_a(IBlockReader p_215613_0_, BlockState p_215613_1_, BlockPos p_215613_2_, BlockState p_215613_3_, BlockPos p_215613_4_, Direction p_215613_5_, int p_215613_6_) {
      boolean flag = p_215613_1_.isSolid() && p_215613_1_.isTransparent();
      boolean flag1 = p_215613_3_.isSolid() && p_215613_3_.isTransparent();
      if (!flag && !flag1) {
         return p_215613_6_;
      } else {
         VoxelShape voxelshape = flag ? p_215613_1_.getRenderShape(p_215613_0_, p_215613_2_) : VoxelShapes.empty();
         VoxelShape voxelshape1 = flag1 ? p_215613_3_.getRenderShape(p_215613_0_, p_215613_4_) : VoxelShapes.empty();
         return VoxelShapes.doAdjacentCubeSidesFillSquare(voxelshape, voxelshape1, p_215613_5_) ? 16 : p_215613_6_;
      }
   }

   protected boolean isRoot(long pos) {
      return pos == Long.MAX_VALUE;
   }

   /**
    * Computes level propagated from neighbors of specified position with given existing level, excluding the given
    * source position.
    */
   protected int computeLevel(long pos, long excludedSourcePos, int level) {
      return 0;
   }

   protected int getLevel(long sectionPosIn) {
      return sectionPosIn == Long.MAX_VALUE ? 0 : 15 - this.storage.getLight(sectionPosIn);
   }

   protected int getLevelFromArray(NibbleArray array, long worldPos) {
      return 15 - array.get(SectionPos.mask(BlockPos.unpackX(worldPos)), SectionPos.mask(BlockPos.unpackY(worldPos)), SectionPos.mask(BlockPos.unpackZ(worldPos)));
   }

   protected void setLevel(long sectionPosIn, int level) {
      this.storage.setLight(sectionPosIn, Math.min(15, 15 - level));
   }

   /**
    * Returns level propagated from start position with specified level to the neighboring end position.
    */
   protected int getEdgeLevel(long startPos, long endPos, int startLevel) {
      return 0;
   }

   public boolean func_215619_a() {
      return this.needsUpdate() || this.storage.needsUpdate() || this.storage.hasSectionsToUpdate();
   }

   public int tick(int toUpdateCount, boolean updateSkyLight, boolean updateBlockLight) {
      if (!this.field_215629_e) {
         if (this.storage.needsUpdate()) {
            toUpdateCount = this.storage.processUpdates(toUpdateCount);
            if (toUpdateCount == 0) {
               return toUpdateCount;
            }
         }

         this.storage.updateSections(this, updateSkyLight, updateBlockLight);
      }

      this.field_215629_e = true;
      if (this.needsUpdate()) {
         toUpdateCount = this.processUpdates(toUpdateCount);
         this.invalidateCaches();
         if (toUpdateCount == 0) {
            return toUpdateCount;
         }
      }

      this.field_215629_e = false;
      this.storage.updateAndNotify();
      return toUpdateCount;
   }

   protected void setData(long sectionPosIn, @Nullable NibbleArray array) {
      this.storage.setData(sectionPosIn, array);
   }

   @Nullable
   public NibbleArray getData(SectionPos p_215612_1_) {
      return this.storage.getArray(p_215612_1_.asLong());
   }

   public int getLightFor(BlockPos worldPos) {
      return this.storage.getLightOrDefault(worldPos.toLong());
   }

   @OnlyIn(Dist.CLIENT)
   public String getDebugString(long sectionPosIn) {
      return "" + this.storage.getLevel(sectionPosIn);
   }

   public void checkLight(BlockPos worldPos) {
      long i = worldPos.toLong();
      this.scheduleUpdate(i);

      for(Direction direction : DIRECTIONS) {
         this.scheduleUpdate(BlockPos.offset(i, direction));
      }

   }

   public void func_215623_a(BlockPos p_215623_1_, int p_215623_2_) {
   }

   public void updateSectionStatus(SectionPos pos, boolean isEmpty) {
      this.storage.updateSectionStatus(pos.asLong(), isEmpty);
   }

   public void func_215620_a(ChunkPos p_215620_1_, boolean p_215620_2_) {
      long i = SectionPos.toSectionColumnPos(SectionPos.asLong(p_215620_1_.x, 0, p_215620_1_.z));
      this.storage.func_215526_b(i, p_215620_2_);
   }

   public void retainChunkData(ChunkPos pos, boolean retain) {
      long i = SectionPos.toSectionColumnPos(SectionPos.asLong(pos.x, 0, pos.z));
      this.storage.retainChunkData(i, retain);
   }

   // FORGE START

   public abstract int queuedUpdateSize();
}