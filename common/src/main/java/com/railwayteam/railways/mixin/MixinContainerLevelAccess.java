package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.moving_bes.GuiBlockLevelAccess;
import com.railwayteam.railways.content.moving_bes.GuiBlockContraptionWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ContainerLevelAccess.class)
public interface MixinContainerLevelAccess {
    @Inject(method = "create", at = @At("HEAD"), cancellable = true)
    private static void railways$createGuiBlockLevelAccessIfGuiContraptionWorld(Level level, BlockPos pos, CallbackInfoReturnable<ContainerLevelAccess> cir) {
        if (level instanceof GuiBlockContraptionWorld guiBlockContraptionWorld) {
            cir.setReturnValue(new GuiBlockLevelAccess(
                    guiBlockContraptionWorld.getLevel(),
                    guiBlockContraptionWorld.contraption.entity,
                    guiBlockContraptionWorld.blockPos
            ));
        }
    }
}
