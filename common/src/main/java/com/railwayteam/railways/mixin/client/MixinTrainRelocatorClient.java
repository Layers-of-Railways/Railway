package com.railwayteam.railways.mixin.client;

import com.railwayteam.railways.content.custom_bogeys.monobogey.IPotentiallyUpsideDownBogeyBlock;
import com.railwayteam.railways.content.custom_bogeys.monobogey.MonoBogeyBlock;
import com.railwayteam.railways.mixin.AccessorCarriageBogey;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.trains.IBogeyBlock;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TrainRelocator;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.UUID;

@Mixin(value = TrainRelocator.class, remap = false) //TODO bogey api
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
                IBogeyBlock type = ((AccessorCarriageBogey) train.carriages.get(0).leadingBogey()).getType();
                if (type instanceof MonoBogeyBlock mbb) {
                    result = result.add(0, 1.0d, 0);
                    if (mbb.isUpsideDown()) {
                        result = result.subtract(0, 1.5, 0);
                    }
                } else if (type instanceof IPotentiallyUpsideDownBogeyBlock pudb && pudb.isUpsideDown()) {
                    result = result.subtract(0, 0.5, 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
