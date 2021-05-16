package com.railwayteam.railways.entities.handcar;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.AnimationProcessor;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.raw.pojo.Bone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

import javax.annotation.Nullable;

public class HandcarModel extends AnimatedGeoModel<HandcarEntity> {
    public ResourceLocation getModelLocation()
    {
        return new ResourceLocation("railways", "geo/handcar.geo.json");
    }

    public ResourceLocation getTextureLocation()
    {
        return new ResourceLocation("railways", "textures/entity/handcar.png");
    }

    public ResourceLocation getAnimationFileLocation()
    {
        return new ResourceLocation("railways", "animations/handcar.animation.json");
    }

    @Override
    public ResourceLocation getModelLocation(HandcarEntity object)
    {
        return getModelLocation();
    }

    @Override
    public ResourceLocation getTextureLocation(HandcarEntity object)
    {
        return getTextureLocation();
    }

    @Override
    public ResourceLocation getAnimationFileLocation(HandcarEntity object)
    {
        return getAnimationFileLocation();
    }

    final double toRotateWheels = 0.07;

    @Override
    public void setLivingAnimations(HandcarEntity entity, Integer uniqueID, @Nullable AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);

        if (entity.isMoving && !Minecraft.getInstance().isGamePaused()) {
            String l = "leftwheel";
            String r = "rightwheel";
            AnimationProcessor p = this.getAnimationProcessor();
            IBone[] wheels = new IBone[]{p.getBone(r+"1"),p.getBone(r+"2"),p.getBone(l+"1"),p.getBone(l+"2")};
            entity.wheelZ = entity.wheelZ % 360;
            entity.wheelZ += entity.movementDirection ? toRotateWheels : -toRotateWheels;
            for(IBone wheel : wheels) {
                wheel.setRotationZ((float) entity.wheelZ);
            }
        }
    }
}
