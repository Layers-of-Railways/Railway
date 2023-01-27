package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.custom_bogeys.monobogey.IPotentiallyUpsideDownBogeyBlock;
import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import com.simibubi.create.content.logistics.trains.entity.CarriageContraption;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Contraption.class, remap = false)
public abstract class MixinContraption {
    @Shadow public BlockPos anchor;
    private BlockState anchorState;

    @Inject(method = "searchMovedStructure", at = @At("HEAD"))
    private void storeAnchorState(Level world, BlockPos pos, Direction forcedDirection, CallbackInfoReturnable<Boolean> cir) {
        this.anchorState = world.getBlockState(pos);
    }

    @Inject(method = "searchMovedStructure", at = @At("RETURN"))
    private void moveHangingAnchor(Level world, BlockPos pos, Direction forcedDirection, CallbackInfoReturnable<Boolean> cir) {
        /*Contraption this_ = (Contraption) (Object) this;
        if (this_ instanceof CarriageContraption && anchorState.getBlock() instanceof IPotentiallyUpsideDownBogeyBlock pudb && pudb.isUpsideDown()) {
            this.anchor = this.anchor.above(2);
        }*/
        this.anchorState = null;
    }

/*    @Redirect(method = "searchMovedStructure", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lcom/simibubi/create/content/contraptions/components/structureMovement/Contraption;anchor:Lnet/minecraft/core/BlockPos;"))
    private void handleHangingBogey(Contraption instance, BlockPos value) {
        if (instance instanceof CarriageContraption && anchorState.getBlock() instanceof IPotentiallyUpsideDownBogeyBlock pudb && pudb.isUpsideDown()) {
            instance.anchor = value.above(2); //FIXME this causes rotation issues, move anchor position of bogey instead
        } else {
            instance.anchor = value;
        }
    }*/
}
