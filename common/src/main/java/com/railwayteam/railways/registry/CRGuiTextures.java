package com.railwayteam.railways.registry;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.switches.TrackSwitchBlock;
import com.simibubi.create.foundation.gui.UIRenderHelper;
import com.simibubi.create.foundation.gui.element.ScreenElement;
import com.simibubi.create.foundation.utility.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;

/*
Copied from Create
 */
public enum CRGuiTextures implements ScreenElement {
    TRAIN_HUD_SWITCH_BRASS("widgets", 0, 0, 42, 16),
    TRAIN_HUD_SWITCH_ANDESITE("widgets", 0, 16, 42, 16),
    TRAIN_HUD_SWITCH_LEFT("widgets", 1, 33, 10, 10),
    TRAIN_HUD_SWITCH_STRAIGHT("widgets", 13, 33, 10, 10),
    TRAIN_HUD_SWITCH_RIGHT("widgets", 25, 33, 10, 10),
    TRAIN_HUD_SWITCH_LEFT_WRONG("widgets", 1, 45, 10, 10),
    TRAIN_HUD_SWITCH_STRAIGHT_WRONG("widgets", 13, 45, 10, 10),
    TRAIN_HUD_SWITCH_RIGHT_WRONG("widgets", 25, 45, 10, 10),
    TRAIN_HUD_SWITCH_LOCKED("widgets", 37, 45, 10, 10),
    ;

    public static final int FONT_COLOR = 0x575F7A;

    public final ResourceLocation location;
    public int width, height;
    public int startX, startY;

    private CRGuiTextures(String location, int width, int height) {
        this(location, 0, 0, width, height);
    }

    private CRGuiTextures(int startX, int startY) {
        this("icons", startX * 16, startY * 16, 16, 16);
    }

    private CRGuiTextures(String location, int startX, int startY, int width, int height) {
        this(Railways.MODID, location, startX, startY, width, height);
    }

    private CRGuiTextures(String namespace, String location, int startX, int startY, int width, int height) {
        this.location = new ResourceLocation(namespace, "textures/gui/" + location + ".png");
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
    }

    public static CRGuiTextures getForSwitch(TrackSwitchBlock.SwitchState switchState, boolean isWrong) {
        if (isWrong) {
            return switch (switchState) {
                case NORMAL -> TRAIN_HUD_SWITCH_STRAIGHT_WRONG;
                case REVERSE_RIGHT -> TRAIN_HUD_SWITCH_RIGHT_WRONG;
                case REVERSE_LEFT -> TRAIN_HUD_SWITCH_LEFT_WRONG;
            };
        } else {
            return switch (switchState) {
                case NORMAL -> TRAIN_HUD_SWITCH_STRAIGHT;
                case REVERSE_RIGHT -> TRAIN_HUD_SWITCH_RIGHT;
                case REVERSE_LEFT -> TRAIN_HUD_SWITCH_LEFT;
            };
        }
    }

    @Environment(EnvType.CLIENT)
    public void bind() {
        RenderSystem.setShaderTexture(0, location);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void render(PoseStack ms, int x, int y) {
        bind();
        GuiComponent.blit(ms, x, y, 0, startX, startY, width, height, 256, 256);
    }

    @Environment(EnvType.CLIENT)
    public void render(PoseStack ms, int x, int y, GuiComponent component) {
        bind();
        component.blit(ms, x, y, startX, startY, width, height);
    }

    @Environment(EnvType.CLIENT)
    public void render(PoseStack ms, int x, int y, Color c) {
        bind();
        UIRenderHelper.drawColoredTexture(ms, c, x, y, startX, startY, width, height);
    }
}
