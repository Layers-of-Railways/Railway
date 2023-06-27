package com.railwayteam.railways.content.conductor;

import com.railwayteam.railways.registry.CRPackets;
import com.railwayteam.railways.util.packet.CameraMovePacket;
import com.railwayteam.railways.util.packet.DismountCameraPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ConductorPossessionController {
    private static ClientChunkCache.Storage cameraStorage;
    private static boolean wasUpPressed;
    private static boolean wasDownPressed;
    private static boolean wasLeftPressed;
    private static boolean wasRightPressed;
    private static boolean wasJumpPressed;
    private static boolean wasSprintPressed;
    private static boolean wasMounted;

    private static int ticksSincePacket = 0;
    
    @SuppressWarnings("AssignmentUsedAsCondition") // we are doing this intentionally
    public static void onClientTick(Minecraft mc, boolean start) {
        Entity cameraEntity = mc.cameraEntity;

        if (cameraEntity instanceof ConductorEntity cam) {
            wasMounted = true;
            Options options = mc.options;

            //up/down/left/right handling is split to prevent players who are viewing a camera from moving around in a boat or on a horse
            if (start) {
                if (wasUpPressed = options.keyUp.isDown())
                    options.keyUp.setDown(false);

                if (wasDownPressed = options.keyDown.isDown())
                    options.keyDown.setDown(false);

                if (wasLeftPressed = options.keyLeft.isDown())
                    options.keyLeft.setDown(false);

                if (wasRightPressed = options.keyRight.isDown())
                    options.keyRight.setDown(false);
                if (wasJumpPressed = options.keyJump.isDown())
                    options.keyJump.setDown(false);
                wasSprintPressed = options.keySprint.isDown();

                if (options.keyShift.isDown()) {
                    dismount();
                    options.keyShift.setDown(false);
                }
            }
            else {
                if (wasUpPressed) {
                    //moveViewUp(cam);
                    options.keyUp.setDown(true);
                }

                if (wasDownPressed) {
                    //moveViewDown(cam);
                    options.keyDown.setDown(true);
                }

                if (wasLeftPressed) {
                    //moveViewHorizontally(cam, cam.getYRot(), cam.getYRot() - (float) cam.cameraSpeed * cam.zoomAmount);
                    options.keyLeft.setDown(true);
                }

                if (wasRightPressed) {
                    //moveViewHorizontally(cam, cam.getYRot(), cam.getYRot() + (float) cam.cameraSpeed * cam.zoomAmount);
                    options.keyRight.setDown(true);
                }

                if (wasJumpPressed) {
                    options.keyJump.setDown(true);
                }

                /*if (KeyBindings.cameraZoomIn.isDown())
                    zoomIn(cam);
                else if (KeyBindings.cameraZoomOut.isDown())
                    zoomOut(cam);
                else
                    cam.zooming = false;

                if (KeyBindings.cameraEmitRedstone.consumeClick())
                    emitRedstone(cam);

                if (KeyBindings.cameraActivateNightVision.consumeClick())
                    giveNightVision(cam);*/

                //update other players with the head rotation
                LocalPlayer player = mc.player;
                double yRotChange = player.getYRot() - player.yRotLast;
                double xRotChange = player.getXRot() - player.xRotLast;

                if (yRotChange != 0.0D || xRotChange != 0.0D || ++ticksSincePacket > 10) {
                    ticksSincePacket = 0;
                    player.connection.send(new ServerboundMovePlayerPacket.Rot(player.getYRot(), player.getXRot(), player.isOnGround()));
                    player.connection.send(new ServerboundPlayerInputPacket(0, 0, false, false));
                }

                // update the server with the camera's position

                //CRPackets.PACKETS.send(new CameraMovePacket(camera, camera.getYRot(), camera.getXRot()));
                CRPackets.PACKETS.send(new CameraMovePacket(cam,
                        new ServerboundMovePlayerPacket.PosRot(cam.getX(), cam.getY(), cam.getZ(),
                                cam.getYRot(), cam.getXRot(), cam.isOnGround())));
            }
        } else if (wasMounted) { // catch in case we didn't want to dismount
            wasMounted = false;
            dismount();
            mc.levelRenderer.allChanged();
        }
        /*else if (resetOverlaysAfterDismount) {
            resetOverlaysAfterDismount = false;
            OverlayRegistry.enableOverlay(ClientHandler.cameraOverlay, false);
            OverlayRegistry.enableOverlay(ClientHandler.hotbarBindOverlay, true);
            CameraController.restoreOverlayStates();
        }*/
    }

    private static void dismount() {
        CRPackets.PACKETS.send(new DismountCameraPacket());
        wasMounted = false;
    }

    public static ClientChunkCache.Storage getCameraStorage() {
        return cameraStorage;
    }

    public static void setCameraStorage(ClientChunkCache.Storage newStorage) {
        cameraStorage = newStorage;
    }

    public static void setRenderPosition(Entity entity) {
        if (entity instanceof ConductorEntity) {
            SectionPos cameraPos = SectionPos.of(entity);

            cameraStorage.viewCenterX = cameraPos.x();
            cameraStorage.viewCenterZ = cameraPos.z();
        }
    }

    public static void tryUpdatePossession(ConductorEntity conductorEntity) {
        if (ClientHandler.getPlayerMountedOnCamera() == conductorEntity)
            setRenderPosition(conductorEntity);
    }

    public static boolean isPossessingConductor(Entity entity) {
        if (!(entity instanceof Player player))
            return false;

        if (player.level.isClientSide)
            return ClientHandler.isPlayerMountedOnCamera();
        else
            return ((ServerPlayer) player).getCamera() instanceof ConductorEntity;
    }

    @Nullable
    public static ConductorEntity getPossessingConductor(Entity entity) {
        if (!(entity instanceof Player player))
            return null;

        if (player.level.isClientSide)
            return ClientHandler.getPlayerMountedOnCamera();
        else
            return ((ServerPlayer) player).getCamera() instanceof ConductorEntity ce ? ce : null;
    }

    public static boolean wasUpPressed() {
        return wasUpPressed;
    }

    public static boolean wasDownPressed() {
        return wasDownPressed;
    }

    public static boolean wasLeftPressed() {
        return wasLeftPressed;
    }

    public static boolean wasRightPressed() {
        return wasRightPressed;
    }

    public static boolean wasSprintPressed() {
        return wasSprintPressed;
    }

    public static boolean wasJumpPressed() {
        return wasJumpPressed;
    }
}
