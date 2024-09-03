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

package com.railwayteam.railways.content.cycle_menu;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.content.bogey_menu.handler.BogeyMenuEventsHandler;
import com.railwayteam.railways.mixin.client.AccessorToolboxHandlerClient;
import com.railwayteam.railways.registry.CRKeys;
import com.railwayteam.railways.util.client.ClientUtils;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RadialTagCycleMenu extends AbstractSimiScreen {

    private int ticksOpen;
    private int hoveredSlot;
    private boolean scrollMode;
    private int scrollSlot = 0;
    private final TagKey<Item> tag;
    private final List<Item> cycle;
    private final @Nullable CompoundTag stackTag;

    RadialTagCycleMenu(TagKey<Item> tag, List<Item> cycle, @Nullable CompoundTag stackTag) {
        hoveredSlot = -1;
        this.cycle = cycle;
        this.tag = tag;
        this.stackTag = stackTag;
    }

    @SuppressWarnings({"IntegerDivisionInFloatingPointContext", "DuplicatedCode"})
    @Override
    protected void renderWindow(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        float fade = Mth.clamp((ticksOpen + AnimationTickHolder.getPartialTicks()) / 10f, 1 / 512f, 1);

        hoveredSlot = -1;
        Window window = Minecraft.getInstance().getWindow();
        float hoveredX = mouseX - window.getGuiScaledWidth() / 2;
        float hoveredY = mouseY - window.getGuiScaledHeight() / 2;

        float distance = hoveredX * hoveredX + hoveredY * hoveredY;
        if (distance > 25 && distance < 10000)
            hoveredSlot =
                    (Mth.floor((AngleHelper.deg(Mth.atan2(hoveredY, hoveredX)) + 360 + 180 - 22.5f)) % 360)
                            / 45;
        if (scrollMode && distance > 150)
            scrollMode = false;

        ms.pushPose();
        ms.translate(width / 2, height / 2, 0);
        Component tip = null;
        ResourceLocation tagLoc = tag.location();
        Component title = Components.translatable("tag.item." + tagLoc.getNamespace() + "." + tagLoc.getPath().replace('/', '.'));

        /*
        core rendering
         */


        for (int slot = 0; slot < 8; slot++) {
            ms.pushPose();
            double radius = -40 + (10 * (1 - fade) * (1 - fade));
            double angle = slot * 45 - 45;
            TransformStack.cast(ms)
                    .rotateZ(angle)
                    .translate(0, radius, 0)
                    .rotateZ(-angle);
            ms.translate(-12, -12, 0);

            boolean selected = (slot == (scrollMode ? scrollSlot : hoveredSlot));

            if (slot < cycle.size()) {
                ItemStack stack = new ItemStack(cycle.get(slot));
                if (stackTag != null) {
                    stack.setTag(stackTag.copy());
                }

                if (minecraft != null) {

                    AllGuiTextures.TOOLBELT_SLOT.render(ms, 0, 0, this);
                    GuiGameElement.of(stack)
                            .at(3, 3)
                            .render(ms);


                    if (selected) {
                        AllGuiTextures.TOOLBELT_SLOT_HIGHLIGHT.render(ms, -1, -1, this);
                        tip = Components.empty().append(stack.getHoverName())
                                .withStyle(ChatFormatting.GOLD);
                    }
                }
            } else {
                AllGuiTextures.TOOLBELT_EMPTY_SLOT.render(ms, 0, 0, this);
            }

            ms.popPose();
        }

        /*
        end core rendering
         */

        {
            int i1 = (int) (fade * 255.0F);
            if (i1 > 255)
                i1 = 255;

            if (i1 > 8) {
                ms.pushPose();
                ms.translate(0, -80, 0.0F);
                drawComponent(ms, title, i1);
            }
        }


        ms.popPose();

        if (tip != null) {
            int i1 = (int) (fade * 255.0F);
            if (i1 > 255)
                i1 = 255;

            if (i1 > 8) {
                ms.pushPose();
                ms.translate((float) (width / 2), (float) (height - 68), 0.0F);
                drawComponent(ms, tip, i1);
            }
        }
    }

    @SuppressWarnings("DuplicatedCode")
    private void drawComponent(PoseStack ms, Component title, int i1) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        int k1 = 16777215;
        int k = i1 << 24 & -16777216;
        int l = font.width(title);
        font.draw(ms, title, (float) (-l / 2), -4.0F, k1 | k);
        RenderSystem.disableBlend();
        ms.popPose();
    }

    @Override
    public void renderBackground(@NotNull PoseStack ms, int vOffset) {
        int a = ((int) (0x50 * Math.min(1, (ticksOpen + AnimationTickHolder.getPartialTicks()) / 20f))) << 24;
        fillGradient(ms, 0, 0, this.width, this.height, 0x101010 | a, 0x101010 | a);
    }

    @Override
    public void tick() {
        ticksOpen++;
        super.tick();
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        int selected = scrollMode ? scrollSlot : hoveredSlot;

        if (button == 0) {
            if (selected >= 0 && selected < cycle.size()) {
                TagCycleHandlerClient.select(cycle.get(selected));
                onClose();
                BogeyMenuEventsHandler.COOLDOWN = 2;
                TagCycleHandlerClient.COOLDOWN = 2;
                AccessorToolboxHandlerClient.setCOOLDOWN(2);
                return true;
            }
        }

        return super.mouseClicked(x, y, button);
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        Window window = Minecraft.getInstance().getWindow();
        double hoveredX = mouseX - window.getGuiScaledWidth() / 2;
        double hoveredY = mouseY - window.getGuiScaledHeight() / 2;
        double distance = hoveredX * hoveredX + hoveredY * hoveredY;
        if (distance <= 150) {
            scrollMode = true;
            scrollSlot = (((int) (scrollSlot - delta)) + 8) % 8;
            for (int i = 0; i < 10; i++) {

                if (scrollSlot < cycle.size())
                    break;

                scrollSlot -= Mth.sign(delta);
                scrollSlot = (scrollSlot + 8) % 8;
            }
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyReleased(int code, int scanCode, int modifiers) {
        InputConstants.Key mouseKey = InputConstants.getKey(code, scanCode);
        if (ClientUtils.isActiveAndMatches(CRKeys.CYCLE_MENU.getKeybind(), mouseKey)) {
            onClose();
            return true;
        }
        return super.keyReleased(code, scanCode, modifiers);
    }
}
