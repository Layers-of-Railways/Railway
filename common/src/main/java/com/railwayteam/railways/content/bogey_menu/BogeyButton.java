package com.railwayteam.railways.content.bogey_menu;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class BogeyButton extends Button {
    public BogeyButton(int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress);
    }

    public BogeyButton(int x, int y, int width, int height, Component message, OnPress onPress, OnTooltip onTooltip) {
        super(x, y, width, height, message, onPress, onTooltip);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) { super.render(poseStack, mouseX, mouseY, partialTick); } // NO-OP

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return super.mouseScrolled(mouseX, mouseY, delta);
    }
}
