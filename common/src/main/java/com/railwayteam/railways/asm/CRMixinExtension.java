package com.railwayteam.railways.asm;


import com.llamalad7.mixinextras.lib.apache.commons.tuple.Pair;
import com.llamalad7.mixinextras.utils.MixinInternals;
import com.railwayteam.railways.annotation.mixin.DevEnvMixin;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.transformer.ext.IExtension;
import org.spongepowered.asm.mixin.transformer.ext.ITargetClassContext;
import org.spongepowered.asm.util.Annotations;

import java.util.ArrayList;
import java.util.List;

/**
 * Hey kid, want some "Extra Mixin Tools"?
 */
public class CRMixinExtension implements IExtension {
    @Override
    public boolean checkActive(MixinEnvironment environment) { return true; }

    @Override
    public void preApply(ITargetClassContext context) {
        for (Pair<IMixinInfo, ClassNode> pair : MixinInternals.getMixinsFor(context)) {
            ClassNode classNode = pair.getValue();

            classNode.methods.removeIf(methodNode -> removeIfDevMixin(methodNode.visibleAnnotations, methodNode.invisibleAnnotations));
            classNode.fields.removeIf(fieldNode -> removeIfDevMixin(fieldNode.visibleAnnotations, fieldNode.invisibleAnnotations));
        }
    }

    @Override
    public void postApply(ITargetClassContext context) {}

    @Override
    public void export(MixinEnvironment env, String name, boolean force, ClassNode classNode) {}

    public static boolean removeIfDevMixin(List<AnnotationNode> visibleAnnotations, List<AnnotationNode> invisibleAnnotations) {
        if (visibleAnnotations != null) {
            for (AnnotationNode annotationNode : visibleAnnotations) {
                if (annotationNode.desc.equals(Type.getDescriptor(DevEnvMixin.class))) {
                    if (Annotations.getValue(annotationNode, "noSafetyChecks", Boolean.FALSE))
                        return true;
                    else if (isSafeToRemove(visibleAnnotations, invisibleAnnotations))
                        return true;
                }
            }
        }

        return false;
    }

    private static final String ACCESSOR_DESCRIPTOR = Type.getDescriptor(Accessor.class);
    private static final String INVOKER_DESCRIPTOR = Type.getDescriptor(Invoker.class);

    public static boolean isSafeToRemove(List<AnnotationNode> visibleAnnotations, List<AnnotationNode> invisibleAnnotations) {
        List<String> descriptorList = new ArrayList<>();
        visibleAnnotations.forEach(annotationNode -> descriptorList.add(annotationNode.desc));
        invisibleAnnotations.forEach(annotationNode -> descriptorList.add(annotationNode.desc));

        boolean willExplodeIfRemoved = descriptorList.contains(ACCESSOR_DESCRIPTOR) || descriptorList.contains(INVOKER_DESCRIPTOR);
        return !willExplodeIfRemoved;
    }
}
