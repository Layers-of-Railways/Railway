/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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

    BOGEY_MENU("bogeymenu", 279, 184),
    BOGEY_MENU_SCROLL_BAR("bogeymenu", 280, 0, 8, 15),
    BOGEY_MENU_SCROLL_BAR_DISABLED("bogeymenu", 288, 0, 8, 15),
    ;

    public static final int FONT_COLOR = 0x575F7A;

    public final ResourceLocation location;
    public final int width, height;
    public final int startX, startY;

    CRGuiTextures(String location, int width, int height) {
        this(location, 0, 0, width, height);
    }

    CRGuiTextures(int startX, int startY) {
        this("icons", startX * 16, startY * 16, 16, 16);
    }

    CRGuiTextures(String location, int startX, int startY, int width, int height) {
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
    public void render(PoseStack ms, int x, int y, int textureWidth, int textureHeight) {
        bind();
        GuiComponent.blit(ms, x, y, startX, startY, width, height, textureWidth, textureHeight);
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
