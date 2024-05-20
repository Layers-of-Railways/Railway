package com.railwayteam.railways.annotation.mixin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * You bear all the consequences of using this. Don't shoot yourself in the foot I guess? Try wearing proper mixin equipment?
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DevEnvMixin {}
