package com.railwayteam.railways.compat.incompatible_mods.optifine;

import com.railwayteam.railways.config.CRConfigs;
import com.railwayteam.railways.mixin.client.AccessorWarningScreen;
import com.railwayteam.railways.util.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.WarningScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

@SuppressWarnings("ConstantConditions")
@Environment(EnvType.CLIENT)
public class OptifineWarningScreen extends WarningScreen {
    public OptifineWarningScreen() {
        super(HEADER, MESSAGE, CHECK_MESSAGE, NARRATED_TEXT);
    }

    @Override
    protected void initButtons(int yOffset) {
        addRenderableWidget(
                new Button(width / 2 - 155, 100 + yOffset, 150, 20,
                        OPEN_MODS_FOLDER, buttonWidget -> Util.getPlatform().openFile(Utils.modsDir().toFile())
                )
        );

        addRenderableWidget(
                new Button(width / 2 - 155 + 160, 100 + yOffset, 150, 20,
                        OPTIFINE_ALTERNATIVES, buttonWidget -> Util.getPlatform().openUri(
                                "https://optifine.alternatives.lambdaurora.dev/"
                        )
                )
        );

        addRenderableWidget(
                new Button(width / 2 - 75, 130 + yOffset, 150, 20,
                        PROCEED_ANYWAY,
                        buttonWidget -> {
                            CRConfigs.client().disableOptifineWarning.set(true);
                            Minecraft.getInstance().setScreen(new TitleScreen());
                        }
                )
        );
    }

    @Override
    protected void init() {
        ((AccessorWarningScreen) this).setMessageText(MultiLineLabel.create(font, MESSAGE, width - 50));
        int yOffset = (((AccessorWarningScreen) this).getMessageText().getLineCount() + 1) * font.lineHeight * 2 - 20;
        initButtons(yOffset);
    }


    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    private static final MutableComponent HEADER = Component.translatable("header.railways.optifine").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD);
    private static final Component MESSAGE = Component.translatable("message.railways.optifine");
    private static final Component CHECK_MESSAGE = Component.translatable("multiplayerWarning.check");
    private static final Component NARRATED_TEXT = HEADER.copy().append("\n").append(MESSAGE);

    private static final Component OPEN_MODS_FOLDER = Component.translatable("label.railways.open_mods_folder");
    private static final Component OPTIFINE_ALTERNATIVES = Component.translatable("label.railways.optifine_alternatives");
    private static final Component PROCEED_ANYWAY = Component.translatable("label.railways.proceed_anyway");

}
