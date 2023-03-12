package com.railwayteam.railways.content.conductor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.railwayteam.railways.content.conductor.toolbox.MountedToolboxHolder;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ConductorFlagLayer<T extends ConductorEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

  public ConductorFlagLayer(RenderLayerParent<T, M> pRenderer) {
    super(pRenderer);
  }

  @Override
  public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, @NotNull T conductorEntity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
    if (conductorEntity.isHoldingSchedulesClient()) {

      poseStack.pushPose();

      //poseStack.mulPose(Vector3f.XP.rotationDegrees(180.0f));
      //poseStack.translate(-0.5d, -1.2d, -0.94d);


      CachedBufferer.partial(CRBlockPartials.CONDUCTOR_WHISTLE_FLAGS.get(conductorEntity.getColor()), Blocks.AIR.defaultBlockState())
          .translate(-0.78125, 0.15, -0.688)
          .light(packedLight)
          .renderInto(poseStack, buffer.getBuffer(RenderType.cutoutMipped()));

      poseStack.popPose();
    }
  }
}
