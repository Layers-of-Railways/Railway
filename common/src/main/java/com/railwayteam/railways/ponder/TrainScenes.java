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

package com.railwayteam.railways.ponder;


import com.railwayteam.railways.content.coupling.coupler.TrackCouplerBlock;
import com.railwayteam.railways.content.coupling.coupler.TrackCouplerBlockEntity;
import com.railwayteam.railways.content.switches.TrackSwitchBlock.SwitchConstraint;
import com.railwayteam.railways.content.switches.TrackSwitchBlock.SwitchState;
import com.railwayteam.railways.content.switches.TrackSwitchBlockEntity;
import com.railwayteam.railways.content.switches.TrackSwitchBlockEntity.PonderData;
import com.railwayteam.railways.mixin_interfaces.IStandardBogeyTEVirtualCoupling;
import com.railwayteam.railways.registry.CRBlocks;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity;
import com.simibubi.create.content.trains.signal.SignalBlock;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.foundation.ponder.*;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.ponder.element.ParrotElement;
import com.simibubi.create.foundation.ponder.element.WorldSectionElement;
import com.simibubi.create.foundation.ponder.instruction.PonderInstruction;
import com.simibubi.create.foundation.utility.Pointing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TrainScenes {
    public static void signaling(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("train_semaphore", "Visualizing Signal states using Semaphores");
        scene.configureBasePlate(1, 0, 15);
        scene.scaleSceneView(.5f);
        scene.showBasePlate();
        scene.rotateCameraY(85);

        for (int i = 16; i >= 0; i--) {
            scene.world.showSection(util.select.position(i, 1, 7), Direction.DOWN);
            scene.world.showSection(util.select.position(i, 1, 15 - i), Direction.DOWN);
            scene.idle(1);
        }

        scene.world.toggleControls(util.grid.at(13, 3, 7));
        scene.world.toggleControls(util.grid.at(13, 3, 1));
        scene.world.toggleControls(util.grid.at(13, 3, 4));

        BlockPos signal1 = new BlockPos(12,1,10);
        BlockPos signal2 = new BlockPos(9,1,2);
        BlockPos signal3 = new BlockPos(7,1,12);
        BlockPos signal4 = new BlockPos(4,1,4);

        BlockPos semaphore1a = new BlockPos(12,4,10);
        BlockPos semaphore1b = new BlockPos(12,6,10);
        BlockPos semaphore2a = new BlockPos(9,4,2);
        BlockPos semaphore2b = new BlockPos(9,6,2);
        BlockPos semaphore3 = new BlockPos(7,2,12);
        BlockPos semaphore4 = new BlockPos(4,4,4);

        Selection train1 = util.select.fromTo(11, 2, 6, 15, 3, 8);
        Selection train2 = util.select.fromTo(15, 2, 3, 11, 3, 5);
        Selection train3 = util.select.fromTo(11, 2, 0, 15, 3, 2);

        scene.idle(3);
        scene.world.showSection(util.select.position(signal1), Direction.DOWN);
        scene.idle(3);
        scene.world.showSection(util.select.position(signal2), Direction.DOWN);
        scene.idle(3);
        scene.world.showSection(util.select.position(signal3), Direction.DOWN);
        scene.idle(3);
        scene.world.showSection(util.select.position(signal4), Direction.DOWN);

        scene.world.replaceBlocks(util.select.position(semaphore1a.above()), Blocks.AIR.defaultBlockState(),false);
        scene.idle(15);
        scene.world.showSection(util.select.fromTo(signal1.offset(0,1,0),signal1.offset(0,2,0)), Direction.DOWN);
        scene.idle(10);
        scene.world.showSection(util.select.position(semaphore1a), Direction.DOWN);
        scene.idle(10);
        scene.overlay.showText(60)
                .pointAt(util.vector.topOf(semaphore1a))
                .attachKeyFrame()
                .placeNearTarget()
                .text("Semaphores are placed on poles above Train Signals");
        scene.idle(65);

        scene.world.showSection(util.select.fromTo(signal4.offset(0,1,0),signal4.offset(0,2,0)), Direction.DOWN);
        scene.idle(10);


        scene.overlay.showText(60)
                .pointAt(util.vector.topOf(semaphore4.below()))
                .attachKeyFrame()
                .placeNearTarget()
                .text("Semaphore poles can be made of different materials");
        scene.idle(50);

        scene.world.showSection(util.select.position(semaphore4), Direction.DOWN);

        scene.idle(10);
        scene.world.replaceBlocks(util.select.position(semaphore2a.above()), Blocks.AIR.defaultBlockState(),false);

        scene.idle(5);
        scene.world.showSection(util.select.fromTo(signal2.offset(0,1,0),signal2.offset(0,2,0)), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.position(semaphore2a), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(signal3.offset(0,1,0),signal3.offset(0,1,0)), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.position(semaphore3), Direction.DOWN);

        scene.idle(10);
        scene.special.movePointOfInterest(new Vec3(0,3,8));

        ElementLink<WorldSectionElement> trainElement = scene.world.showIndependentSection(train1, null);
        ElementLink<ParrotElement> birb1 =
                scene.special.createBirb(util.vector.centerOf(18, 3, 7), ParrotElement.FacePointOfInterestPose::new);
        scene.world.moveSection(trainElement, util.vector.of(4, 0, 0), 0);
        scene.world.moveSection(trainElement, util.vector.of(-9, 0, 0), 30);
        scene.world.animateBogey(util.grid.at(13, 2, 7), 9f, 30);
        scene.special.moveParrot(birb1, util.vector.of(-9, 0, 0), 30);
        scene.idle(10);

        scene.world.changeSignalState(signal1, SignalBlockEntity.SignalState.RED);
        scene.effects.indicateRedstone(semaphore1a);
        scene.world.changeSignalState(signal2, SignalBlockEntity.SignalState.RED);
        scene.effects.indicateRedstone(semaphore2a);

        scene.idle(35);

        ElementLink<WorldSectionElement> trainElement2 = scene.world.showIndependentSection(train3, null);
        ElementLink<ParrotElement> birb2 =
                scene.special.createBirb(util.vector.centerOf(18, 3, 7), ParrotElement.FacePointOfInterestPose::new);
        scene.world.moveSection(trainElement2, util.vector.of(4, 0, 6), 0);
        scene.world.moveSection(trainElement2, util.vector.of(-3.5, 0, 0), 25);
        scene.world.animateBogey(util.grid.at(13, 2, 1), 3.5f, 25);
        scene.special.moveParrot(birb2, util.vector.of(-3.5, 0, 0), 25);
        scene.idle(30);
        scene.special.movePointOfInterest(semaphore1a);
        scene.idle(10);

        scene.overlay.showText(70)
                .pointAt(util.vector.blockSurface(semaphore1a, Direction.WEST))
                .attachKeyFrame()
                .placeNearTarget()
                .text("Semaphores show the current state of the signal below the pole");
        scene.idle(80);

        scene.special.movePointOfInterest(new Vec3(-3,3,8));
        scene.idle(5);
        scene.world.moveSection(trainElement, util.vector.of(-10, 0, 0), 35);
        scene.world.animateBogey(util.grid.at(13, 2, 7), 10f, 35);
        scene.special.moveParrot(birb1, util.vector.of(-10, 0, 0), 35);
        scene.idle(5);
        scene.world.changeSignalState(signal1, SignalBlockEntity.SignalState.GREEN);
        scene.world.changeSignalState(signal2, SignalBlockEntity.SignalState.GREEN);
        scene.world.changeSignalState(signal4, SignalBlockEntity.SignalState.RED);
        scene.idle(5);

        scene.world.hideIndependentSection(trainElement, null);
        scene.special.hideElement(birb1, null);
        scene.idle(10);

        scene.world.moveSection(trainElement2, util.vector.of(-11.5, 0, 0), 40);
        scene.world.animateBogey(util.grid.at(13, 2, 1), 11.5f, 40);
        scene.special.moveParrot(birb2, util.vector.of(-11.5, 0, 0), 40);
        scene.idle(3);
        scene.world.changeSignalState(signal1, SignalBlockEntity.SignalState.RED);
        scene.world.changeSignalState(signal2, SignalBlockEntity.SignalState.RED);
        scene.idle(5);
        scene.world.changeSignalState(signal4, SignalBlockEntity.SignalState.GREEN);

        scene.idle(20);

        scene.world.changeSignalState(signal1, SignalBlockEntity.SignalState.GREEN);
        scene.world.changeSignalState(signal2, SignalBlockEntity.SignalState.GREEN);
        scene.world.changeSignalState(signal4, SignalBlockEntity.SignalState.RED);
        scene.idle(20);
        scene.special.movePointOfInterest(signal1);

        scene.overlay.showControls(
                new InputWindowElement(util.vector.blockSurface(signal1, Direction.EAST), Pointing.RIGHT).rightClick()
                        .withWrench(),
                40);
        scene.idle(6);
        scene.world.cycleBlockProperty(signal1, SignalBlock.TYPE);
        scene.idle(15);

        float pY = 3 / 16f;

        scene.overlay.showText(60)
                .pointAt(util.vector.blockSurface(signal1, Direction.WEST))
                .attachKeyFrame()
                .placeNearTarget()
                .text("If a brass signal has multiple valid exit paths...");
        scene.idle(70);

        Vec3 m1 = util.vector.topOf(12, 0, 7)
                .add(0, pY, 0);
        Vec3 m2 = util.vector.topOf(4, 0, 7)
                .add(0, pY, 0);
        Vec3 m3 = util.vector.topOf(12, 0, 3)
                .add(0, pY, 0);
        Vec3 m4 = util.vector.topOf(5, 0, 10)
                .add(0, pY, 0);
        Vec3 c1 = util.vector.topOf(8, 0, 7).add(0,pY,0);

        AABB bb = new AABB(m1, m1);
        scene.overlay.chaseBoundingBoxOutline(PonderPalette.OUTPUT, bb, bb, 1);
        scene.overlay.chaseBoundingBoxOutline(PonderPalette.OUTPUT, bb, bb.inflate(.45f, 0, .45f), 30);
        scene.idle(10);

        AABB bb2 = bb.move(-.45, 0, 0);
        scene.overlay.showBigLine(PonderPalette.OUTPUT, c1, m1.add(-.45, 0, 0), 20);
        scene.idle(10);
        scene.overlay.showBigLine(PonderPalette.OUTPUT, m2.add(.45, 0, 0),c1 , 10);
        scene.overlay.showBigLine(PonderPalette.OUTPUT, m4.add(.45, 0, -.45), c1, 10);

        scene.idle(20);
        scene.special.movePointOfInterest(semaphore1a);

        scene.overlay.showText(70)
                .pointAt(util.vector.blockSurface(semaphore1a, Direction.WEST))
                .attachKeyFrame()
                .placeNearTarget()
                .text("... a second semaphore can be placed above the first");
        scene.idle(50);



        scene.world.restoreBlocks(util.select.position(semaphore1a.above()));
        scene.world.showSection(util.select.position(semaphore1b.below()), Direction.DOWN);
        scene.idle(10);
        scene.world.showSection(util.select.position(semaphore1b), Direction.DOWN);
        scene.idle(30);

        scene.overlay.showText(70)
                .pointAt(util.vector.blockSurface(semaphore1a, Direction.WEST))
                .attachKeyFrame()
                .placeNearTarget()
                .text("The bottom semaphore will now close if any of the available paths are blocked");
        scene.idle(70);

        bb2 = new AABB(new BlockPos(semaphore1a)).inflate(-0.25,0,-0.25);
        scene.overlay.chaseBoundingBoxOutline(PonderPalette.OUTPUT, bb2, bb2, 30);

        bb = new AABB(m1, m1);
        scene.overlay.chaseBoundingBoxOutline(PonderPalette.OUTPUT, bb, bb, 1);
        scene.overlay.chaseBoundingBoxOutline(PonderPalette.OUTPUT, bb, bb.inflate(.45f, 0, .45f), 30);
        scene.idle(10);

        scene.overlay.showBigLine(PonderPalette.OUTPUT, c1, m1.add(-.45, 0, 0), 20);
        scene.idle(10);
        scene.overlay.showBigLine(PonderPalette.OUTPUT, m2.add(.45, 0, 0),c1 , 10);
        scene.overlay.showBigLine(PonderPalette.OUTPUT, m4.add(.45, 0, -.45), c1, 10);
        scene.idle(10);

        scene.overlay.chaseBoundingBoxOutline(PonderPalette.RED, bb2, bb2, 30);
        scene.overlay.showBigLine(PonderPalette.RED, m2.add(.45, 0, 0), m1.add(-.45, 0, 0), 30);
        scene.world.changeSignalState(signal1, SignalBlockEntity.SignalState.YELLOW);

        scene.special.movePointOfInterest(semaphore1b);
        scene.idle(30);
        scene.overlay.showText(60)
                .pointAt(util.vector.blockSurface(semaphore1b, Direction.WEST))
                .attachKeyFrame()
                .placeNearTarget()
                .text("The top semaphore will close if all paths are blocked");
        scene.idle(60);

        bb2 = new AABB(new BlockPos(semaphore1b)).inflate(-0.25,0,-0.25);
        scene.overlay.chaseBoundingBoxOutline(PonderPalette.OUTPUT, bb2, bb2, 30);

        bb = new AABB(m1, m1);
        scene.overlay.chaseBoundingBoxOutline(PonderPalette.OUTPUT, bb, bb, 1);
        scene.overlay.chaseBoundingBoxOutline(PonderPalette.OUTPUT, bb, bb.inflate(.45f, 0, .45f), 30);
        scene.idle(10);

        scene.overlay.showBigLine(PonderPalette.OUTPUT, c1, m1.add(-.45, 0, 0), 20);
        scene.idle(10);
        scene.overlay.showBigLine(PonderPalette.OUTPUT, m2.add(.45, 0, 0),c1 , 10);
        scene.overlay.showBigLine(PonderPalette.OUTPUT, m4.add(.45, 0, -.45), c1, 10);
        scene.idle(10);

        scene.overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, bb2, bb2, 30);
        scene.overlay.showBigLine(PonderPalette.GREEN, c1, m1.add(-.45, 0, 0), 30);
        scene.overlay.showBigLine(PonderPalette.GREEN, m4.add(.45, 0, -.45), c1, 30);

        scene.idle(30);

        ElementLink<WorldSectionElement> trainElement3 = scene.world.showIndependentSection(train2, null);
        scene.world.rotateSection(trainElement3, 0, 45, 0, 0);
        scene.world.moveSection(trainElement3, util.vector.of(4, 0, -6), 0);
        scene.world.moveSection(trainElement3, util.vector.of(-14, 0, 14), 40);
        scene.world.animateBogey(util.grid.at(13, 2, 4), -14f, 40);
        ElementLink<ParrotElement> birb3 =
                scene.special.createBirb(util.vector.of(18, 3.5, -2), ParrotElement.FacePointOfInterestPose::new);
        scene.special.moveParrot(birb3, util.vector.of(-14, 0, 14), 40);
        scene.idle(12);
        scene.world.changeSignalState(signal2, SignalBlockEntity.SignalState.RED);
        scene.world.changeSignalState(signal1, SignalBlockEntity.SignalState.RED);
        scene.idle(20);
        scene.world.changeSignalState(signal2, SignalBlockEntity.SignalState.GREEN);
        scene.world.changeSignalState(signal1, SignalBlockEntity.SignalState.YELLOW);
        scene.world.changeSignalState(signal3, SignalBlockEntity.SignalState.RED);
        scene.idle(20);
        scene.overlay.chaseBoundingBoxOutline(PonderPalette.OUTPUT, bb2, bb2, 30);

        bb = new AABB(m1, m1);
        scene.overlay.chaseBoundingBoxOutline(PonderPalette.OUTPUT, bb, bb, 1);
        scene.overlay.chaseBoundingBoxOutline(PonderPalette.OUTPUT, bb, bb.inflate(.45f, 0, .45f), 30);
        scene.idle(10);

        scene.overlay.showBigLine(PonderPalette.OUTPUT, c1, m1.add(-.45, 0, 0), 20);
        scene.idle(10);
        scene.overlay.showBigLine(PonderPalette.OUTPUT, m2.add(.45, 0, 0),c1 , 10);
        scene.overlay.showBigLine(PonderPalette.OUTPUT, m4.add(.45, 0, -.45), c1, 10);
        scene.idle(10);

        scene.overlay.chaseBoundingBoxOutline(PonderPalette.RED, bb2, bb2, 30);
        scene.overlay.showBigLine(PonderPalette.RED, m2.add(.45, 0, 0), m1.add(-.45, 0, 0), 30);
        scene.overlay.showBigLine(PonderPalette.RED, m4.add(.45, 0, -.45), c1, 30);
        scene.world.changeSignalState(signal1, SignalBlockEntity.SignalState.RED);

        scene.idle(20);
        scene.special.movePointOfInterest(semaphore2b);

        scene.world.restoreBlocks(util.select.position(semaphore2a.above()));
        scene.world.showSection(util.select.position(semaphore2b.below()), Direction.DOWN);
        scene.idle(10);
        scene.world.showSection(util.select.position(semaphore2b), Direction.DOWN);
        scene.idle(30);

        scene.overlay.showText(80)
                .pointAt(util.vector.blockSurface(semaphore2b, Direction.WEST))
                .attachKeyFrame()
                .placeNearTarget()
                .text("When 2 semaphores are placed on a non-brass signal, they both close simultaneously");
        scene.idle(80);

        trainElement = scene.world.showIndependentSection(train1, null);
        scene.world.rotateSection(trainElement, 0, 45, 0, 0);
        scene.world.moveSection(trainElement, util.vector.of(4, 0, -9), 0);
        scene.world.moveSection(trainElement, util.vector.of(-9, 0, 9), 40);
        scene.world.animateBogey(util.grid.at(13, 2, 7), -9f, 40);
        birb1 = scene.special.createBirb(util.vector.of(18, 3.5, -2), ParrotElement.FacePointOfInterestPose::new);
        scene.special.moveParrot(birb1, util.vector.of(-9, 0, 9), 40);

        scene.idle(15);
        scene.world.changeSignalState(signal2, SignalBlockEntity.SignalState.RED);
        scene.idle(10);
    }

    public static void trackSwitch(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("train_switch", "Using Track Switches");
        scene.configureBasePlate(0, 0, 15);
        scene.scaleSceneView(.45f);
        scene.setSceneOffsetY(-1);
        scene.showBasePlate();
//        scene.rotateCameraY(85);


        BlockPos switchPos = util.grid.at(4, 1, 1);
        BlockPos switchTargetPos = util.grid.at(7, 1, 1);
        BlockPos straightEndPos = util.grid.at(7, 1, 12);
        BlockPos leftButtonPos = util.grid.at(5, 1, 1);
        BlockPos straightButtonPos = util.grid.at(4, 1, 0);
        BlockPos rightButtonPos = util.grid.at(3, 1, 1);
        BlockPos comparatorPos = util.grid.at(4, 1, 2);
        BlockPos redstonePos = util.grid.at(4, 1, 3);
        BlockPos targetPos = util.grid.at(3, 1, 3);
        BlockPos nixiePos = util.grid.at(3, 2, 3);

        BlockPos trackPos = util.grid.at(7, 1, 0);
        BlockPos leftTrackPos = util.grid.at(12, 1, 12);
        BlockPos leftTrack2Pos = util.grid.at(13, 1, 13);
        BlockPos rightTrackPos = util.grid.at(2, 1, 12);
        BlockPos rightTrack2Pos = util.grid.at(1, 1, 13);

        Selection leftTrack = util.select.position(leftTrackPos);
        Selection leftTrack2 = util.select.position(leftTrack2Pos);
        Selection rightTrack = util.select.position(rightTrackPos);
        Selection rightTrack2 = util.select.position(rightTrack2Pos);
        Selection switchSel = util.select.position(switchPos);
        Selection comparator = util.select.position(comparatorPos);
        Selection redstone = util.select.position(redstonePos);
        Selection targetBlock = util.select.position(targetPos);
        Selection nixie = util.select.position(nixiePos);
        Selection leftButton = util.select.position(leftButtonPos);
        Selection straightButton = util.select.position(straightButtonPos);
        Selection rightButton = util.select.position(rightButtonPos);

        scene.world.showSection(leftTrack, Direction.DOWN);
        scene.world.showSection(rightTrack, Direction.DOWN);
        for (int i = 0; i < 15; i++) {
            scene.world.showSection(util.select.position(7, 1, i), Direction.DOWN);
            if (i == 1) {
                scene.world.showSection(leftTrack2, Direction.DOWN);
                scene.world.showSection(rightTrack2, Direction.DOWN);
            }
            scene.idle(1);
        }

        scene.idle(10);

        Vec3 target = util.vector.topOf(switchTargetPos.below());
        AABB bb = new AABB(target, target).move(0, 2 / 16f, 0);

        scene.overlay.showControls(new InputWindowElement(target, Pointing.DOWN).rightClick()
                .withItem(CRBlocks.ANDESITE_SWITCH.asStack()), 40);
        scene.idle(6);
        scene.overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, bb, bb, 1);
        scene.overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, bb, bb.inflate(.45f, 1 / 16f, .45f), 60);
        scene.idle(10);

        scene.overlay.showText(50)
                .pointAt(target)
                .placeNearTarget()
                .colored(PonderPalette.GREEN)
                .text("Select a Train Track then place the Switch nearby");
        scene.idle(20);

        scene.world.showSection(switchSel, Direction.DOWN);
        initSwitch(scene, switchPos, new PonderData(util.vector.topOf(switchTargetPos.below()),
                util.vector.topOf(leftTrackPos.below()),
                util.vector.topOf(straightEndPos.below()),
                util.vector.topOf(rightTrackPos.below())));
        scene.idle(15);
        scene.overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, bb, new AABB(switchPos), 20);
        scene.idle(25);

        scene.overlay.showText(70)
                .pointAt(util.vector.blockSurface(switchPos, Direction.WEST))
                .attachKeyFrame()
                .placeNearTarget()
                .text("The Switch will direct any Trains passing over the marker");

        scene.idle(70);

        scene.overlay.showControls(new InputWindowElement(util.vector.blockSurface(switchPos, Direction.EAST), Pointing.RIGHT)
                .rightClick(), 60);
        scene.idle(10);
        scene.overlay.showText(70)
                .pointAt(util.vector.blockSurface(switchPos, Direction.WEST))
                .attachKeyFrame()
                .placeNearTarget()
                .text("Cycle the switch to the right by using it");

        setSwitchState(scene, switchPos, SwitchState.REVERSE_RIGHT);
        scene.idle(70);

        scene.overlay.showControls(new InputWindowElement(util.vector.blockSurface(switchPos, Direction.EAST), Pointing.RIGHT)
                .rightClick().whileSneaking(), 20);
        scene.idle(10);
        scene.overlay.showText(70)
                .pointAt(util.vector.blockSurface(switchPos, Direction.WEST))
                .attachKeyFrame()
                .placeNearTarget()
                .text("Cycle the switch to the left by using it while sneaking");

        setSwitchState(scene, switchPos, SwitchState.NORMAL);
        scene.idle(25);
        scene.overlay.showControls(new InputWindowElement(util.vector.blockSurface(switchPos, Direction.EAST), Pointing.RIGHT)
                .rightClick().whileSneaking(), 40);
        setSwitchState(scene, switchPos, SwitchState.REVERSE_LEFT);
        scene.idle(45);

        scene.idle(20);

        scene.world.modifyBlock(redstonePos, state -> state.setValue(RedStoneWireBlock.POWER, 1), false);
        scene.world.modifyBlockEntity(nixiePos, NixieTubeBlockEntity.class, n -> n.updateRedstoneStrength(1));

        scene.world.showSection(comparator, Direction.DOWN);
        scene.idle(3);
        scene.world.showSection(redstone, Direction.DOWN);
        scene.idle(3);
        scene.world.showSection(targetBlock, Direction.DOWN);
        scene.idle(3);
        scene.world.showSection(nixie, Direction.DOWN);
        scene.idle(5);

        scene.overlay.showText(90)
                .pointAt(util.vector.blockSurface(comparatorPos, Direction.WEST))
                .attachKeyFrame()
                .placeNearTarget()
                .text("Switches provide a comparator signal based on the direction: 1 for left, 0 for straight, and 2 for right");
        scene.idle(80);

        scene.world.showSection(leftButton, Direction.DOWN);
        scene.idle(2);
        scene.world.showSection(straightButton, Direction.DOWN);
        scene.idle(2);
        scene.world.showSection(rightButton, Direction.DOWN);
        scene.idle(5);

        scene.idle(20);

        scene.overlay.showText(90)
                .pointAt(util.vector.topOf(rightButtonPos).subtract(0, 12 / 16f, 0))
                .attachKeyFrame()
                .placeNearTarget()
                .text("Switch direction can also be set by a redstone signal, and can be locked by powering the bottom");
        scene.idle(15);

        scene.world.toggleRedstonePower(rightButton);

        setSwitchState(scene, switchPos, SwitchState.REVERSE_RIGHT);
        scene.world.modifyBlock(redstonePos, state -> state.setValue(RedStoneWireBlock.POWER, 2), false);
        scene.world.modifyBlockEntity(nixiePos, NixieTubeBlockEntity.class, n -> {
            n.updateRedstoneStrength(2);
            n.updateDisplayedStrings();
        });

        scene.idle(30);
        scene.world.toggleRedstonePower(rightButton);

        scene.idle(10);

        scene.world.toggleRedstonePower(straightButton);

        setSwitchState(scene, switchPos, SwitchState.NORMAL);
        scene.world.modifyBlock(redstonePos, state -> state.setValue(RedStoneWireBlock.POWER, 0), false);
        scene.world.modifyBlockEntity(nixiePos, NixieTubeBlockEntity.class, n -> {
            n.updateRedstoneStrength(0);
            n.updateDisplayedStrings();
        });
        scene.world.toggleRedstonePower(comparator);

        scene.idle(30);
        scene.world.toggleRedstonePower(straightButton);

        scene.idle(20);

        for (int i = 0; i < 3; i++) {
            scene.world.createEntity((level) -> new Arrow(level, switchPos.getX() + 0.5, 30, switchPos.getZ() + 0.5) {
                @Override
                protected void onHitBlock(@NotNull BlockHitResult result) {
                    super.onHitBlock(result);
                    if (level.getBlockEntity(result.getBlockPos()) instanceof TrackSwitchBlockEntity switchBE) {
                        switchBE.setStatePonder(switchBE.getState().nextStateForPonder(SwitchConstraint.NONE));

                        int output = switchBE.getTargetAnalogOutput();
                        level.setBlockAndUpdate(comparatorPos, level.getBlockState(comparatorPos)
                                .setValue(ComparatorBlock.POWERED, output != 0));
                        level.setBlockAndUpdate(redstonePos, level.getBlockState(redstonePos)
                                .setValue(RedStoneWireBlock.POWER, output));
                        if (level.getBlockEntity(nixiePos) instanceof NixieTubeBlockEntity nixieBE) {
                            nixieBE.updateRedstoneStrength(output);
                            nixieBE.updateDisplayedStrings();
                        }

                        if (level instanceof PonderWorld ponderWorld)
                            ponderWorld.scene.forEach(WorldSectionElement.class, WorldSectionElement::queueRedraw);
                    }
                    discard();
                }
            });
            scene.idle(20);

            if (i == 0) {
                scene.overlay.showText(70)
                        .pointAt(util.vector.blockSurface(switchPos, Direction.WEST))
                        .attachKeyFrame()
                        .placeNearTarget()
                        .text("Projectiles also cycle switches on impact");
            }
        }

        scene.idle(60);

        /*
        scene.idleSeconds(5);
        scene.debug.debugSchematic(); // */
    }

    private static void initSwitch(SceneBuilder scene, BlockPos switchPos, PonderData data) {
        scene.addInstruction(new PonderInstruction() {
            @Override
            public boolean isComplete() {
                return true;
            }

            private Optional<TrackSwitchBlockEntity> getSwitch(PonderScene scene) {
                if (scene.getWorld().getBlockEntity(switchPos) instanceof TrackSwitchBlockEntity switchBE)
                    return Optional.of(switchBE);
                return Optional.empty();
            }

            @Override
            public void reset(PonderScene scene) {
                super.reset(scene);
                getSwitch(scene).ifPresent(switchBE -> switchBE.ponderData = null);
            }

            @Override
            public void tick(PonderScene scene) {
                getSwitch(scene).ifPresent(switchBE -> switchBE.ponderData = data);
            }
        });
    }

    private static void clearSwitch(SceneBuilder scene, BlockPos switchPos) {
        scene.addInstruction(new PonderInstruction() {
            @Override
            public boolean isComplete() {
                return true;
            }

            private Optional<TrackSwitchBlockEntity> getSwitch(PonderScene scene) {
                if (scene.getWorld().getBlockEntity(switchPos) instanceof TrackSwitchBlockEntity switchBE)
                    return Optional.of(switchBE);
                return Optional.empty();
            }

            @Override
            public void reset(PonderScene scene) {
                super.reset(scene);
                getSwitch(scene).ifPresent(switchBE -> switchBE.ponderData = null);
            }

            @Override
            public void tick(PonderScene scene) {
                getSwitch(scene).ifPresent(switchBE -> switchBE.ponderData = null);
            }
        });
    }

    private static void setSwitchState(SceneBuilder scene, BlockPos switchPos, SwitchState state) {
        scene.addInstruction(new PonderInstruction() {
            @Override
            public boolean isComplete() {
                return true;
            }

            private Optional<TrackSwitchBlockEntity> getSwitch(PonderScene scene) {
                if (scene.getWorld().getBlockEntity(switchPos) instanceof TrackSwitchBlockEntity switchBE)
                    return Optional.of(switchBE);
                return Optional.empty();
            }

            private SwitchState prevState = SwitchState.NORMAL;

            @Override
            public void reset(PonderScene scene) {
                super.reset(scene);
                getSwitch(scene).ifPresent(switchBE -> switchBE.setStatePonder(prevState));
            }

            @Override
            public void tick(PonderScene scene) {
                getSwitch(scene).ifPresent(switchBE -> {
                    prevState = switchBE.getState();
                    switchBE.setStatePonder(state);
                });
            }
        });
    }

    public static void coupling(SceneBuilder scene, SceneBuildingUtil util) {
        // Coupler placement sequence 'borrowed' from Create's Station placement sequence
        scene.title("train_coupler", "Using a Coupler");
        scene.configureBasePlate(0, 0, 21);
        scene.scaleSceneView(.45f);
        scene.showBasePlate();

        /*scene.debug.debugSchematic();
        scene.idle(1000);*/


        // reveal tracks
        for (int i = 21; i >= 0; i--) {
            scene.world.showSection(util.select.position(i, 1, 10), Direction.DOWN);
            scene.idle(1);
        }

        // setup positions
        BlockPos locoLeadingBogeyPos = new BlockPos(2, 2, 18);
        BlockPos locoTrailingBogeyPos = new BlockPos(5, 2, 18);
        BlockPos cargoBogeyPos = new BlockPos(17, 2, 18);

        Selection coupler = util.select.position(10,1,7);
        Selection redstoneDust = util.select.position(10,1,6);
        Selection button = util.select.position(10,1,5);
        Selection station = util.select.position(1, 1, 7);

        BlockPos couplerPad1 = new BlockPos(12, 1, 10);
        BlockPos couplerPad1Under = new BlockPos(12, 0, 10);
        BlockPos couplerPos = new BlockPos(10,1,7);
        BlockPos redstoneDustPos = new BlockPos(10,1,6);
        BlockPos buttonPos = new BlockPos(10,1,5);
        BlockPos stationPos = new BlockPos(1, 1, 7);

        Selection train1 = util.select.fromTo(0, 2, 20, 8, 6, 16);
        Selection train2 = util.select.fromTo(14, 2, 20, 20, 4, 16);

        Vec3 couplerPad1TopMarker = util.vector.topOf(couplerPad1Under).add(0, 3 / 16f, 0);
        Vec3 couplerTop = util.vector.topOf(couplerPos);
        Vec3 redstoneDustTop = util.vector.topOf(redstoneDustPos);
        Vec3 buttonTop = util.vector.topOf(buttonPos);
        Vec3 stationTop = util.vector.topOf(stationPos);

        // coupler placement
        AABB bb = new AABB(util.vector.topOf(couplerPad1Under), util.vector.topOf(couplerPad1Under)).move(0, 2 / 16f, 0);

        scene.overlay
            .showControls(new InputWindowElement(couplerPad1TopMarker, Pointing.DOWN).rightClick()
                .withItem(CRBlocks.TRACK_COUPLER.asStack()), 40);
        scene.idle(6);
        scene.overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, bb, bb, 1);
        scene.overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, bb, bb.inflate(.45f, 1 / 16f, .45f), 100);
        scene.idle(10);

        scene.overlay.showText(70)
            .pointAt(couplerPad1TopMarker)
            .placeNearTarget()
            .colored(PonderPalette.GREEN)
            .attachKeyFrame()
            .text("Select a Train Track then place the Coupler nearby");
        scene.idle(60);

        // reveal coupler
        scene.world.showSection(coupler, Direction.DOWN);
        scene.idle(15);
        scene.overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, bb,
            new AABB(couplerPos), 20);
        scene.idle(25);

        scene.overlay.showText(100)
                .pointAt(couplerTop)
                .placeNearTarget()
                .attachKeyFrame()
                .text("The Train Coupler lets you couple and decouple trains without disassembling them");
        scene.idle(120);

        scene.overlay
            .showControls(new InputWindowElement(couplerTop, Pointing.DOWN).scroll()
                .withWrench(), 60);
        scene.overlay.showScrollInput(couplerTop, Direction.DOWN, 60);
        scene.idle(5);

        scene.overlay.showText(70)
                .pointAt(couplerTop)
                .placeNearTarget()
                .colored(PonderPalette.GREEN)
                .attachKeyFrame()
                .text("By scrolling with a wrench you can change the gap between the couple/decouple pads");
        scene.idle(80);

        int delta = 0;
        boolean back;
        // move coupler pad forward, back, and forward again
        for (int i = 0; i < 11; i++) {
            back = i > 1 && i < 6;

            movePlate(scene, util, couplerPos, new BlockPos(-3 - delta, 0, 3), i < 7 ? 6 : 2);
            delta += back ? -1 : 1;
        }

        scene.idle(30);

        // show mode cycling with a wrench

        scene.overlay.showControls(new InputWindowElement(couplerTop, Pointing.DOWN).rightClick()
                .withWrench(), 60);
        scene.idle(5);

        scene.overlay.showText(70)
            .pointAt(couplerTop)
            .placeNearTarget()
            .colored(PonderPalette.GREEN)
            .attachKeyFrame()
            .text("By using a wrench you can cycle between coupling, decoupling, and both modes");
        scene.idle(80);

        for (int i = 0; i < 3; i++) {
            scene.world.modifyBlock(couplerPos, s -> s.cycle(TrackCouplerBlock.MODE), false);
            scene.idle(15);
        }

        // show station and explain use for alignment
        scene.world.showSection(station, Direction.DOWN);
        scene.idle(5);

        scene.overlay.showText(70)
            .pointAt(stationTop)
            .placeNearTarget()
            .colored(PonderPalette.GREEN)
            .attachKeyFrame()
            .text("Stations can be used to help align trains for coupling");
        scene.idle(80);

        // prepare trains
        ElementLink<WorldSectionElement> trainElement1 = scene.world.showIndependentSection(train1, Direction.DOWN);
        //scene.idle(2);
        ElementLink<WorldSectionElement> trainElement2 = scene.world.showIndependentSection(train2, Direction.DOWN);
        //scene.idle(2);

        scene.world.moveSection(trainElement1, util.vector.of(20, 0, -8), 0);
        scene.world.moveSection(trainElement2, util.vector.of(15, 0, -8), 0);

        // start coupler rendering
        coupleTrain(scene, cargoBogeyPos, 8, Direction.NORTH);

        // move trains and animate bogeys to coupler
        scene.world.moveSection(trainElement1, util.vector.of(-20, 0, 0), 50);
        scene.world.moveSection(trainElement2, util.vector.of(-20, 0, 0), 50);
        scene.world.animateBogey(locoLeadingBogeyPos, 20, 50);
        scene.world.animateBogey(locoTrailingBogeyPos, 20, 50);
        scene.world.animateBogey(cargoBogeyPos, 20, 50);

        scene.idle(50);
        scene.world.animateTrainStation(stationPos, true);

        // face scene head-on (to emphasize coupler alignment)
        scene.rotateCameraY(20);

        scene.idle(10);

        // reveal redstone and button
        scene.world.showSection(redstoneDust, Direction.DOWN);
        scene.idle(1);
        scene.world.showSection(button, Direction.DOWN);

        scene.idle(20);

        scene.overlay.showText(70)
            .pointAt(buttonTop)
            .placeNearTarget()
            .colored(PonderPalette.RED)
            .attachKeyFrame()
            .text("By powering a coupler you can couple/decouple a properly aligned train");
        scene.idle(80);

        // 'press' button
        scene.world.modifyBlock(buttonPos, s -> s.setValue(ButtonBlock.POWERED, true), false);
        scene.world.modifyBlock(redstoneDustPos, s -> s.setValue(RedStoneWireBlock.POWER, 15), false);

        // end coupler rendering
        decoupleTrain(scene, cargoBogeyPos);

        scene.idle(20);

        // 'unpress' button
        scene.world.modifyBlock(buttonPos, s -> s.setValue(ButtonBlock.POWERED, false), false);
        scene.world.modifyBlock(redstoneDustPos, s -> s.setValue(RedStoneWireBlock.POWER, 0), false);

        scene.idle(5);

        // move locomotive out of the scene
        scene.world.moveSection(trainElement1, util.vector.of(-40, 0, 0), 100);
        scene.world.animateBogey(locoLeadingBogeyPos, 40, 100);
        scene.world.animateBogey(locoTrailingBogeyPos, 40, 100);
        scene.world.animateTrainStation(stationPos, false);

        scene.idle(10);

        scene.rotateCameraY(-20);
    }

    /*public static void couplingAlignment(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("train_coupler_alignment", "Aligning Trains for Coupling");
        scene.configureBasePlate(0, 0, 21);
        scene.scaleSceneView(.45f);
        scene.showBasePlate();



        // reveal tracks
        for (int i = 21; i >= 0; i--) {
            scene.world.showSection(util.select.position(i, 1, 10), Direction.DOWN);
            scene.idle(1);
        }

        // setup positions
        BlockPos locoLeadingBogeyPos = new BlockPos(2, 2, 18);
        BlockPos locoTrailingBogeyPos = new BlockPos(5, 2, 18);
        BlockPos cargoBogeyPos = new BlockPos(17, 2, 18);

        Selection coupler = util.select.position(10,1,7);
        Selection redstoneDust = util.select.position(10,1,6);
        Selection button = util.select.position(10,1,5);
        Selection station = util.select.position(1, 1, 7);

        BlockPos couplerPad1 = new BlockPos(12, 1, 10);
        BlockPos couplerPad1Under = new BlockPos(12, 0, 10);
        BlockPos couplerPos = new BlockPos(10,1,7);
        BlockPos redstoneDustPos = new BlockPos(10,1,6);
        BlockPos buttonPos = new BlockPos(10,1,5);
        BlockPos stationPos = new BlockPos(1, 1, 7);

        Selection train1 = util.select.fromTo(0, 2, 20, 8, 6, 16);
        Selection train2 = util.select.fromTo(14, 2, 20, 20, 4, 16);

        Vec3 couplerPad1TopMarker = util.vector.topOf(couplerPad1Under).add(0, 3 / 16f, 0);
        Vec3 couplerTop = util.vector.topOf(couplerPos);
        Vec3 redstoneDustTop = util.vector.topOf(redstoneDustPos);
        Vec3 buttonTop = util.vector.topOf(buttonPos);
        Vec3 stationTop = util.vector.topOf(stationPos);


        // reveal coupler
        scene.world.showSection(coupler, Direction.DOWN);
        scene.idle(5);

        // show station and explain use for alignment
        scene.world.showSection(station, Direction.DOWN);
        scene.idle(5);

        // prepare trains
        ElementLink<WorldSectionElement> trainElement1 = scene.world.showIndependentSection(train1, Direction.DOWN);
        //scene.idle(2);
        ElementLink<WorldSectionElement> trainElement2 = scene.world.showIndependentSection(train2, Direction.DOWN);
        //scene.idle(2);

        scene.world.moveSection(trainElement1, util.vector.of(20, 0, -8), 0);
        scene.world.moveSection(trainElement2, util.vector.of(15, 0, -8), 0);

        // start coupler rendering
        coupleTrain(scene, cargoBogeyPos, 8, Direction.NORTH);

        // move trains and animate bogeys to coupler
        scene.world.moveSection(trainElement1, util.vector.of(-20, 0, 0), 50);
        scene.world.moveSection(trainElement2, util.vector.of(-20, 0, 0), 50);
        scene.world.animateBogey(locoLeadingBogeyPos, 20, 50);
        scene.world.animateBogey(locoTrailingBogeyPos, 20, 50);
        scene.world.animateBogey(cargoBogeyPos, 20, 50);

        scene.idle(50);
        scene.world.animateTrainStation(stationPos, true);

        // face scene head-on (to emphasize coupler alignment)
        scene.rotateCameraY(20);

        scene.idle(10);

        // reveal redstone and button
        scene.world.showSection(redstoneDust, Direction.DOWN);
        scene.idle(1);
        scene.world.showSection(button, Direction.DOWN);

        scene.idle(20);

        scene.overlay.showText(70)
            .pointAt(buttonTop)
            .placeNearTarget()
            .colored(PonderPalette.RED)
            .attachKeyFrame()
            .text("By powering a coupler you can couple/decouple a properly aligned train");
        scene.idle(80);

        // 'press' button
        scene.world.modifyBlock(buttonPos, s -> s.setValue(ButtonBlock.POWERED, true), false);
        scene.world.modifyBlock(redstoneDustPos, s -> s.setValue(RedStoneWireBlock.POWER, 15), false);

        // end coupler rendering
        decoupleTrain(scene, cargoBogeyPos);

        scene.idle(20);

        // 'unpress' button
        scene.world.modifyBlock(buttonPos, s -> s.setValue(ButtonBlock.POWERED, false), false);
        scene.world.modifyBlock(redstoneDustPos, s -> s.setValue(RedStoneWireBlock.POWER, 0), false);

        scene.idle(5);

        // move locomotive out of the scene
        scene.world.moveSection(trainElement1, util.vector.of(-40, 0, 0), 100);
        scene.world.animateBogey(locoLeadingBogeyPos, 40, 100);
        scene.world.animateBogey(locoTrailingBogeyPos, 40, 100);
        scene.world.animateTrainStation(stationPos, false);

        scene.idle(10);

        scene.rotateCameraY(-20);
    }*/

    // Coupler Ponder only code
    public static void movePlate(SceneBuilder scene, SceneBuildingUtil util, BlockPos couplerPos, BlockPos plate, int idleTicks) {
        scene.world.modifyBlockEntityNBT(util.select.position(couplerPos), TrackCouplerBlockEntity.class, nbt -> nbt.put("SecondaryTargetTrack", NbtUtils.writeBlockPos(plate)));
        scene.idle(idleTicks);
    }

    public static void coupleTrain(SceneBuilder scene, BlockPos pos, double distance, Direction direction) {
        scene.addInstruction(PonderInstruction.simple(ponderScene -> {
            PonderWorld world = ponderScene.getWorld();
            world.getBlockEntity(pos, AllBlockEntityTypes.BOGEY.get()).ifPresent(sbte -> {
                if (sbte instanceof IStandardBogeyTEVirtualCoupling virtualCoupling) {
                    virtualCoupling.setCouplingDistance(distance);
                    virtualCoupling.setCouplingDirection(direction);
                }
            });
        }));
    }

    public static void decoupleTrain(SceneBuilder scene, BlockPos pos) {
        scene.addInstruction(PonderInstruction.simple(ponderScene -> {
            PonderWorld world = ponderScene.getWorld();
            world.getBlockEntity(pos, AllBlockEntityTypes.BOGEY.get()).ifPresent(sbte -> {
                if (sbte instanceof IStandardBogeyTEVirtualCoupling virtualCoupling) {
                    virtualCoupling.setCouplingDistance(-1);
                    virtualCoupling.setCouplingDirection(Direction.UP);
                }
            });
        }));
    }
}
