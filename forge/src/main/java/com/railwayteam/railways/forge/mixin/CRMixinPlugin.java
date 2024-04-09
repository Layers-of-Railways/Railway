package com.railwayteam.railways.forge.mixin;

import com.railwayteam.railways.forge.asm.ContainerLevelAccessASM;
import com.railwayteam.railways.forge.asm.RollingModeEnumAdder;
import com.railwayteam.railways.util.ConditionalMixinManager;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class CRMixinPlugin implements IMixinConfigPlugin {
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
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // Adds an Enum
        if (targetClassName.equals("com.simibubi.create.content.contraptions.actors.roller.RollerBlockEntity$RollingMode"))
            RollingModeEnumAdder.processRollingMode(targetClass);
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // Adds an instanceof check and return
        if (targetClassName.equals("net.minecraft.world.inventory.ContainerLevelAccess"))
            ContainerLevelAccessASM.processNode(targetClass);
    }
}
