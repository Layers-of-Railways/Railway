/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2025 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.content.conductor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.railwayteam.railways.content.conductor.toolbox.MountedToolbox;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllTags;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createmod.catnip.data.Iterate;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
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
      MountedToolbox holder = conductorEntity.getToolbox();
      BlockState blockState = ((BlockItem) itemstack.getItem()).getBlock().defaultBlockState();
      SuperByteBuffer body =
          CachedBuffers.partial(CRBlockPartials.TOOLBOX_BODIES.get(holder.getColor()), blockState);
      SuperByteBuffer lid =
              CachedBuffers.partial(AllPartialModels.TOOLBOX_LIDS.get(holder.getColor()), blockState);
      SuperByteBuffer drawer = CachedBuffers.partial(AllPartialModels.TOOLBOX_DRAWER, blockState);

      float lidAngle = holder.lid.getValue(partialTick);
      float drawerOffset = holder.drawers.getValue(partialTick);

      poseStack.pushPose();

      poseStack.mulPose(Axis.XP.rotationDegrees(180.0f));
      poseStack.translate(-0.5d, -1.2d, -0.94d);

      double rotate = 0;

      VertexConsumer builder = buffer.getBuffer(RenderType.cutoutMipped());
      body.center()
          .rotateY((float) rotate)
          .uncenter()
          .translate(0, 6 / 16f, 12 / 16f)
          .translate(0, -6 / 16f, -12 / 16f)
          .light(packedLight)
          .renderInto(poseStack, builder);

      lid.center()
          .rotateY((float) rotate)
          .uncenter()
          .translate(0, 6 / 16f, 12 / 16f)
          .rotateXDegrees(60 * lidAngle)
          .translate(0, -6 / 16f, -12 / 16f)
          .light(packedLight)
          .renderInto(poseStack, builder);

      for (int offset : Iterate.zeroAndOne) {
        drawer.center()
            .rotateY((float) rotate)
            .uncenter()
            .translate(0, offset / 8f, -drawerOffset * .175f * (2 - offset))
            .light(packedLight)
            .renderInto(poseStack, builder);
      }
      poseStack.popPose();
    }
  }
}
