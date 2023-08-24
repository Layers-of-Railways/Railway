package com.railwayteam.railways.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.content.conductor.ConductorEntityModel;
import com.railwayteam.railways.content.conductor.ConductorRenderer;
import com.railwayteam.railways.registry.CREntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class MixinPlayerRenderer extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private ConductorEntity visualEntity;

    private MixinPlayerRenderer(EntityRendererProvider.Context context, PlayerModel<AbstractClientPlayer> model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Inject(
            method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"), cancellable = true)
    private void renderConductorInstead(AbstractClientPlayer entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if (!ConductorEntity.isPlayerDisguised(entity)) {
            this.shadowRadius = 0.5f;
            return;
        }
        this.shadowRadius = 0.2f;
        Minecraft mc = Minecraft.getInstance();
        if (entity.isLocalPlayer() && mc.options.getCameraType().isFirstPerson() && Mods.FIGURA.isLoaded)
            return;
        ci.cancel();

        if (visualEntity == null) {
            visualEntity = new ConductorEntity(CREntities.CONDUCTOR.get(), entity.level);
        }

        snr$transformVisualConductor(entity, visualEntity);
        visualEntity.visualBaseModel = getModel();
        visualEntity.visualBaseModel.crouching = entity.isCrouching();
        visualEntity.visualBaseModel.swimAmount = entity.getSwimAmount(partialTicks);
        visualEntity.visualBaseEntity = entity;
        Minecraft.getInstance().getEntityRenderDispatcher().render(visualEntity, 0, 0, 0,
                0, partialTicks, matrixStack, buffer, packedLight);
    }

    private static void snr$transformVisualConductor(AbstractClientPlayer player, ConductorEntity conductor) {
        conductor.xo = player.xo;
        conductor.yo = player.yo;
        conductor.zo = player.zo;
        conductor.xOld = player.xOld;
        conductor.yOld = player.yOld;
        conductor.zOld = player.zOld;
        conductor.xRotO = player.xRotO;
        conductor.yRotO = player.yRotO;
        ((AccessorEntity) conductor).setXRot(player.getXRot());
        ((AccessorEntity) conductor).setYRot(player.getYRot());

        conductor.yHeadRot = player.yHeadRot;
        conductor.yBodyRot = player.yBodyRot;
        conductor.yBodyRotO = player.yBodyRotO;
        conductor.yHeadRotO = player.yHeadRotO;
        ((AccessorWalkAnimationState) conductor.walkAnimation).setPosition(player.walkAnimation.position());
        conductor.walkAnimation.setSpeed(player.walkAnimation.speed());
        ((AccessorWalkAnimationState) conductor.walkAnimation).setSpeedOld(((AccessorWalkAnimationState) player.walkAnimation).getSpeedOld());

        conductor.tickCount = player.tickCount;

        conductor.setOnGround(player.onGround());

        conductor.setItemSlot(EquipmentSlot.HEAD, player.getItemBySlot(EquipmentSlot.HEAD));
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.getItem() == Items.ELYTRA) {
            conductor.setItemSlot(EquipmentSlot.CHEST, chest);
        } else {
            conductor.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
        }

        conductor.setSharedFlag(7, player.isFallFlying());
    }

    @Inject(method = "renderRightHand", at = @At("HEAD"), cancellable = true)
    private void snr$renderRightHand(PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, AbstractClientPlayer player, CallbackInfo ci) {
        if (!ConductorEntity.isPlayerDisguised(player))
            return;
        ci.cancel();

        if (visualEntity == null) {
            visualEntity = new ConductorEntity(CREntities.CONDUCTOR.get(), player.level);
        }

        snr$transformVisualConductor(player, visualEntity);
        visualEntity.visualBaseModel = getModel();
        visualEntity.visualBaseModel.crouching = player.isCrouching();
        visualEntity.visualBaseModel.swimAmount = player.getSwimAmount(0);
        visualEntity.visualBaseEntity = player;
        if (Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(visualEntity) instanceof ConductorRenderer conductorRenderer) {
            this.snr$renderHand(matrixStack, buffer, combinedLight, visualEntity, conductorRenderer.getModel().rightArm,
                    visualEntity.visualBaseModel, conductorRenderer.getModel(), conductorRenderer);
        }
    }

    @Inject(method = "renderLeftHand", at = @At("HEAD"), cancellable = true)
    private void snr$renderLeftHand(PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, AbstractClientPlayer player, CallbackInfo ci) {
        if (!ConductorEntity.isPlayerDisguised(player))
            return;
        ci.cancel();

        if (visualEntity == null) {
            visualEntity = new ConductorEntity(CREntities.CONDUCTOR.get(), player.level);
        }

        snr$transformVisualConductor(player, visualEntity);
        visualEntity.visualBaseModel = getModel();
        visualEntity.visualBaseModel.crouching = player.isCrouching();
        visualEntity.visualBaseModel.swimAmount = player.getSwimAmount(0);
        visualEntity.visualBaseEntity = player;
        if (Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(visualEntity) instanceof ConductorRenderer conductorRenderer) {
            this.snr$renderHand(matrixStack, buffer, combinedLight, visualEntity, conductorRenderer.getModel().leftArm,
                    visualEntity.visualBaseModel, conductorRenderer.getModel(), conductorRenderer);
        }
    }

    private void snr$renderHand(PoseStack matrixStack, MultiBufferSource buffer, int combinedLight,
                                ConductorEntity conductor, ModelPart rendererArm, PlayerModel<?> playermodel,
                                ConductorEntityModel<ConductorEntity> conductorModel, ConductorRenderer conductorRenderer) {
        playermodel.attackTime = 0.0f;
        playermodel.crouching = false;
        playermodel.swimAmount = 0.0f;
        conductorModel.setupAnim(conductor, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        rendererArm.xRot = 0.0f;
        matrixStack.pushPose();
        matrixStack.translate(0, -0.3, 0);
        rendererArm.render(matrixStack,
                buffer.getBuffer(RenderType.entitySolid(conductorRenderer.getTextureLocation(conductor))),
                combinedLight, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
    }
}
