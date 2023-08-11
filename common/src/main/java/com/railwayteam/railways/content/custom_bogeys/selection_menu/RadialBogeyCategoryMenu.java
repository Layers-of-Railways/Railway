package com.railwayteam.railways.content.custom_bogeys.selection_menu;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.railwayteam.railways.content.custom_bogeys.CategoryIcon;
import com.railwayteam.railways.mixin.client.AccessorToolboxHandlerClient;
import com.railwayteam.railways.registry.CRBogeyStyles;
import com.railwayteam.railways.registry.CRIcons;
import com.railwayteam.railways.registry.CRTrackMaterials.CRTrackType;
import com.railwayteam.railways.util.Utils;
import com.simibubi.create.AllKeys;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.bogey.BogeySizes.BogeySize;
import com.simibubi.create.content.trains.bogey.BogeyStyle;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import com.simibubi.create.foundation.gui.widget.Indicator;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        try {
            RenderSystem.runAsFancy(() -> render.accept(new RenderInfo(ms, bufferSource, 0xF000F0)));
        } catch (Exception e) {
            if (Utils.isDevEnv())
                throw e;
        }
        bufferSource.endBatch();

        modelViewStack.popPose();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }

    private static final Map<BogeyStyle, List<Pair<BogeyStyle, BogeySize>>> CACHED_RENDER_CYCLES = new HashMap<>();
    private static final Map<BogeyStyle, boolean[]> CACHED_COMPATS = new HashMap<>();
    private static final Map<ResourceLocation, Indicator.State[]> CACHED_CATEGORY_COMPATS = new HashMap<>();

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

        boolean selectedAny = false;

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
                    selectedAny |= selected;
                    ResourceLocation id = BogeyCategoryHandlerClient.getCategoryId(slot);
                    ItemLike icon = BogeyCategoryHandlerClient.getCategoryIcon(id).get();

                    if (minecraft != null) {

                        AllGuiTextures.TOOLBELT_SLOT.render(ms, 0, 0, this);
                        if (icon instanceof CategoryIcon categoryIcon) {
                            renderIcon(categoryIcon, ms);
                        } else {
                            GuiGameElement.of(icon)
                                    .at(3, 3)
                                    .render(ms);
                        }


                        if (selected) {
                            AllGuiTextures.TOOLBELT_SLOT_HIGHLIGHT.render(ms, -1, -1, this);
                            tip = Components.translatable("railways.style_select.category." + id.getNamespace() + "." + id.getPath())
                                    .withStyle(ChatFormatting.GOLD);

                            if (id != MANAGE_FAVORITES_CATEGORY) {
                                if (id == BogeyCategoryHandlerClient.FAVORITES_CATEGORY)
                                    CACHED_CATEGORY_COMPATS.remove(BogeyCategoryHandlerClient.FAVORITES_CATEGORY);
                                Indicator.State[] compats = CACHED_CATEGORY_COMPATS.computeIfAbsent(id, (k) -> {
                                    boolean[] anyOk = new boolean[] {false, false, false};
                                    boolean[] allOk = new boolean[] {true, true, true};
                                    boolean hasContents = false;
                                    for (BogeyStyle style : BogeyCategoryHandlerClient.getStylesInCategory(id).values()) {
                                        if (CRBogeyStyles.hideInSelectionMenu(style))
                                            continue;
                                        hasContents = true;
                                        boolean[] c = new boolean[3];
                                        c[0] = CRBogeyStyles.styleFitsTrack(style, CRTrackType.NARROW_GAUGE);
                                        c[1] = CRBogeyStyles.styleFitsTrack(style, TrackType.STANDARD);
                                        c[2] = CRBogeyStyles.styleFitsTrack(style, CRTrackType.WIDE_GAUGE);

                                        for (BogeyStyle subStyle : CRBogeyStyles.getSubStyles(style)) {
                                            c[0] |= CRBogeyStyles.styleFitsTrack(subStyle, CRTrackType.NARROW_GAUGE);
                                            c[1] |= CRBogeyStyles.styleFitsTrack(subStyle, TrackType.STANDARD);
                                            c[2] |= CRBogeyStyles.styleFitsTrack(subStyle, CRTrackType.WIDE_GAUGE);
                                        }

                                        for (int i = 0; i < 3; i++) {
                                            anyOk[i] |= c[i];
                                            allOk[i] &= c[i];
                                        }
                                    }
                                    Indicator.State[] states = new Indicator.State[3];
                                    for (int i = 0; i < 3; i++) {
                                        if (!hasContents) {
                                            states[i] = Indicator.State.OFF;
                                        } else if (allOk[i]) {
                                            states[i] = Indicator.State.GREEN;
                                        } else if (anyOk[i]) {
                                            states[i] = Indicator.State.YELLOW;
                                        } else {
                                            states[i] = Indicator.State.RED;
                                        }
                                    }
                                    return states;
                                });

                                PoseStack ms2 = new PoseStack();
                                ms2.translate(width / 2, height / 2, 0);
                                renderCompatLabels(ms2, compats[0], compats[1], compats[2]);
                            }
                        }
                    }
                } else {
                    AllGuiTextures.TOOLBELT_EMPTY_SLOT.render(ms, 0, 0, this);
                }
            } else if (state == State.PICK_STYLE) {
                if (slot < BogeyCategoryHandlerClient.styleCount(selectedCategory)) {
                    selectedAny |= selected;
                    /* render bogey */
                    BogeyStyle style = BogeyCategoryHandlerClient.getStyle(selectedCategory, slot);

                    int finalSlot = slot;
                    List<Pair<BogeyStyle, BogeySize>> renderCycle = CACHED_RENDER_CYCLES.computeIfAbsent(style, (s) -> {
                        List<Pair<BogeyStyle, BogeySize>> cycle = new ArrayList<>();
                        {
                            BogeySize size = BogeyCategoryHandlerClient.getSize(selectedCategory, finalSlot);
                            if (size == null) {
                                for (BogeySize size1 : style.validSizes()) {
                                    cycle.add(Pair.of(style, size1));
                                }
                            } else {
                                cycle.add(Pair.of(style, size));
                            }
                        }

                        for (BogeyStyle subStyle : CRBogeyStyles.getSubStyles(style)) {
                            for (BogeySize size1 : subStyle.validSizes()) {
                                cycle.add(Pair.of(subStyle, size1));
                            }
                        }
                        return cycle;
                    });

                    int cycleIdx = ticksOpen / 40;
                    Pair<BogeyStyle, BogeySize> renderPair = renderCycle.get(cycleIdx % renderCycle.size());
                    BogeyStyle renderStyle = renderPair.getFirst();
                    BogeySize renderSize = renderPair.getSecond();

                    //BogeyRenderer renderer = style.getInWorldRenderInstance(size);
                    Block renderBlock = style.getBlockOfSize(renderSize);
                    if (renderBlock instanceof AbstractBogeyBlock<?> bogeyBlock && minecraft != null) {

                        double bogeyX = Math.cos(angleRad) * radius;
                        double bogeyY = Math.sin(angleRad) * radius;

                        Consumer<RenderInfo> render = (info) -> {
                            PoseStack ms2 = info.ms;
                            ms2.pushPose();
                            //ms2.translate(-0.5, -0.5, -0.5);
                            BlockState bogeyState = renderBlock.defaultBlockState().setValue(AbstractBogeyBlock.AXIS, Direction.Axis.Z);
                            minecraft.getBlockRenderer().renderSingleBlock(
                                    bogeyState, ms2,
                                    info.buffers, info.packedLight, OverlayTexture.NO_OVERLAY
                            );
                            ms2.popPose();
                            bogeyBlock.render(bogeyState, -3 * AnimationTickHolder.getRenderTime(minecraft.level), ms2, partialTicks, info.buffers,
                                    info.packedLight, OverlayTexture.NO_OVERLAY, renderStyle, new CompoundTag());
                        };

                        if (selected) {
                            renderInInventory(guiLeft - 130, guiTop, 30, render);
                            tip = Components.empty().append(style.displayName)//style.displayName
                                    .withStyle(ChatFormatting.GOLD);

                            boolean[] compats = CACHED_COMPATS.computeIfAbsent(style, (k) -> {
                                boolean[] c = new boolean[] {false, false, false};
                                for (Pair<BogeyStyle, BogeySize> pair : renderCycle) {
                                    c[0] |= CRBogeyStyles.styleFitsTrack(pair.getFirst(), CRTrackType.NARROW_GAUGE);
                                    c[1] |= CRBogeyStyles.styleFitsTrack(pair.getFirst(), TrackType.STANDARD);
                                    c[2] |= CRBogeyStyles.styleFitsTrack(pair.getFirst(), CRTrackType.WIDE_GAUGE);
                                }
                                return c;
                            });

                            PoseStack ms2 = new PoseStack();
                            ms2.translate(width / 2, height / 2, 0);
                            renderCompatLabels(ms2, compats[0], compats[1], compats[2]);
                        }

                        if (BogeyCategoryHandlerClient.hasIcon(style, renderSize)) {
                            AllGuiTextures.TOOLBELT_SLOT.render(ms, 0, 0, this);
                            renderIcon(BogeyCategoryHandlerClient.getIcon(style, renderSize), ms);
                            if (selected) {
                                AllGuiTextures.TOOLBELT_SLOT_HIGHLIGHT.render(ms, -1, -1, this);
                            }
                        } else {
                            renderInInventory(guiLeft + bogeyX, guiTop + bogeyY, selected ? 10 : 8, render);
                        }

                    }

                    /* end render bogey */
                    //*
                } else { // */
                    AllGuiTextures.TOOLBELT_EMPTY_SLOT.render(ms, 0, 0, this);
                }
            }

            ms.popPose();
        }

        if ((state == State.PICK_STYLE || state == State.PICK_CATEGORY) && !selectedAny) {
            renderCompatLabels(ms);
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

    private void renderCompatLabels(PoseStack ms) {
        renderCompatLabels(ms, Indicator.State.OFF, Indicator.State.OFF, Indicator.State.OFF);
    }

    private void renderCompatLabels(PoseStack ms, boolean narrowCompat, boolean standardCompat, boolean wideCompat) {
        renderCompatLabels(ms,
            narrowCompat ? Indicator.State.GREEN : Indicator.State.RED,
            standardCompat ? Indicator.State.GREEN : Indicator.State.RED,
            wideCompat ? Indicator.State.GREEN : Indicator.State.RED);
    }

    private void renderCompatLabels(PoseStack ms, Indicator.State narrowState, Indicator.State standardState, Indicator.State wideState) {
        ms.pushPose();
        ms.translate(-27, (height/2) - 55, 0);
        String[] labels = new String[] {"N", "S", "W"};
        Indicator.State[] states = new Indicator.State[] {narrowState, standardState, wideState};
        for (int i = 0; i < 3; i++) {
            ms.pushPose();
            ms.translate(i * 18., 0, 0);
            Indicator.State state = states[i];
            AllGuiTextures toDraw = switch (state) {
                case ON -> AllGuiTextures.INDICATOR_WHITE;
                case OFF -> AllGuiTextures.INDICATOR;
                case RED -> AllGuiTextures.INDICATOR_RED;
                case YELLOW -> AllGuiTextures.INDICATOR_YELLOW;
                case GREEN -> AllGuiTextures.INDICATOR_GREEN;
            };
            toDraw.render(ms, 0, 0);
            {
                ms.pushPose();
                ms.translate(9, 9, 0);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                Component label = Components.literal(labels[i]);
                int l = font.width(label);
                font.drawShadow(ms, label, (float) (-l / 2), 0, 0xFFFFFFFF);
                RenderSystem.disableBlend();
                ms.popPose();
            }

            ms.popPose();
        }
        ms.popPose();
    }

    private void renderIcon(CategoryIcon categoryIcon, PoseStack ms) {
        renderIcon(categoryIcon.location, ms);
    }

    private void renderIcon(ResourceLocation location, PoseStack ms) {
        ms.pushPose();
        RenderSystem.setShaderTexture(0, location);
        GuiComponent.blit(ms, 3, 3, 0, 0, 0, 16, 16, 16, 16);
        ms.popPose();
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
                BogeySize size = BogeyCategoryHandlerClient.getSize(selectedCategory, selected);
                if (favoriteSlot != null) {
                    if (BogeyCategoryHandlerClient.getFavorites().size() <= favoriteSlot) {
                        BogeyCategoryHandlerClient.getFavorites().add(style);
                    } else {
                        BogeyCategoryHandlerClient.getFavorites().remove((int) favoriteSlot);
                        BogeyCategoryHandlerClient.getFavorites().add(favoriteSlot, style);
                    }
                    optimizeFavorites();
                } else {
                    BogeyCategoryHandlerClient.setSelectedStyle(style, size);
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
