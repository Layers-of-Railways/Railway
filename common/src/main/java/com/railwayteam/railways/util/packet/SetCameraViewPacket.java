package com.railwayteam.railways.util.packet;

import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.content.conductor.ConductorPossessionController;
import com.railwayteam.railways.multiloader.S2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class SetCameraViewPacket implements S2CPacket {
    private final int id;
    public SetCameraViewPacket(Entity camera) {
        id = camera.getId();
    }

    public SetCameraViewPacket(FriendlyByteBuf buf) {
        id = buf.readVarInt();
    }
    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(id);
    }

    @Override
    public void handle(Minecraft mc) {
        Entity entity = mc.level.getEntity(id);
        boolean isCamera = entity instanceof ConductorEntity;

        if (isCamera || entity instanceof Player) {
            mc.setCameraEntity(entity);

            if (isCamera) {
/*                ConductorPossessionController.previousCameraType = mc.options.getCameraType();
                mc.options.setCameraType(CameraType.FIRST_PERSON);
                mc.gui.setOverlayMessage(Utils.localize("mount.onboard", mc.options.keyShift.getTranslatedKeyMessage()), false);*/
                ConductorPossessionController.setRenderPosition(entity);
                if (mc.player != null) {
                    mc.player.xxa = 0.0f;
                    mc.player.zza = 0.0f;
                    mc.player.setJumping(false);
                }
            }
/*            else if (ConductorPossessionController.previousCameraType != null)
                mc.options.setCameraType(CameraController.previousCameraType);*/

            mc.levelRenderer.allChanged();

/*            if (isCamera) {
                CameraController.resetOverlaysAfterDismount = true;
                CameraController.saveOverlayStates();
                OverlayRegistry.enableOverlay(ForgeIngameGui.EXPERIENCE_BAR_ELEMENT, false);
                OverlayRegistry.enableOverlay(ForgeIngameGui.JUMP_BAR_ELEMENT, false);
                OverlayRegistry.enableOverlay(ForgeIngameGui.POTION_ICONS_ELEMENT, false);
                OverlayRegistry.enableOverlay(ClientHandler.cameraOverlay, true);
                OverlayRegistry.enableOverlay(ClientHandler.hotbarBindOverlay, false);
            }*/
        }
    }
}
