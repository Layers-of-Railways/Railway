package net.minecraft.client.renderer.entity.layers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ArmorLayer<T extends LivingEntity, M extends BipedModel<T>, A extends BipedModel<T>> extends LayerRenderer<T, M> {
   protected final A modelLeggings;
   protected final A modelArmor;
   private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();

   protected ArmorLayer(IEntityRenderer<T, M> entityRendererIn, A modelLeggingsIn, A modelArmorIn) {
      super(entityRendererIn);
      this.modelLeggings = modelLeggingsIn;
      this.modelArmor = modelArmorIn;
   }

   public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      this.renderArmorPart(matrixStackIn, bufferIn, entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, EquipmentSlotType.CHEST, packedLightIn);
      this.renderArmorPart(matrixStackIn, bufferIn, entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, EquipmentSlotType.LEGS, packedLightIn);
      this.renderArmorPart(matrixStackIn, bufferIn, entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, EquipmentSlotType.FEET, packedLightIn);
      this.renderArmorPart(matrixStackIn, bufferIn, entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, EquipmentSlotType.HEAD, packedLightIn);
   }

   private void renderArmorPart(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, T entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, EquipmentSlotType slotIn, int packedLightIn) {
      ItemStack itemstack = entityLivingBaseIn.getItemStackFromSlot(slotIn);
      if (itemstack.getItem() instanceof ArmorItem) {
         ArmorItem armoritem = (ArmorItem)itemstack.getItem();
         if (armoritem.getEquipmentSlot() == slotIn) {
            A a = this.getModelFromSlot(slotIn);
            a = getArmorModelHook(entityLivingBaseIn, itemstack, slotIn, a);
            ((BipedModel)this.getEntityModel()).setModelAttributes(a);
            a.setLivingAnimations(entityLivingBaseIn, limbSwing, limbSwingAmount, partialTicks);
            this.setModelSlotVisible(a, slotIn);
            a.setRotationAngles(entityLivingBaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            boolean flag = this.isLegSlot(slotIn);
            boolean flag1 = itemstack.hasEffect();
            if (armoritem instanceof net.minecraft.item.IDyeableArmorItem) { // Allow this for anything, not only cloth
               int i = ((net.minecraft.item.IDyeableArmorItem)armoritem).getColor(itemstack);
               float f = (float)(i >> 16 & 255) / 255.0F;
               float f1 = (float)(i >> 8 & 255) / 255.0F;
               float f2 = (float)(i & 255) / 255.0F;
               renderArmor(matrixStackIn, bufferIn, packedLightIn, flag1, a, f, f1, f2, this.getArmorResource(entityLivingBaseIn, itemstack, slotIn, null));
               renderArmor(matrixStackIn, bufferIn, packedLightIn, flag1, a, 1.0F, 1.0F, 1.0F, this.getArmorResource(entityLivingBaseIn, itemstack, slotIn, "overlay"));
            } else {
               renderArmor(matrixStackIn, bufferIn, packedLightIn, flag1, a, 1.0F, 1.0F, 1.0F, this.getArmorResource(entityLivingBaseIn, itemstack, slotIn, null));
            }

         }
      }
   }

   private void renderModel(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, ArmorItem armorItemIn, boolean glintIn, A modelIn, boolean legSlotIn, float red, float green, float blue, @Nullable String overlayIn) {
      renderArmor(matrixStackIn, bufferIn, packedLightIn, glintIn, modelIn, red, green, blue, this.getArmorResource(armorItemIn, legSlotIn, overlayIn));
   }
   private void renderArmor(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, boolean glintIn, A modelIn, float red, float green, float blue, ResourceLocation armorResource) {
      IVertexBuilder ivertexbuilder = ItemRenderer.getBuffer(bufferIn, RenderType.getEntityCutoutNoCull(armorResource), false, glintIn);
      modelIn.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
   }

   public A getModelFromSlot(EquipmentSlotType slotIn) {
      return (A)(this.isLegSlot(slotIn) ? this.modelLeggings : this.modelArmor);
   }

   private boolean isLegSlot(EquipmentSlotType slotIn) {
      return slotIn == EquipmentSlotType.LEGS;
   }

   @Deprecated //Use the more sensitive version getArmorResource below
   private ResourceLocation getArmorResource(ArmorItem armor, boolean legSlotIn, @Nullable String suffixOverlayIn) {
      String s = "textures/models/armor/" + armor.getArmorMaterial().getName() + "_layer_" + (legSlotIn ? 2 : 1) + (suffixOverlayIn == null ? "" : "_" + suffixOverlayIn) + ".png";
      return ARMOR_TEXTURE_RES_MAP.computeIfAbsent(s, ResourceLocation::new);
   }

   protected abstract void setModelSlotVisible(A modelIn, EquipmentSlotType slotIn);

   protected abstract void setModelVisible(A model);


   /*=================================== FORGE START =========================================*/

   /**
    * Hook to allow item-sensitive armor model. for LayerBipedArmor.
    */
   protected A getArmorModelHook(T entity, ItemStack itemStack, EquipmentSlotType slot, A model) {
      return model;
   }

   /**
    * More generic ForgeHook version of the above function, it allows for Items to have more control over what texture they provide.
    *
    * @param entity Entity wearing the armor
    * @param stack ItemStack for the armor
    * @param slot Slot ID that the item is in
    * @param type Subtype, can be null or "overlay"
    * @return ResourceLocation pointing at the armor's texture
    */
   public ResourceLocation getArmorResource(net.minecraft.entity.Entity entity, ItemStack stack, EquipmentSlotType slot, @javax.annotation.Nullable String type) {
      ArmorItem item = (ArmorItem)stack.getItem();
      String texture = item.getArmorMaterial().getName();
      String domain = "minecraft";
      int idx = texture.indexOf(':');
      if (idx != -1)
      {
         domain = texture.substring(0, idx);
         texture = texture.substring(idx + 1);
      }
      String s1 = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, (isLegSlot(slot) ? 2 : 1), type == null ? "" : String.format("_%s", type));

      s1 = net.minecraftforge.client.ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
      ResourceLocation resourcelocation = ARMOR_TEXTURE_RES_MAP.get(s1);

      if (resourcelocation == null)
      {
         resourcelocation = new ResourceLocation(s1);
         ARMOR_TEXTURE_RES_MAP.put(s1, resourcelocation);
      }

      return resourcelocation;
   }
   /*=================================== FORGE END ===========================================*/
}