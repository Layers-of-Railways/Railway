package com.railwayteam.railways.mixin.client;

import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.multiplayer.WarningScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WarningScreen.class)
public interface AccessorWarningScreen {

    @Accessor("message")
    MultiLineLabel getMessageText();

    @Accessor("message")
    void setMessageText(MultiLineLabel messageText);
}
