package com.railwayteam.railways.mixin.client;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.mixin_interfaces.ILimited;
import com.railwayteam.railways.registry.CRPackets;
import com.railwayteam.railways.util.Utils;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TrainIconType;
import com.simibubi.create.content.trains.station.*;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = StationScreen.class, remap = false)
public abstract class MixinStationScreen extends AbstractStationScreen {
    @Shadow private EditBox trainNameBox;
    private Checkbox limitEnableCheckbox;
    private List<ResourceLocation> iconTypes;
    private ScrollInput iconTypeScroll;

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

            //fixme no idea what is going on here in the slightest
//            @Override
//            public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
//                Minecraft minecraft = Minecraft.getInstance();
//                RenderSystem.setShaderTexture(0, TEXTURE);
//                RenderSystem.enableDepthTest();
//                Font font = minecraft.font;
//                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
//                RenderSystem.enableBlend();
//                RenderSystem.defaultBlendFunc();
//                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//                Checkbox.blit(poseStack, this.x, this.y, this.isFocused() ? 20.0f : 0.0f, this.selected() ? 20.0f : 0.0f, 20, this.height, 64, 64);
//                this.renderBg(poseStack, minecraft, mouseX, mouseY);
//                Checkbox.drawString(poseStack, font, this.getMessage(), this.x + 24, this.y + (this.height - 8) / 2, TEXT_COLOR | Mth.ceil(this.alpha * 255.0f) << 24);
//                if (this.isHoveredOrFocused()) {
//                    renderComponentTooltip(poseStack, ImmutableList.of(Components.translatable("railways.station.train_limit.tooltip.1"), Components.translatable("railways.station.train_limit.tooltip.2")), mouseX, mouseY);
//                }
//            }
        };
        addRenderableWidget(limitEnableCheckbox);

        iconTypes = TrainIconType.REGISTRY.keySet()
                .stream()
                .toList();
        iconTypeScroll = new ScrollInput(x + 4, y + 17, 184, 14).titled(Lang.translateDirect("station.icon_type"));
        iconTypeScroll.withRange(0, iconTypes.size());
        iconTypeScroll.withStepFunction(ctx -> -iconTypeScroll.standardStep()
                .apply(ctx));
        iconTypeScroll.calling(s -> {
            Train train = displayedTrain.get();
            if (train != null) {
                train.icon = TrainIconType.byId(iconTypes.get(s));
                Utils.sendCreatePacketToServer(
                        new TrainEditPacket(train.id, trainNameBox.getValue(), train.icon.getId()));
            }
        });
        iconTypeScroll.active = false;
//        addRenderableWidget(iconTypeScroll);
    }

    @Inject(method = "tickTrainDisplay", at = @At("HEAD"))
    private void tickIconScroll(CallbackInfo ci) {
        Train train = displayedTrain.get();

        if (train == null) {
            if (iconTypeScroll.active) {
                iconTypeScroll.active = false;
                removeWidget(iconTypeScroll);
            }

            Train imminentTrain = getImminent();

            if (imminentTrain != null) {
                iconTypeScroll.active = true;
                iconTypeScroll.setState(iconTypes.indexOf(imminentTrain.icon.getId()));
                addRenderableWidget(iconTypeScroll);
            }
        }
    }
}
