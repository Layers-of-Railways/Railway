package com.railwayteam.railways.content.bogey_menu.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class BogeyMenuButton extends Button {
    public BogeyMenuButton(int x, int y, int width, int height, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress);
    }

    @Override // NO-OP, We take care of rendering ourselves as buttons for text doesn't update properly
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) { }
}
