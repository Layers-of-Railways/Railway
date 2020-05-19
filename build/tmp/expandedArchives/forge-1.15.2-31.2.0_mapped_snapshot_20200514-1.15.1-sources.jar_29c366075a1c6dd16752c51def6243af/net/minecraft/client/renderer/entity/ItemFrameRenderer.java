package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemFrameRenderer extends EntityRenderer<ItemFrameEntity> {
   private static final ModelResourceLocation LOCATION_MODEL = new ModelResourceLocation("item_frame", "map=false");
   private static final ModelResourceLocation LOCATION_MODEL_MAP = new ModelResourceLocation("item_frame", "map=true");
   private final Minecraft mc = Minecraft.getInstance();
   private final net.minecraft.client.renderer.ItemRenderer itemRenderer;

   public ItemFrameRenderer(EntityRendererManager renderManagerIn, net.minecraft.client.renderer.ItemRenderer itemRendererIn) {
      super(renderManagerIn);
      this.itemRenderer = itemRendererIn;
   }

   public void render(ItemFrameEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
      super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
      matrixStackIn.push();
      Direction direction = entityIn.getHorizontalFacing();
      Vec3d vec3d = this.getRenderOffset(entityIn, partialTicks);
      matrixStackIn.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
      double d0 = 0.46875D;
      matrixStackIn.translate((double)direction.getXOffset() * 0.46875D, (double)direction.getYOffset() * 0.46875D, (double)direction.getZOffset() * 0.46875D);
      matrixStackIn.rotate(Vector3f.XP.rotationDegrees(entityIn.rotationPitch));
      matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0F - entityIn.rotationYaw));
      BlockRendererDispatcher blockrendererdispatcher = this.mc.getBlockRendererDispatcher();
      ModelManager modelmanager = blockrendererdispatcher.getBlockModelShapes().getModelManager();
      ModelResourceLocation modelresourcelocation = entityIn.getDisplayedItem().getItem() instanceof FilledMapItem ? LOCATION_MODEL_MAP : LOCATION_MODEL;
      matrixStackIn.push();
      matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
      blockrendererdispatcher.getBlockModelRenderer().renderModelBrightnessColor(matrixStackIn.getLast(), bufferIn.getBuffer(Atlases.getSolidBlockType()), (BlockState)null, modelmanager.getModel(modelresourcelocation), 1.0F, 1.0F, 1.0F, packedLightIn, OverlayTexture.NO_OVERLAY);
      matrixStackIn.pop();
      ItemStack itemstack = entityIn.getDisplayedItem();
      if (!itemstack.isEmpty()) {
         MapData mapdata = FilledMapItem.getMapData(itemstack, entityIn.world);
         matrixStackIn.translate(0.0D, 0.0D, 0.4375D);
         int i = mapdata != null ? entityIn.getRotation() % 4 * 2 : entityIn.getRotation();
         matrixStackIn.rotate(Vector3f.ZP.rotationDegrees((float)i * 360.0F / 8.0F));
         if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderItemInFrameEvent(entityIn, this, matrixStackIn, bufferIn, packedLightIn))) {
         if (mapdata != null) {
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(180.0F));
            float f = 0.0078125F;
            matrixStackIn.scale(0.0078125F, 0.0078125F, 0.0078125F);
            matrixStackIn.translate(-64.0D, -64.0D, 0.0D);
            matrixStackIn.translate(0.0D, 0.0D, -1.0D);
            if (mapdata != null) {
               this.mc.gameRenderer.getMapItemRenderer().renderMap(matrixStackIn, bufferIn, mapdata, true, packedLightIn);
            }
         } else {
            matrixStackIn.scale(0.5F, 0.5F, 0.5F);
            this.itemRenderer.renderItem(itemstack, ItemCameraTransforms.TransformType.FIXED, packedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
         }
         }
      }

      matrixStackIn.pop();
   }

   public Vec3d getRenderOffset(ItemFrameEntity entityIn, float partialTicks) {
      return new Vec3d((double)((float)entityIn.getHorizontalFacing().getXOffset() * 0.3F), -0.25D, (double)((float)entityIn.getHorizontalFacing().getZOffset() * 0.3F));
   }

   /**
    * Returns the location of an entity's texture.
    */
   public ResourceLocation getEntityTexture(ItemFrameEntity entity) {
      return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
   }

   protected boolean canRenderName(ItemFrameEntity entity) {
      if (Minecraft.isGuiEnabled() && !entity.getDisplayedItem().isEmpty() && entity.getDisplayedItem().hasDisplayName() && this.renderManager.pointedEntity == entity) {
         double d0 = this.renderManager.squareDistanceTo(entity);
         float f = entity.isDiscrete() ? 32.0F : 64.0F;
         return d0 < (double)(f * f);
      } else {
         return false;
      }
   }

   protected void renderName(ItemFrameEntity entityIn, String displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
      super.renderName(entityIn, entityIn.getDisplayedItem().getDisplayName().getFormattedText(), matrixStackIn, bufferIn, packedLightIn);
   }
}