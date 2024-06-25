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

package com.railwayteam.railways.content.bogey_menu;

import com.google.common.collect.ImmutableList;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.railwayteam.railways.api.bogeymenu.v0.entry.BogeyEntry;
import com.railwayteam.railways.api.bogeymenu.v0.entry.CategoryEntry;
import com.railwayteam.railways.content.bogey_menu.components.BogeyMenuButton;
import com.railwayteam.railways.content.bogey_menu.handler.BogeyMenuHandlerClient;
import com.railwayteam.railways.impl.bogeymenu.v0.BogeyMenuManagerImpl;
import com.railwayteam.railways.registry.CRGuiTextures;
import com.railwayteam.railways.registry.CRIcons;
import com.railwayteam.railways.registry.CRPackets;
import com.railwayteam.railways.util.client.ClientTextUtils;
import com.railwayteam.railways.util.packet.BogeyStyleSelectionPacket;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import com.simibubi.create.foundation.gui.widget.*;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class BogeyMenuScreen extends AbstractSimiScreen {
    private final CRGuiTextures background = CRGuiTextures.BOGEY_MENU;
    // The names of bogey categories
    private final List<Component> categoryComponentList = BogeyMenuManagerImpl.CATEGORIES.stream()
            .map(CategoryEntry::getName)
            .toList();
    // The category that is currently selected
    private CategoryEntry selectedCategory = BogeyMenuManagerImpl.CATEGORIES.get(0);
    private int categoryIndex = 0; // for the scroll input on window resize
    // The list of bogies being displayed
    BogeyEntry[] bogeyList = new BogeyEntry[6];
    // The list of bogey selection buttons
    BogeyMenuButton[] bogeyMenuButtons = new BogeyMenuButton[6];
    // The bogey that is currently selected
    BogeyEntry selectedBogey;
    // Amount scrolled, 0 = top and 1 = bottom
    private float scrollOffs;
    // True if the scrollbar is being dragged
    private boolean scrolling;
    private int ticksOpen;
    private boolean soundPlayed;
    private TooltipArea longBogeyTooltipArea;
    private IconButton favouriteButton;

    @Override
    protected void init() {
        setWindowSize(background.width, background.height);
        super.init();
        clearWidgets();

        int x = guiLeft;
        int y = guiTop;

        // Need buttons first, otherwise setupList will crash
        for (int i = 0; i < 6; i++) {
            addRenderableWidget(bogeyMenuButtons[i] = new BogeyMenuButton(x + 19, y + 41 + (i * 18), 82, 17, bogeySelection(i)));
        }

        // Initial setup
        setupList(selectedCategory);
        selectedBogey = selectedCategory.getBogeyEntryList().get(0);

        // Scrolling Initial setup
        scrollOffs = 0;
        scrollTo(0);

        // Category selector START
        Label categoryLabel = new Label(x + 14, y + 25, Components.immutableEmpty()).withShadow();
        ScrollInput categoryScrollInput = new SelectionScrollInput(x + 9, y + 20, 77, 18)
                .forOptions(categoryComponentList)
                .writingTo(categoryLabel)
                .setState(categoryIndex)
                .calling(categoryIndex -> {
                    scrollOffs = 0.0F;
                    scrollTo(0.0F);
                    this.categoryIndex = categoryIndex;
                    setupList(selectedCategory = BogeyMenuManagerImpl.CATEGORIES.get(categoryIndex));

                    if (!selectedCategory.getBogeyEntryList().isEmpty()) {
                        selectedBogey = selectedCategory.getBogeyEntryList().get(0);
                    } else {
                        selectedBogey = null;
                    }
                });

        addRenderableWidget(categoryLabel);
        addRenderableWidget(categoryScrollInput);

        favouriteButton = new IconButton(x + background.width - 167, y + background.height - 49, CRIcons.I_FAVORITE);
        favouriteButton.withCallback(this::onFavorite);
        addRenderableWidget(favouriteButton);

        // Close Button
        IconButton closeButton = new IconButton(x + background.width - 33, y + background.height - 24, AllIcons.I_CONFIRM);
        closeButton.withCallback(this::onMenuClose);
        addRenderableWidget(closeButton);


        String[] gaugeText = new String[] {"narrow", "standard", "wide"};
        for (int i = 0; i < 3; i++) {
            addRenderableOnly(new TooltipArea(x + 163 + (i * 18), y + 135, 18, 18)
                    .withTooltip(ImmutableList.of(
                            Component.translatable("railways.gui.bogey_menu.gauge_description")
                                    .withStyle(s -> s.withColor(0x5391E1)),
                            Component.translatable("railways.gui.bogey_menu." + gaugeText[i] + "_gauge")
                                    .withStyle(ChatFormatting.GRAY)
                    ))
            );
        }

        longBogeyTooltipArea = new TooltipArea(x + 122, y + 20, 136, 18);
        addRenderableOnly(longBogeyTooltipArea);
    }

    @Override
    protected void renderWindow(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;

        // Render Background
        background.render(ms, x, y, 512, 512);

        // Header (Bogey Preview Text) START
        MutableComponent header = Component.translatable("railways.gui.bogey_menu.title");
        int halfWidth = background.width / 2;
        int halfHeaderWidth = font.width(header) / 2;
        font.draw(ms, header, x + halfWidth - halfHeaderWidth, y + 4, 0x582424);

        // Train casing on right side of screen where arrow is pointing START
        ms.pushPose();

        TransformStack msr = TransformStack.cast(ms);
        msr.pushPose()
                .translate(x + background.width + 4, y + background.height + 4, 100)
                .scale(40)
                .rotateX(-22)
                .rotateY(63);

        GuiGameElement.of(AllBlocks.RAILWAY_CASING.getDefaultState()).render(ms);

        ms.popPose();

        // Render scroll bar
        // Formula is barPos = startLoc + (endLoc - startLoc) * scrollOffs
        int scrollBarPos = (int) (41 + (134 - 41) * scrollOffs);
        CRGuiTextures barTexture = canScroll() ? CRGuiTextures.BOGEY_MENU_SCROLL_BAR : CRGuiTextures.BOGEY_MENU_SCROLL_BAR_DISABLED;
        barTexture.render(ms, x + 11, y + scrollBarPos, 512, 512);

        // Render the bogey icons & bogey names
        for (int i = 0; i < 6; i++) {
            BogeyEntry bogeyEntry = bogeyList[i];
            if (bogeyEntry != null) {
                // Icon
                ResourceLocation icon = bogeyEntry.iconLocation();
                if (icon != null)
                    renderIcon(icon, ms, x + 20, y + 42 + (i * 18));

                // Text
                Component bogeyName = ClientTextUtils.getComponentWithWidthCutoff(bogeyEntry.bogeyStyle().displayName, 55);
                // button has already been added in init, now just draw text
                font.drawShadow(ms, bogeyName, x + 40, y + 46 + (i * 18), 0xFFFFFF);
            }
        }

        // Draw bogey name, gauge indicators and render bogey
        if (selectedBogey != null) {
            Component displayName = selectedBogey.bogeyStyle().displayName;
            // Bogey Name
            Component bogeyName = ClientTextUtils.getComponentWithWidthCutoff(displayName, 126);
            drawCenteredString(ms, font, bogeyName, x + 190, y + 25, 0xFFFFFF);

            if (font.width(displayName) > 126) {
                longBogeyTooltipArea.withTooltip(ImmutableList.of(displayName));
                longBogeyTooltipArea.visible = true;
            } else {
                longBogeyTooltipArea.visible = false;
            }

            // Gauge Indicators
            Indicator.State[] states = BogeyMenuHandlerClient.getTrackCompat(selectedBogey);
            for (int i = 0; i < 3; i++) {
                AllGuiTextures indicator = switch (states[i]) {
                    case ON -> AllGuiTextures.INDICATOR_WHITE;
                    case OFF -> AllGuiTextures.INDICATOR;
                    case RED -> AllGuiTextures.INDICATOR_RED;
                    case YELLOW -> AllGuiTextures.INDICATOR_YELLOW;
                    case GREEN -> AllGuiTextures.INDICATOR_GREEN;
                };
                indicator.render(ms, x + 163 + (i * 18), y + 128);
            }

            // Render Bogey
            BogeyStyle style = selectedBogey.bogeyStyle();

            float bogeyScale = selectedBogey.scale();

            List<Pair<BogeyStyle, BogeySizes.BogeySize>> renderCycle = BogeyMenuHandlerClient.getRenderCycle(style);

            Pair<BogeyStyle, BogeySizes.BogeySize> renderPair = renderCycle.get((ticksOpen / 40) % renderCycle.size());
            BogeyStyle renderStyle = renderPair.getFirst();
            BogeySizes.BogeySize renderSize = renderPair.getSecond();

            if (BogeyMenuManagerImpl.SIZES_TO_SCALE.containsKey(renderPair)) {
                bogeyScale = BogeyMenuManagerImpl.SIZES_TO_SCALE.get(renderPair);
            }

            Block renderBlock = style.getBlockOfSize(renderSize);
            BlockState bogeyState = renderBlock.defaultBlockState().setValue(AbstractBogeyBlock.AXIS, Direction.Axis.Z);
            if (minecraft == null || !(renderBlock instanceof AbstractBogeyBlock<?> bogeyBlock)) return;

            float defaultScale = BogeyMenuManagerImpl.defaultScale;
            float scalePercentage = bogeyScale / defaultScale;

            // Push current pose and Setup model view
            ms.pushPose();
            PoseStack modelViewStack = RenderSystem.getModelViewStack();
            modelViewStack.pushPose();
            modelViewStack.translate(18 * scalePercentage, 6 * scalePercentage, 0);
            modelViewStack.translate(x + 189.5, y + 86, 1500);
            modelViewStack.scale(1, 1, -1);
            RenderSystem.applyModelViewMatrix();

            // Setup pose and lighting correctly
            ms.translate(0, 0, 1000);
            ms.scale(bogeyScale, bogeyScale, bogeyScale);
            Quaternion zRot = Vector3f.ZP.rotationDegrees(180);
            Quaternion xRot = Vector3f.XP.rotationDegrees(-20);
            Quaternion yRot = Vector3f.YP.rotationDegrees(45);
            zRot.mul(xRot);
            zRot.mul(yRot);
            ms.mulPose(zRot);
            Lighting.setupForEntityInInventory();

            // Setup vars for rendering
            Minecraft mc = Minecraft.getInstance();
            MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
            int light = 0xF000F0;
            int overlay = OverlayTexture.NO_OVERLAY;
            float wheelAngle = -3 * AnimationTickHolder.getRenderTime(minecraft.level);

            // Render Bogey Block & Bogey
            minecraft.getBlockRenderer().renderSingleBlock(bogeyState, ms, bufferSource, light, overlay);
            bogeyBlock.render(bogeyState, wheelAngle, ms, partialTicks, bufferSource, light, overlay, renderStyle, new CompoundTag());

            // End batch, pop modelViewStack & apply and pop the pose
            bufferSource.endBatch();
            modelViewStack.popPose();
            RenderSystem.applyModelViewMatrix();
            ms.popPose();

            // Clear depth rectangle to allow proper tooltips
            {
                double x0 = x + 120;
                double y0 = y + 48;
                double w = 140;
                double h = 77;
                double bottom = y0+h;

                Window window = mc.getWindow();
                double scale = window.getGuiScale();

                RenderSystem.clearDepth(0.5); // same depth as gui
                RenderSystem.enableScissor((int) (x0*scale), window.getHeight() - (int) (bottom*scale), (int) (w*scale), (int) (h*scale));

                RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, false);

                RenderSystem.disableScissor();
                RenderSystem.clearDepth(1.0);
            }
        }
    }

    private void renderIcon(ResourceLocation icon, PoseStack ms, int x, int y) {
        ms.pushPose();
        RenderSystem.setShaderTexture(0, icon);
        blit(ms, x, y, 0, 0, 0, 16, 16, 16, 16);
        ms.popPose();
    }

    private void setupList(CategoryEntry categoryEntry) {
        setupList(categoryEntry, 0);
    }

    private void setupList(CategoryEntry categoryEntry, int offset) {
        List<BogeyEntry> bogies = categoryEntry.getBogeyEntryList();

        // Max of 6 slots, objects inside the slots will be mutated later
        for (int i = 0; i < 6; i++) {
            if (i < bogies.size()) {
                bogeyList[i] = bogies.get(i+offset);
                bogeyMenuButtons[i].active = true;
            } else {
                // I know, this is silly but its best way to know if rendering should be skipped
                bogeyList[i] = null;
                bogeyMenuButtons[i].active = false;
            }
        }
    }

    @Override
    public void tick() {
        ticksOpen++;
        soundPlayed = false;
        super.tick();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (selectedBogey == null)
                onClose();
            else
                onMenuClose();

            return true;
        } else if (keyCode == GLFW.GLFW_KEY_ENTER) {
            onMenuClose();

            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (insideScrollbar(mouseX, mouseY)) {
                scrolling = canScroll();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0)
            scrolling = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (!scrolling) return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);

        int scrollbarLeft = guiTop + 41;
        int scrollbarRight = scrollbarLeft + 108;
        float scrollFactor = (float) ((mouseY - scrollbarLeft - 7.5F) / (scrollbarRight - scrollbarLeft - 15.0F));
        scrollOffs = Mth.clamp(scrollFactor, 0.0F, 1.0F);
        scrollTo(scrollOffs);

        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        super.mouseScrolled(mouseX, mouseY, delta);
        if (!canScroll()) return false;
        if (insideCategorySelector(mouseX, mouseY)) return false;
        if (selectedCategory.getBogeyEntryList().size() < 6) return false;

        double listSize = selectedCategory.getBogeyEntryList().size() - 6;
        float scrollFactor = (float) (delta / listSize);

        final float oldScrollOffs = scrollOffs;

        scrollOffs = Mth.clamp(scrollOffs - scrollFactor, 0.0F, 1.0F);
        scrollTo(scrollOffs);

        if (!soundPlayed && scrollOffs != oldScrollOffs)
            Minecraft.getInstance()
                    .getSoundManager()
                    .play(SimpleSoundInstance.forUI(AllSoundEvents.SCROLL_VALUE.getMainEvent(),
                            1.5f + 0.1f * scrollOffs));
        soundPlayed = true;

        return true;
    }

    private void scrollTo(float pos) {
        List<BogeyEntry> bogies = selectedCategory.getBogeyEntryList();
        float listSize = bogies.size() - 6;
        int index = (int) ((double) (pos * listSize) + 0.5);

        setupList(selectedCategory, index);
    }

    private boolean canScroll() {
        return selectedCategory.getBogeyEntryList().size() > 6;
    }

    private boolean insideCategorySelector(double mouseX, double mouseY) {
        int scrollbarLeftX = guiLeft + 11;
        int scrollbarTopY = guiTop + 20;
        int scrollbarRightX = scrollbarLeftX + 90;
        int scrollbarBottomY = scrollbarTopY + 34;

        return mouseX >= scrollbarLeftX && mouseY >= scrollbarTopY && mouseX < scrollbarRightX && mouseY < scrollbarBottomY;
    }

    private boolean insideScrollbar(double mouseX, double mouseY) {
        int scrollbarLeftX = guiLeft + 11;
        int scrollbarTopY = guiTop + 41;
        int scrollbarRightX = scrollbarLeftX + 8;
        int scrollbarBottomY = scrollbarTopY + 108;

        return mouseX >= scrollbarLeftX && mouseY >= scrollbarTopY && mouseX < scrollbarRightX && mouseY < scrollbarBottomY;
    }

    private Button.OnPress bogeySelection(int index) {
        return b -> {
            selectedBogey = bogeyList[index];
            updateFavorites();
        };
    }

    private void onFavorite() {
        if (selectedBogey != null) {
            BogeyMenuHandlerClient.toggleFavorite(selectedBogey.bogeyStyle());
            updateFavorites();
        }
    }

    private void updateFavorites() {
        favouriteButton.setIcon(BogeyMenuHandlerClient.isFavorited(selectedBogey.bogeyStyle())
            ? CRIcons.I_FAVORITED
            : CRIcons.I_FAVORITE
        );

        if (selectedCategory == CategoryEntry.FavoritesCategory.INSTANCE) {
            scrollTo(scrollOffs);
        }
    }

    private void onMenuClose() {
        if (selectedBogey == null) return;

        BogeyStyle style = selectedBogey.bogeyStyle();
        BogeySizes.BogeySize size = BogeyMenuHandlerClient.getSize(style);

        CRPackets.PACKETS.send(new BogeyStyleSelectionPacket(style, size));

        onClose();
    }
}
