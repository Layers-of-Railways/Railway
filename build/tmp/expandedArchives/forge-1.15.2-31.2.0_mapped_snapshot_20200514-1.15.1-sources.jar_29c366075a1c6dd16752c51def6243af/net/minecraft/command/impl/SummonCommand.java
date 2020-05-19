package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntitySummonArgument;
import net.minecraft.command.arguments.NBTCompoundTagArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class SummonCommand {
   private static final SimpleCommandExceptionType SUMMON_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.summon.failed"));

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("summon").requires((p_198740_0_) -> {
         return p_198740_0_.hasPermissionLevel(2);
      }).then(Commands.argument("entity", EntitySummonArgument.entitySummon()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes((p_198738_0_) -> {
         return summonEntity(p_198738_0_.getSource(), EntitySummonArgument.getEntityId(p_198738_0_, "entity"), p_198738_0_.getSource().getPos(), new CompoundNBT(), true);
      }).then(Commands.argument("pos", Vec3Argument.vec3()).executes((p_198735_0_) -> {
         return summonEntity(p_198735_0_.getSource(), EntitySummonArgument.getEntityId(p_198735_0_, "entity"), Vec3Argument.getVec3(p_198735_0_, "pos"), new CompoundNBT(), true);
      }).then(Commands.argument("nbt", NBTCompoundTagArgument.nbt()).executes((p_198739_0_) -> {
         return summonEntity(p_198739_0_.getSource(), EntitySummonArgument.getEntityId(p_198739_0_, "entity"), Vec3Argument.getVec3(p_198739_0_, "pos"), NBTCompoundTagArgument.getNbt(p_198739_0_, "nbt"), false);
      })))));
   }

   private static int summonEntity(CommandSource source, ResourceLocation type, Vec3d pos, CompoundNBT nbt, boolean randomizeProperties) throws CommandSyntaxException {
      CompoundNBT compoundnbt = nbt.copy();
      compoundnbt.putString("id", type.toString());
      if (EntityType.getKey(EntityType.LIGHTNING_BOLT).equals(type)) {
         LightningBoltEntity lightningboltentity = new LightningBoltEntity(source.getWorld(), pos.x, pos.y, pos.z, false);
         source.getWorld().addLightningBolt(lightningboltentity);
         source.sendFeedback(new TranslationTextComponent("commands.summon.success", lightningboltentity.getDisplayName()), true);
         return 1;
      } else {
         ServerWorld serverworld = source.getWorld();
         Entity entity = EntityType.func_220335_a(compoundnbt, serverworld, (p_218914_2_) -> {
            p_218914_2_.setLocationAndAngles(pos.x, pos.y, pos.z, p_218914_2_.rotationYaw, p_218914_2_.rotationPitch);
            return !serverworld.summonEntity(p_218914_2_) ? null : p_218914_2_;
         });
         if (entity == null) {
            throw SUMMON_FAILED.create();
         } else {
            if (randomizeProperties && entity instanceof MobEntity) {
               ((MobEntity)entity).onInitialSpawn(source.getWorld(), source.getWorld().getDifficultyForLocation(new BlockPos(entity)), SpawnReason.COMMAND, (ILivingEntityData)null, (CompoundNBT)null);
            }

            source.sendFeedback(new TranslationTextComponent("commands.summon.success", entity.getDisplayName()), true);
            return 1;
         }
      }
   }
}