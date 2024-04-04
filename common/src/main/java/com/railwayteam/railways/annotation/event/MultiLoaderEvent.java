package com.railwayteam.railways.annotation.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Used to mark methods that are called via abstracted loader-specific events.
 * <p>
 * Meant purely for documentation.
 */
@Target(ElementType.METHOD)
public @interface MultiLoaderEvent { }
