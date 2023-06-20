package com.railwayteam.railways.content.switches;

import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.mixin.client.AccessorControlsHandler;
import com.railwayteam.railways.registry.CRGuiTextures;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsHandler;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.Nullable;

public class TrainHUDSwitchExtension {

    public static @Nullable TrackSwitchBlock.SwitchState switchState;
    public static boolean isAutomaticSwitch = false;
    public static boolean isWrong = false;
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

    public static void renderOverlay(PoseStack poseStack, float partialTicks, int width, int height) {
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

        poseStack.pushPose();
        poseStack.translate(width / 2 - 91, height - 29, 0);

        if (switchProgress.getValue(partialTicks) > 0) {
            CRGuiTextures bg = isAutomaticSwitch ?
                    CRGuiTextures.TRAIN_HUD_SWITCH_BRASS :
                    CRGuiTextures.TRAIN_HUD_SWITCH_ANDESITE;
            //bg.render(poseStack, 141, (int) (-bg.height * switchProgress.getValue(partialTicks)));
            bg.bind();
            GuiComponent.blit(poseStack, 141, (int) (-16 * switchProgress.getValue(partialTicks) - 0.5), 0,
                    bg.startX, bg.startY, bg.width, (int) (bg.height * switchProgress.getValue(partialTicks) + 0.5),
                    256, 256);
        }

        if (switchProgress.getValue(partialTicks) > 0.99 && switchState != null) {
            switch (switchState) {
                case NORMAL -> CRGuiTextures.getForSwitch(switchState, isWrong).render(poseStack, 152, -13);
                case REVERSE_LEFT -> CRGuiTextures.getForSwitch(switchState, isWrong).render(poseStack, 142, -13);
                case REVERSE_RIGHT -> CRGuiTextures.getForSwitch(switchState, isWrong).render(poseStack, 162, -13);
            }
        }

        poseStack.popPose();
    }
}
