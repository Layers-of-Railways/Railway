package com.railwayteam.railways.content.Conductor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.simibubi.create.AllTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ConductorToolboxLayer<T extends ConductorEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
  public ConductorToolboxLayer(RenderLayerParent<T, M> pRenderer) {
    super(pRenderer);
  }

  @Override
  public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, @NotNull T conductorEntity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
    ItemStack itemstack = conductorEntity.getToolboxDisplayStack();
    if (itemstack.is(AllTags.AllItemTags.TOOLBOXES.tag)) {
      poseStack.pushPose();
      poseStack.translate(0.0D, (double) 0.2F, (double) 0.4F);
      poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
      poseStack.scale(1.2f, 1.2f, 1.2f);
      Minecraft.getInstance().getItemInHandRenderer().renderItem(conductorEntity, itemstack, ItemTransforms.TransformType.GROUND, false, poseStack, buffer, packedLight);
      poseStack.popPose();
    }
  }
}
