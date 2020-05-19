package net.minecraft.world.lighting;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WorldLightManager implements ILightListener {
   @Nullable
   private final LightEngine<?, ?> blockLight;
   @Nullable
   private final LightEngine<?, ?> skyLight;

   public WorldLightManager(IChunkLightProvider provider, boolean hasBlockLight, boolean hasSkyLight) {
      this.blockLight = hasBlockLight ? new BlockLightEngine(provider) : null;
      this.skyLight = hasSkyLight ? new SkyLightEngine(provider) : null;
   }

   public void checkBlock(BlockPos blockPosIn) {
      if (this.blockLight != null) {
         this.blockLight.checkLight(blockPosIn);
      }

      if (this.skyLight != null) {
         this.skyLight.checkLight(blockPosIn);
      }

   }

   public void onBlockEmissionIncrease(BlockPos blockPosIn, int p_215573_2_) {
      if (this.blockLight != null) {
         this.blockLight.func_215623_a(blockPosIn, p_215573_2_);
      }

   }

   public boolean hasLightWork() {
      if (this.skyLight != null && this.skyLight.func_215619_a()) {
         return true;
      } else {
         return this.blockLight != null && this.blockLight.func_215619_a();
      }
   }

   public int tick(int toUpdateCount, boolean updateSkyLight, boolean updateBlockLight) {
      if (this.blockLight != null && this.skyLight != null) {
         int i = toUpdateCount / 2;
         int j = this.blockLight.tick(i, updateSkyLight, updateBlockLight);
         int k = toUpdateCount - i + j;
         int l = this.skyLight.tick(k, updateSkyLight, updateBlockLight);
         return j == 0 && l > 0 ? this.blockLight.tick(l, updateSkyLight, updateBlockLight) : l;
      } else if (this.blockLight != null) {
         return this.blockLight.tick(toUpdateCount, updateSkyLight, updateBlockLight);
      } else {
         return this.skyLight != null ? this.skyLight.tick(toUpdateCount, updateSkyLight, updateBlockLight) : toUpdateCount;
      }
   }

   public void updateSectionStatus(SectionPos pos, boolean isEmpty) {
      if (this.blockLight != null) {
         this.blockLight.updateSectionStatus(pos, isEmpty);
      }

      if (this.skyLight != null) {
         this.skyLight.updateSectionStatus(pos, isEmpty);
      }

   }

   public void enableLightSources(ChunkPos p_215571_1_, boolean p_215571_2_) {
      if (this.blockLight != null) {
         this.blockLight.func_215620_a(p_215571_1_, p_215571_2_);
      }

      if (this.skyLight != null) {
         this.skyLight.func_215620_a(p_215571_1_, p_215571_2_);
      }

   }

   public IWorldLightListener getLightEngine(LightType type) {
      if (type == LightType.BLOCK) {
         return (IWorldLightListener)(this.blockLight == null ? IWorldLightListener.Dummy.INSTANCE : this.blockLight);
      } else {
         return (IWorldLightListener)(this.skyLight == null ? IWorldLightListener.Dummy.INSTANCE : this.skyLight);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public String getDebugInfo(LightType p_215572_1_, SectionPos p_215572_2_) {
      if (p_215572_1_ == LightType.BLOCK) {
         if (this.blockLight != null) {
            return this.blockLight.getDebugString(p_215572_2_.asLong());
         }
      } else if (this.skyLight != null) {
         return this.skyLight.getDebugString(p_215572_2_.asLong());
      }

      return "n/a";
   }

   public void setData(LightType type, SectionPos pos, @Nullable NibbleArray array) {
      if (type == LightType.BLOCK) {
         if (this.blockLight != null) {
            this.blockLight.setData(pos.asLong(), array);
         }
      } else if (this.skyLight != null) {
         this.skyLight.setData(pos.asLong(), array);
      }

   }

   public void retainData(ChunkPos pos, boolean retain) {
      if (this.blockLight != null) {
         this.blockLight.retainChunkData(pos, retain);
      }

      if (this.skyLight != null) {
         this.skyLight.retainChunkData(pos, retain);
      }

   }

   public int getLightSubtracted(BlockPos blockPosIn, int amount) {
      int i = this.skyLight == null ? 0 : this.skyLight.getLightFor(blockPosIn) - amount;
      int j = this.blockLight == null ? 0 : this.blockLight.getLightFor(blockPosIn);
      return Math.max(j, i);
   }
}