package com.railwayteam.railways.mixin;

import com.railwayteam.railways.util.ConditionalMixinManager;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class CRMixinPlugin implements IMixinConfigPlugin {
    public static final Logger LOGGER = LoggerFactory.getLogger("Railways/MixinPlugin");

    @Override
    public void onLoad(String mixinPackage) { } // NO-OP

    @Override
    public String getRefMapperConfig() { return null; } // DEFAULT

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return ConditionalMixinManager.shouldApply(mixinClassName);
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { } // NO-OP

    @Override
    public List<String> getMixins() { return null; } // DEFAULT

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { } // NO-OP

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { } // NO-OP
}
