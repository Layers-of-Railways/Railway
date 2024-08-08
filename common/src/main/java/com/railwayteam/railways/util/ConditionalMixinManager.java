/*
 * MIT License
 *
 * Copyright (c) 2023-Present Bawnorton
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.railwayteam.railways.util;

import com.railwayteam.railways.annotation.mixin.ConditionalMixin;
import com.railwayteam.railways.annotation.mixin.DevEnvMixin;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.mixin.CRMixinPlugin;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.slf4j.event.Level;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;

import java.io.IOException;
import java.util.List;

public class ConditionalMixinManager {
    public static boolean shouldApply(String className) {
        var logger = CRMixinPlugin.LOGGER.atLevel(Utils.isDevEnv() ? Level.INFO : Level.DEBUG);
        try {
            List<AnnotationNode> annotationNodes = MixinService.getService().getBytecodeProvider().getClassNode(className).visibleAnnotations;
            if (annotationNodes == null) return true;

            boolean shouldApply = true;
            for (AnnotationNode node : annotationNodes) {
                if (node.desc.equals(Type.getDescriptor(ConditionalMixin.class))) {
                    List<Mods> mods = Annotations.getValue(node, "mods", true, Mods.class);
                    boolean applyIfPresent = Annotations.getValue(node, "applyIfPresent", Boolean.TRUE);
                    boolean anyModsLoaded = anyModsLoaded(mods);
                    shouldApply = anyModsLoaded == applyIfPresent;
                    logger.log("{} is{}being applied because the mod(s) {} are{}loaded", className, shouldApply ? " " : " not ", mods, anyModsLoaded ? " " : " not ");
                }
                if (node.desc.equals(Type.getDescriptor(DevEnvMixin.class))) {
                    shouldApply &= Utils.isDevEnv();
                    logger.log("{} is {}being applied because we are {}in a development environment", className, shouldApply ? "" : "not ", Utils.isDevEnv() ? "" : "not ");
                }
            }
            return shouldApply;
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean anyModsLoaded(List<Mods> mods) {
        for (Mods mod : mods) {
            if (mod.isLoaded) return true;
        }
        return false;
    }
}
