package com.railwayteam.railways.mixin.client;

import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.contraptions.components.structureMovement.glue.SuperGlueSelectionHandler;
import com.simibubi.create.content.contraptions.components.structureMovement.glue.SuperGlueSelectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(value = SuperGlueSelectionHandler.class, remap = false)
public class MixinSuperGlueSelectionHandler {

    @Shadow private BlockPos hoveredPos;
    @Shadow private Object clusterOutlineSlot;
    @Shadow private int clusterCooldown;
    private static final int CONTROL_HIGHLIGHT = 0x41bdc6;

    @Inject(method = "tick", at = @At(value = "RETURN", ordinal = 4))
    private void controlHighlightsGlueReturn(CallbackInfo ci) {
        controlHighlightsGlue();
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void controlHighlightsGlueTail(CallbackInfo ci) {
        controlHighlightsGlue();
    }

    private void controlHighlightsGlue() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.keySprint.isDown()) {
            Set<BlockPos> cluster = SuperGlueSelectionHelper.searchGlueGroup(mc.level, hoveredPos, hoveredPos, true);
            if (cluster != null) {
                CreateClient.OUTLINER.showCluster(clusterOutlineSlot, cluster)
                    .colored(CONTROL_HIGHLIGHT)
                    .withFaceTextures(AllSpecialTextures.GLUE, AllSpecialTextures.HIGHLIGHT_CHECKERED)
                    .disableNormals()
                    .lineWidth(1 / 24f);

                clusterCooldown = 10;
            }
        }
    }
}
