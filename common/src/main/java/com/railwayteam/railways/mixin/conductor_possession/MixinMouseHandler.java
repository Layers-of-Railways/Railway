package com.railwayteam.railways.mixin.conductor_possession;

import com.railwayteam.railways.content.conductor.ClientHandler;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MouseHandler.class)
public class MixinMouseHandler {
    @ModifyVariable(method = "onPress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getOverlay()Lnet/minecraft/client/gui/screens/Overlay;", ordinal = 1, shift = At.Shift.BY, by = 2), name = {"bl", "flag"})
    private boolean noPressingMouseWhilePossessing(boolean value) {
        return !ClientHandler.isPlayerMountedOnCamera() && value;
    }

    @ModifyVariable(method = "onPress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getOverlay()Lnet/minecraft/client/gui/screens/Overlay;", ordinal = 1, shift = At.Shift.BY, by = 2), argsOnly = true, ordinal = 1)
    private int noPressingMouseWhilePossessing2(int action) {
        return ClientHandler.isPlayerMountedOnCamera() ? 0 : action;
    }
}
