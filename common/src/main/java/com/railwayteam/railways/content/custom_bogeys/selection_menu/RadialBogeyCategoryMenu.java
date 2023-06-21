package com.railwayteam.railways.content.custom_bogeys.selection_menu;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.railwayteam.railways.mixin.client.AccessorToolboxHandlerClient;
import com.railwayteam.railways.registry.CRIcons;
import com.railwayteam.railways.util.Utils;
import com.simibubi.create.AllKeys;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.BogeySizes.BogeySize;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static com.railwayteam.railways.content.custom_bogeys.selection_menu.BogeyCategoryHandlerClient.MANAGE_FAVORITES_CATEGORY;
import static com.railwayteam.railways.content.custom_bogeys.selection_menu.BogeyCategoryHandlerClient.optimizeFavorites;

public class RadialBogeyCategoryMenu extends AbstractSimiScreen {

    private State state;
    private int ticksOpen;
    private int hoveredSlot;
    private boolean scrollMode;
    private int scrollSlot = 0;

    @Nullable
    private ResourceLocation selectedCategory;

    @Nullable
    private Integer favoriteSlot; // should be index to store in

    private static final int CENTER = -5;

    private static final int MANAGE_FAVORITES = -7;

    public RadialBogeyCategoryMenu(State state) {
        this.state = state;
        hoveredSlot = -1;
    }

    private record RenderInfo(PoseStack ms, MultiBufferSource.BufferSource buffers, int packedLight) {}

    private void renderInInventory(double posX, double posY, int scale, Consumer<RenderInfo> render) {
        PoseStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushPose();
        modelViewStack.translate(posX, posY, 1050.0);
        modelViewStack.scale(1.0f, 1.0f, -1.0f);
        RenderSystem.applyModelViewMatrix();
        PoseStack ms = new PoseStack();
        ms.translate(0.0, 0.0, 1000.0);
        ms.scale(scale, scale, scale);
        Quaternion zp180 = Vector3f.ZP.rotationDegrees(180.0f);
        Quaternion xRot = Vector3f.XP.rotationDegrees(-20.0f);
        Quaternion yRot = Vector3f.YP.rotationDegrees(45.0f);
        zp180.mul(xRot);
        zp180.mul(yRot);
        ms.mulPose(zp180);
        Lighting.setupForEntityInInventory();

        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderSystem.runAsFancy(() -> render.accept(new RenderInfo(ms, bufferSource, 0xF000F0)));
        bufferSource.endBatch();

        modelViewStack.popPose();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }

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
        boolean renderCenterSlot = false;//state == State.PICK_STYLE;
        if (scrollMode && distance > 150)
            scrollMode = false;
        if (renderCenterSlot && distance <= 150)
            hoveredSlot = CENTER;

        ms.pushPose();
        ms.translate(width / 2, height / 2, 0);
        Component tip = null;
        Component title = null;

        /*
        core rendering
         */

        if (favoriteSlot == null && selectedCategory != MANAGE_FAVORITES_CATEGORY) {
            if (hoveredX > 60 && hoveredX < 100 && hoveredY > -20 && hoveredY < 20)
                hoveredSlot = MANAGE_FAVORITES;

            ms.pushPose();
            ms.translate(80 + (-5 * (1 - fade) * (1 - fade)), 0, 0);
            AllGuiTextures.TOOLBELT_SLOT.render(ms, -12, -12, this);
            ms.translate(-0.5, 0.5, 0);
            CRIcons.I_FAVORITE.render(ms, -9, -9, this);
            ms.translate(0.5, -0.5, 0);
            if (!scrollMode && hoveredSlot == MANAGE_FAVORITES) {
                AllGuiTextures.TOOLBELT_SLOT_HIGHLIGHT.render(ms, -13, -13, this);
                tip = Components.translatable(favoriteSlot == null ? "railways.style_select.manage_favorites" : "railways.style_select.managing_favorites")
                        .withStyle(ChatFormatting.GOLD);
            }
            ms.popPose();
        }


        if (favoriteSlot != null) {
            title = Components.translatable("railways.style_select.title.favorites.pick_style")
                    .withStyle(ChatFormatting.LIGHT_PURPLE);
        } else if (selectedCategory == MANAGE_FAVORITES_CATEGORY) {
            title = Components.translatable("railways.style_select.title.favorites.pick_slot")
                    .withStyle(ChatFormatting.LIGHT_PURPLE);
        } else if (selectedCategory != null) {
            title = Components.translatable("railways.style_select.category")
                    .append(Components.translatable(
                            "railways.style_select.category." + selectedCategory.getNamespace() + "." + selectedCategory.getPath()
                    )).withStyle(ChatFormatting.GOLD);
        }

        for (int slot = 0; slot < 8; slot++) {
            ms.pushPose();
            double radius = -40 + (10 * (1 - fade) * (1 - fade));
            double angle = slot * 45 - 45;
            double angleRad = Math.toRadians(angle + 90);
            TransformStack.cast(ms)
                    .rotateZ(angle)
                    .translate(0, radius, 0)
                    .rotateZ(-angle);
            ms.translate(-12, -12, 0);

            boolean selected = (slot == (scrollMode ? scrollSlot : hoveredSlot));

            if (state == State.PICK_CATEGORY) {
                if (slot < BogeyCategoryHandlerClient.categoryCount()) {
                    ResourceLocation id = BogeyCategoryHandlerClient.getCategoryId(slot);
                    ItemLike icon = BogeyCategoryHandlerClient.getCategoryIcon(id).get();

                    if (minecraft != null) {

                        AllGuiTextures.TOOLBELT_SLOT.render(ms, 0, 0, this);
                        GuiGameElement.of(icon)
                                .at(3, 3)
                                .render(ms);

                        if (selected) {
                            AllGuiTextures.TOOLBELT_SLOT_HIGHLIGHT.render(ms, -1, -1, this);
                            tip = Components.translatable("railways.style_select.category." + id.getNamespace() + "." + id.getPath())
                                    .withStyle(ChatFormatting.GOLD);
                        }
                    }
                } else {
                    AllGuiTextures.TOOLBELT_EMPTY_SLOT.render(ms, 0, 0, this);
                }
            } else if (state == State.PICK_STYLE) {
                if (slot < BogeyCategoryHandlerClient.styleCount(selectedCategory)) {
                    /* render bogey */
                    ResourceLocation id = BogeyCategoryHandlerClient.getStyleId(selectedCategory, slot);
                    BogeyStyle style = BogeyCategoryHandlerClient.getStyle(selectedCategory, id);
                    int sizeIdx = ticksOpen / 40;
                    BogeySize size = style.validSizes().toArray(BogeySize[]::new)[sizeIdx % style.validSizes().size()];

                    //BogeyRenderer renderer = style.getInWorldRenderInstance(size);
                    Block block = style.getBlockOfSize(size);
                    if (block instanceof AbstractBogeyBlock<?> bogeyBlock && minecraft != null) {

                        double bogeyX = Math.cos(angleRad) * radius;
                        double bogeyY = Math.sin(angleRad) * radius;

                        Consumer<RenderInfo> render = (info) -> {
                            PoseStack ms2 = info.ms;
                            ms2.pushPose();
                            ms2.translate(-0.5, -0.5, -0.5);
                            minecraft.getBlockRenderer().renderSingleBlock(
                                    block.defaultBlockState().setValue(AbstractBogeyBlock.AXIS, Direction.Axis.Z), ms2,
                                    info.buffers, info.packedLight, OverlayTexture.NO_OVERLAY
                            );
                            ms2.popPose();
                            bogeyBlock.render(null, 0.0f, ms2, partialTicks, info.buffers,
                                    info.packedLight, OverlayTexture.NO_OVERLAY, style, new CompoundTag());
                        };

                        if (selected) {
                            renderInInventory(guiLeft - 130, guiTop, 30, render);
                            tip = Components.empty().append(style.displayName)//style.displayName
                                    .withStyle(ChatFormatting.GOLD);
                        }

                        renderInInventory(guiLeft + bogeyX, guiTop + bogeyY, selected ? 10 : 8, render);

                    }

                    /* end render bogey */
                    //*
                } else { // */
                    AllGuiTextures.TOOLBELT_EMPTY_SLOT.render(ms, 0, 0, this);
                }
            }

            ms.popPose();
        }

        if (renderCenterSlot) {
            ms.pushPose();
            AllGuiTextures.TOOLBELT_SLOT.render(ms, -12, -12, this);
            AllIcons.I_CONFIG_BACK.render(ms, -9, -9, this);
            if (!scrollMode && CENTER == hoveredSlot) {
                AllGuiTextures.TOOLBELT_SLOT_HIGHLIGHT.render(ms, -13, -13, this);
                tip = Components.translatable("railways.style_select.back_to_groups")
                        .withStyle(ChatFormatting.GOLD);
            }
            ms.popPose();
        }
        /*
        end core rendering
         */

        if (title != null) {
            int i1 = (int) (fade * 255.0F);
            if (i1 > 255)
                i1 = 255;

            if (i1 > 8) {
                ms.pushPose();
                ms.translate(0, -80, 0.0F);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                int k1 = 16777215;
                int k = i1 << 24 & -16777216;
                int l = font.width(title);
                font.draw(ms, title, (float) (-l / 2), -4.0F, k1 | k);
                RenderSystem.disableBlend();
                ms.popPose();
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
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                int k1 = 16777215;
                int k = i1 << 24 & -16777216;
                int l = font.width(tip);
                font.draw(ms, tip, (float) (-l / 2), -4.0F, k1 | k);
                RenderSystem.disableBlend();
                ms.popPose();
            }
        }
    }

    @Override
    public void renderBackground(@NotNull PoseStack ms, int vOffset) {
        int a = ((int) (0x50 * Math.min(1, (ticksOpen + AnimationTickHolder.getPartialTicks()) / 20f))) << 24;
        fillGradient(ms, 0, 0, this.width, this.height, 0x101010 | a, 0x101010 | a);
    }

    @Override
    public void tick() {
        if (state == State.PICK_STYLE && selectedCategory == null)
            state = State.PICK_CATEGORY;
        ticksOpen++;
        super.tick();
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        int selected = scrollMode ? scrollSlot : hoveredSlot;

        if (button == 0) {
            if (selected == MANAGE_FAVORITES) {
                state = State.PICK_STYLE;
                selectedCategory = MANAGE_FAVORITES_CATEGORY;
                return true;
            }
            if (state == State.PICK_CATEGORY && selected >= 0 && selected < BogeyCategoryHandlerClient.categoryCount()) {
                state = State.PICK_STYLE;
                selectedCategory = BogeyCategoryHandlerClient.getCategoryId(selected);
                return true;
            }

            if (state == State.PICK_STYLE && selectedCategory == MANAGE_FAVORITES_CATEGORY) {
                state = State.PICK_CATEGORY;
                favoriteSlot = selected;
                selectedCategory = null;
                return true;
            }

            if (state == State.PICK_STYLE && selected >= 0 && selected < BogeyCategoryHandlerClient.styleCount(selectedCategory)) {
                BogeyStyle style = BogeyCategoryHandlerClient.getStyle(selectedCategory, selected);
                if (favoriteSlot != null) {
                    if (BogeyCategoryHandlerClient.getFavorites().size() <= favoriteSlot) {
                        BogeyCategoryHandlerClient.getFavorites().add(style);
                    } else {
                        BogeyCategoryHandlerClient.getFavorites().remove((int) favoriteSlot);
                        BogeyCategoryHandlerClient.getFavorites().add(favoriteSlot, style);
                    }
                    optimizeFavorites();
                } else {
                    BogeyCategoryHandlerClient.setSelectedStyle(style);
                }
                onClose();
                BogeyCategoryHandlerClient.COOLDOWN = 2;
                AccessorToolboxHandlerClient.setCOOLDOWN(2);
                return true;
            }
            /*if (selected == DEPOSIT) {
                onClose();
                ToolboxHandlerClient.COOLDOWN = 2;
                return true;
            }

            if (state == RadialToolboxMenu.State.SELECT_BOX && selected >= 0 && selected < toolboxes.size()) {
                state = RadialToolboxMenu.State.SELECT_ITEM;
                selectedBox = toolboxes.get(selected);
                return true;
            }

            if (state == RadialToolboxMenu.State.DETACH || state == RadialToolboxMenu.State.SELECT_ITEM || state == RadialToolboxMenu.State.SELECT_ITEM_UNEQUIP) {
                if (selected == UNEQUIP || selected >= 0) {
                    onClose();
                    ToolboxHandlerClient.COOLDOWN = 2;
                    return true;
                }
            }*/
        }

        if (button == 1) { // right click to go back
            if (state == State.PICK_STYLE && selectedCategory != MANAGE_FAVORITES_CATEGORY) {
                state = State.PICK_CATEGORY;
                return true;
            }
        }

        return super.mouseClicked(x, y, button);
    }

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

                if (state == State.PICK_CATEGORY) {
                    if (scrollSlot < BogeyCategoryHandlerClient.categoryCount())
                        break;
                }

                if (state == State.PICK_STYLE) {
                    if (scrollSlot < BogeyCategoryHandlerClient.styleCount(selectedCategory))
                        break;
                }

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

    enum State {
        PICK_CATEGORY, PICK_STYLE
    }
}
