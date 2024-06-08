/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.content.handcar;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.mixin_interfaces.IDeployAnywayBlockItem;
import com.railwayteam.railways.mixin_interfaces.IHandcarTrain;
import com.railwayteam.railways.multiloader.PlayerSelection;
import com.railwayteam.railways.registry.CRPackets;
import com.railwayteam.railways.registry.CRTrackMaterials.CRTrackType;
import com.railwayteam.railways.util.packet.CurvedTrackHandcarPlacementPacket;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.schematics.SchematicWorld;
import com.simibubi.create.content.trains.entity.*;
import com.simibubi.create.content.trains.entity.TravellingPoint.SteerDirection;
import com.simibubi.create.content.trains.graph.*;
import com.simibubi.create.content.trains.track.*;
import com.simibubi.create.content.trains.track.TrackMaterial.TrackType;
import com.simibubi.create.content.trains.track.TrackTargetingBlockItem.OverlapResult;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Lang;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

public class HandcarItem extends BlockItem implements IDeployAnywayBlockItem {
    public HandcarItem(Block block, Properties properties) {
        super(block, properties);
    }

    private HandcarBlock getBogeyBlock() {
        return (HandcarBlock) getBlock();
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();

        if (player == null)
            return InteractionResult.FAIL;

        if (state.getBlock() instanceof ITrackBlock track) {
            TrackType trackType = track.getMaterial().trackType;
            if (!(trackType == TrackType.STANDARD || trackType == CRTrackType.UNIVERSAL))
                return InteractionResult.FAIL;
            if (level.isClientSide)
                return InteractionResult.SUCCESS;

            Vec3 lookAngle = player.getLookAngle();
            boolean front = track.getNearestTrackAxis(level, pos, state, lookAngle)
                .getSecond() == Direction.AxisDirection.POSITIVE;

            MutableObject<OverlapResult> result = new MutableObject<>(null);
            MutableObject<TrackGraphLocation> resultLoc = new MutableObject<>(null);
            withGraphLocation(level, pos, front, null, (overlap, location) -> {
                result.setValue(overlap);
                resultLoc.setValue(location);
            });

            if (result.getValue().feedback != null) {
                player.displayClientMessage(Lang.translateDirect(result.getValue().feedback)
                    .withStyle(ChatFormatting.RED), true);
                AllSoundEvents.DENY.play(level, null, pos, .5f, 1);
                return InteractionResult.FAIL;
            }

            TrackGraphLocation loc = resultLoc.getValue();
            if (loc == null)
                return InteractionResult.FAIL;

            boolean success = placeHandcar(loc, level, player, pos);
            if (success) {
                stack.shrink(1);
            }
            return success ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        }


        return InteractionResult.PASS;
    }

    @ApiStatus.Internal
    @NotNull
    public boolean placeHandcar(TrackGraphLocation trackGraphLocation, Level level, Player player, BlockPos soundPos) {
        TrackGraph graph = trackGraphLocation.graph;
        TrackNode node1 = graph.locateNode(trackGraphLocation.edge.getFirst());
        TrackNode node2 = graph.locateNode(trackGraphLocation.edge.getSecond());
        TrackEdge edge = graph.getConnectionsFrom(node1).get(node2);
        if (edge == null)
            return false;

        double offset = getBogeyBlock().getWheelPointSpacing() / 2;
        TravellingPoint tp1 = new TravellingPoint(node1, node2, edge, trackGraphLocation.position, false);
        TravellingPoint tp2 = new TravellingPoint(node1, node2, edge, trackGraphLocation.position, false);
        tp1.travel(graph, offset, tp1.steer(SteerDirection.NONE, new Vec3(0, 1, 0)));
        tp2.travel(graph, -offset, tp2.steer(SteerDirection.NONE, new Vec3(0, 1, 0)));/*

        tp1.travel(graph, 10, tp1.steer(SteerDirection.NONE, new Vec3(0, 1, 0)));
        tp2.travel(graph, 10, tp2.steer(SteerDirection.NONE, new Vec3(0, 1, 0)));
        tp1.travel(graph, -10, tp1.steer(SteerDirection.NONE, new Vec3(0, 1, 0)));
        tp2.travel(graph, -10, tp2.steer(SteerDirection.NONE, new Vec3(0, 1, 0)));*/

        if (!(level instanceof ServerLevel serverLevel))
            return false;
        makeTrain(
            player.getUUID(),
            graph,
            tp1,
            tp2,
            serverLevel
        );


        AllSoundEvents.CONTROLLER_CLICK.play(level, null, soundPos, 1, 1);
        return true;
    }

    @Environment(EnvType.CLIENT)
    public boolean useOnCurve(TrackBlockOutline.BezierPointSelection selection, ItemStack stack) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        TrackBlockEntity be = selection.blockEntity();
        BezierTrackPointLocation loc = selection.loc();
        boolean front = player.getLookAngle()
            .dot(selection.direction()) < 0;

        BezierConnection bc = be.getConnections().get(loc.curveTarget());

        TrackType trackType = bc.getMaterial().trackType;
        if (!(trackType == TrackType.STANDARD || trackType == CRTrackType.UNIVERSAL))
            return false;

        CRPackets.PACKETS.send(new CurvedTrackHandcarPlacementPacket(be.getBlockPos(), loc.curveTarget(),
            loc.segment(), front, player.getInventory().selected));
        return true;
    }

    private @Nullable Train makeTrain(UUID owner, TrackGraph graph, TravellingPoint tp1, TravellingPoint tp2,
                                      ServerLevel level) {
        CarriageContraption contraption = new CarriageContraption(Direction.EAST);

        /* Fake world for assembly */
        SchematicWorld assemblyWorld = new SchematicWorld(level);
        StructureTemplate template = level.getStructureManager().get(Railways.asResource("handcar/assembly")).orElse(null);
        if (template == null) return null;
        StructurePlaceSettings settings = new StructurePlaceSettings();
        template.placeInWorld(assemblyWorld, BlockPos.ZERO, BlockPos.ZERO, settings, level.getRandom(), Block.UPDATE_CLIENTS);
        assemblyWorld.getEntityStream().forEach(e -> e.level = assemblyWorld);
        try {
            /*
            Assembly schematic must be 3x3x3 with the bogey at the central block
             */
            contraption.assemble(assemblyWorld, new BlockPos(1, 1, 1));
        } catch (AssemblyException e) {
            return null;
        }
        /* Done assembling */

        contraption.expandBoundsAroundAxis(Axis.Y);

        CarriageBogey bogey = new CarriageBogey(getBogeyBlock(), false, null, tp1, tp2);
        Carriage carriage = new Carriage(bogey, null, 0);
        Train train = new Train(UUID.randomUUID(), owner, graph, List.of(carriage), new ArrayList<>(), true);

        ((IHandcarTrain) train).railways$setHandcar(true);

        carriage.setContraption(level, contraption);

        train.name = Components.translatable("block.railways.handcar");
        train.collectInitiallyOccupiedSignalBlocks();
        Create.RAILWAYS.addTrain(train);
        CRPackets.PACKETS.sendTo(PlayerSelection.all(), new TrainPacket(train, true));
        return train;
    }

    public static void withGraphLocation(Level level, BlockPos pos, boolean front,
                                         BezierTrackPointLocation targetBezier,
                                         BiConsumer<OverlapResult, TrackGraphLocation> callback) {

        BlockState state = level.getBlockState(pos);

        if (!(state.getBlock() instanceof ITrackBlock track)) {
            callback.accept(OverlapResult.NO_TRACK, null);
            return;
        }

        List<Vec3> trackAxes = track.getTrackAxes(level, pos, state);
        if (targetBezier == null && trackAxes.size() > 1) {
            callback.accept(OverlapResult.JUNCTION, null);
            return;
        }

        Direction.AxisDirection targetDirection = front ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;
        TrackGraphLocation location =
            targetBezier != null ? TrackGraphHelper.getBezierGraphLocationAt(level, pos, targetDirection, targetBezier)
                : TrackGraphHelper.getGraphLocationAt(level, pos, targetDirection, trackAxes.get(0));

        if (location == null) {
            callback.accept(OverlapResult.NO_TRACK, null);
            return;
        }

        Couple<TrackNode> nodes = location.edge.map(location.graph::locateNode);
        TrackEdge edge = location.graph.getConnection(nodes);
        if (edge == null)
            return;

        callback.accept(OverlapResult.VALID, location);
    }
}
