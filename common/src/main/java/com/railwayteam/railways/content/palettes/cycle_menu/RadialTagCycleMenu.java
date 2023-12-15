package com.railwayteam.railways.content.palettes.cycle_menu;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.content.custom_bogeys.selection_menu.BogeyCategoryHandlerClient;
import com.railwayteam.railways.mixin.client.AccessorToolboxHandlerClient;
import com.railwayteam.railways.util.Utils;
import com.simibubi.create.AllKeys;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.List;

public class RadialTagCycleMenu extends AbstractSimiScreen {

    private int ticksOpen;
    private int hoveredSlot;
    private boolean scrollMode;
    private int scrollSlot = 0;
    private final TagKey<Item> tag;
    private final List<Item> cycle;

    RadialTagCycleMenu(TagKey<Item> tag, List<Item> cycle) {
        hoveredSlot = -1;
        this.cycle = cycle;
        this.tag = tag;
    }

    @SuppressWarnings({"IntegerDivisionInFloatingPointContext", "DuplicatedCode"})
    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        float fade = Mth.clamp((ticksOpen + AnimationTickHolder.getPartialTicks()) / 10f, 1 / 512f, 1);

        PoseStack ms = graphics.pose();

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
                ItemLike item = cycle.get(slot);

                if (minecraft != null) {

                    AllGuiTextures.TOOLBELT_SLOT.render(graphics, 0, 0);
                    GuiGameElement.of(item)
                        .at(3, 3)
                        .render(graphics);


                    if (selected) {
                        AllGuiTextures.TOOLBELT_SLOT_HIGHLIGHT.render(graphics, -1, -1);
                        tip = Components.empty().append(item.asItem().getDescription())
                            .withStyle(ChatFormatting.GOLD);
                    }
                }
            } else {
                AllGuiTextures.TOOLBELT_EMPTY_SLOT.render(graphics, 0, 0);
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
                drawComponent(graphics, title, i1);
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
                drawComponent(graphics, tip, i1);
            }
        }
    }

    @SuppressWarnings("DuplicatedCode")
    private void drawComponent(GuiGraphics graphics, Component title, int i1) {
        PoseStack ms = graphics.pose();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        int k1 = 16777215;
        int k = i1 << 24 & -16777216;
        int l = font.width(title);
        graphics.drawString(font, title, (-l / 2), (int) -4.0F, k1 | k);
        RenderSystem.disableBlend();
        ms.popPose();
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics) {
        PoseStack ms = guiGraphics.pose();

        int a = ((int) (0x50 * Math.min(1, (ticksOpen + AnimationTickHolder.getPartialTicks()) / 20f))) << 24;
        guiGraphics.fillGradient(0, 0, this.width, this.height, 0x101010 | a, 0x101010 | a);
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
                BogeyCategoryHandlerClient.COOLDOWN = 2;
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
        if (Utils.isActiveAndMatches(AllKeys.TOOL_MENU.getKeybind(), mouseKey)) {
            onClose();
            return true;
        }
        return super.keyReleased(code, scanCode, modifiers);
    }
}
