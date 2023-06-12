package com.railwayteam.railways.mixin.client;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.mixin_interfaces.ILimited;
import com.railwayteam.railways.registry.CRPackets;
import com.simibubi.create.content.trains.station.AbstractStationScreen;
import com.simibubi.create.content.trains.station.GlobalStation;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.content.trains.station.StationScreen;
import com.simibubi.create.foundation.utility.Components;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = StationScreen.class, remap = false)
public abstract class MixinStationScreen extends AbstractStationScreen {
    private Checkbox limitEnableCheckbox;

    private MixinStationScreen(StationBlockEntity te, GlobalStation station) {
        super(te, station);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/station/StationScreen;tickTrainDisplay()V"), remap = true)
    private void initCheckbox(CallbackInfo ci) {
        int x = guiLeft;
        int y = guiTop;
        limitEnableCheckbox = new Checkbox(x + background.width - 98, y + background.height - 26, 50, 20, Components.translatable("railways.station.train_limit"), station != null && ((ILimited) station).isLimitEnabled()) {
            private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");
            private static final int TEXT_COLOR = 0xEFEFEF;

            @Override
            public void onPress() {
                super.onPress();
                CRPackets.PACKETS.send(ILimited.makeLimitEnabledPacket(blockEntity.getBlockPos(), this.selected()));
            }

            @Override
            public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
                Minecraft minecraft = Minecraft.getInstance();
                RenderSystem.setShaderTexture(0, TEXTURE);
                RenderSystem.enableDepthTest();
                Font font = minecraft.font;
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                Checkbox.blit(poseStack, this.x, this.y, this.isFocused() ? 20.0f : 0.0f, this.selected() ? 20.0f : 0.0f, 20, this.height, 64, 64);
                this.renderBg(poseStack, minecraft, mouseX, mouseY);
                Checkbox.drawString(poseStack, font, this.getMessage(), this.x + 24, this.y + (this.height - 8) / 2, TEXT_COLOR | Mth.ceil(this.alpha * 255.0f) << 24);
                if (this.isHoveredOrFocused()) {
                    renderComponentTooltip(poseStack, ImmutableList.of(Components.translatable("railways.station.train_limit.tooltip.1"), Components.translatable("railways.station.train_limit.tooltip.2")), mouseX, mouseY);
                }
            }
        };
        addRenderableWidget(limitEnableCheckbox);
    }
}
