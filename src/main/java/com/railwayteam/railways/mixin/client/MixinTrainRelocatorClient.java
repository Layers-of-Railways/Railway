package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.content.custom_bogeys.monobogey.MonoBogeyBlock;
import com.railwayteam.railways.mixin.AccessorCarriageBogey;
import com.railwayteam.railways.registry.CRBlocks;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.logistics.trains.entity.Train;
import com.simibubi.create.content.logistics.trains.entity.TrainRelocator;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.UUID;

@Mixin(value = TrainRelocator.class, remap = false)
public class MixinTrainRelocatorClient {
    @Shadow
    static UUID relocatingTrain;

    @Redirect(method = "relocateClient", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"))
    @Coerce
    private static Object changeVisualizedHeight(List<Vec3> instance, int i) {
        Vec3 result = instance.get(i);
        try {
            if (CreateClient.RAILWAYS.trains.containsKey(relocatingTrain)) {
                Train train = CreateClient.RAILWAYS.trains.get(relocatingTrain);
                if (((AccessorCarriageBogey) train.carriages.get(0).leadingBogey()).getType() instanceof MonoBogeyBlock) {
                    result = result.add(0, 1.0d, 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
