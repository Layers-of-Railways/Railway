package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.content.custom_tracks.phantom.PhantomSpriteManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextureAtlas.class)
public abstract class MixinTextureAtlas {
    @Shadow @Final private ResourceLocation location;

    @Inject(method = "cycleAnimationFrames", at = @At("RETURN"))
    private void railways$cycleAnimationFrames(CallbackInfo ci) {
        if (this.location == InventoryMenu.BLOCK_ATLAS) {
            Minecraft mc = Minecraft.getInstance();
            PhantomSpriteManager.renderTick(mc);
        }
    }
}
