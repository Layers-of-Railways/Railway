package com.railwayteam.railways.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.railwayteam.railways.annotation.mixin.ConditionalMixin;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.content.animated_flywheel.FlywheelMovementBehaviour;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllMovementBehaviours;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

// Prevent future conflicts if EF updates
@ConditionalMixin(mods = Mods.EXTENDEDFLYWHEELS, applyIfPresent = false)
@Mixin(AllBlocks.class)
public class MixinAllBlocks {
    @WrapOperation(
            method = "<clinit>",
            at = @At(
                value = "INVOKE",
                target = "Lcom/tterrag/registrate/builders/BlockBuilder;properties(Lcom/tterrag/registrate/util/nullness/NonNullUnaryOperator;)Lcom/tterrag/registrate/builders/BlockBuilder;",
                ordinal = 2
            ),
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=sequenced_gearshift")
            )
    )
    private static <T extends Block, P> BlockBuilder<T, P> railways$addFlywheelMovementBehaviour(BlockBuilder<T, P> instance, NonNullUnaryOperator<BlockBehaviour.Properties> func, Operation<BlockBuilder<T, P>> original) {
        return original.call(instance, func).onRegister(AllMovementBehaviours.movementBehaviour(new FlywheelMovementBehaviour()));
    }
}
