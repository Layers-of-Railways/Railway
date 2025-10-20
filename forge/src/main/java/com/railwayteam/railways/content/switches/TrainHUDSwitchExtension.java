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

package com.railwayteam.railways.content.switches;

import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.mixin.client.AccessorControlsHandler;
import com.railwayteam.railways.registry.CRGuiTextures;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsHandler;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.Nullable;

public class TrainHUDSwitchExtension {

    public static @Nullable TrackSwitchBlock.SwitchState switchState;
    public static boolean isAutomaticSwitch = false;
    public static boolean isWrong = false;
    public static boolean isLocked = false;
    static LerpedFloat switchProgress = LerpedFloat.linear();

    public static void tick() {
        if (AccessorControlsHandler.getEntityRef().get() == null)
            switchState = null;
        switchProgress.chase(switchState != null ? 1.0 : 0.0, .5, LerpedFloat.Chaser.EXP);
        switchProgress.tickChaser();
    }

    private static Carriage getCarriage() {
        if (!(ControlsHandler.getContraption() instanceof CarriageContraptionEntity cce))
            return null;
        return cce.getCarriage();
    }

    public static void renderOverlay(GuiGraphics graphics, float partialTicks, int width, int height) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR)
            return;

        if (!(ControlsHandler.getContraption() instanceof CarriageContraptionEntity cce))
            return;
        Carriage carriage = cce.getCarriage();
        if (carriage == null)
            return;
        Entity cameraEntity = Minecraft.getInstance()
                .getCameraEntity();
        if (cameraEntity == null)
            return;
        BlockPos localPos = ControlsHandler.getControlsPos();
        if (localPos == null)
            return;

        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(width / 2 - 91, height - 29, 0);

        if (switchProgress.getValue(partialTicks) > 0) {
            CRGuiTextures bg = isAutomaticSwitch ?
                    CRGuiTextures.TRAIN_HUD_SWITCH_BRASS :
                    CRGuiTextures.TRAIN_HUD_SWITCH_ANDESITE;
            //bg.render(poseStack, 131, (int) (-bg.height * switchProgress.getValue(partialTicks)));
            bg.bind();
            graphics.blit(bg.location, 131, (int) (-16 * switchProgress.getValue(partialTicks) - 0.5), 0,
                    bg.startX, bg.startY, bg.width, (int) (bg.height * switchProgress.getValue(partialTicks) + 0.5),
                    256, 256);
        }

        if (switchProgress.getValue(partialTicks) > 0.99 && switchState != null) {
            switch (switchState) {
                case NORMAL -> CRGuiTextures.getForSwitch(switchState, isWrong).render(graphics, 152, -13);
                case REVERSE_LEFT -> CRGuiTextures.getForSwitch(switchState, isWrong).render(graphics, 142, -13);
                case REVERSE_RIGHT -> CRGuiTextures.getForSwitch(switchState, isWrong).render(graphics, 162, -13);
            }
            if (isLocked)
                CRGuiTextures.TRAIN_HUD_SWITCH_LOCKED.render(graphics, 134, -13);
        }

        poseStack.popPose();
    }
}
