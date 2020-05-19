package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.IntFunction;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.BlockPredicateArgument;
import net.minecraft.command.arguments.DimensionArgument;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.IRangeArgument;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.command.arguments.ObjectiveArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.RotationArgument;
import net.minecraft.command.arguments.ScoreHolderArgument;
import net.minecraft.command.arguments.SwizzleArgument;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.command.impl.data.DataCommand;
import net.minecraft.command.impl.data.IDataAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.nbt.ShortNBT;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.CustomServerBossInfo;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootPredicateManager;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class ExecuteCommand {
   private static final Dynamic2CommandExceptionType TOO_MANY_BLOCKS = new Dynamic2CommandExceptionType((p_208885_0_, p_208885_1_) -> {
      return new TranslationTextComponent("commands.execute.blocks.toobig", p_208885_0_, p_208885_1_);
   });
   private static final SimpleCommandExceptionType TEST_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.execute.conditional.fail"));
   private static final DynamicCommandExceptionType TEST_FAILED_COUNT = new DynamicCommandExceptionType((p_210446_0_) -> {
      return new TranslationTextComponent("commands.execute.conditional.fail_count", p_210446_0_);
   });
   private static final BinaryOperator<ResultConsumer<CommandSource>> COMBINE_ON_RESULT_COMPLETE = (p_209937_0_, p_209937_1_) -> {
      return (p_209939_2_, p_209939_3_, p_209939_4_) -> {
         p_209937_0_.onCommandComplete(p_209939_2_, p_209939_3_, p_209939_4_);
         p_209937_1_.onCommandComplete(p_209939_2_, p_209939_3_, p_209939_4_);
      };
   };
   private static final SuggestionProvider<CommandSource> field_229760_e_ = (p_229763_0_, p_229763_1_) -> {
      LootPredicateManager lootpredicatemanager = p_229763_0_.getSource().getServer().func_229736_aP_();
      return ISuggestionProvider.suggestIterable(lootpredicatemanager.func_227513_a_(), p_229763_1_);
   };

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      LiteralCommandNode<CommandSource> literalcommandnode = dispatcher.register(Commands.literal("execute").requires((p_198387_0_) -> {
         return p_198387_0_.hasPermissionLevel(2);
      }));
      dispatcher.register(Commands.literal("execute").requires((p_229766_0_) -> {
         return p_229766_0_.hasPermissionLevel(2);
      }).then(Commands.literal("run").redirect(dispatcher.getRoot())).then(makeIfCommand(literalcommandnode, Commands.literal("if"), true)).then(makeIfCommand(literalcommandnode, Commands.literal("unless"), false)).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork(literalcommandnode, (p_198384_0_) -> {
         List<CommandSource> list = Lists.newArrayList();

         for(Entity entity : EntityArgument.getEntitiesAllowingNone(p_198384_0_, "targets")) {
            list.add(p_198384_0_.getSource().withEntity(entity));
         }

         return list;
      }))).then(Commands.literal("at").then(Commands.argument("targets", EntityArgument.entities()).fork(literalcommandnode, (p_229809_0_) -> {
         List<CommandSource> list = Lists.newArrayList();

         for(Entity entity : EntityArgument.getEntitiesAllowingNone(p_229809_0_, "targets")) {
            list.add(p_229809_0_.getSource().withWorld((ServerWorld)entity.world).withPos(entity.getPositionVector()).withRotation(entity.getPitchYaw()));
         }

         return list;
      }))).then(Commands.literal("store").then(makeStoreSubcommand(literalcommandnode, Commands.literal("result"), true)).then(makeStoreSubcommand(literalcommandnode, Commands.literal("success"), false))).then(Commands.literal("positioned").then(Commands.argument("pos", Vec3Argument.vec3()).redirect(literalcommandnode, (p_229808_0_) -> {
         return p_229808_0_.getSource().withPos(Vec3Argument.getVec3(p_229808_0_, "pos")).withEntityAnchorType(EntityAnchorArgument.Type.FEET);
      })).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork(literalcommandnode, (p_229807_0_) -> {
         List<CommandSource> list = Lists.newArrayList();

         for(Entity entity : EntityArgument.getEntitiesAllowingNone(p_229807_0_, "targets")) {
            list.add(p_229807_0_.getSource().withPos(entity.getPositionVector()));
         }

         return list;
      })))).then(Commands.literal("rotated").then(Commands.argument("rot", RotationArgument.rotation()).redirect(literalcommandnode, (p_229806_0_) -> {
         return p_229806_0_.getSource().withRotation(RotationArgument.getRotation(p_229806_0_, "rot").getRotation(p_229806_0_.getSource()));
      })).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork(literalcommandnode, (p_201083_0_) -> {
         List<CommandSource> list = Lists.newArrayList();

         for(Entity entity : EntityArgument.getEntitiesAllowingNone(p_201083_0_, "targets")) {
            list.add(p_201083_0_.getSource().withRotation(entity.getPitchYaw()));
         }

         return list;
      })))).then(Commands.literal("facing").then(Commands.literal("entity").then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("anchor", EntityAnchorArgument.entityAnchor()).fork(literalcommandnode, (p_229805_0_) -> {
         List<CommandSource> list = Lists.newArrayList();
         EntityAnchorArgument.Type entityanchorargument$type = EntityAnchorArgument.getEntityAnchor(p_229805_0_, "anchor");

         for(Entity entity : EntityArgument.getEntitiesAllowingNone(p_229805_0_, "targets")) {
            list.add(p_229805_0_.getSource().withRotation(entity, entityanchorargument$type));
         }

         return list;
      })))).then(Commands.argument("pos", Vec3Argument.vec3()).redirect(literalcommandnode, (p_198381_0_) -> {
         return p_198381_0_.getSource().withRotation(Vec3Argument.getVec3(p_198381_0_, "pos"));
      }))).then(Commands.literal("align").then(Commands.argument("axes", SwizzleArgument.swizzle()).redirect(literalcommandnode, (p_201091_0_) -> {
         return p_201091_0_.getSource().withPos(p_201091_0_.getSource().getPos().align(SwizzleArgument.getSwizzle(p_201091_0_, "axes")));
      }))).then(Commands.literal("anchored").then(Commands.argument("anchor", EntityAnchorArgument.entityAnchor()).redirect(literalcommandnode, (p_201089_0_) -> {
         return p_201089_0_.getSource().withEntityAnchorType(EntityAnchorArgument.getEntityAnchor(p_201089_0_, "anchor"));
      }))).then(Commands.literal("in").then(Commands.argument("dimension", DimensionArgument.getDimension()).redirect(literalcommandnode, (p_229804_0_) -> {
         return p_229804_0_.getSource().withWorld(p_229804_0_.getSource().getServer().getWorld(DimensionArgument.getDimensionArgument(p_229804_0_, "dimension")));
      }))));
   }

   private static ArgumentBuilder<CommandSource, ?> makeStoreSubcommand(LiteralCommandNode<CommandSource> parent, LiteralArgumentBuilder<CommandSource> literal, boolean storingResult) {
      literal.then(Commands.literal("score").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).then(Commands.argument("objective", ObjectiveArgument.objective()).redirect(parent, (p_201468_1_) -> {
         return storeIntoScore(p_201468_1_.getSource(), ScoreHolderArgument.getScoreHolder(p_201468_1_, "targets"), ObjectiveArgument.getObjective(p_201468_1_, "objective"), storingResult);
      }))));
      literal.then(Commands.literal("bossbar").then(Commands.argument("id", ResourceLocationArgument.resourceLocation()).suggests(BossBarCommand.SUGGESTIONS_PROVIDER).then(Commands.literal("value").redirect(parent, (p_201457_1_) -> {
         return storeIntoBossbar(p_201457_1_.getSource(), BossBarCommand.getBossbar(p_201457_1_), true, storingResult);
      })).then(Commands.literal("max").redirect(parent, (p_229795_1_) -> {
         return storeIntoBossbar(p_229795_1_.getSource(), BossBarCommand.getBossbar(p_229795_1_), false, storingResult);
      }))));

      for(DataCommand.IDataProvider datacommand$idataprovider : DataCommand.field_218955_b) {
         datacommand$idataprovider.createArgument(literal, (p_229765_3_) -> {
            return p_229765_3_.then(Commands.argument("path", NBTPathArgument.nbtPath()).then(Commands.literal("int").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(parent, (p_229801_2_) -> {
               return storeIntoNBT(p_229801_2_.getSource(), datacommand$idataprovider.createAccessor(p_229801_2_), NBTPathArgument.getNBTPath(p_229801_2_, "path"), (p_229800_1_) -> {
                  return IntNBT.valueOf((int)((double)p_229800_1_ * DoubleArgumentType.getDouble(p_229801_2_, "scale")));
               }, storingResult);
            }))).then(Commands.literal("float").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(parent, (p_229798_2_) -> {
               return storeIntoNBT(p_229798_2_.getSource(), datacommand$idataprovider.createAccessor(p_229798_2_), NBTPathArgument.getNBTPath(p_229798_2_, "path"), (p_229797_1_) -> {
                  return FloatNBT.valueOf((float)((double)p_229797_1_ * DoubleArgumentType.getDouble(p_229798_2_, "scale")));
               }, storingResult);
            }))).then(Commands.literal("short").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(parent, (p_229794_2_) -> {
               return storeIntoNBT(p_229794_2_.getSource(), datacommand$idataprovider.createAccessor(p_229794_2_), NBTPathArgument.getNBTPath(p_229794_2_, "path"), (p_229792_1_) -> {
                  return ShortNBT.valueOf((short)((int)((double)p_229792_1_ * DoubleArgumentType.getDouble(p_229794_2_, "scale"))));
               }, storingResult);
            }))).then(Commands.literal("long").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(parent, (p_229790_2_) -> {
               return storeIntoNBT(p_229790_2_.getSource(), datacommand$idataprovider.createAccessor(p_229790_2_), NBTPathArgument.getNBTPath(p_229790_2_, "path"), (p_229788_1_) -> {
                  return LongNBT.valueOf((long)((double)p_229788_1_ * DoubleArgumentType.getDouble(p_229790_2_, "scale")));
               }, storingResult);
            }))).then(Commands.literal("double").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(parent, (p_229784_2_) -> {
               return storeIntoNBT(p_229784_2_.getSource(), datacommand$idataprovider.createAccessor(p_229784_2_), NBTPathArgument.getNBTPath(p_229784_2_, "path"), (p_229781_1_) -> {
                  return DoubleNBT.valueOf((double)p_229781_1_ * DoubleArgumentType.getDouble(p_229784_2_, "scale"));
               }, storingResult);
            }))).then(Commands.literal("byte").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect(parent, (p_229774_2_) -> {
               return storeIntoNBT(p_229774_2_.getSource(), datacommand$idataprovider.createAccessor(p_229774_2_), NBTPathArgument.getNBTPath(p_229774_2_, "path"), (p_229762_1_) -> {
                  return ByteNBT.valueOf((byte)((int)((double)p_229762_1_ * DoubleArgumentType.getDouble(p_229774_2_, "scale"))));
               }, storingResult);
            }))));
         });
      }

      return literal;
   }

   private static CommandSource storeIntoScore(CommandSource source, Collection<String> targets, ScoreObjective objective, boolean storingResult) {
      Scoreboard scoreboard = source.getServer().getScoreboard();
      return source.withResultConsumer((p_229769_4_, p_229769_5_, p_229769_6_) -> {
         for(String s : targets) {
            Score score = scoreboard.getOrCreateScore(s, objective);
            int i = storingResult ? p_229769_6_ : (p_229769_5_ ? 1 : 0);
            score.setScorePoints(i);
         }

      }, COMBINE_ON_RESULT_COMPLETE);
   }

   private static CommandSource storeIntoBossbar(CommandSource source, CustomServerBossInfo bar, boolean storingValue, boolean storingResult) {
      return source.withResultConsumer((p_229779_3_, p_229779_4_, p_229779_5_) -> {
         int i = storingResult ? p_229779_5_ : (p_229779_4_ ? 1 : 0);
         if (storingValue) {
            bar.setValue(i);
         } else {
            bar.setMax(i);
         }

      }, COMBINE_ON_RESULT_COMPLETE);
   }

   private static CommandSource storeIntoNBT(CommandSource source, IDataAccessor accessor, NBTPathArgument.NBTPath pathIn, IntFunction<INBT> tagConverter, boolean storingResult) {
      return source.withResultConsumer((p_229772_4_, p_229772_5_, p_229772_6_) -> {
         try {
            CompoundNBT compoundnbt = accessor.getData();
            int i = storingResult ? p_229772_6_ : (p_229772_5_ ? 1 : 0);
            pathIn.func_218076_b(compoundnbt, () -> {
               return tagConverter.apply(i);
            });
            accessor.mergeData(compoundnbt);
         } catch (CommandSyntaxException var9) {
            ;
         }

      }, COMBINE_ON_RESULT_COMPLETE);
   }

   private static ArgumentBuilder<CommandSource, ?> makeIfCommand(CommandNode<CommandSource> parent, LiteralArgumentBuilder<CommandSource> literal, boolean isIf) {
      literal.then(Commands.literal("block").then(Commands.argument("pos", BlockPosArgument.blockPos()).then(buildIfResult(parent, Commands.argument("block", BlockPredicateArgument.blockPredicate()), isIf, (p_210438_0_) -> {
         return BlockPredicateArgument.getBlockPredicate(p_210438_0_, "block").test(new CachedBlockInfo(((CommandSource)p_210438_0_.getSource()).getWorld(), BlockPosArgument.getLoadedBlockPos(p_210438_0_, "pos"), true));
      })))).then(Commands.literal("score").then(Commands.argument("target", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).then(Commands.argument("targetObjective", ObjectiveArgument.objective()).then(Commands.literal("=").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).then(buildIfResult(parent, Commands.argument("sourceObjective", ObjectiveArgument.objective()), isIf, (p_229803_0_) -> {
         return compareScores(p_229803_0_, Integer::equals);
      })))).then(Commands.literal("<").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).then(buildIfResult(parent, Commands.argument("sourceObjective", ObjectiveArgument.objective()), isIf, (p_229802_0_) -> {
         return compareScores(p_229802_0_, (p_229793_0_, p_229793_1_) -> {
            return p_229793_0_ < p_229793_1_;
         });
      })))).then(Commands.literal("<=").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).then(buildIfResult(parent, Commands.argument("sourceObjective", ObjectiveArgument.objective()), isIf, (p_229799_0_) -> {
         return compareScores(p_229799_0_, (p_229789_0_, p_229789_1_) -> {
            return p_229789_0_ <= p_229789_1_;
         });
      })))).then(Commands.literal(">").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).then(buildIfResult(parent, Commands.argument("sourceObjective", ObjectiveArgument.objective()), isIf, (p_229796_0_) -> {
         return compareScores(p_229796_0_, (p_229782_0_, p_229782_1_) -> {
            return p_229782_0_ > p_229782_1_;
         });
      })))).then(Commands.literal(">=").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).then(buildIfResult(parent, Commands.argument("sourceObjective", ObjectiveArgument.objective()), isIf, (p_201088_0_) -> {
         return compareScores(p_201088_0_, (p_229768_0_, p_229768_1_) -> {
            return p_229768_0_ >= p_229768_1_;
         });
      })))).then(Commands.literal("matches").then(buildIfResult(parent, Commands.argument("range", IRangeArgument.intRange()), isIf, (p_229787_0_) -> {
         return checkScore(p_229787_0_, IRangeArgument.IntRange.getIntRange(p_229787_0_, "range"));
      })))))).then(Commands.literal("blocks").then(Commands.argument("start", BlockPosArgument.blockPos()).then(Commands.argument("end", BlockPosArgument.blockPos()).then(Commands.argument("destination", BlockPosArgument.blockPos()).then(buildIfBlocks(parent, Commands.literal("all"), isIf, false)).then(buildIfBlocks(parent, Commands.literal("masked"), isIf, true)))))).then(Commands.literal("entity").then(Commands.argument("entities", EntityArgument.entities()).fork(parent, (p_229791_1_) -> {
         return checkIfMatches(p_229791_1_, isIf, !EntityArgument.getEntitiesAllowingNone(p_229791_1_, "entities").isEmpty());
      }).executes(func_218834_a(isIf, (p_229780_0_) -> {
         return EntityArgument.getEntitiesAllowingNone(p_229780_0_, "entities").size();
      })))).then(Commands.literal("predicate").then(buildIfResult(parent, Commands.argument("predicate", ResourceLocationArgument.resourceLocation()).suggests(field_229760_e_), isIf, (p_229761_0_) -> {
         return func_229767_a_((CommandSource)p_229761_0_.getSource(), ResourceLocationArgument.func_228259_c_(p_229761_0_, "predicate"));
      })));

      for(DataCommand.IDataProvider datacommand$idataprovider : DataCommand.field_218956_c) {
         literal.then(datacommand$idataprovider.createArgument(Commands.literal("data"), (p_229764_3_) -> {
            return p_229764_3_.then(Commands.argument("path", NBTPathArgument.nbtPath()).fork(parent, (p_229777_2_) -> {
               return checkIfMatches(p_229777_2_, isIf, func_218831_a(datacommand$idataprovider.createAccessor(p_229777_2_), NBTPathArgument.getNBTPath(p_229777_2_, "path")) > 0);
            }).executes(func_218834_a(isIf, (p_229773_1_) -> {
               return func_218831_a(datacommand$idataprovider.createAccessor(p_229773_1_), NBTPathArgument.getNBTPath(p_229773_1_, "path"));
            })));
         }));
      }

      return literal;
   }

   private static Command<CommandSource> func_218834_a(boolean p_218834_0_, ExecuteCommand.INumericTest p_218834_1_) {
      return p_218834_0_ ? (p_229783_1_) -> {
         int i = p_218834_1_.test(p_229783_1_);
         if (i > 0) {
            p_229783_1_.getSource().sendFeedback(new TranslationTextComponent("commands.execute.conditional.pass_count", i), false);
            return i;
         } else {
            throw TEST_FAILED.create();
         }
      } : (p_229771_1_) -> {
         int i = p_218834_1_.test(p_229771_1_);
         if (i == 0) {
            p_229771_1_.getSource().sendFeedback(new TranslationTextComponent("commands.execute.conditional.pass"), false);
            return 1;
         } else {
            throw TEST_FAILED_COUNT.create(i);
         }
      };
   }

   private static int func_218831_a(IDataAccessor p_218831_0_, NBTPathArgument.NBTPath p_218831_1_) throws CommandSyntaxException {
      return p_218831_1_.func_218069_b(p_218831_0_.getData());
   }

   private static boolean compareScores(CommandContext<CommandSource> context, BiPredicate<Integer, Integer> comparison) throws CommandSyntaxException {
      String s = ScoreHolderArgument.getSingleScoreHolderNoObjectives(context, "target");
      ScoreObjective scoreobjective = ObjectiveArgument.getObjective(context, "targetObjective");
      String s1 = ScoreHolderArgument.getSingleScoreHolderNoObjectives(context, "source");
      ScoreObjective scoreobjective1 = ObjectiveArgument.getObjective(context, "sourceObjective");
      Scoreboard scoreboard = context.getSource().getServer().getScoreboard();
      if (scoreboard.entityHasObjective(s, scoreobjective) && scoreboard.entityHasObjective(s1, scoreobjective1)) {
         Score score = scoreboard.getOrCreateScore(s, scoreobjective);
         Score score1 = scoreboard.getOrCreateScore(s1, scoreobjective1);
         return comparison.test(score.getScorePoints(), score1.getScorePoints());
      } else {
         return false;
      }
   }

   private static boolean checkScore(CommandContext<CommandSource> context, MinMaxBounds.IntBound bounds) throws CommandSyntaxException {
      String s = ScoreHolderArgument.getSingleScoreHolderNoObjectives(context, "target");
      ScoreObjective scoreobjective = ObjectiveArgument.getObjective(context, "targetObjective");
      Scoreboard scoreboard = context.getSource().getServer().getScoreboard();
      return !scoreboard.entityHasObjective(s, scoreobjective) ? false : bounds.test(scoreboard.getOrCreateScore(s, scoreobjective).getScorePoints());
   }

   private static boolean func_229767_a_(CommandSource p_229767_0_, ILootCondition p_229767_1_) {
      ServerWorld serverworld = p_229767_0_.getWorld();
      LootContext.Builder lootcontext$builder = (new LootContext.Builder(serverworld)).withParameter(LootParameters.POSITION, new BlockPos(p_229767_0_.getPos())).withNullableParameter(LootParameters.THIS_ENTITY, p_229767_0_.getEntity());
      return p_229767_1_.test(lootcontext$builder.build(LootParameterSets.COMMAND));
   }

   /**
    * If actual and expected match, returns a collection containing only the source player.
    */
   private static Collection<CommandSource> checkIfMatches(CommandContext<CommandSource> context, boolean actual, boolean expected) {
      return (Collection<CommandSource>)(expected == actual ? Collections.singleton(context.getSource()) : Collections.emptyList());
   }

   private static ArgumentBuilder<CommandSource, ?> buildIfResult(CommandNode<CommandSource> context, ArgumentBuilder<CommandSource, ?> builder, boolean value, ExecuteCommand.IBooleanTest test) {
      return builder.fork(context, (p_229786_2_) -> {
         return checkIfMatches(p_229786_2_, value, test.test(p_229786_2_));
      }).executes((p_229776_2_) -> {
         if (value == test.test(p_229776_2_)) {
            ((CommandSource)p_229776_2_.getSource()).sendFeedback(new TranslationTextComponent("commands.execute.conditional.pass"), false);
            return 1;
         } else {
            throw TEST_FAILED.create();
         }
      });
   }

   private static ArgumentBuilder<CommandSource, ?> buildIfBlocks(CommandNode<CommandSource> parent, ArgumentBuilder<CommandSource, ?> literal, boolean isIf, boolean isMasked) {
      return literal.fork(parent, (p_229778_2_) -> {
         return checkIfMatches(p_229778_2_, isIf, countMatchingBlocks(p_229778_2_, isMasked).isPresent());
      }).executes(isIf ? (p_229785_1_) -> {
         return checkBlockCountIf(p_229785_1_, isMasked);
      } : (p_229775_1_) -> {
         return checkBlockCountUnless(p_229775_1_, isMasked);
      });
   }

   private static int checkBlockCountIf(CommandContext<CommandSource> context, boolean isMasked) throws CommandSyntaxException {
      OptionalInt optionalint = countMatchingBlocks(context, isMasked);
      if (optionalint.isPresent()) {
         context.getSource().sendFeedback(new TranslationTextComponent("commands.execute.conditional.pass_count", optionalint.getAsInt()), false);
         return optionalint.getAsInt();
      } else {
         throw TEST_FAILED.create();
      }
   }

   private static int checkBlockCountUnless(CommandContext<CommandSource> context, boolean isMasked) throws CommandSyntaxException {
      OptionalInt optionalint = countMatchingBlocks(context, isMasked);
      if (optionalint.isPresent()) {
         throw TEST_FAILED_COUNT.create(optionalint.getAsInt());
      } else {
         context.getSource().sendFeedback(new TranslationTextComponent("commands.execute.conditional.pass"), false);
         return 1;
      }
   }

   private static OptionalInt countMatchingBlocks(CommandContext<CommandSource> context, boolean isMasked) throws CommandSyntaxException {
      return countMatchingBlocks(context.getSource().getWorld(), BlockPosArgument.getLoadedBlockPos(context, "start"), BlockPosArgument.getLoadedBlockPos(context, "end"), BlockPosArgument.getLoadedBlockPos(context, "destination"), isMasked);
   }

   private static OptionalInt countMatchingBlocks(ServerWorld worldIn, BlockPos begin, BlockPos end, BlockPos destination, boolean isMasked) throws CommandSyntaxException {
      MutableBoundingBox mutableboundingbox = new MutableBoundingBox(begin, end);
      MutableBoundingBox mutableboundingbox1 = new MutableBoundingBox(destination, destination.add(mutableboundingbox.getLength()));
      BlockPos blockpos = new BlockPos(mutableboundingbox1.minX - mutableboundingbox.minX, mutableboundingbox1.minY - mutableboundingbox.minY, mutableboundingbox1.minZ - mutableboundingbox.minZ);
      int i = mutableboundingbox.getXSize() * mutableboundingbox.getYSize() * mutableboundingbox.getZSize();
      if (i > 32768) {
         throw TOO_MANY_BLOCKS.create(32768, i);
      } else {
         int j = 0;

         for(int k = mutableboundingbox.minZ; k <= mutableboundingbox.maxZ; ++k) {
            for(int l = mutableboundingbox.minY; l <= mutableboundingbox.maxY; ++l) {
               for(int i1 = mutableboundingbox.minX; i1 <= mutableboundingbox.maxX; ++i1) {
                  BlockPos blockpos1 = new BlockPos(i1, l, k);
                  BlockPos blockpos2 = blockpos1.add(blockpos);
                  BlockState blockstate = worldIn.getBlockState(blockpos1);
                  if (!isMasked || blockstate.getBlock() != Blocks.AIR) {
                     if (blockstate != worldIn.getBlockState(blockpos2)) {
                        return OptionalInt.empty();
                     }

                     TileEntity tileentity = worldIn.getTileEntity(blockpos1);
                     TileEntity tileentity1 = worldIn.getTileEntity(blockpos2);
                     if (tileentity != null) {
                        if (tileentity1 == null) {
                           return OptionalInt.empty();
                        }

                        CompoundNBT compoundnbt = tileentity.write(new CompoundNBT());
                        compoundnbt.remove("x");
                        compoundnbt.remove("y");
                        compoundnbt.remove("z");
                        CompoundNBT compoundnbt1 = tileentity1.write(new CompoundNBT());
                        compoundnbt1.remove("x");
                        compoundnbt1.remove("y");
                        compoundnbt1.remove("z");
                        if (!compoundnbt.equals(compoundnbt1)) {
                           return OptionalInt.empty();
                        }
                     }

                     ++j;
                  }
               }
            }
         }

         return OptionalInt.of(j);
      }
   }

   @FunctionalInterface
   interface IBooleanTest {
      boolean test(CommandContext<CommandSource> p_test_1_) throws CommandSyntaxException;
   }

   @FunctionalInterface
   interface INumericTest {
      int test(CommandContext<CommandSource> p_test_1_) throws CommandSyntaxException;
   }
}