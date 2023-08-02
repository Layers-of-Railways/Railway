package com.railwayteam.railways.forge.mixin;

import com.railwayteam.railways.forge.RollingModeEnumAdder;
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
    }

    @Override
    public boolean shouldApplyMixin(String s, String s1) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        if (targetClassName.equals("com.simibubi.create.content.contraptions.actors.roller.RollerBlockEntity$RollingMode")) {
            RollingModeEnumAdder.processRollingMode(targetClass);
        }
    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {}
}
