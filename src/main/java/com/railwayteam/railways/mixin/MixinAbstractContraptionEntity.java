package com.railwayteam.railways.mixin;

import com.railwayteam.railways.content.custom_bogeys.monobogey.IPotentiallyUpsideDownBogey;
import com.simibubi.create.content.contraptions.components.structureMovement.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import com.simibubi.create.content.contraptions.components.structureMovement.StructureTransform;
import com.simibubi.create.content.logistics.trains.entity.CarriageContraptionEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = AbstractContraptionEntity.class, remap = false)
public abstract class MixinAbstractContraptionEntity {
    @Shadow protected abstract StructureTransform makeStructureTransform();

    @Shadow protected Contraption contraption;

    @Redirect(method = "disassemble", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/components/structureMovement/AbstractContraptionEntity;makeStructureTransform()Lcom/simibubi/create/content/contraptions/components/structureMovement/StructureTransform;"))
    private StructureTransform changeTransformForHangingBogeys(AbstractContraptionEntity instance) {
        StructureTransform transform = makeStructureTransform();
        if (((Object) this) instanceof CarriageContraptionEntity) { //no it isn't always false, silly intellij
            if (((AccessorCarriageBogey) ((AccessorCarriageContraptionEntity) this).snr_getCarriage().leadingBogey()).getType() instanceof IPotentiallyUpsideDownBogey pudb && pudb.isUpsideDown()) {
                transform.offset = transform.offset.above(2);
            }
        }
        return transform;
    }
}
