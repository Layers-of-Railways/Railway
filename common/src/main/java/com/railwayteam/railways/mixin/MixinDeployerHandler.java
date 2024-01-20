package com.railwayteam.railways.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.railwayteam.railways.mixin_interfaces.IDeployAnywayBlockItem;
import com.simibubi.create.content.kinetics.deployer.DeployerHandler;
import net.minecraft.world.item.BlockItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;

@Mixin(DeployerHandler.class)
public class MixinDeployerHandler {
    // target `item instanceof BlockItem`
    @WrapOperation(method = "activateInner", constant = @Constant(classValue = BlockItem.class, ordinal = 0))
    private static boolean handcarsAreDeployable(Object obj, Operation<Boolean> original) {
        if (obj instanceof IDeployAnywayBlockItem)
            return false;
        return original.call(obj);
    }
}
