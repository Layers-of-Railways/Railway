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

package com.railwayteam.railways.content.qol;

import com.railwayteam.railways.content.buffer.TrackBufferBlockEntity;
import com.railwayteam.railways.content.coupling.coupler.SecondaryTrackTargetingBehaviour;
import com.railwayteam.railways.util.EntityUtils;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.equipment.wrench.WrenchItem;
import com.simibubi.create.content.trains.graph.*;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.content.trains.signal.SignalBoundary;
import com.simibubi.create.content.trains.signal.SingleBlockEntityEdgePoint;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockOutline;
import com.simibubi.create.content.trains.track.TrackBlockOutline.BezierPointSelection;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.utility.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class TrackEdgePointHighlighter {
    private static class HolderSet {
        final Object BLOCK_BOX = new Object();
        final Object CONNECTOR = new Object();
        final Object TRACK_BOX = new Object();
        final Object CONNECTOR2 = new Object();
        final Object TRACK_BOX2 = new Object();
        final Object ARROW_A = new Object();
        final Object ARROW_B = new Object();
    }

    private static final ArrayList<HolderSet> HOLDERS = new ArrayList<>();

    public static void clientTick(Minecraft mc) {
        LocalPlayer player = mc.player;
        if (player == null || !EntityUtils.isHoldingItem(player, (item) -> item instanceof WrenchItem)) return;

        ClientLevel level = mc.level;
        if (level != null && mc.hitResult instanceof BlockHitResult blockHit) {
            BlockPos pos = blockHit.getBlockPos();
            BlockEntity be = level.getBlockEntity(pos);
            if (!showOutlinesFrom(be, pos, 0, false)) {
                BlockState state = level.getBlockState(pos);
                Vec3 lookAngle = player.getLookAngle();
                TrackGraphLocation loc = null;

                if (state.getBlock() instanceof ITrackBlock track) {
                    loc = TrackGraphHelper.getGraphLocationAt(level, pos, track.getNearestTrackAxis(level, pos, state, lookAngle)
                        .getSecond(), track.getTrackAxes(level, pos, state).get(0));
                }

                if (loc == null) {
                    BezierPointSelection bezier = TrackBlockOutline.result;
                    if (bezier != null) {
                        loc = TrackGraphHelper.getBezierGraphLocationAt(
                            level,
                            bezier.blockEntity().getBlockPos(),
                            lookAngle.dot(bezier.direction()) < 0
                                ? Direction.AxisDirection.POSITIVE
                                : Direction.AxisDirection.NEGATIVE,
                            bezier.loc()
                        );
                    }
                }

                if (loc != null) {
                    TrackGraph graph = loc.graph;
                    TrackNode node1 = graph.locateNode(loc.edge.getFirst());
                    TrackNode node2 = graph.locateNode(loc.edge.getSecond());
                    TrackEdge edge = graph.getConnectionsFrom(node1).get(node2);
                    if (edge == null)
                        return;

                    TrackGraphLocation finalLoc = loc;
                    edge.getEdgeData().getPoints().stream().sorted((a, b) -> {
                            double distA = Math.abs(a.getLocationOn(edge) - finalLoc.position);
                            double distB = Math.abs(b.getLocationOn(edge) - finalLoc.position);
                            return Double.compare(distA, distB);
                        }).filter(point -> Math.abs(point.getLocationOn(edge) - finalLoc.position) <= 0.5f)
                        .limit(1).forEach(point -> {
                            if (point instanceof SingleBlockEntityEdgePoint single && single.getBlockEntityPos() != null) {
                                showOutlinesFrom(mc.level.getBlockEntity(single.getBlockEntityPos()), single.getBlockEntityPos(), 0, true);
                            } else if (point instanceof SignalBoundary signal) {
                                MutableInt index = new MutableInt(0);
                                //noinspection CodeBlock2Expr
                                signal.blockEntities.forEach(positions -> {
                                    positions.keySet().forEach(pos2 -> {
                                        if (pos2 != null) {
                                            showOutlinesFrom(mc.level.getBlockEntity(pos2), pos2, index.getAndAdd(1), true);
                                        }
                                    });
                                });
                            }
                        });
                }
            }
        }
    }

    private static boolean showOutlinesFrom(BlockEntity be, BlockPos pos, int index, boolean padHovered) {
        if (be instanceof TrackBufferBlockEntity)
            return false;
        HolderSet holder;
        if (HOLDERS.size() <= index) {
            holder = new HolderSet();
            HOLDERS.add(holder);
        } else {
            holder = HOLDERS.get(index);
        }
        TrackTargetingBehaviour<?> trackTarget1 = TrackTargetingBehaviour.get(be, TrackTargetingBehaviour.TYPE);
        if (trackTarget1 != null) {
            AABB aa = new AABB(pos).inflate(1 / 32f);
            CreateClient.OUTLINER.chaseAABB(holder.BLOCK_BOX, aa)
                .colored(Color.SPRING_GREEN)
                .lineWidth(1 / 16f);

            AABB bb = new AABB(trackTarget1.getPositionForMapMarker()).contract(0, 10 / 16f, 0);
            Vec3 bbCenter = bb.getCenter();
            CreateClient.OUTLINER.chaseAABB(holder.TRACK_BOX, bb)
                .colored(Color.SPRING_GREEN)
                .lineWidth(1 / 16f);

            CreateClient.OUTLINER.showLine(holder.CONNECTOR, aa.getCenter(), bbCenter)
                .colored(Color.SPRING_GREEN)
                .lineWidth(1 / 16f);

            Arrow: if (be instanceof SignalBlockEntity) {
                if (!trackTarget1.hasValidTrack()) break Arrow;
                TrackGraphLocation location = trackTarget1.determineGraphLocation();
                if (location == null) break Arrow;

                TrackEdge edge = location.graph.getConnection(location.edge.map(location.graph::locateNode));
                if (edge == null) break Arrow;

                Vec3 forward = edge.getDirectionAt(location.position);
                Vec3 normal = edge.getNormal(location.graph, location.position);
                Vec3 side = forward.cross(normal).normalize();

                Vec3 point, sideA, sideB;

                if (padHovered) {
                    Vec3 arrowCenter = be.getBlockPos().getCenter().add(0, 0.625f, 0);
                    Vec3 back = forward.scale(0.3f);
                    Vec3 realSide = side.scale(0.45f + 0.3f + 0.5f);

                    point = arrowCenter.add(forward.scale(0.5f + 0.45f));
                    sideA = arrowCenter.add(realSide).subtract(back);
                    sideB = arrowCenter.subtract(realSide).subtract(back);
                } else {
                    Vec3 arrowCenter = bbCenter.add(0, 1/8f, 0);
                    Vec3 realSide = side.scale(0.45f);

                    point = arrowCenter.add(forward.scale(0.45f));
                    sideA = arrowCenter.add(realSide);
                    sideB = arrowCenter.subtract(realSide);
                }

                CreateClient.OUTLINER.showLine(holder.ARROW_A, point, sideA)
                    .colored(Color.SPRING_GREEN)
                    .lineWidth(1 / 16f);

                CreateClient.OUTLINER.showLine(holder.ARROW_B, point, sideB)
                    .colored(Color.SPRING_GREEN)
                    .lineWidth(1 / 16f);
            }

            SecondaryTrackTargetingBehaviour<?> trackTarget2 = SecondaryTrackTargetingBehaviour.get(be, SecondaryTrackTargetingBehaviour.TYPE);
            if (trackTarget2 != null) {
                AABB cc = new AABB(trackTarget2.getGlobalPosition()).contract(0, 10 / 16f, 0);
                CreateClient.OUTLINER.chaseAABB(holder.TRACK_BOX2, cc)
                    .colored(Color.GREEN)
                    .lineWidth(1 / 16f);

                CreateClient.OUTLINER.showLine(holder.CONNECTOR2, bbCenter, cc.getCenter())
                    .colored(Color.GREEN)
                    .lineWidth(1 / 16f);
            }
            return true;
        }
        return false;
    }
}
