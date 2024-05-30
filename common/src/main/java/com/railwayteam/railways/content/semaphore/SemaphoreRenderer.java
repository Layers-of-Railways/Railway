/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.content.semaphore;

import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlock;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.RenderTypes;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class SemaphoreRenderer  extends SafeBlockEntityRenderer<SemaphoreBlockEntity> {
    public SemaphoreRenderer(BlockEntityRendererProvider.Context context) {}
    @Override
    protected void renderSafe(SemaphoreBlockEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        ms.pushPose();
        BlockState blockState = te.getBlockState();

        float yRot = AngleHelper.horizontalAngle(blockState.getValue(NixieTubeBlock.FACING))+180;

        TransformStack msr = TransformStack.cast(ms);
        msr.centre()
                .rotateY(yRot)
                .unCentre();

        boolean yellow = te.isDistantSignal;






        float pos = te.armPosition.getValue(partialTicks);

        float target = te.armPosition.getChaseTarget();

        pos = (2*pos-1)*(target-0.5f)+0.5f;//flips pos between 0 and 1 depending on target

        float fallTime = 0.3f;
        if(pos<fallTime)
            pos = 1f-pos*pos/(fallTime*fallTime);
        else {

            pos = (pos-fallTime)/(1f-fallTime);
            float bounce = (float)(Math.exp(-pos*4.0)*Math.sin(pos*Math.PI*3.0));
            float smoothing = 0.1f;
            bounce = (float)Math.sqrt(bounce*bounce+smoothing*smoothing)-smoothing;
            pos = bounce/3f;
        }

        pos = -(2*pos-1)*(target-0.5f)+0.5f;

        float angle = pos*0.78f;

        boolean top = pos<0.2;
        boolean bottom = pos>0.8;
        boolean flipped = blockState.getValue(SemaphoreBlock.FLIPPED);
        boolean upside_down = blockState.getValue(SemaphoreBlock.UPSIDE_DOWN);
        PartialModel arm;
        if (upside_down) {
            arm = flipped?
                yellow? CRBlockPartials.SEMAPHORE_ARM_YELLOW_FLIPPED_UPSIDE_DOWN:CRBlockPartials.SEMAPHORE_ARM_RED_FLIPPED_UPSIDE_DOWN:
                yellow? CRBlockPartials.SEMAPHORE_ARM_YELLOW_UPSIDE_DOWN:CRBlockPartials.SEMAPHORE_ARM_RED_UPSIDE_DOWN;
        } else {
            arm = flipped?
                yellow? CRBlockPartials.SEMAPHORE_ARM_YELLOW_FLIPPED:CRBlockPartials.SEMAPHORE_ARM_RED_FLIPPED:
                yellow? CRBlockPartials.SEMAPHORE_ARM_YELLOW:CRBlockPartials.SEMAPHORE_ARM_RED;
        }
        CachedBufferer.partial(arm, blockState)
                .light(light)
                .rotateCentered(Direction.EAST,angle * (upside_down?-1:1))
                .renderInto(ms, buffer.getBuffer(RenderType.solid()));

        float renderTime = AnimationTickHolder.getRenderTime(te.getLevel());

        top = top && (renderTime % 40 < 3 || te.isValid);


        if((top||bottom))
        {
            ms.pushPose();
            if (upside_down) {
                if (bottom)
                    ms.translate(8 / 16.0, 9 / 16.0, 15 / 16.0);
                else
                    ms.translate(8 / 16.0, 4 / 16.0, 14 / 16.0);
            } else {
                if (bottom)
                    ms.translate(8 / 16.0, 7 / 16.0, 15 / 16.0);
                else
                    ms.translate(8 / 16.0, 12 / 16.0, 14 / 16.0);
            }



            CachedBufferer.partial(AllPartialModels.SIGNAL_WHITE_CUBE, blockState)
                    .light(0xF000F0)
                    .disableDiffuse()
                    .scale(1, 1, 1)
                    .renderInto(ms, buffer.getBuffer(RenderType.translucent()));



            CachedBufferer
                    .partial(
                            bottom ? AllPartialModels.SIGNAL_WHITE_GLOW:yellow?AllPartialModels.SIGNAL_YELLOW_GLOW:AllPartialModels.SIGNAL_RED_GLOW,
                            blockState)
                    .light(0xF000F0)
                    .disableDiffuse()
                    .scale(1.5f,2, 2)
                    .renderInto(ms, buffer.getBuffer(RenderTypes.getAdditive()));

            CachedBufferer
                    .partial(bottom?CRBlockPartials.SEMAPHORE_LAMP_WHITE:yellow?CRBlockPartials.SEMAPHORE_LAMP_YELLOW:CRBlockPartials.SEMAPHORE_LAMP_RED
                            , blockState)
                    .light(0xF000F0)
                    .disableDiffuse()
                    .scale(1 + 1 / 16f)
                    .renderInto(ms, buffer.getBuffer(RenderTypes.getAdditive()));



            ms.popPose();
        }

        ms.popPose();
    }
}
