package net.minecraft.client.renderer;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.VertexBuilderUtils;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemRenderer implements IResourceManagerReloadListener {
   public static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
   private static final Set<Item> ITEM_MODEL_BLACKLIST = Sets.newHashSet(Items.AIR);
   public float zLevel;
   private final ItemModelMesher itemModelMesher;
   private final TextureManager textureManager;
   private final ItemColors itemColors;

   public ItemRenderer(TextureManager textureManagerIn, ModelManager modelManagerIn, ItemColors itemColorsIn) {
      this.textureManager = textureManagerIn;
      this.itemModelMesher = new net.minecraftforge.client.ItemModelMesherForge(modelManagerIn);

      for(Item item : Registry.ITEM) {
         if (!ITEM_MODEL_BLACKLIST.contains(item)) {
            this.itemModelMesher.register(item, new ModelResourceLocation(Registry.ITEM.getKey(item), "inventory"));
         }
      }

      this.itemColors = itemColorsIn;
   }

   public ItemModelMesher getItemModelMesher() {
      return this.itemModelMesher;
   }

   private void renderModel(IBakedModel modelIn, ItemStack stack, int combinedLightIn, int combinedOverlayIn, MatrixStack matrixStackIn, IVertexBuilder bufferIn) {
      Random random = new Random();
      long i = 42L;

      for(Direction direction : Direction.values()) {
         random.setSeed(42L);
         this.renderQuads(matrixStackIn, bufferIn, modelIn.getQuads((BlockState)null, direction, random), stack, combinedLightIn, combinedOverlayIn);
      }

      random.setSeed(42L);
      this.renderQuads(matrixStackIn, bufferIn, modelIn.getQuads((BlockState)null, (Direction)null, random), stack, combinedLightIn, combinedOverlayIn);
   }

   public void renderItem(ItemStack itemStackIn, ItemCameraTransforms.TransformType transformTypeIn, boolean leftHand, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn, IBakedModel modelIn) {
      if (!itemStackIn.isEmpty()) {
         matrixStackIn.push();
         boolean flag = transformTypeIn == ItemCameraTransforms.TransformType.GUI;
         boolean flag1 = flag || transformTypeIn == ItemCameraTransforms.TransformType.GROUND || transformTypeIn == ItemCameraTransforms.TransformType.FIXED;
         if (itemStackIn.getItem() == Items.TRIDENT && flag1) {
            modelIn = this.itemModelMesher.getModelManager().getModel(new ModelResourceLocation("minecraft:trident#inventory"));
         }

         modelIn = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(matrixStackIn, modelIn, transformTypeIn, leftHand);
         matrixStackIn.translate(-0.5D, -0.5D, -0.5D);
         if (!modelIn.isBuiltInRenderer() && (itemStackIn.getItem() != Items.TRIDENT || flag1)) {
            RenderType rendertype = RenderTypeLookup.getRenderType(itemStackIn);
            RenderType rendertype1;
            if (flag && Objects.equals(rendertype, Atlases.getTranslucentBlockType())) {
               rendertype1 = Atlases.getTranslucentCullBlockType();
            } else {
               rendertype1 = rendertype;
            }

            IVertexBuilder ivertexbuilder = getBuffer(bufferIn, rendertype1, true, itemStackIn.hasEffect());
            this.renderModel(modelIn, itemStackIn, combinedLightIn, combinedOverlayIn, matrixStackIn, ivertexbuilder);
         } else {
            itemStackIn.getItem().getItemStackTileEntityRenderer().render(itemStackIn, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
         }

         matrixStackIn.pop();
      }
   }

   public static IVertexBuilder getBuffer(IRenderTypeBuffer bufferIn, RenderType renderTypeIn, boolean isItemIn, boolean glintIn) {
      return glintIn ? VertexBuilderUtils.newDelegate(bufferIn.getBuffer(isItemIn ? RenderType.getGlint() : RenderType.getEntityGlint()), bufferIn.getBuffer(renderTypeIn)) : bufferIn.getBuffer(renderTypeIn);
   }

   public void renderQuads(MatrixStack matrixStackIn, IVertexBuilder bufferIn, List<BakedQuad> quadsIn, ItemStack itemStackIn, int combinedLightIn, int combinedOverlayIn) {
      boolean flag = !itemStackIn.isEmpty();
      MatrixStack.Entry matrixstack$entry = matrixStackIn.getLast();

      for(BakedQuad bakedquad : quadsIn) {
         int i = -1;
         if (flag && bakedquad.hasTintIndex()) {
            i = this.itemColors.getColor(itemStackIn, bakedquad.getTintIndex());
         }

         float f = (float)(i >> 16 & 255) / 255.0F;
         float f1 = (float)(i >> 8 & 255) / 255.0F;
         float f2 = (float)(i & 255) / 255.0F;
         bufferIn.addVertexData(matrixstack$entry, bakedquad, f, f1, f2, combinedLightIn, combinedOverlayIn, true);
      }

   }

   public IBakedModel getItemModelWithOverrides(ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entitylivingbaseIn) {
      Item item = stack.getItem();
      IBakedModel ibakedmodel;
      if (item == Items.TRIDENT) {
         ibakedmodel = this.itemModelMesher.getModelManager().getModel(new ModelResourceLocation("minecraft:trident_in_hand#inventory"));
      } else {
         ibakedmodel = this.itemModelMesher.getItemModel(stack);
      }

      return !item.hasCustomProperties() ? ibakedmodel : this.getModelWithOverrides(ibakedmodel, stack, worldIn, entitylivingbaseIn);
   }

   private IBakedModel getModelWithOverrides(IBakedModel model, ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn) {
      IBakedModel ibakedmodel = model.getOverrides().getModelWithOverrides(model, stack, worldIn, entityIn);
      return ibakedmodel == null ? this.itemModelMesher.getModelManager().getMissingModel() : ibakedmodel;
   }

   public void renderItem(ItemStack itemStackIn, ItemCameraTransforms.TransformType transformTypeIn, int combinedLightIn, int combinedOverlayIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn) {
      this.renderItem((LivingEntity)null, itemStackIn, transformTypeIn, false, matrixStackIn, bufferIn, (World)null, combinedLightIn, combinedOverlayIn);
   }

   public void renderItem(@Nullable LivingEntity livingEntityIn, ItemStack itemStackIn, ItemCameraTransforms.TransformType transformTypeIn, boolean leftHand, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, @Nullable World worldIn, int combinedLightIn, int combinedOverlayIn) {
      if (!itemStackIn.isEmpty()) {
         IBakedModel ibakedmodel = this.getItemModelWithOverrides(itemStackIn, worldIn, livingEntityIn);
         this.renderItem(itemStackIn, transformTypeIn, leftHand, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, ibakedmodel);
      }
   }

   public void renderItemIntoGUI(ItemStack stack, int x, int y) {
      this.renderItemModelIntoGUI(stack, x, y, this.getItemModelWithOverrides(stack, (World)null, (LivingEntity)null));
   }

   protected void renderItemModelIntoGUI(ItemStack stack, int x, int y, IBakedModel bakedmodel) {
      RenderSystem.pushMatrix();
      this.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
      this.textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmapDirect(false, false);
      RenderSystem.enableRescaleNormal();
      RenderSystem.enableAlphaTest();
      RenderSystem.defaultAlphaFunc();
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.translatef((float)x, (float)y, 100.0F + this.zLevel);
      RenderSystem.translatef(8.0F, 8.0F, 0.0F);
      RenderSystem.scalef(1.0F, -1.0F, 1.0F);
      RenderSystem.scalef(16.0F, 16.0F, 16.0F);
      MatrixStack matrixstack = new MatrixStack();
      IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
      boolean flag = !bakedmodel.func_230044_c_();
      if (flag) {
         RenderHelper.setupGuiFlatDiffuseLighting();
      }

      this.renderItem(stack, ItemCameraTransforms.TransformType.GUI, false, matrixstack, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, bakedmodel);
      irendertypebuffer$impl.finish();
      RenderSystem.enableDepthTest();
      if (flag) {
         RenderHelper.setupGui3DDiffuseLighting();
      }

      RenderSystem.disableAlphaTest();
      RenderSystem.disableRescaleNormal();
      RenderSystem.popMatrix();
   }

   public void renderItemAndEffectIntoGUI(ItemStack stack, int xPosition, int yPosition) {
      this.renderItemAndEffectIntoGUI(Minecraft.getInstance().player, stack, xPosition, yPosition);
   }

   public void renderItemAndEffectIntoGUI(@Nullable LivingEntity entityIn, ItemStack itemIn, int x, int y) {
      if (!itemIn.isEmpty()) {
         this.zLevel += 50.0F;

         try {
            this.renderItemModelIntoGUI(itemIn, x, y, this.getItemModelWithOverrides(itemIn, (World)null, entityIn));
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering item");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being rendered");
            crashreportcategory.addDetail("Item Type", () -> {
               return String.valueOf((Object)itemIn.getItem());
            });
            crashreportcategory.addDetail("Registry Name", () -> String.valueOf(itemIn.getItem().getRegistryName()));
            crashreportcategory.addDetail("Item Damage", () -> {
               return String.valueOf(itemIn.getDamage());
            });
            crashreportcategory.addDetail("Item NBT", () -> {
               return String.valueOf((Object)itemIn.getTag());
            });
            crashreportcategory.addDetail("Item Foil", () -> {
               return String.valueOf(itemIn.hasEffect());
            });
            throw new ReportedException(crashreport);
         }

         this.zLevel -= 50.0F;
      }
   }

   public void renderItemOverlays(FontRenderer fr, ItemStack stack, int xPosition, int yPosition) {
      this.renderItemOverlayIntoGUI(fr, stack, xPosition, yPosition, (String)null);
   }

   /**
    * Renders the stack size and/or damage bar for the given ItemStack.
    */
   public void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, @Nullable String text) {
      if (!stack.isEmpty()) {
         MatrixStack matrixstack = new MatrixStack();
         if (stack.getCount() != 1 || text != null) {
            String s = text == null ? String.valueOf(stack.getCount()) : text;
            matrixstack.translate(0.0D, 0.0D, (double)(this.zLevel + 200.0F));
            IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
            fr.renderString(s, (float)(xPosition + 19 - 2 - fr.getStringWidth(s)), (float)(yPosition + 6 + 3), 16777215, true, matrixstack.getLast().getMatrix(), irendertypebuffer$impl, false, 0, 15728880);
            irendertypebuffer$impl.finish();
         }

         if (stack.getItem().showDurabilityBar(stack)) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.disableAlphaTest();
            RenderSystem.disableBlend();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            double health = stack.getItem().getDurabilityForDisplay(stack);
            int i = Math.round(13.0F - (float)health * 13.0F);
            int j = stack.getItem().getRGBDurabilityForDisplay(stack);
            this.draw(bufferbuilder, xPosition + 2, yPosition + 13, 13, 2, 0, 0, 0, 255);
            this.draw(bufferbuilder, xPosition + 2, yPosition + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
            RenderSystem.enableBlend();
            RenderSystem.enableAlphaTest();
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
         }

         ClientPlayerEntity clientplayerentity = Minecraft.getInstance().player;
         float f3 = clientplayerentity == null ? 0.0F : clientplayerentity.getCooldownTracker().getCooldown(stack.getItem(), Minecraft.getInstance().getRenderPartialTicks());
         if (f3 > 0.0F) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            Tessellator tessellator1 = Tessellator.getInstance();
            BufferBuilder bufferbuilder1 = tessellator1.getBuffer();
            this.draw(bufferbuilder1, xPosition, yPosition + MathHelper.floor(16.0F * (1.0F - f3)), 16, MathHelper.ceil(16.0F * f3), 255, 255, 255, 127);
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
         }

      }
   }

   /**
    * Draw with the WorldRenderer
    */
   private void draw(BufferBuilder renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
      renderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      renderer.pos((double)(x + 0), (double)(y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
      renderer.pos((double)(x + 0), (double)(y + height), 0.0D).color(red, green, blue, alpha).endVertex();
      renderer.pos((double)(x + width), (double)(y + height), 0.0D).color(red, green, blue, alpha).endVertex();
      renderer.pos((double)(x + width), (double)(y + 0), 0.0D).color(red, green, blue, alpha).endVertex();
      Tessellator.getInstance().draw();
   }

   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.itemModelMesher.rebuildCache();
   }

   @Override
   public net.minecraftforge.resource.IResourceType getResourceType() {
      return net.minecraftforge.resource.VanillaResourceType.MODELS;
   }
}