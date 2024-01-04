package com.railwayteam.railways.mixin.compat.malilib;

import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiTextFieldGeneric.class)
public abstract class MixinGuiTextFieldGeneric extends EditBox {
    private MixinGuiTextFieldGeneric(Font font, int x, int y, int width, int height, Component message) {
        super(font, x, y, width, height, message);
    }

    @Inject(method = "setCursorPosition", at = @At("HEAD"), cancellable = true)
    private void fixCursorPosition(int pos, CallbackInfo ci) {
        super.setCursorPosition(pos);
        ci.cancel();
    }
}
