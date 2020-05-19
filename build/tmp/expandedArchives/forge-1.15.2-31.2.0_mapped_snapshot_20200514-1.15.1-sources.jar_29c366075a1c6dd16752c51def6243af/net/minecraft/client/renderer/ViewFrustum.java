package net.minecraft.client.renderer;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.ChunkRender;


@OnlyIn(Dist.CLIENT)
public class ViewFrustum {
   protected final WorldRenderer renderGlobal;
   protected final World world;
   protected int countChunksY;
   protected int countChunksX;
   protected int countChunksZ;
   public ChunkRenderDispatcher.ChunkRender[] renderChunks;

   public ViewFrustum(ChunkRenderDispatcher renderDispatcherIn, World worldIn, int countChunksIn, WorldRenderer renderGlobalIn) {
      this.renderGlobal = renderGlobalIn;
      this.world = worldIn;
      this.setCountChunksXYZ(countChunksIn);
      this.createRenderChunks(renderDispatcherIn);
   }

   protected void createRenderChunks(ChunkRenderDispatcher renderChunkFactory) {
      int i = this.countChunksX * this.countChunksY * this.countChunksZ;
      this.renderChunks = new ChunkRenderDispatcher.ChunkRender[i];

      for(int j = 0; j < this.countChunksX; ++j) {
         for(int k = 0; k < this.countChunksY; ++k) {
            for(int l = 0; l < this.countChunksZ; ++l) {
               int i1 = this.getIndex(j, k, l);
               this.renderChunks[i1] = renderChunkFactory.new ChunkRender();
               this.renderChunks[i1].setPosition(j * 16, k * 16, l * 16);
            }
         }
      }

   }

   public void deleteGlResources() {
      for(ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender : this.renderChunks) {
         chunkrenderdispatcher$chunkrender.deleteGlResources();
      }

   }

   private int getIndex(int x, int y, int z) {
      return (z * this.countChunksY + y) * this.countChunksX + x;
   }

   protected void setCountChunksXYZ(int renderDistanceChunks) {
      int i = renderDistanceChunks * 2 + 1;
      this.countChunksX = i;
      this.countChunksY = 16;
      this.countChunksZ = i;
   }

   public void updateChunkPositions(double viewEntityX, double viewEntityZ) {
      int i = MathHelper.floor(viewEntityX);
      int j = MathHelper.floor(viewEntityZ);

      for(int k = 0; k < this.countChunksX; ++k) {
         int l = this.countChunksX * 16;
         int i1 = i - 8 - l / 2;
         int j1 = i1 + Math.floorMod(k * 16 - i1, l);

         for(int k1 = 0; k1 < this.countChunksZ; ++k1) {
            int l1 = this.countChunksZ * 16;
            int i2 = j - 8 - l1 / 2;
            int j2 = i2 + Math.floorMod(k1 * 16 - i2, l1);

            for(int k2 = 0; k2 < this.countChunksY; ++k2) {
               int l2 = k2 * 16;
               ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender = this.renderChunks[this.getIndex(k, k2, k1)];
               chunkrenderdispatcher$chunkrender.setPosition(j1, l2, j2);
            }
         }
      }

   }

   public void markForRerender(int sectionX, int sectionY, int sectionZ, boolean rerenderOnMainThread) {
      int i = Math.floorMod(sectionX, this.countChunksX);
      int j = Math.floorMod(sectionY, this.countChunksY);
      int k = Math.floorMod(sectionZ, this.countChunksZ);
      ChunkRenderDispatcher.ChunkRender chunkrenderdispatcher$chunkrender = this.renderChunks[this.getIndex(i, j, k)];
      chunkrenderdispatcher$chunkrender.setNeedsUpdate(rerenderOnMainThread);
   }

   @Nullable
   protected ChunkRenderDispatcher.ChunkRender getRenderChunk(BlockPos pos) {
      int i = MathHelper.intFloorDiv(pos.getX(), 16);
      int j = MathHelper.intFloorDiv(pos.getY(), 16);
      int k = MathHelper.intFloorDiv(pos.getZ(), 16);
      if (j >= 0 && j < this.countChunksY) {
         i = MathHelper.normalizeAngle(i, this.countChunksX);
         k = MathHelper.normalizeAngle(k, this.countChunksZ);
         return this.renderChunks[this.getIndex(i, j, k)];
      } else {
         return null;
      }
   }
}