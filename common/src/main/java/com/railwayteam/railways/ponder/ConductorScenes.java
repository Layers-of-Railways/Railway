/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.ponder;

import com.mojang.authlib.GameProfile;
import com.railwayteam.railways.content.conductor.ConductorEntity;
import com.railwayteam.railways.registry.CREntities;
import com.railwayteam.railways.registry.CRItems;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.ponder.ElementLink;
import com.simibubi.create.foundation.ponder.PonderPalette;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.element.EntityElement;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.utility.Pointing;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Rotations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.phys.Vec3;

public class ConductorScenes {

  public static ElementLink<EntityElement> makePlayerStand(SceneBuilder scene, String playerName, int leatherColor, Vec3 pos) {
    ItemStack playerHead = new ItemStack(Items.PLAYER_HEAD);
    GameProfile gameprofile = new GameProfile(null, playerName);
    /*try {
      Minecraft.getInstance().getSkinManager().registerSkins(gameprofile, null, false);
    } catch (NullPointerException ignored) {}*/
    SkullBlockEntity.updateGameprofile(gameprofile, (p_151177_) -> {
      CompoundTag itemTag = playerHead.getOrCreateTag();
      itemTag.put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), p_151177_));
      playerHead.setTag(itemTag);
    });

    ElementLink<EntityElement> player = scene.world.createEntity(w -> {
      ArmorStand entity = EntityType.ARMOR_STAND.create(w);
      entity.setPos(pos.x, pos.y, pos.z);
      entity.xo = pos.x;
      entity.yo = pos.y;
      entity.zo = pos.z;
      entity.animationPosition = 0;
      entity.yRotO = 210;
      entity.setYRot(210);
      entity.yHeadRotO = 210;
      entity.yHeadRot = 210;
      entity.setInvisible(true);
      return entity;
    });

    scene.world.modifyEntity(player, entity -> {
      entity.setItemSlot(EquipmentSlot.HEAD, playerHead);
      CompoundTag leatherTag = new CompoundTag();
      {
        CompoundTag displayTag = new CompoundTag();
        displayTag.putInt("color", leatherColor);
        leatherTag.put("display", displayTag);
      }
      ItemStack chestplate = new ItemStack(Items.LEATHER_CHESTPLATE);
      ItemStack leggings = new ItemStack(Items.LEATHER_LEGGINGS);
      ItemStack boots = new ItemStack(Items.LEATHER_BOOTS);
      chestplate.setTag(leatherTag);
      leggings.setTag(leatherTag);
      boots.setTag(leatherTag);
      entity.setItemSlot(EquipmentSlot.CHEST, chestplate);
      entity.setItemSlot(EquipmentSlot.LEGS, leggings);
      entity.setItemSlot(EquipmentSlot.FEET, boots);
    });
    return player;
  }

  public static ElementLink<EntityElement> makeConductor(SceneBuilder scene, DyeColor color, Vec3 pos) {
    return scene.world.createEntity(w -> {
      ConductorEntity entity = CREntities.CONDUCTOR.create(w);
      entity.setColor(color);
      entity.setItemSlot(EquipmentSlot.HEAD, CRItems.ITEM_CONDUCTOR_CAP.get(color).asStack());
      entity.setPos(pos.x, pos.y, pos.z);
      entity.xo = pos.x;
      entity.yo = pos.y;
      entity.zo = pos.z;
      entity.animationPosition = 0;
      entity.yRotO = 210;
      entity.setYRot(210);
      entity.yHeadRotO = 210;
      entity.yHeadRot = 210;
      return entity;
    });
  }

  public static void constructing(SceneBuilder scene, SceneBuildingUtil util) {
    scene.title("conductor_constructing", "Constructing a Conductor");
    scene.configureBasePlate(0, 0, 3);
    scene.scaleSceneView(1.0f);
    scene.showBasePlate();
    scene.rotateCameraY(85);

    scene.idle(10);
    BlockPos casing = util.grid.at(1, 1, 1);
    BlockPos deployer = util.grid.at(1, 3, 1);
    BlockPos cog = util.grid.at(1, 3, 2);

    scene.world.showSection(util.select.position(casing), Direction.DOWN);

    scene.overlay.showText(60)
        .pointAt(util.vector.topOf(casing))
        .attachKeyFrame()
        .placeNearTarget()
        .text("Conductors are constructed with an andesite casing.");

    scene.idle(20);

    scene.overlay.showControls(new InputWindowElement(util.vector.blockSurface(casing, Direction.UP), Pointing.DOWN)
        .rightClick()
        .withItem(CRItems.ITEM_CONDUCTOR_CAP.get(DyeColor.RED).asStack()), 40);

    scene.idle(50);
    scene.world.showSection(util.select.position(deployer), Direction.DOWN);
    scene.world.showSection(util.select.position(cog), Direction.NORTH);
    scene.world.setKineticSpeed(util.select.position(cog), 16);

    scene.idle(5);
    scene.world.moveDeployer(deployer, 1, 25);
    scene.idle(26);

    scene.world.destroyBlock(casing);
    ElementLink<EntityElement> conductor = makeConductor(scene, DyeColor.RED, Vec3.atBottomCenterOf(casing));

    scene.world.moveDeployer(deployer, -1, 25);
    scene.idle(30);

    scene.world.hideSection(util.select.position(cog), Direction.SOUTH);
    scene.world.hideSection(util.select.position(deployer), Direction.UP);
  }

  public static void redstoning(SceneBuilder scene, SceneBuildingUtil util) {
    scene.title("conductor_redstoning", "Remote Redstone with Conductors");
    scene.configureBasePlate(0, 0, 6);
    scene.scaleSceneView(1.0f);
    scene.showBasePlate();
    scene.rotateCameraY(85);

    scene.idle(10);
    BlockPos button = util.grid.at(3, 1, 0);
    BlockPos corner = util.grid.at(5, 1, 0);
    BlockPos lamp = util.grid.at(5, 1, 5);
    BlockPos buttonConductorPos = util.grid.at(4, 1, 1);

    ElementLink<EntityElement> buttonConductor = makeConductor(scene, DyeColor.RED, Vec3.atBottomCenterOf(buttonConductorPos));
    scene.world.modifyEntity(buttonConductor, entity -> {
      entity.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(button));
    });

    for (int x = 3; x <= 5; x++) { //button to corner
      scene.world.showSection(util.select.position(util.grid.at(x, 1, 0)), Direction.DOWN);
      scene.idle(2);
    }
    for (int z = 1; z <= 5; z++) { //corner to lamp
      scene.world.showSection(util.select.position(util.grid.at(5, 1, z)), Direction.DOWN);
      scene.idle(2);
    }

    scene.overlay.showText(50)
        .pointAt(util.vector.topOf(buttonConductorPos))
        .attachKeyFrame()
        .placeNearTarget()
        .text("Conductors can press buttons when a player looks at them.");

    scene.idle(20);
    ElementLink<EntityElement> player = makePlayerStand(scene, "Slimeist", 0x00FF00, Vec3.atBottomCenterOf(util.grid.at(2, 1, 2)));

    scene.world.modifyEntity(player, entity -> {
      entity.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(buttonConductorPos.below(2)));
    });

    scene.idle(15);

    for (int i = 0; i <= 20; i++) {
      int finalI = i; //lambda requires this for some reason
      scene.world.modifyEntity(player, entity -> {
        ArmorStand stand = (ArmorStand) entity;
        stand.setHeadPose(new Rotations(finalI, 0, 0));
      });
      scene.idle(1);
    }

    scene.idle(20);

    scene.overlay.showBigLine(PonderPalette.RED, util.vector.centerOf(util.grid.at(2, 2, 2)), util.vector.topOf(buttonConductorPos), 40);
    scene.world.toggleRedstonePower(util.select.fromTo(button, corner));
    scene.world.toggleRedstonePower(util.select.fromTo(corner.south(), lamp));
    scene.effects.indicateRedstone(button);

    scene.idle(30);

    for (int i = 20; i > 0; i--) {
      int finalI = i; //lambda requires this for some reason
      scene.world.modifyEntity(player, entity -> {
        ArmorStand stand = (ArmorStand) entity;
        stand.setHeadPose(new Rotations(finalI, 0, 0));
      });
      scene.idle(1);
      if (i == 10) {
        scene.world.toggleRedstonePower(util.select.fromTo(button, corner));
        scene.world.toggleRedstonePower(util.select.fromTo(corner.south(), lamp.north()));
      }
      if (i == 6) {
        scene.world.toggleRedstonePower(util.select.position(lamp));
      }
    }
    scene.rotateCameraY(-85);

    scene.idle(30);

    BlockPos piston = util.grid.at(0, 1, 5);
    BlockPos lever = util.grid.at(1, 1, 5);

    scene.world.showSection(util.select.position(piston), Direction.DOWN);
    scene.idle(30);
    scene.world.showSection(util.select.position(lever), Direction.WEST);

    BlockPos leverConductorPos = util.grid.at(3, 1, 5);

    scene.idle(10);

    ElementLink<EntityElement> leverConductor = makeConductor(scene, DyeColor.BLUE, Vec3.atBottomCenterOf(leverConductorPos));
    scene.world.modifyEntity(leverConductor, entity -> {
      entity.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(lever));
    });

    scene.overlay.showText(30)
        .pointAt(util.vector.topOf(leverConductorPos))
        .attachKeyFrame()
        .placeNearTarget()
        .text("Conductors also toggle levers when a player looks at them.");

    scene.idle(35);

    scene.world.modifyEntity(player, entity -> {
      entity.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(leverConductorPos));
      ArmorStand stand = (ArmorStand) entity;
      stand.setHeadPose(new Rotations(entity.getXRot(), 0, 0));
    });

    scene.idle(2);

    scene.overlay.showBigLine(PonderPalette.BLUE, util.vector.centerOf(util.grid.at(2, 2, 2)), util.vector.topOf(leverConductorPos), 40);
    scene.world.toggleRedstonePower(util.select.position(lever));
    scene.effects.indicateRedstone(lever);
    scene.world.modifyBlock(piston, (state) -> state.setValue(PistonBaseBlock.EXTENDED, true), false);
    scene.world.showSection(util.select.position(util.grid.at(0, 2, 5)), Direction.UP);
    scene.world.setBlock(piston.above(), Blocks.PISTON_HEAD.defaultBlockState().setValue(PistonHeadBlock.FACING, Direction.UP), false);

    scene.idle(30);

    for (int i = 0; i <= 10; i++) {
      int finalI = i;
      scene.world.modifyEntity(player, entity -> {
        ArmorStand stand = (ArmorStand) entity;
        stand.setHeadPose(new Rotations(entity.getXRot(), finalI*3.5f, 0));
      });
      scene.idle(1);
    }

    scene.idle(10);

    scene.world.modifyEntity(player, entity -> {
      entity.setItemSlot(EquipmentSlot.HEAD, CRItems.ITEM_CONDUCTOR_CAP.get(DyeColor.RED).asStack());
    });

    scene.overlay.showText(30)
        .pointAt(util.vector.topOf(util.grid.at(2, 1, 2)))
        .attachKeyFrame()
        .placeNearTarget()
        .text("Conductors do not activate redstone if a player is wearing a different-colored cap from them.");

    scene.idle(25);

    for (int i = 10; i > 0; i--) {
      int finalI = i;
      scene.world.modifyEntity(player, entity -> {
        ArmorStand stand = (ArmorStand) entity;
        stand.setHeadPose(new Rotations(entity.getXRot(), finalI*3.5f, 0));
      });
      scene.idle(1);
    }
    Vec3 playerPoint = util.vector.centerOf(util.grid.at(2, 2, 2));
    Vec3 conductorPoint = util.vector.topOf(leverConductorPos);
    Vec3 middlePoint = playerPoint.add(conductorPoint).scale(0.5f);
    scene.overlay.showLine(PonderPalette.RED, playerPoint, middlePoint, 40);
    scene.overlay.showLine(PonderPalette.BLUE, conductorPoint, middlePoint, 40);

    scene.idle(45); //end

    scene.world.modifyEntity(player, entity -> {
      entity.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(buttonConductorPos.below(2)));
      ((ArmorStand) entity).setHeadPose(new Rotations(0, 0, 0));
    });

    scene.idle(15);

    for (int i = 0; i <= 20; i++) {
      int finalI = i; //lambda requires this for some reason
      scene.world.modifyEntity(player, entity -> {
        ArmorStand stand = (ArmorStand) entity;
        stand.setHeadPose(new Rotations(finalI, 0, 0));
      });
      scene.idle(1);
    }

    scene.idle(20);

    scene.overlay.showBigLine(PonderPalette.RED, util.vector.centerOf(util.grid.at(2, 2, 2)), util.vector.topOf(buttonConductorPos), 40);
    scene.world.toggleRedstonePower(util.select.fromTo(button, corner));
    scene.world.toggleRedstonePower(util.select.fromTo(corner.south(), lamp));
    scene.effects.indicateRedstone(button);

    scene.idle(30);

    for (int i = 20; i > 0; i--) {
      int finalI = i; //lambda requires this for some reason
      scene.world.modifyEntity(player, entity -> {
        ArmorStand stand = (ArmorStand) entity;
        stand.setHeadPose(new Rotations(finalI, 0, 0));
      });
      scene.idle(1);
      if (i == 10) {
        scene.world.toggleRedstonePower(util.select.fromTo(button, corner));
        scene.world.toggleRedstonePower(util.select.fromTo(corner.south(), lamp.north()));
      }
      if (i == 6) {
        scene.world.toggleRedstonePower(util.select.position(lamp));
      }
    }
  }

  public static void toolboxing(SceneBuilder scene, SceneBuildingUtil util) {
    scene.title("conductor_toolboxing", "Toolbox Transport with Conductors");
    scene.configureBasePlate(0, 0, 3);
    scene.scaleSceneView(1.0f);
    scene.showBasePlate();
    scene.rotateCameraY(85);

    scene.idle(10);
    BlockPos conductorPos = util.grid.at(1, 1, 1);

    ElementLink<EntityElement> buttonConductor = makeConductor(scene, DyeColor.RED, Vec3.atBottomCenterOf(conductorPos));

    scene.overlay.showText(40)
        .pointAt(util.vector.topOf(conductorPos))
        .attachKeyFrame()
        .placeNearTarget()
        .text("Toolboxes function normally when equipped on Conductors.");

    scene.idle(45);

    ItemStack toolboxStack = AllBlocks.TOOLBOXES.get(DyeColor.LIME).asStack();

    scene.overlay.showControls(new InputWindowElement(util.vector.topOf(conductorPos), Pointing.DOWN).rightClick().withItem(toolboxStack), 40);

    scene.idle(35);

    scene.world.modifyEntity(buttonConductor, entity -> {
      ((ConductorEntity) entity).equipToolbox(toolboxStack);
    });

    for (int i = 0; i <= 72; i++) {
      int finalI = i;
      scene.world.modifyEntity(buttonConductor, entity -> {
        entity.setYRot((finalI * 5f) + 210);
        entity.setYBodyRot((finalI * 5f) + 210);
        entity.setYHeadRot((finalI * 5f) + 210);
        entity.setOldPosAndRot();
      });
      scene.idle(1);
    }

    scene.overlay.showControls(new InputWindowElement(util.vector.topOf(conductorPos), Pointing.DOWN).rightClick().whileSneaking(), 20);

    scene.idle(15);

    scene.world.modifyEntity(buttonConductor, entity -> {
      ((ConductorEntity) entity).unequipToolbox();
    });
    scene.world.createItemEntity(util.vector.of(1.0, 1.0, 1.0), Vec3.ZERO, toolboxStack);
  }
}
