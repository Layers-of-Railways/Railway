package com.railwayteam.railways.content.bogey_menu;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.api.bogeymenu.entry.BogeyEntry;
import com.railwayteam.railways.api.bogeymenu.entry.CategoryEntry;
import com.railwayteam.railways.impl.bogeymenu.BogeyMenuManagerImpl;
import com.railwayteam.railways.registry.CRGuiTextures;
import com.railwayteam.railways.registry.CRIcons;
import com.railwayteam.railways.util.client.ClientTextUtils;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.gui.AbstractSimiScreen;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.Label;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.gui.widget.SelectionScrollInput;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class BogeyMenuScreen extends AbstractSimiScreen {
    private final CRGuiTextures background = CRGuiTextures.BOGEY_MENU;
    private final List<Component> categoryComponentList = BogeyMenuManagerImpl.CATEGORIES.stream()
            .map(CategoryEntry::getName)
            .toList();
    // I know its cursed
    private CategoryEntry selectedCategory = BogeyMenuManagerImpl.CATEGORIES.get(0);
    List<BogeyEntry> bogeyList = new ArrayList<>();
    BogeyEntry selectedBogey;
    // Amount scrolled, 0 = top and 1 = bottom
    private float scrollOffs;

    @Override
    protected void init() {
        setWindowSize(background.width, background.height);
        super.init();
        clearWidgets();

        int x = guiLeft;
        int y = guiTop;

        // Initial setup
        setupList(selectedCategory);

        // Scrolling Initial setup
        scrollOffs = 0.0F;
        scrollTo(0.0F);

        // Category selector START
        Label categoryLabel = new Label(x + 14, y + 25, Components.immutableEmpty()).withShadow();
        ScrollInput categoryScrollInput = new SelectionScrollInput(x + 9, y + 20, 77, 18)
                .forOptions(categoryComponentList)
                .titled(Component.translatable("railways.gui.bogey_menu.category"))
                .writingTo(categoryLabel)
                .calling(categoryIndex -> {
                    selectedCategory = BogeyMenuManagerImpl.CATEGORIES.get(categoryIndex);
                    setupList(selectedCategory);
                });

        addRenderableWidget(categoryLabel);
        addRenderableWidget(categoryScrollInput);
        // Category selector END

        // Favourite bogey Button
        IconButton favouriteButton = new IconButton(x + background.width - 167, y + background.height - 49, CRIcons.I_FAVORITE);
        // todo change this
        favouriteButton.withCallback(this::onClose);
        addRenderableWidget(favouriteButton);

        // Close Button
        IconButton closeButton = new IconButton(x + background.width - 33, y + background.height - 24, AllIcons.I_CONFIRM);
        closeButton.withCallback(this::onClose);
        addRenderableWidget(closeButton);
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
        // Header (Bogey Preview Text) END

        // Train casing on right side of screen where arrow is pointing START
        ms.pushPose();

        TransformStack msr = TransformStack.cast(ms);
        msr.pushPose()
                .translate(guiLeft + background.width + 4, guiTop + background.height + 4, 100)
                .scale(40)
                .rotateX(-22)
                .rotateY(63);

        GuiGameElement.of(AllBlocks.RAILWAY_CASING.getDefaultState()).render(ms);

        ms.popPose();
        // Train casing on right side of screen where arrow is pointing END

        // Render the bogey icons & bogey names
        for (int i = 0; i < 6; i++) {
            BogeyEntry bogeyEntry = bogeyList.get(i);
            if (bogeyEntry != null) {
                // Icon
                renderIcon(bogeyEntry.iconLocation(), ms, x + 20, y + 42 + (i * 18));

                // Text
                Component bogeyName = ClientTextUtils.getComponentWithWidthCutoff(bogeyEntry.bogeyStyle().displayName, 55);
                addRenderableWidget(new BogeyButton(x + 19, y + 41 + (i * 18), 82, 17, bogeySelection(i)));
                font.drawShadow(ms, bogeyName, x + 40, y + 46 + (i * 18), 0xFFFFFF);
            }
        }
    }

    private void renderIcon(ResourceLocation icon, PoseStack ms, int x, int y) {
        ms.pushPose();
        RenderSystem.setShaderTexture(0, icon);
        GuiComponent.blit(ms, x, y, 0, 0, 0, 16, 16, 16, 16);
        ms.popPose();
    }

    private void setupList(CategoryEntry categoryEntry) {
        List<BogeyEntry> bogies = categoryEntry.getBogeyEntryList();

        // Clear to get ready for adding new bogies
        bogeyList.clear();

        // Max of 6 slots, objects inside the slots will be mutated later
        for (int i = 0; i < 6; i++) {
            if (i < bogies.size()) {
                bogeyList.add(bogies.get(i));
            } else {
                // I know, this is silly but its best way to know if rendering should be skipped
                bogeyList.add(null);
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (selectedCategory.getBogeyEntryList().size() < 6) return false;

        double listSize = selectedCategory.getBogeyEntryList().size() - 6;
        float scrollFactor = (float) (delta / listSize);
        scrollOffs = Mth.clamp(scrollOffs - scrollFactor, 0.0F, 1.0F);
        scrollTo(scrollOffs);

        return true;
    }

    private void scrollTo(float pos) {
        List<BogeyEntry> bogies = selectedCategory.getBogeyEntryList();
        float listSize = bogies.size() - 6;
        int index = (int) ((double) (pos * listSize) + 0.5);

        for (int i = 0; i < 6; i++) {
            bogeyList.set(i, bogies.get(index + i));
        }
    }

    private Button.OnPress bogeySelection(int index) {
        return b -> selectedBogey = bogeyList.get(index);
    }
}
