package com.railwayteam.railways.mixin.client;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.simibubi.create.content.trains.bogey.BogeyInstance;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.foundation.utility.Iterate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BogeyInstance.Frame.class, remap = false) //TODO bogey api
public class MixinBogeyInstance_Frame implements IBogeyFrameCanBeMonorail<BogeyInstance.Frame> {
    @Mutable
    @Shadow @Final private ModelData frame;

    @Mutable
    @Shadow @Final private ModelData[] wheels;
    private boolean isMonorail = false;
    private boolean isMonorailUpsideDown = false;
    private boolean isLeadingBogeyUpsideDown = false;

    private MaterialManager materialManager;

    @Override
    public boolean isMonorail() {
        return isMonorail;
    }

    @Override
    public BogeyInstance.Frame setMonorail(boolean upsideDown, boolean leadingUpsideDown) {
        if (!this.isMonorail && materialManager != null) {
            frame.delete();
            frame = materialManager.defaultSolid()
                .material(Materials.TRANSFORMED)
                .getModel(CRBlockPartials.MONOBOGEY_FRAME)
                .createInstance();

            for (ModelData wheel : wheels) {
                wheel.delete();
            }

            wheels = new ModelData[4];
            materialManager.defaultSolid()
                .material(Materials.TRANSFORMED)
                .getModel(CRBlockPartials.MONOBOGEY_WHEEL)
                .createInstances(wheels);
            materialManager = null;
            for (ModelData shaft : ((AccessorBogeyInstance) this).getShafts())
                shaft.delete();
        }
        this.isMonorailUpsideDown = upsideDown;
        this.isLeadingBogeyUpsideDown = leadingUpsideDown;
        this.isMonorail = true;
        return (BogeyInstance.Frame) (Object) this;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initMonorail(CarriageBogey bogey, MaterialManager materialManager, CallbackInfo ci) {
        this.materialManager = materialManager;
    }

    @Inject(method = "beginFrame", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/BogeyInstance;beginFrame(FLcom/mojang/blaze3d/vertex/PoseStack;)V", shift = At.Shift.AFTER, remap = true), cancellable = true)
    private void beginMonorailFrame(float wheelAngle, PoseStack ms, CallbackInfo ci) {
        if (isMonorail()) {
            ci.cancel();

            if (ms == null) {
                frame.setEmptyTransform();
                for (boolean left : Iterate.trueAndFalse) {
                    for (int front : Iterate.positiveAndNegative) {
                        wheels[(left ? 1 : 0) + (front + 1)].setEmptyTransform();
                    }
                }
                return;
            }

            frame.setTransform(ms);
/*                .translateY(isMonorailUpsideDown ?
                    (isLeadingBogeyUpsideDown ? 3 : 1) : //this should all happen in CarriageContraptionEntityRenderer.translateBogey
                    (isLeadingBogeyUpsideDown ? 2 : 0))
                .rotateZ(isMonorailUpsideDown ? 180 : 0);

            float wheelY = isMonorailUpsideDown ? 35 /16f : 3 / 16f;
            if (isMonorailUpsideDown != isLeadingBogeyUpsideDown)
                wheelY += isLeadingBogeyUpsideDown ? 2 : -2;*/

            float wheelY = 3 / 16f;

            for (boolean left : Iterate.trueAndFalse) {
                for (int front : Iterate.positiveAndNegative) {
                    wheels[(left ? 1 : 0) + (front + 1)].setTransform(ms)
                        .translate(left ? -12 / 16f : 12 / 16f, wheelY, front * 15 / 16f) //base position
                        .rotateY(left ? wheelAngle : -wheelAngle)
                        .translate(15/16f, 0, 0/16f);
                }
            }
        }
    }
}
