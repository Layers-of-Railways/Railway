package com.railwayteam.railways.content.gangway;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.railwayteam.railways.content.smokestack.DieselSmokeStackBlock;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.Couple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.railwayteam.railways.util.Utils.getBounds;

public class GangwayRenderer {
    public static void renderAll(PoseStack ms, MultiBufferSource buffer, Vec3 camera) {
        Collection<Train> trains = CreateClient.RAILWAYS.trains.values();
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        BlockState air = Blocks.AIR.defaultBlockState();
        float partialTicks = AnimationTickHolder.getPartialTicks();
        Level level = Minecraft.getInstance().level;

        for (Train train : trains) {
            List<Carriage> carriages = train.carriages;
            for (Carriage carriage : carriages) {
                CarriageContraptionEntity entity = carriage.getDimensional(level).entity.get();

                if (entity == null)
                    continue;

                for (Map.Entry<BlockPos, StructureBlockInfo> entry : entity.getContraption().getBlocks().entrySet()) {
                    BlockPos blockPos = entry.getKey();
                    StructureBlockInfo info = entry.getValue();
                    if (!(info.state.getBlock() instanceof DieselSmokeStackBlock))
                        continue;
                    Vec3 anchor = carriage.leadingBogey().getAnchorPosition();
                    if (anchor == null)
                        break;
                    ms.pushPose();
                    maybeRender(level, blockPos, ms, buffer, camera, (CarriageContraption) entity.getContraption(), entity, train);
                    ms.popPose();
                }
            }
        }
    }

    private static void maybeRender(Level renderWorld, BlockPos localPos, PoseStack ms, MultiBufferSource buffer, Vec3 camera,
                                    @NotNull CarriageContraption cc, @NotNull CarriageContraptionEntity cce,
                                    @NotNull Train train) {
        float partialTicks = AnimationTickHolder.getPartialTicks();
        Couple<BlockPos> blockBounds = getBounds(cc.anchor, cc.getBlocks().keySet());
        BlockPos minPos = blockBounds.getFirst();
        BlockPos maxPos = blockBounds.getSecond();

        boolean front;

        switch (cc.getAssemblyDirection().getAxis()) {
            case X -> {
                if (localPos.getX() != minPos.getX() && localPos.getX() != maxPos.getX())
                    return;
                front = (cc.getAssemblyDirection().getAxisDirection() == Direction.AxisDirection.POSITIVE) ? (localPos.getX() == minPos.getX()) : (localPos.getX() == maxPos.getX());
            }
            case Z -> {
                if (localPos.getZ() != minPos.getZ() && localPos.getZ() != maxPos.getZ())
                    return;
                front = (cc.getAssemblyDirection().getAxisDirection() == Direction.AxisDirection.POSITIVE) ? (localPos.getZ() == minPos.getZ()) : (localPos.getZ() == maxPos.getZ());
            }
            default -> {
                return;
            }
        }

        int carriageIndex = cce.carriageIndex;
        if (front && carriageIndex == 0) return;
        int lastIndex = train.carriages.size() - 1;
        if (!front && carriageIndex == lastIndex) return;

        int adjacentCarriageIdx = front ? carriageIndex - 1 : carriageIndex + 1;
        Carriage adjacentCarriage = train.carriages.get(adjacentCarriageIdx);
        CarriageContraptionEntity adjacentCCE = adjacentCarriage.getDimensional(renderWorld.dimension()).entity.get();
        if (adjacentCCE == null)
            return;

        CarriageContraption adjacentCC = (CarriageContraption) adjacentCCE.getContraption();

        Couple<BlockPos> adjacentBounds = getBounds(adjacentCC.anchor, adjacentCC.getBlocks().keySet());
        BlockPos adjMinPos = adjacentBounds.getFirst();
        BlockPos adjMaxPos = adjacentBounds.getSecond();

        BlockPos neighborPos;

        if (adjacentCC.getAssemblyDirection().getAxis() == Axis.X) {
            int otherX = adjacentCC.getAssemblyDirection().getAxisDirection() == Direction.AxisDirection.POSITIVE ^ front ? adjMinPos.getX() : adjMaxPos.getX();
            neighborPos = new BlockPos(otherX, localPos.getY(), localPos.getZ());
        } else {
            int otherZ = adjacentCC.getAssemblyDirection().getAxisDirection() == Direction.AxisDirection.POSITIVE ^ front ? adjMinPos.getZ() : adjMaxPos.getZ();
            neighborPos = new BlockPos(localPos.getX(), localPos.getY(), otherZ);
        }

        StructureBlockInfo neighborInfo = adjacentCC.getBlocks().get(neighborPos);
        if (neighborInfo == null)
            return;

        if (!(neighborInfo.state.getBlock() instanceof DieselSmokeStackBlock))
            return;

        Vec3 myGlobalPos = cce.toGlobalVector(Vec3.atCenterOf(localPos), partialTicks);
        Vec3 neighborGlobalPos = adjacentCCE.toGlobalVector(Vec3.atCenterOf(neighborPos), partialTicks);
        Vec3 centerOffset = neighborGlobalPos.subtract(myGlobalPos).scale(0.2);
        double offsetToCenter = neighborGlobalPos.subtract(myGlobalPos).length()/2.;//train.carriageSpacing.get(front ? carriageIndex - 1 : carriageIndex) / 4.;
        for (double offset = 0.0; offset < offsetToCenter; offset += 12 / 16f) {
            ms.pushPose();
            ms.translate(myGlobalPos.x - camera.x - 0.5, myGlobalPos.y - camera.y - 0.5, myGlobalPos.z - camera.z - 0.5);
            Vec3 intermediateOffset = new Vec3(
                cc.getAssemblyDirection().getAxis() == Axis.X ? (front ? -offset : offset) : 0,
                0,
                cc.getAssemblyDirection().getAxis() == Axis.Z ? (front ? -offset : offset) : 0);
            CachedBufferer.partial(AllPartialModels.PECULIAR_BELL, Blocks.AIR.defaultBlockState())
//                .translate(intermediateOffset)
                .centre()
                //.rotateY(AngleHelper.horizontalAngle(facing))
                //.rotateX(AngleHelper.verticalAngle(facing))
                //.rotateZ(angle)
                .unCentre()
                /*.light(matrices.getWorld(),
                    ContraptionRenderDispatcher.getContraptionWorldLight(context, renderWorld))*/
                .light(LevelRenderer.getLightColor(renderWorld, new BlockPos(myGlobalPos.x, myGlobalPos.y, myGlobalPos.z)))
                .renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));
            ms.popPose();
        }
    }
}
