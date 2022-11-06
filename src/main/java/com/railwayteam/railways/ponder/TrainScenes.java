package com.railwayteam.railways.ponder;

import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.SignalBlock;
import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.SignalTileEntity;
import com.simibubi.create.foundation.ponder.*;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.ponder.element.ParrotElement;
import com.simibubi.create.foundation.ponder.element.WorldSectionElement;
import com.simibubi.create.foundation.utility.Pointing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

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
        BlockPos semaphore3 = new BlockPos(7,3,12);
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

        scene.world.changeSignalState(signal1, SignalTileEntity.SignalState.RED);
        scene.effects.indicateRedstone(semaphore1a);
        scene.world.changeSignalState(signal2, SignalTileEntity.SignalState.RED);
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
        scene.world.changeSignalState(signal1, SignalTileEntity.SignalState.GREEN);
        scene.world.changeSignalState(signal2, SignalTileEntity.SignalState.GREEN);
        scene.world.changeSignalState(signal4, SignalTileEntity.SignalState.RED);
        scene.idle(5);

        scene.world.hideIndependentSection(trainElement, null);
        scene.special.hideElement(birb1, null);
        scene.idle(10);

        scene.world.moveSection(trainElement2, util.vector.of(-11.5, 0, 0), 40);
        scene.world.animateBogey(util.grid.at(13, 2, 1), 11.5f, 40);
        scene.special.moveParrot(birb2, util.vector.of(-11.5, 0, 0), 40);
        scene.idle(3);
        scene.world.changeSignalState(signal1, SignalTileEntity.SignalState.RED);
        scene.world.changeSignalState(signal2, SignalTileEntity.SignalState.RED);
        scene.idle(5);
        scene.world.changeSignalState(signal4, SignalTileEntity.SignalState.GREEN);

        scene.idle(20);

        scene.world.changeSignalState(signal1, SignalTileEntity.SignalState.GREEN);
        scene.world.changeSignalState(signal2, SignalTileEntity.SignalState.GREEN);
        scene.world.changeSignalState(signal4, SignalTileEntity.SignalState.RED);
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
        scene.world.changeSignalState(signal1, SignalTileEntity.SignalState.YELLOW);

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
        scene.world.changeSignalState(signal2, SignalTileEntity.SignalState.RED);
        scene.world.changeSignalState(signal1, SignalTileEntity.SignalState.RED);
        scene.idle(20);
        scene.world.changeSignalState(signal2, SignalTileEntity.SignalState.GREEN);
        scene.world.changeSignalState(signal1, SignalTileEntity.SignalState.YELLOW);
        scene.world.changeSignalState(signal3, SignalTileEntity.SignalState.RED);
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
        scene.world.changeSignalState(signal1, SignalTileEntity.SignalState.RED);

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
        scene.world.changeSignalState(signal2, SignalTileEntity.SignalState.RED);
        scene.idle(10);
    }
}
