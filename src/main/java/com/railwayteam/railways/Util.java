package com.railwayteam.railways;

import com.railwayteam.railways.items.ConductorItem;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.RailBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Predicate;

public class Util {
  public enum Vector {
    NORTH    ( 0, 0, -1, "n"),
    SOUTH    ( 0, 0,  1, "s"),
    EAST     ( 1, 0,  0, "e"),
    WEST     (-1, 0,  0, "w"),
    NORTHWEST(-1, 0, -1, "nw"),
    NORTHEAST( 1, 0, -1, "ne"),
    SOUTHWEST(-1, 0,  1, "sw"),
    SOUTHEAST( 1, 0,  1, "se");

    public Vector3d value;
    public String name;

    private Vector(int x, int y, int z, String name) {
      value = new Vector3d(x, y, z);
      this.name = name;
    }

    public static Vector getClosest (BlockPos candidate) {
      return getClosest(new Vector3d(
        Math.signum(Math.round(candidate.getX())),
        0,
        Math.signum(Math.round(candidate.getZ()))
      ));
    }

    public static Vector getClosest (Vector3d candidate) {
      for (Vector v : values()) {
        if (Integer.signum((int) candidate.getX()) != v.value.getX()) continue;
        if (Integer.signum((int) candidate.getZ()) != v.value.getZ()) continue;
        return v;
      }
      return SOUTH;
    }

    public Vector getOpposite () {
      return getOpposite(this);
    }

    public static Vector getOpposite (Vector in) {
      switch (in) {
        case NORTH: return SOUTH;
        case SOUTH: return NORTH;
        case EAST:  return WEST;
        case WEST:  return EAST;
        case NORTHWEST: return SOUTHEAST;
        case NORTHEAST: return SOUTHWEST;
        case SOUTHWEST: return NORTHEAST;
        case SOUTHEAST: return NORTHWEST;
      }
      return SOUTH; // should never get here
    }
  }

  public static BlockPos opposite (BlockPos in) {
    return new BlockPos (in.getX()*-1, in.getY()*-1, in.getZ()*-1);
  }

  public static Vector3d opposite(Vector3d in) {
    return new Vector3d (in.getX()*-1, in.getY()*-1, in.getZ()*-1);
  }

  public static <T extends Entity> T getClosestEntity(Entity closestTo, List<T> entities, Predicate<T> check) {
    T closest = null;

    for(T entity : entities) {
      if(!check.test(entity)) continue;
      if(closest == null) { closest = entity; continue; }
      if(closest.getDistance(closestTo) < closest.getDistance(closestTo)) closest = entity;
    }

    return closest;
  }

  public static <T extends Entity> T getClosestEntity(Entity closestTo, List<T> entities) {
    return getClosestEntity(closestTo, entities, (e) -> true);
  }

  public static Method Entity_canBeRidden_Method = ObfuscationReflectionHelper.findMethod(Entity.class, "func_184228_n", Entity.class);
  public static Method Entity_canFitPassenger_Method = ObfuscationReflectionHelper.findMethod(Entity.class, "func_184219_q", Entity.class);
  public static boolean canEntitySitInMinecart(Entity entity, MinecartEntity minecart) {
    try {
      return (boolean) Entity_canBeRidden_Method.invoke(minecart, entity) && (boolean) Entity_canFitPassenger_Method.invoke(minecart, minecart);
    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace(); // this should be impossible
    }
    return false;
  }

  public static final ITag.INamedTag<Item> IronSheet = AllTags.forgeItemTag("plates/iron");
  public static final ResourceLocation EngineerCapsLoc = new ResourceLocation("railways", "engineer_caps");
  public static final Tags.IOptionalNamedTag<Item> EngineerCaps = ItemTags.createOptional(EngineerCapsLoc);

  public static ITag.INamedTag<Item> getForgeItemTag(String name) {
    return AllTags.forgeItemTag(name);
  }

  public static ITag.INamedTag<Block> getForgeBlockTag(String name) {
    return AllTags.forgeBlockTag(name);
  }

  public static ITag<Item> getMinecraftItemTag(String name) {
    return ItemTags.getCollection().get(new ResourceLocation(name));
  }

  public static ITag<Block> getMinecraftBlockTag(String name) {
    return BlockTags.getCollection().get(new ResourceLocation(name));
  }

  public interface Animatable extends IAnimatable, AnimationUtil {
    <E extends IAnimatable> AnimationBuilder getAnimation(AnimationEvent<E> event);

    default <E extends IAnimatable> PlayState getPlayState(AnimationEvent<E> event) {
      return PlayState.CONTINUE;
    }

    default <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
      setAnim(event, getAnimation(event));
      return getPlayState(event);
    }

    default int getTransitionLength() {
      return 0;
    }

    @Override
    default void registerControllers(AnimationData data) {
      data.addAnimationController(new AnimationController(this, "controller", getTransitionLength(), this::predicate));
    }
  }

  public interface AnimationUtil {
    default String getAnimationPrefix() {return "";}
    default String getAnimationSuffix() {return "";}

    default AnimationBuilder animation(String name, boolean shouldLoop) {
      return new AnimationBuilder().addAnimation(getAnimationPrefix() + name + getAnimationSuffix(), shouldLoop);
    }

    default void setAnim(AnimationEvent<?> event, AnimationBuilder builder) {
      event.getController().setAnimation(builder);
    }

    default void setAnim(AnimationEvent<?> event, String name, boolean shouldLoop) {
      setAnim(event, animation(name, shouldLoop));
    }

    default AnimationBuilder anim(String name, boolean shouldLoop) {
      return animation(name, shouldLoop);
    }
  }

  public interface WrenchableEntity {
    default ActionResultType onWrenched(PlayerEntity plr, Hand hand, Entity entity) {
      ItemStack stack = plr.getHeldItem(hand);
      if(stack.getItem().equals(AllItems.WRENCH.get()) && plr.isSneaking()) {
        entity.remove();
        afterWrenched(plr, hand);
        return ActionResultType.SUCCESS;
      }
      return ActionResultType.PASS;
    }

    default void afterWrenched(PlayerEntity plr, Hand hand) { }
  }
}
