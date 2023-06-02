package com.railwayteam.railways.content.extended_sliding_doors;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.utility.Components;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class ToggleButton extends AbstractButton {
//    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");
//    private static final int TEXT_COLOR = 14737632;
    private boolean selected;
    private final ResourceLocation enabledTexture;
    private final ResourceLocation disabledTexture;

    public ToggleButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, boolean pSelected, ResourceLocation enabledTexture, ResourceLocation disabledTexture) {
        super(pX, pY, pWidth, pHeight, pMessage);
        this.selected = pSelected;
        this.enabledTexture = enabledTexture;
        this.disabledTexture = disabledTexture;
    }

    public void onPress() {
        this.selected = !this.selected;
    }

    public boolean selected() {
        return this.selected;
    }

    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
        pNarrationElementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                pNarrationElementOutput.add(NarratedElementType.USAGE, Components.translatable("narration.checkbox.usage.focused"));
            } else {
                pNarrationElementOutput.add(NarratedElementType.USAGE, Components.translatable("narration.checkbox.usage.hovered"));
            }
        }

    }

    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.setShaderTexture(0, selected ? enabledTexture : disabledTexture);
        RenderSystem.enableDepthTest();
        Font font = minecraft.font;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        blit(pPoseStack, this.x, this.y, this.isFocused() ? 20.0F : 0.0F, this.selected ? 20.0F : 0.0F, 20, this.height, 64, 64);
        this.renderBg(pPoseStack, minecraft, pMouseX, pMouseY);
        /*if (this.showLabel) {
            drawString(pPoseStack, font, this.getMessage(), this.x + 24, this.y + (this.height - 8) / 2, 14737632 | Mth.ceil(this.alpha * 255.0F) << 24);
        }*/

    }
}
