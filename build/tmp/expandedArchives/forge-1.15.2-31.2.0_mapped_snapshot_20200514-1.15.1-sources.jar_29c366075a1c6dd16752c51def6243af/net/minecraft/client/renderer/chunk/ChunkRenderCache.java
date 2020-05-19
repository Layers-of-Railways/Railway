package net.minecraft.client.renderer.chunk;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChunkRenderCache implements ILightReader {
   protected final int chunkStartX;
   protected final int chunkStartZ;
   protected final BlockPos cacheStartPos;
   protected final int cacheSizeX;
   protected final int cacheSizeY;
   protected final int cacheSizeZ;
   protected final Chunk[][] chunks;
   protected final BlockState[] blockStates;
   protected final IFluidState[] fluidStates;
   protected final World world;

   /**
    * generates a RenderChunkCache, but returns null if the chunk is empty (contains only air)
    */
   @Nullable
   public static ChunkRenderCache generateCache(World worldIn, BlockPos from, BlockPos to, int padding) {
      int i = from.getX() - padding >> 4;
      int j = from.getZ() - padding >> 4;
      int k = to.getX() + padding >> 4;
      int l = to.getZ() + padding >> 4;
      Chunk[][] achunk = new Chunk[k - i + 1][l - j + 1];

      for(int i1 = i; i1 <= k; ++i1) {
         for(int j1 = j; j1 <= l; ++j1) {
            achunk[i1 - i][j1 - j] = worldIn.getChunk(i1, j1);
         }
      }

      boolean flag = true;

      for(int l1 = from.getX() >> 4; l1 <= to.getX() >> 4; ++l1) {
         for(int k1 = from.getZ() >> 4; k1 <= to.getZ() >> 4; ++k1) {
            Chunk chunk = achunk[l1 - i][k1 - j];
            if (!chunk.isEmptyBetween(from.getY(), to.getY())) {
               flag = false;
            }
         }
      }

      if (flag) {
         return null;
      } else {
         int i2 = 1;
         BlockPos blockpos = from.add(-1, -1, -1);
         BlockPos blockpos1 = to.add(1, 1, 1);
         return new ChunkRenderCache(worldIn, i, j, achunk, blockpos, blockpos1);
      }
   }

   public ChunkRenderCache(World worldIn, int chunkStartXIn, int chunkStartZIn, Chunk[][] chunksIn, BlockPos startPos, BlockPos endPos) {
      this.world = worldIn;
      this.chunkStartX = chunkStartXIn;
      this.chunkStartZ = chunkStartZIn;
      this.chunks = chunksIn;
      this.cacheStartPos = startPos;
      this.cacheSizeX = endPos.getX() - startPos.getX() + 1;
      this.cacheSizeY = endPos.getY() - startPos.getY() + 1;
      this.cacheSizeZ = endPos.getZ() - startPos.getZ() + 1;
      this.blockStates = new BlockState[this.cacheSizeX * this.cacheSizeY * this.cacheSizeZ];
      this.fluidStates = new IFluidState[this.cacheSizeX * this.cacheSizeY * this.cacheSizeZ];

      for(BlockPos blockpos : BlockPos.getAllInBoxMutable(startPos, endPos)) {
         int i = (blockpos.getX() >> 4) - chunkStartXIn;
         int j = (blockpos.getZ() >> 4) - chunkStartZIn;
         Chunk chunk = chunksIn[i][j];
         int k = this.getIndex(blockpos);
         this.blockStates[k] = chunk.getBlockState(blockpos);
         this.fluidStates[k] = chunk.getFluidState(blockpos);
      }

   }

   protected final int getIndex(BlockPos pos) {
      return this.getIndex(pos.getX(), pos.getY(), pos.getZ());
   }

   protected int getIndex(int xIn, int yIn, int zIn) {
      int i = xIn - this.cacheStartPos.getX();
      int j = yIn - this.cacheStartPos.getY();
      int k = zIn - this.cacheStartPos.getZ();
      return k * this.cacheSizeX * this.cacheSizeY + j * this.cacheSizeX + i;
   }

   public BlockState getBlockState(BlockPos pos) {
      return this.blockStates[this.getIndex(pos)];
   }

   public IFluidState getFluidState(BlockPos pos) {
      return this.fluidStates[this.getIndex(pos)];
   }

   public WorldLightManager getLightManager() {
      return this.world.getLightManager();
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos pos) {
      return this.getTileEntity(pos, Chunk.CreateEntityType.IMMEDIATE);
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos pos, Chunk.CreateEntityType creationType) {
      int i = (pos.getX() >> 4) - this.chunkStartX;
      int j = (pos.getZ() >> 4) - this.chunkStartZ;
      return this.chunks[i][j].getTileEntity(pos, creationType);
   }

   public int getBlockColor(BlockPos blockPosIn, ColorResolver colorResolverIn) {
      return this.world.getBlockColor(blockPosIn, colorResolverIn);
   }
}