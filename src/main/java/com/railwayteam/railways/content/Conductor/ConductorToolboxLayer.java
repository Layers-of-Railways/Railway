package com.railwayteam.railways.content.Conductor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.railwayteam.railways.content.Conductor.toolbox.MountedToolboxHolder;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
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
    if (itemstack.is(AllTags.AllItemTags.TOOLBOXES.tag) && conductorEntity.isCarryingToolbox()) {
      MountedToolboxHolder holder = conductorEntity.getToolboxHolder();
      BlockState blockState = ((BlockItem) itemstack.getItem()).getBlock().defaultBlockState();
      SuperByteBuffer body =
          CachedBufferer.partial(CRBlockPartials.TOOLBOX_BODIES.get(holder.getColor()), blockState);
      SuperByteBuffer lid =
          CachedBufferer.partial(AllBlockPartials.TOOLBOX_LIDS.get(holder.getColor()), blockState);
      SuperByteBuffer drawer = CachedBufferer.partial(AllBlockPartials.TOOLBOX_DRAWER, blockState);

      float lidAngle = holder.lid.getValue(partialTick);
      float drawerOffset = holder.drawers.getValue(partialTick);

      poseStack.pushPose();

      poseStack.mulPose(Vector3f.XP.rotationDegrees(190.0f));
      poseStack.translate(-0.5d, -0.65d, -0.97d);

      double rotate = 0;

      VertexConsumer builder = buffer.getBuffer(RenderType.cutoutMipped());
      body.centre()
          .rotateY(rotate)
          .unCentre()
          .translate(0, 6 / 16f, 12 / 16f)
          .translate(0, -6 / 16f, -12 / 16f)
          .light(packedLight)
          .renderInto(poseStack, builder);

      lid.centre()
          .rotateY(rotate)
          .unCentre()
          .translate(0, 6 / 16f, 12 / 16f)
          .rotateX(60 * lidAngle)
          .translate(0, -6 / 16f, -12 / 16f)
          .light(packedLight)
          .renderInto(poseStack, builder);

      for (int offset : Iterate.zeroAndOne) {
        drawer.centre()
            .rotateY(rotate)
            .unCentre()
            .translate(0, offset * 1 / 8f, -drawerOffset * .175f * (2 - offset))
            .light(packedLight)
            .renderInto(poseStack, builder);
      }
      poseStack.popPose();
      /*
      poseStack.pushPose();
      poseStack.translate(0.0D, (double) 0.2F, (double) 0.4F);
      poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
      poseStack.scale(1.2f, 1.2f, 1.2f);
      Minecraft.getInstance().getItemInHandRenderer().renderItem(conductorEntity, itemstack, ItemTransforms.TransformType.GROUND, false, poseStack, buffer, packedLight);
      poseStack.popPose();*/
    }
  }
}
