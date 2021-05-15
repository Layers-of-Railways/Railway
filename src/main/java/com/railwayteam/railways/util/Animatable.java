package com.railwayteam.railways.util;

import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

public interface Animatable extends IAnimatable, AnimationUtil {
        <E extends IAnimatable> AnimationBuilder getAnimation(AnimationEvent<E> event);

        default <E extends IAnimatable> PlayState getPlayState(AnimationEvent<E> event, AnimationBuilder returnedAnimation) {
            return PlayState.CONTINUE;
        }

        default <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
            return getPlayState(event, setAnim(event, getAnimation(event)));
        }

        default int getTransitionLength() {
            return 0;
        }

        @Override
        default void registerControllers(AnimationData data) {
            data.addAnimationController(new AnimationController(this, "controller", getTransitionLength(), this::predicate));
        }
    }