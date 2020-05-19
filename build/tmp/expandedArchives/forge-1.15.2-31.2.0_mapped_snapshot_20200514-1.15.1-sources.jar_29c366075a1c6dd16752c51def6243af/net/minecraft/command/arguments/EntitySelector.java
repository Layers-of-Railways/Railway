package net.minecraft.command.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.world.server.ServerWorld;

public class EntitySelector {
   private final int limit;
   private final boolean includeNonPlayers;
   private final boolean currentWorldOnly;
   private final Predicate<Entity> filter;
   private final MinMaxBounds.FloatBound distance;
   private final Function<Vec3d, Vec3d> positionGetter;
   @Nullable
   private final AxisAlignedBB aabb;
   private final BiConsumer<Vec3d, List<? extends Entity>> sorter;
   private final boolean self;
   @Nullable
   private final String username;
   @Nullable
   private final UUID uuid;
   @Nullable
   private final EntityType<?> type;
   private final boolean checkPermission;

   public EntitySelector(int p_i50800_1_, boolean p_i50800_2_, boolean p_i50800_3_, Predicate<Entity> p_i50800_4_, MinMaxBounds.FloatBound p_i50800_5_, Function<Vec3d, Vec3d> p_i50800_6_, @Nullable AxisAlignedBB p_i50800_7_, BiConsumer<Vec3d, List<? extends Entity>> p_i50800_8_, boolean p_i50800_9_, @Nullable String p_i50800_10_, @Nullable UUID p_i50800_11_, @Nullable EntityType<?> p_i50800_12_, boolean p_i50800_13_) {
      this.limit = p_i50800_1_;
      this.includeNonPlayers = p_i50800_2_;
      this.currentWorldOnly = p_i50800_3_;
      this.filter = p_i50800_4_;
      this.distance = p_i50800_5_;
      this.positionGetter = p_i50800_6_;
      this.aabb = p_i50800_7_;
      this.sorter = p_i50800_8_;
      this.self = p_i50800_9_;
      this.username = p_i50800_10_;
      this.uuid = p_i50800_11_;
      this.type = p_i50800_12_;
      this.checkPermission = p_i50800_13_;
   }

   public int getLimit() {
      return this.limit;
   }

   public boolean includesEntities() {
      return this.includeNonPlayers;
   }

   public boolean isSelfSelector() {
      return this.self;
   }

   public boolean isWorldLimited() {
      return this.currentWorldOnly;
   }

   private void checkPermission(CommandSource source) throws CommandSyntaxException {
      if (this.checkPermission && !source.hasPermissionLevel(2)) {
         throw EntityArgument.SELECTOR_NOT_ALLOWED.create();
      }
   }

   public Entity selectOne(CommandSource source) throws CommandSyntaxException {
      this.checkPermission(source);
      List<? extends Entity> list = this.select(source);
      if (list.isEmpty()) {
         throw EntityArgument.ENTITY_NOT_FOUND.create();
      } else if (list.size() > 1) {
         throw EntityArgument.TOO_MANY_ENTITIES.create();
      } else {
         return list.get(0);
      }
   }

   public List<? extends Entity> select(CommandSource source) throws CommandSyntaxException {
      this.checkPermission(source);
      if (!this.includeNonPlayers) {
         return this.selectPlayers(source);
      } else if (this.username != null) {
         ServerPlayerEntity serverplayerentity = source.getServer().getPlayerList().getPlayerByUsername(this.username);
         return (List<? extends Entity>)(serverplayerentity == null ? Collections.emptyList() : Lists.newArrayList(serverplayerentity));
      } else if (this.uuid != null) {
         for(ServerWorld serverworld1 : source.getServer().getWorlds()) {
            Entity entity = serverworld1.getEntityByUuid(this.uuid);
            if (entity != null) {
               return Lists.newArrayList(entity);
            }
         }

         return Collections.emptyList();
      } else {
         Vec3d vec3d = this.positionGetter.apply(source.getPos());
         Predicate<Entity> predicate = this.updateFilter(vec3d);
         if (this.self) {
            return (List<? extends Entity>)(source.getEntity() != null && predicate.test(source.getEntity()) ? Lists.newArrayList(source.getEntity()) : Collections.emptyList());
         } else {
            List<Entity> list = Lists.newArrayList();
            if (this.isWorldLimited()) {
               this.getEntities(list, source.getWorld(), vec3d, predicate);
            } else {
               for(ServerWorld serverworld : source.getServer().getWorlds()) {
                  this.getEntities(list, serverworld, vec3d, predicate);
               }
            }

            return this.sortAndLimit(vec3d, list);
         }
      }
   }

   /**
    * Gets all entities matching this selector, and adds them to the passed list.
    */
   private void getEntities(List<Entity> result, ServerWorld worldIn, Vec3d pos, Predicate<Entity> predicate) {
      if (this.aabb != null) {
         result.addAll(worldIn.getEntitiesWithinAABB(this.type, this.aabb.offset(pos), predicate));
      } else {
         result.addAll(worldIn.getEntities(this.type, predicate));
      }

   }

   public ServerPlayerEntity selectOnePlayer(CommandSource source) throws CommandSyntaxException {
      this.checkPermission(source);
      List<ServerPlayerEntity> list = this.selectPlayers(source);
      if (list.size() != 1) {
         throw EntityArgument.PLAYER_NOT_FOUND.create();
      } else {
         return list.get(0);
      }
   }

   public List<ServerPlayerEntity> selectPlayers(CommandSource source) throws CommandSyntaxException {
      this.checkPermission(source);
      if (this.username != null) {
         ServerPlayerEntity serverplayerentity2 = source.getServer().getPlayerList().getPlayerByUsername(this.username);
         return (List<ServerPlayerEntity>)(serverplayerentity2 == null ? Collections.emptyList() : Lists.newArrayList(serverplayerentity2));
      } else if (this.uuid != null) {
         ServerPlayerEntity serverplayerentity1 = source.getServer().getPlayerList().getPlayerByUUID(this.uuid);
         return (List<ServerPlayerEntity>)(serverplayerentity1 == null ? Collections.emptyList() : Lists.newArrayList(serverplayerentity1));
      } else {
         Vec3d vec3d = this.positionGetter.apply(source.getPos());
         Predicate<Entity> predicate = this.updateFilter(vec3d);
         if (this.self) {
            if (source.getEntity() instanceof ServerPlayerEntity) {
               ServerPlayerEntity serverplayerentity3 = (ServerPlayerEntity)source.getEntity();
               if (predicate.test(serverplayerentity3)) {
                  return Lists.newArrayList(serverplayerentity3);
               }
            }

            return Collections.emptyList();
         } else {
            List<ServerPlayerEntity> list;
            if (this.isWorldLimited()) {
               list = source.getWorld().getPlayers(predicate::test);
            } else {
               list = Lists.newArrayList();

               for(ServerPlayerEntity serverplayerentity : source.getServer().getPlayerList().getPlayers()) {
                  if (predicate.test(serverplayerentity)) {
                     list.add(serverplayerentity);
                  }
               }
            }

            return this.sortAndLimit(vec3d, list);
         }
      }
   }

   /**
    * Returns a modified version of the predicate on this selector that also checks the AABB and distance.
    */
   private Predicate<Entity> updateFilter(Vec3d pos) {
      Predicate<Entity> predicate = this.filter;
      if (this.aabb != null) {
         AxisAlignedBB axisalignedbb = this.aabb.offset(pos);
         predicate = predicate.and((p_197344_1_) -> {
            return axisalignedbb.intersects(p_197344_1_.getBoundingBox());
         });
      }

      if (!this.distance.isUnbounded()) {
         predicate = predicate.and((p_211376_2_) -> {
            return this.distance.testSquared(p_211376_2_.getDistanceSq(pos));
         });
      }

      return predicate;
   }

   private <T extends Entity> List<T> sortAndLimit(Vec3d pos, List<T> entities) {
      if (entities.size() > 1) {
         this.sorter.accept(pos, entities);
      }

      return entities.subList(0, Math.min(this.limit, entities.size()));
   }

   public static ITextComponent joinNames(List<? extends Entity> entities) {
      return TextComponentUtils.makeList(entities, Entity::getDisplayName);
   }
}