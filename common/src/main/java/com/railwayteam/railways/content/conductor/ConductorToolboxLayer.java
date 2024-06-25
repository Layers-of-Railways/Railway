/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
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
import com.mojang.math.Vector3f;
import com.railwayteam.railways.content.conductor.toolbox.MountedToolbox;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.Iterate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
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
          CachedBufferer.partial(CRBlockPartials.TOOLBOX_BODIES.get(holder.getColor()), blockState);
      SuperByteBuffer lid =
          CachedBufferer.partial(AllPartialModels.TOOLBOX_LIDS.get(holder.getColor()), blockState);
      SuperByteBuffer drawer = CachedBufferer.partial(AllPartialModels.TOOLBOX_DRAWER, blockState);

      float lidAngle = holder.lid.getValue(partialTick);
      float drawerOffset = holder.drawers.getValue(partialTick);

      poseStack.pushPose();

      poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0f));
      poseStack.translate(-0.5d, -1.2d, -0.94d);

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
            .translate(0, offset / 8f, -drawerOffset * .175f * (2 - offset))
            .light(packedLight)
            .renderInto(poseStack, builder);
      }
      poseStack.popPose();
    }
  }
}
