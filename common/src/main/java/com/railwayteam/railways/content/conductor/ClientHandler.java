package com.railwayteam.railways.content.conductor;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

public class ClientHandler {
    public static boolean isPlayerMountedOnCamera() {
        return Minecraft.getInstance().cameraEntity instanceof ConductorEntity;
    }

    @Nullable
    public static ConductorEntity getPlayerMountedOnCamera() {
        return Minecraft.getInstance().cameraEntity instanceof ConductorEntity ce ? ce : null;
    }

    public static boolean isPossessed(ConductorEntity conductor) {
        return getPlayerMountedOnCamera() == conductor;
    }
}
