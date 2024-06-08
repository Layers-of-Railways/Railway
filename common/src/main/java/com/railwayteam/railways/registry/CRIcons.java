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
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.railwayteam.railways.Railways;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.element.DelegatedStencilElement;
import com.simibubi.create.foundation.utility.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class CRIcons extends AllIcons {
    public static final ResourceLocation ICON_ATLAS = Railways.asResource("textures/gui/icons.png");
    public static final int ICON_ATLAS_SIZE = 256;

    private static int x = 0, y = -1;
    private int iconX;
    private int iconY;

    public static final CRIcons
        I_SEARCH_DOWN = newRow(),
        I_SEARCH_UP = next();
    public static final CRIcons
        I_COUPLING_BOTH = newRow(),
        I_COUPLING_COUPLE = next(),
        I_COUPLING_DECOUPLE = next();
    public static final CRIcons
        I_DOOR_MANUAL = newRow(),
        I_DOOR_NORMAL = next(),
        I_DOOR_SPECIAL = next(),
        I_DOOR_SPECIAL_INVERTED = next();

    public static final CRIcons
        I_SWITCH_MANUAL = newRow(),
        I_SWITCH_AUTO = next();

    public static final CRIcons
        I_SWAP_TRACKS = newRow();

    public static final CRIcons
        I_NARROW = newRow(),
        I_STANDARD = next(),
        I_WIDE = next(),
        I_FAVORITE = next(),
        I_FAVORITED = next();

    public CRIcons(int x, int y) {
        super(x, y);
        iconX = x * 16;
        iconY = y * 16;
    }

    private static CRIcons next() {
        return new CRIcons(++x, y);
    }

    private static CRIcons newRow() {
        return new CRIcons(x = 0, ++y);
    }

    @Environment(EnvType.CLIENT)
    public void bind() {
        RenderSystem.setShaderTexture(0, ICON_ATLAS);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void render(PoseStack matrixStack, int x, int y) {
        bind();
        GuiComponent.blit(matrixStack, x, y, 0, iconX, iconY, 16, 16, 256, 256);
    }

    @Environment(EnvType.CLIENT)
    public void render(PoseStack matrixStack, int x, int y, GuiComponent component) {
        bind();
        component.blit(matrixStack, x, y, iconX, iconY, 16, 16);
    }

    @Environment(EnvType.CLIENT)
    public void render(PoseStack ms, MultiBufferSource buffer, int color) {
        VertexConsumer builder = buffer.getBuffer(RenderType.text(ICON_ATLAS));
        Matrix4f matrix = ms.last().pose();
        Color rgb = new Color(color);
        int light = LightTexture.FULL_BRIGHT;

        Vec3 vec1 = new Vec3(0, 0, 0);
        Vec3 vec2 = new Vec3(0, 1, 0);
        Vec3 vec3 = new Vec3(1, 1, 0);
        Vec3 vec4 = new Vec3(1, 0, 0);

        float u1 = iconX * 1f / ICON_ATLAS_SIZE;
        float u2 = (iconX + 16) * 1f / ICON_ATLAS_SIZE;
        float v1 = iconY * 1f / ICON_ATLAS_SIZE;
        float v2 = (iconY + 16) * 1f / ICON_ATLAS_SIZE;

        vertex(builder, matrix, vec1, rgb, u1, v1, light);
        vertex(builder, matrix, vec2, rgb, u1, v2, light);
        vertex(builder, matrix, vec3, rgb, u2, v2, light);
        vertex(builder, matrix, vec4, rgb, u2, v1, light);
    }

    @Environment(EnvType.CLIENT)
    private void vertex(VertexConsumer builder, Matrix4f matrix, Vec3 vec, Color rgb, float u, float v, int light) {
        builder.vertex(matrix, (float) vec.x, (float) vec.y, (float) vec.z)
                .color(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), 255)
                .uv(u, v)
                .uv2(light)
                .endVertex();
    }

    @Environment(EnvType.CLIENT)
    public DelegatedStencilElement asStencil() {
        return new DelegatedStencilElement().withStencilRenderer((ms, w, h, alpha) -> this.render(ms, 0, 0)).withBounds(16, 16);
    }
}
