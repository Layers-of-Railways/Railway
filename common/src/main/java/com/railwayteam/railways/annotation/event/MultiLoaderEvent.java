package com.railwayteam.railways.annotation.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark methods that are called via abstracted loader-specific events.
 * <p>
 * Meant purely for documentation and does not exist at compile-time or runtime.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface MultiLoaderEvent { }
