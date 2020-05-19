package net.minecraft.client.renderer.model;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.SpriteMap;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.fluid.IFluidState;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelManager extends ReloadListener<ModelBakery> implements AutoCloseable {
   private Map<ResourceLocation, IBakedModel> modelRegistry = new java.util.HashMap<>();
   private SpriteMap atlases;
   private final BlockModelShapes modelProvider;
   private final TextureManager textureManager;
   private final BlockColors blockColors;
   private int maxMipmapLevel;
   private IBakedModel defaultModel;
   private Object2IntMap<BlockState> stateModelIds;

   public ModelManager(TextureManager textureManagerIn, BlockColors blockColorsIn, int maxMipmapLevelIn) {
      this.textureManager = textureManagerIn;
      this.blockColors = blockColorsIn;
      this.maxMipmapLevel = maxMipmapLevelIn;
      this.modelProvider = new BlockModelShapes(this);
   }

   public IBakedModel getModel(ResourceLocation modelLocation) {
      return this.modelRegistry.getOrDefault(modelLocation, this.defaultModel);
   }

   public IBakedModel getModel(ModelResourceLocation modelLocation) {
      return this.modelRegistry.getOrDefault(modelLocation, this.defaultModel);
   }

   public IBakedModel getMissingModel() {
      return this.defaultModel;
   }

   public BlockModelShapes getBlockModelShapes() {
      return this.modelProvider;
   }

   /**
    * Performs any reloading that can be done off-thread, such as file IO
    */
   protected ModelBakery prepare(IResourceManager resourceManagerIn, IProfiler profilerIn) {
      profilerIn.startTick();
      net.minecraftforge.client.model.ModelLoader modelbakery = new net.minecraftforge.client.model.ModelLoader(resourceManagerIn, this.blockColors, profilerIn, this.maxMipmapLevel);
      profilerIn.endTick();
      return modelbakery;
   }

   protected void apply(ModelBakery objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
      profilerIn.startTick();
      profilerIn.startSection("upload");
      if (this.atlases != null) {
         this.atlases.close();
      }

      this.atlases = objectIn.uploadTextures(this.textureManager, profilerIn);
      this.modelRegistry = objectIn.getTopBakedModels();
      this.stateModelIds = objectIn.getStateModelIds();
      this.defaultModel = this.modelRegistry.get(ModelBakery.MODEL_MISSING);
      net.minecraftforge.client.ForgeHooksClient.onModelBake(this, this.modelRegistry, (net.minecraftforge.client.model.ModelLoader) objectIn);
      profilerIn.endStartSection("cache");
      this.modelProvider.reloadModels();
      profilerIn.endSection();
      profilerIn.endTick();
   }

   public boolean needsRenderUpdate(BlockState oldState, BlockState newState) {
      if (oldState == newState) {
         return false;
      } else {
         int i = this.stateModelIds.getInt(oldState);
         if (i != -1) {
            int j = this.stateModelIds.getInt(newState);
            if (i == j) {
               IFluidState ifluidstate = oldState.getFluidState();
               IFluidState ifluidstate1 = newState.getFluidState();
               return ifluidstate != ifluidstate1;
            }
         }

         return true;
      }
   }

   public AtlasTexture getAtlasTexture(ResourceLocation locationIn) {
      if (this.atlases == null) throw new RuntimeException("getAtlasTexture called too early!");
      return this.atlases.getAtlasTexture(locationIn);
   }

   public void close() {
      this.atlases.close();
   }

   public void setMaxMipmapLevel(int levelIn) {
      this.maxMipmapLevel = levelIn;
   }

   // TODO
   //@Override
   public net.minecraftforge.resource.IResourceType getResourceType() {
      return net.minecraftforge.resource.VanillaResourceType.MODELS;
   }
}