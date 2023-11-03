package com.railwayteam.railways.fabric.mixin;

import com.railwayteam.railways.compat.Mods;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class CRMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String s) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    } // null is default

    @Override
    public boolean shouldApplyMixin(String targetName, String mixinName) {
        if (mixinName.contains("compat.voicechat")) {
            return Mods.VOICECHAT.isLoaded;
        }
        // Fix null issue between create & farmers delight
        if (mixinName.contains("compat.farmersdelight_fabric")) {
            return Mods.FARMERSDELIGHT.isLoaded;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {} // hook to remove targets

    @Override
    public List<String> getMixins() {
        return null;
    } // null to not append extra mixins to the mixin config

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {}
}
