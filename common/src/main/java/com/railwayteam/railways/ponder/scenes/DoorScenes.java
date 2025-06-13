/*
 * Steam 'n' Rails
 * Copyright (c) 2025 The Railways Team
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

package com.railwayteam.railways.ponder.scenes;

import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.elevator.ElevatorContactBlock;
import com.simibubi.create.content.decoration.palettes.AllPaletteBlocks;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class DoorScenes {
    public static void modes(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("door_modes", "Door Modes");
        scene.configureBasePlate(1, 0, 5);
        //scene.scaleSceneView(.85f);
        scene.removeShadow();
        //scene.setSceneOffsetY(-1.5f);

        BlockPos leverPos = util.grid().at(2, 1, 0);
        BlockPos redstonePos = util.grid().at(2, 1, 1);
        BlockPos doorBottomPos = util.grid().at(2, 1, 2);
        BlockPos doorTopPos = util.grid().at(2, 2, 2);
        BlockPos pulleyPos = util.grid().at(2, 11, 2);
        BlockPos elevatorDoorBottomPos = util.grid().at(2, 7, 1);
        BlockPos elevatorDoorTopPos = util.grid().at(2, 8, 1);
        BlockPos outerContactLowerPos = util.grid().at(0, 1, 2);
        BlockPos outerContactUpperPos = util.grid().at(0, 7, 2);

        Selection lever = util.select().position(leverPos);
        Selection redstone = util.select().position(redstonePos);
        Selection leverAndRedstone = util.select().fromTo(leverPos, redstonePos);
        Selection door = util.select().fromTo(doorBottomPos, doorTopPos);

        Selection elevatorFloor = util.select().fromTo(3, 6, 3, 1, 6, 1);
        Selection elevatorRoof = util.select().fromTo(3, 10, 3, 1, 10, 1);
        Selection elevatorDoor = util.select().fromTo(2, 7, 1, 2, 8, 1);
        Selection girder1 = util.select().fromTo(3, 7, 3, 3, 9, 3);
        Selection girder2 = util.select().fromTo(3, 7, 1, 3, 9, 1);
        Selection girder3 = util.select().fromTo(1, 7, 1, 1, 9, 1);
        Selection girder4 = util.select().fromTo(1, 7, 3, 1, 9, 3);
        Selection controls = util.select().fromTo(3, 7, 2, 3, 7, 2);
        Selection innerContact = util.select().fromTo(1, 7, 2, 1, 7, 2);
        Selection outerContactUpper = util.select().position(outerContactUpperPos);
        Selection outerContactLower = util.select().position(outerContactLowerPos);

        Selection powerVertical = util.select().fromTo(5, 0, 3, 5, 10, 3);
        Selection cogs = util.select().fromTo(4, 10, 3, 4, 11, 2);
        Selection elevatorPulley = util.select().fromTo(3, 11, 2, 2, 11, 2);

        Selection lowerFloor = util.select().fromTo(4, 0, 0, 0, 0, 4);
        Selection lowerFloorCutout = util.select().fromTo(1, 0, 3, 3, 0, 1);
        Selection upperFloorOutside = util.select().fromTo(4, 6, 0, 0, 6, 4).substract(elevatorFloor);

        Selection glassArea = util.select().fromTo(3, 6, 0, 1, 6, 0);

        // ACTION!

        openDoor(scene, elevatorDoorBottomPos); // open elevator door in preparation
        scene.world().cycleBlockProperty(outerContactLowerPos, ElevatorContactBlock.CALLING);
        scene.world().setBlocks(glassArea, AllPaletteBlocks.FRAMED_GLASS.getDefaultState(), false);

        // show baseplate and door

        ElementLink<WorldSectionElement> camLink = scene.world().showIndependentSection(lowerFloor, Direction.UP);
        scene.idle(5);
        scene.world().showSection(door, Direction.WEST);
        scene.idle(2);
        scene.world().showSection(redstone, Direction.SOUTH);
        scene.idle(2);
        scene.world().showSection(lever, Direction.SOUTH);

        scene.idle(10);

        // explain that doors have multiple modes
        scene.overlay().showText(80)
            .colored(PonderPalette.GREEN)
            .text("Doors have multiple modes of operation: Normal, Manual, and Special")
            .pointAt(util.vector().topOf(doorBottomPos))
            .attachKeyFrame()
            .placeNearTarget();

        scene.idle(80);

        Vec3 blockSurface = util.vector().blockSurface(doorBottomPos, Direction.NORTH)
            .add(0, 1 / 16f, 0);
        scene.overlay().showFilterSlotInput(blockSurface, Direction.NORTH, 60);
        scene.overlay().showControls(blockSurface, Pointing.DOWN, 60)
            .scroll()
            .withItem(AllItems.WRENCH.asStack());
        scene.idle(10);
        scene.overlay().showText(60)
            .pointAt(blockSurface)
            .placeNearTarget()
            .attachKeyFrame()
            .sharedText(Create.asResource("behaviour_modify_value_panel"));
        scene.idle(70);

        Vec3 upperDoorSurface = util.vector().blockSurface(doorTopPos, Direction.NORTH);

        scene.overlay().showText(60)
            .text("In normal mode (the default) and manual mode, manual operation of the door is possible")
            .pointAt(upperDoorSurface.add(-8 / 16f, 0, 0))
            .placeNearTarget()
            .attachKeyFrame();
        scene.idle(30);
        scene.overlay().showControls(upperDoorSurface, Pointing.DOWN, 20).rightClick();
        scene.idle(10);
        openDoor(scene, doorBottomPos);
        scene.idle(30);
        closeDoor(scene, doorBottomPos);
        scene.idle(10);

        scene.overlay().showText(60)
            .text("and redstone operation works in normal mode and special mode")
            .pointAt(util.vector().blockSurface(leverPos, Direction.UP).subtract(0, 4 / 16f, 0))
            .placeNearTarget()
            .attachKeyFrame();
        scene.idle(20);
        scene.world().toggleRedstonePower(leverAndRedstone);
        openDoor(scene, doorBottomPos);
        scene.idle(30);
        scene.world().toggleRedstonePower(leverAndRedstone);
        closeDoor(scene, doorBottomPos);
        scene.idle(30);

        scene.world().hideSection(leverAndRedstone, Direction.NORTH);
        scene.world().hideSection(door, Direction.WEST);
        scene.idle(10);
        scene.world().setBlocks(lowerFloorCutout, Blocks.AIR.defaultBlockState(), false);
//        scene.world().hideSection(lowerFloorCutout, Direction.DOWN);

        scene.idle(10);

        SceneBuilder sceneBuilder = scene;

        scene.addInstruction(new PonderInstruction() {

            private final LerpedFloat scale = LerpedFloat.linear().startWithValue(1.0);

            @Override
            public void onScheduled(PonderScene scene) {
                super.onScheduled(scene);
                scale.setValue(1.0);
                scale.chase(.85, .3, LerpedFloat.Chaser.EXP);
            }

            @Override
            public void reset(PonderScene scene) {
                super.reset(scene);
                scale.setValue(1.0);
                scale.chase(.85, .3, LerpedFloat.Chaser.EXP);
                sceneBuilder.scaleSceneView(1.0f);
            }

            @Override
            public boolean isComplete() {
                return scale.settled();
            }

            @Override
            public void tick(PonderScene scene) {
                scale.tickChaser();
                sceneBuilder.scaleSceneView(scale.getValue());
            }
        });

        scene.addKeyframe();

        scene.idle(5);

        scene.world().showSectionAndMerge(powerVertical, Direction.WEST, camLink);
        scene.world().showSectionAndMerge(cogs, Direction.DOWN, camLink);
        scene.world().showSectionAndMerge(elevatorPulley, Direction.DOWN, camLink);
        scene.world().movePulley(pulleyPos, 1, 0);
        scene.world().showSectionAndMerge(upperFloorOutside, Direction.DOWN, camLink);

        scene.idle(15);

        scene.world().moveSection(camLink, new Vec3(0, -7, 0), 10);

        scene.idle(10);

        ElementLink<WorldSectionElement> elevator = scene.world().showIndependentSection(elevatorFloor, Direction.UP);
        scene.world().moveSection(elevator, new Vec3(0, -7, 0), 0);

        scene.idle(5);

        scene.world().showSectionAndMerge(girder1, Direction.DOWN, elevator);
        scene.world().showSectionAndMerge(girder2, Direction.DOWN, elevator);
        scene.world().showSectionAndMerge(girder3, Direction.DOWN, elevator);
        scene.world().showSectionAndMerge(girder4, Direction.DOWN, elevator);

        scene.idle(5);

        scene.world().showSectionAndMerge(controls, Direction.WEST, elevator);
        scene.world().showSectionAndMerge(innerContact, Direction.WEST, elevator);
        scene.world().showSectionAndMerge(outerContactUpper, Direction.EAST, camLink);
        scene.world().showSectionAndMerge(outerContactLower, Direction.EAST, camLink);

        scene.idle(5);

        scene.world().showSectionAndMerge(elevatorRoof, Direction.NORTH, elevator);
        scene.world().showSectionAndMerge(elevatorDoor, Direction.SOUTH, elevator);

        scene.idle(20);
        scene.addKeyframe();

        scene.idle(15);

        scene.world().cycleBlockProperty(outerContactLowerPos, ElevatorContactBlock.CALLING);
        scene.world().cycleBlockProperty(outerContactUpperPos, ElevatorContactBlock.POWERING);

        closeDoor(scene, elevatorDoorBottomPos);

        // move elevator down
        scene.world().movePulley(pulleyPos, 6, 60);
        scene.world().moveSection(elevator, new Vec3(0, 1, 0), 60);
        scene.world().moveSection(camLink, new Vec3(0, 7, 0), 60);

        scene.idle(10);

        scene.overlay().showText(80)
            .colored(PonderPalette.BLUE)
            .text("Doors in normal and special modes open and close when a contraption moves or stops")
            .pointAt(util.vector().blockSurface(elevatorDoorTopPos.below(6), Direction.NORTH))
            .placeNearTarget();
        scene.idle(50);
        openDoor(scene, elevatorDoorBottomPos);
        scene.world().cycleBlockProperty(outerContactLowerPos, ElevatorContactBlock.POWERING);
        scene.world().cycleBlockProperty(outerContactLowerPos, ElevatorContactBlock.CALLING);
        scene.idle(60);

        // move elevator up

        // call elevator with upper, remove power from lower contact
        scene.world().cycleBlockProperty(outerContactUpperPos, ElevatorContactBlock.CALLING);
        scene.world().cycleBlockProperty(outerContactLowerPos, ElevatorContactBlock.POWERING);

        scene.world().movePulley(pulleyPos, -6, 60);
        scene.world().moveSection(elevator, new Vec3(0, -1, 0), 60);
        scene.world().moveSection(camLink, new Vec3(0, -7, 0), 60);

        scene.idle(10);

        scene.overlay().showText(80)
            .colored(PonderPalette.BLUE)
            .text("Doors in manual mode only change when a player interacts with them")
            .pointAt(util.vector().blockSurface(elevatorDoorTopPos.below(6), Direction.NORTH))
            .placeNearTarget()
            .attachKeyFrame();
        scene.idle(50);

        // disable calling state of upper contact, enable powering state of upper contact
        scene.world().cycleBlockProperty(outerContactUpperPos, ElevatorContactBlock.POWERING);
        scene.world().cycleBlockProperty(outerContactUpperPos, ElevatorContactBlock.CALLING);

        scene.idle(60);

        // call elevator down, and move it

        closeDoor(scene, elevatorDoorBottomPos);

        scene.world().cycleBlockProperty(outerContactLowerPos, ElevatorContactBlock.CALLING);
        scene.world().cycleBlockProperty(outerContactUpperPos, ElevatorContactBlock.POWERING);

        scene.world().movePulley(pulleyPos, 6, 60);
        scene.world().moveSection(elevator, new Vec3(0, 1, 0), 60);
        scene.world().moveSection(camLink, new Vec3(0, 7, 0), 60);

        scene.idle(30);

        scene.overlay().showControls(util.vector().topOf(elevatorDoorBottomPos.below(6))
            .add(8 / 16f, 0, 0), Pointing.DOWN, 60).rightClick();
        scene.overlay().showText(70)
            .colored(PonderPalette.RED)
            .text("Doors in special mode cannot be toggled simply by using them")
            .pointAt(util.vector().blockSurface(elevatorDoorTopPos.below(6), Direction.NORTH))
            .placeNearTarget()
            .attachKeyFrame();
        scene.idle(5);
        scene.effects().indicateRedstone(elevatorDoorBottomPos.below(5).north());

        scene.idle(25);

        // mark arrival of elevator by opening door and modifying contacts
        openDoor(scene, elevatorDoorBottomPos);
        scene.world().cycleBlockProperty(outerContactLowerPos, ElevatorContactBlock.POWERING);
        scene.world().cycleBlockProperty(outerContactLowerPos, ElevatorContactBlock.CALLING);
        scene.idle(45);

        // move the elevator up again, and call it
        closeDoor(scene, elevatorDoorBottomPos);

        scene.world().cycleBlockProperty(outerContactUpperPos, ElevatorContactBlock.CALLING);
        scene.world().cycleBlockProperty(outerContactLowerPos, ElevatorContactBlock.POWERING);

        scene.world().movePulley(pulleyPos, -6, 60);
        scene.world().moveSection(elevator, new Vec3(0, -1, 0), 60);
        scene.world().moveSection(camLink, new Vec3(0, -7, 0), 60);

        scene.idle(15);

        scene.overlay().showControls(util.vector().topOf(elevatorDoorBottomPos.below(6))
            .add(8 / 16f, 0, 0), Pointing.DOWN, 50).rightClick().whileSneaking();
        scene.overlay().showText(60)
            .colored(PonderPalette.GREEN)
            .text("Sneaking, however, allows the player to toggle the door anyway")
            .pointAt(util.vector().blockSurface(elevatorDoorTopPos.below(6), Direction.NORTH))
            .placeNearTarget()
            .attachKeyFrame();

        openDoor(scene, elevatorDoorBottomPos);
        scene.effects().indicateSuccess(elevatorDoorBottomPos.below(5).north());

        scene.idle(55);

        // change contact state to mark arrival
        scene.world().cycleBlockProperty(outerContactUpperPos, ElevatorContactBlock.POWERING);
        scene.world().cycleBlockProperty(outerContactUpperPos, ElevatorContactBlock.CALLING);
        scene.idle(15);

        /*
        scene.idle(90);
        scene.debug.debugSchematic();// */
    }

    private static void openDoor(SceneBuilder scene, BlockPos bottom) {
        scene.world().cycleBlockProperty(bottom, SlidingDoorBlock.OPEN);
        scene.world().cycleBlockProperty(bottom, SlidingDoorBlock.VISIBLE);
        scene.world().cycleBlockProperty(bottom.above(), SlidingDoorBlock.VISIBLE);
    }

    private static void closeDoor(SceneBuilder scene, BlockPos bottom) {
        scene.world().cycleBlockProperty(bottom, SlidingDoorBlock.OPEN);
        scene.idle(10);
        scene.world().cycleBlockProperty(bottom, SlidingDoorBlock.VISIBLE);
        scene.world().cycleBlockProperty(bottom.above(), SlidingDoorBlock.VISIBLE);
    }
}
