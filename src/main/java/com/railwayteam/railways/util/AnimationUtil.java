package com.railwayteam.railways.util;

import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public interface AnimationUtil {
        default String getAnimationPrefix() {
            return "";
        }

        default String getAnimationSuffix() {
            return "";
        }

        default AnimationBuilder animation(String name, boolean shouldLoop, boolean addPrefixSuffix) {
            return new AnimationBuilder().addAnimation(getWithPrefixOrRaw(name, addPrefixSuffix), shouldLoop);
        }

        default AnimationBuilder animation(String name, boolean shouldLoop) {
            return animation(name, shouldLoop, true);
        }

        default AnimationBuilder setAnim(AnimationEvent<?> event, AnimationBuilder builder) {
            event.getController().setAnimation(builder);
            return builder;
        }

        default void setAnim(AnimationEvent<?> event, String name, boolean shouldLoop) {
            setAnim(event, animation(name, shouldLoop));
        }

        default String getWithPrefixOrRaw(String s, boolean addPrefixSuffix) {
            return addPrefixSuffix ? getAnimationPrefix() + s + getAnimationSuffix() : s;
        }

        default AnimationBuilder anim(String name, boolean shouldLoop) {
            return animation(name, shouldLoop);
        }

        default AnimationBuilder anim(String name) {
            return anim(name, true);
        }
    }