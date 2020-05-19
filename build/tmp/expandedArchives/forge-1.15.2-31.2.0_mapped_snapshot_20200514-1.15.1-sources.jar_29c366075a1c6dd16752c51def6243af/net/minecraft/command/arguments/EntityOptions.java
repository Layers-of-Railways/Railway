package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.MinMaxBoundsWrapped;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class EntityOptions {
   private static final Map<String, EntityOptions.OptionHandler> REGISTRY = Maps.newHashMap();
   public static final DynamicCommandExceptionType UNKNOWN_ENTITY_OPTION = new DynamicCommandExceptionType((p_208752_0_) -> {
      return new TranslationTextComponent("argument.entity.options.unknown", p_208752_0_);
   });
   public static final DynamicCommandExceptionType INAPPLICABLE_ENTITY_OPTION = new DynamicCommandExceptionType((p_208726_0_) -> {
      return new TranslationTextComponent("argument.entity.options.inapplicable", p_208726_0_);
   });
   public static final SimpleCommandExceptionType NEGATIVE_DISTANCE = new SimpleCommandExceptionType(new TranslationTextComponent("argument.entity.options.distance.negative"));
   public static final SimpleCommandExceptionType NEGATIVE_LEVEL = new SimpleCommandExceptionType(new TranslationTextComponent("argument.entity.options.level.negative"));
   public static final SimpleCommandExceptionType NONPOSITIVE_LIMIT = new SimpleCommandExceptionType(new TranslationTextComponent("argument.entity.options.limit.toosmall"));
   public static final DynamicCommandExceptionType INVALID_SORT = new DynamicCommandExceptionType((p_208749_0_) -> {
      return new TranslationTextComponent("argument.entity.options.sort.irreversible", p_208749_0_);
   });
   public static final DynamicCommandExceptionType INVALID_GAME_MODE = new DynamicCommandExceptionType((p_208740_0_) -> {
      return new TranslationTextComponent("argument.entity.options.mode.invalid", p_208740_0_);
   });
   public static final DynamicCommandExceptionType INVALID_ENTITY_TYPE = new DynamicCommandExceptionType((p_208758_0_) -> {
      return new TranslationTextComponent("argument.entity.options.type.invalid", p_208758_0_);
   });

   public static void register(String id, EntityOptions.IFilter handler, Predicate<EntitySelectorParser> p_202024_2_, ITextComponent tooltip) {
      REGISTRY.put(id, new EntityOptions.OptionHandler(handler, p_202024_2_, tooltip));
   }

   public static void registerOptions() {
      if (REGISTRY.isEmpty()) {
         register("name", (p_197440_0_) -> {
            int i = p_197440_0_.getReader().getCursor();
            boolean flag = p_197440_0_.shouldInvertValue();
            String s = p_197440_0_.getReader().readString();
            if (p_197440_0_.hasNameNotEquals() && !flag) {
               p_197440_0_.getReader().setCursor(i);
               throw INAPPLICABLE_ENTITY_OPTION.createWithContext(p_197440_0_.getReader(), "name");
            } else {
               if (flag) {
                  p_197440_0_.setHasNameNotEquals(true);
               } else {
                  p_197440_0_.setHasNameEquals(true);
               }

               p_197440_0_.addFilter((p_197446_2_) -> {
                  return p_197446_2_.getName().getUnformattedComponentText().equals(s) != flag;
               });
            }
         }, (p_202016_0_) -> {
            return !p_202016_0_.hasNameEquals();
         }, new TranslationTextComponent("argument.entity.options.name.description"));
         register("distance", (p_197439_0_) -> {
            int i = p_197439_0_.getReader().getCursor();
            MinMaxBounds.FloatBound minmaxbounds$floatbound = MinMaxBounds.FloatBound.fromReader(p_197439_0_.getReader());
            if ((minmaxbounds$floatbound.getMin() == null || !(minmaxbounds$floatbound.getMin() < 0.0F)) && (minmaxbounds$floatbound.getMax() == null || !(minmaxbounds$floatbound.getMax() < 0.0F))) {
               p_197439_0_.setDistance(minmaxbounds$floatbound);
               p_197439_0_.setCurrentWorldOnly();
            } else {
               p_197439_0_.getReader().setCursor(i);
               throw NEGATIVE_DISTANCE.createWithContext(p_197439_0_.getReader());
            }
         }, (p_202020_0_) -> {
            return p_202020_0_.getDistance().isUnbounded();
         }, new TranslationTextComponent("argument.entity.options.distance.description"));
         register("level", (p_197438_0_) -> {
            int i = p_197438_0_.getReader().getCursor();
            MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromReader(p_197438_0_.getReader());
            if ((minmaxbounds$intbound.getMin() == null || minmaxbounds$intbound.getMin() >= 0) && (minmaxbounds$intbound.getMax() == null || minmaxbounds$intbound.getMax() >= 0)) {
               p_197438_0_.setLevel(minmaxbounds$intbound);
               p_197438_0_.setIncludeNonPlayers(false);
            } else {
               p_197438_0_.getReader().setCursor(i);
               throw NEGATIVE_LEVEL.createWithContext(p_197438_0_.getReader());
            }
         }, (p_202019_0_) -> {
            return p_202019_0_.getLevel().isUnbounded();
         }, new TranslationTextComponent("argument.entity.options.level.description"));
         register("x", (p_197437_0_) -> {
            p_197437_0_.setCurrentWorldOnly();
            p_197437_0_.setX(p_197437_0_.getReader().readDouble());
         }, (p_202022_0_) -> {
            return p_202022_0_.getX() == null;
         }, new TranslationTextComponent("argument.entity.options.x.description"));
         register("y", (p_197442_0_) -> {
            p_197442_0_.setCurrentWorldOnly();
            p_197442_0_.setY(p_197442_0_.getReader().readDouble());
         }, (p_202021_0_) -> {
            return p_202021_0_.getY() == null;
         }, new TranslationTextComponent("argument.entity.options.y.description"));
         register("z", (p_197464_0_) -> {
            p_197464_0_.setCurrentWorldOnly();
            p_197464_0_.setZ(p_197464_0_.getReader().readDouble());
         }, (p_202029_0_) -> {
            return p_202029_0_.getZ() == null;
         }, new TranslationTextComponent("argument.entity.options.z.description"));
         register("dx", (p_197460_0_) -> {
            p_197460_0_.setCurrentWorldOnly();
            p_197460_0_.setDx(p_197460_0_.getReader().readDouble());
         }, (p_202027_0_) -> {
            return p_202027_0_.getDx() == null;
         }, new TranslationTextComponent("argument.entity.options.dx.description"));
         register("dy", (p_197463_0_) -> {
            p_197463_0_.setCurrentWorldOnly();
            p_197463_0_.setDy(p_197463_0_.getReader().readDouble());
         }, (p_202026_0_) -> {
            return p_202026_0_.getDy() == null;
         }, new TranslationTextComponent("argument.entity.options.dy.description"));
         register("dz", (p_197458_0_) -> {
            p_197458_0_.setCurrentWorldOnly();
            p_197458_0_.setDz(p_197458_0_.getReader().readDouble());
         }, (p_202030_0_) -> {
            return p_202030_0_.getDz() == null;
         }, new TranslationTextComponent("argument.entity.options.dz.description"));
         register("x_rotation", (p_197462_0_) -> {
            p_197462_0_.setXRotation(MinMaxBoundsWrapped.func_207921_a(p_197462_0_.getReader(), true, MathHelper::wrapDegrees));
         }, (p_202028_0_) -> {
            return p_202028_0_.getXRotation() == MinMaxBoundsWrapped.UNBOUNDED;
         }, new TranslationTextComponent("argument.entity.options.x_rotation.description"));
         register("y_rotation", (p_197461_0_) -> {
            p_197461_0_.setYRotation(MinMaxBoundsWrapped.func_207921_a(p_197461_0_.getReader(), true, MathHelper::wrapDegrees));
         }, (p_202036_0_) -> {
            return p_202036_0_.getYRotation() == MinMaxBoundsWrapped.UNBOUNDED;
         }, new TranslationTextComponent("argument.entity.options.y_rotation.description"));
         register("limit", (p_197456_0_) -> {
            int i = p_197456_0_.getReader().getCursor();
            int j = p_197456_0_.getReader().readInt();
            if (j < 1) {
               p_197456_0_.getReader().setCursor(i);
               throw NONPOSITIVE_LIMIT.createWithContext(p_197456_0_.getReader());
            } else {
               p_197456_0_.setLimit(j);
               p_197456_0_.setLimited(true);
            }
         }, (p_202035_0_) -> {
            return !p_202035_0_.isCurrentEntity() && !p_202035_0_.isLimited();
         }, new TranslationTextComponent("argument.entity.options.limit.description"));
         register("sort", (p_197455_0_) -> {
            int i = p_197455_0_.getReader().getCursor();
            String s = p_197455_0_.getReader().readUnquotedString();
            p_197455_0_.setSuggestionHandler((p_202056_0_, p_202056_1_) -> {
               return ISuggestionProvider.suggest(Arrays.asList("nearest", "furthest", "random", "arbitrary"), p_202056_0_);
            });
            byte b0 = -1;
            switch(s.hashCode()) {
            case -938285885:
               if (s.equals("random")) {
                  b0 = 2;
               }
               break;
            case 1510793967:
               if (s.equals("furthest")) {
                  b0 = 1;
               }
               break;
            case 1780188658:
               if (s.equals("arbitrary")) {
                  b0 = 3;
               }
               break;
            case 1825779806:
               if (s.equals("nearest")) {
                  b0 = 0;
               }
            }

            BiConsumer<Vec3d, List<? extends Entity>> biconsumer;
            switch(b0) {
            case 0:
               biconsumer = EntitySelectorParser.NEAREST;
               break;
            case 1:
               biconsumer = EntitySelectorParser.FURTHEST;
               break;
            case 2:
               biconsumer = EntitySelectorParser.RANDOM;
               break;
            case 3:
               biconsumer = EntitySelectorParser.ARBITRARY;
               break;
            default:
               p_197455_0_.getReader().setCursor(i);
               throw INVALID_SORT.createWithContext(p_197455_0_.getReader(), s);
            }

            p_197455_0_.setSorter(biconsumer);
            p_197455_0_.setSorted(true);
         }, (p_202043_0_) -> {
            return !p_202043_0_.isCurrentEntity() && !p_202043_0_.isSorted();
         }, new TranslationTextComponent("argument.entity.options.sort.description"));
         register("gamemode", (p_197452_0_) -> {
            p_197452_0_.setSuggestionHandler((p_202018_1_, p_202018_2_) -> {
               String s1 = p_202018_1_.getRemaining().toLowerCase(Locale.ROOT);
               boolean flag1 = !p_197452_0_.hasGamemodeNotEquals();
               boolean flag2 = true;
               if (!s1.isEmpty()) {
                  if (s1.charAt(0) == '!') {
                     flag1 = false;
                     s1 = s1.substring(1);
                  } else {
                     flag2 = false;
                  }
               }

               for(GameType gametype1 : GameType.values()) {
                  if (gametype1 != GameType.NOT_SET && gametype1.getName().toLowerCase(Locale.ROOT).startsWith(s1)) {
                     if (flag2) {
                        p_202018_1_.suggest('!' + gametype1.getName());
                     }

                     if (flag1) {
                        p_202018_1_.suggest(gametype1.getName());
                     }
                  }
               }

               return p_202018_1_.buildFuture();
            });
            int i = p_197452_0_.getReader().getCursor();
            boolean flag = p_197452_0_.shouldInvertValue();
            if (p_197452_0_.hasGamemodeNotEquals() && !flag) {
               p_197452_0_.getReader().setCursor(i);
               throw INAPPLICABLE_ENTITY_OPTION.createWithContext(p_197452_0_.getReader(), "gamemode");
            } else {
               String s = p_197452_0_.getReader().readUnquotedString();
               GameType gametype = GameType.parseGameTypeWithDefault(s, GameType.NOT_SET);
               if (gametype == GameType.NOT_SET) {
                  p_197452_0_.getReader().setCursor(i);
                  throw INVALID_GAME_MODE.createWithContext(p_197452_0_.getReader(), s);
               } else {
                  p_197452_0_.setIncludeNonPlayers(false);
                  p_197452_0_.addFilter((p_202055_2_) -> {
                     if (!(p_202055_2_ instanceof ServerPlayerEntity)) {
                        return false;
                     } else {
                        GameType gametype1 = ((ServerPlayerEntity)p_202055_2_).interactionManager.getGameType();
                        return flag ? gametype1 != gametype : gametype1 == gametype;
                     }
                  });
                  if (flag) {
                     p_197452_0_.setHasGamemodeNotEquals(true);
                  } else {
                     p_197452_0_.setHasGamemodeEquals(true);
                  }

               }
            }
         }, (p_202048_0_) -> {
            return !p_202048_0_.hasGamemodeEquals();
         }, new TranslationTextComponent("argument.entity.options.gamemode.description"));
         register("team", (p_197449_0_) -> {
            boolean flag = p_197449_0_.shouldInvertValue();
            String s = p_197449_0_.getReader().readUnquotedString();
            p_197449_0_.addFilter((p_197454_2_) -> {
               if (!(p_197454_2_ instanceof LivingEntity)) {
                  return false;
               } else {
                  Team team = p_197454_2_.getTeam();
                  String s1 = team == null ? "" : team.getName();
                  return s1.equals(s) != flag;
               }
            });
            if (flag) {
               p_197449_0_.setHasTeamNotEquals(true);
            } else {
               p_197449_0_.setHasTeamEquals(true);
            }

         }, (p_202038_0_) -> {
            return !p_202038_0_.hasTeamEquals();
         }, new TranslationTextComponent("argument.entity.options.team.description"));
         register("type", (p_197447_0_) -> {
            p_197447_0_.setSuggestionHandler((p_202052_1_, p_202052_2_) -> {
               ISuggestionProvider.suggestIterable(Registry.ENTITY_TYPE.keySet(), p_202052_1_, String.valueOf('!'));
               ISuggestionProvider.suggestIterable(EntityTypeTags.getCollection().getRegisteredTags(), p_202052_1_, "!#");
               if (!p_197447_0_.isTypeLimitedInversely()) {
                  ISuggestionProvider.suggestIterable(Registry.ENTITY_TYPE.keySet(), p_202052_1_);
                  ISuggestionProvider.suggestIterable(EntityTypeTags.getCollection().getRegisteredTags(), p_202052_1_, String.valueOf('#'));
               }

               return p_202052_1_.buildFuture();
            });
            int i = p_197447_0_.getReader().getCursor();
            boolean flag = p_197447_0_.shouldInvertValue();
            if (p_197447_0_.isTypeLimitedInversely() && !flag) {
               p_197447_0_.getReader().setCursor(i);
               throw INAPPLICABLE_ENTITY_OPTION.createWithContext(p_197447_0_.getReader(), "type");
            } else {
               if (flag) {
                  p_197447_0_.setTypeLimitedInversely();
               }

               if (p_197447_0_.func_218115_f()) {
                  ResourceLocation resourcelocation = ResourceLocation.read(p_197447_0_.getReader());
                  Tag<EntityType<?>> tag = EntityTypeTags.getCollection().get(resourcelocation);
                  if (tag == null) {
                     p_197447_0_.getReader().setCursor(i);
                     throw INVALID_ENTITY_TYPE.createWithContext(p_197447_0_.getReader(), resourcelocation.toString());
                  }

                  p_197447_0_.addFilter((p_218127_2_) -> {
                     return tag.contains(p_218127_2_.getType()) != flag;
                  });
               } else {
                  ResourceLocation resourcelocation1 = ResourceLocation.read(p_197447_0_.getReader());
                  EntityType<?> entitytype = Registry.ENTITY_TYPE.getValue(resourcelocation1).orElseThrow(() -> {
                     p_197447_0_.getReader().setCursor(i);
                     return INVALID_ENTITY_TYPE.createWithContext(p_197447_0_.getReader(), resourcelocation1.toString());
                  });
                  if (Objects.equals(EntityType.PLAYER, entitytype) && !flag) {
                     p_197447_0_.setIncludeNonPlayers(false);
                  }

                  p_197447_0_.addFilter((p_202057_2_) -> {
                     return Objects.equals(entitytype, p_202057_2_.getType()) != flag;
                  });
                  if (!flag) {
                     p_197447_0_.func_218114_a(entitytype);
                  }
               }

            }
         }, (p_202047_0_) -> {
            return !p_202047_0_.isTypeLimited();
         }, new TranslationTextComponent("argument.entity.options.type.description"));
         register("tag", (p_197448_0_) -> {
            boolean flag = p_197448_0_.shouldInvertValue();
            String s = p_197448_0_.getReader().readUnquotedString();
            p_197448_0_.addFilter((p_197466_2_) -> {
               if ("".equals(s)) {
                  return p_197466_2_.getTags().isEmpty() != flag;
               } else {
                  return p_197466_2_.getTags().contains(s) != flag;
               }
            });
         }, (p_202041_0_) -> {
            return true;
         }, new TranslationTextComponent("argument.entity.options.tag.description"));
         register("nbt", (p_197450_0_) -> {
            boolean flag = p_197450_0_.shouldInvertValue();
            CompoundNBT compoundnbt = (new JsonToNBT(p_197450_0_.getReader())).readStruct();
            p_197450_0_.addFilter((p_197443_2_) -> {
               CompoundNBT compoundnbt1 = p_197443_2_.writeWithoutTypeId(new CompoundNBT());
               if (p_197443_2_ instanceof ServerPlayerEntity) {
                  ItemStack itemstack = ((ServerPlayerEntity)p_197443_2_).inventory.getCurrentItem();
                  if (!itemstack.isEmpty()) {
                     compoundnbt1.put("SelectedItem", itemstack.write(new CompoundNBT()));
                  }
               }

               return NBTUtil.areNBTEquals(compoundnbt, compoundnbt1, true) != flag;
            });
         }, (p_202046_0_) -> {
            return true;
         }, new TranslationTextComponent("argument.entity.options.nbt.description"));
         register("scores", (p_197457_0_) -> {
            StringReader stringreader = p_197457_0_.getReader();
            Map<String, MinMaxBounds.IntBound> map = Maps.newHashMap();
            stringreader.expect('{');
            stringreader.skipWhitespace();

            while(stringreader.canRead() && stringreader.peek() != '}') {
               stringreader.skipWhitespace();
               String s = stringreader.readUnquotedString();
               stringreader.skipWhitespace();
               stringreader.expect('=');
               stringreader.skipWhitespace();
               MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromReader(stringreader);
               map.put(s, minmaxbounds$intbound);
               stringreader.skipWhitespace();
               if (stringreader.canRead() && stringreader.peek() == ',') {
                  stringreader.skip();
               }
            }

            stringreader.expect('}');
            if (!map.isEmpty()) {
               p_197457_0_.addFilter((p_197465_1_) -> {
                  Scoreboard scoreboard = p_197465_1_.getServer().getScoreboard();
                  String s1 = p_197465_1_.getScoreboardName();

                  for(Entry<String, MinMaxBounds.IntBound> entry : map.entrySet()) {
                     ScoreObjective scoreobjective = scoreboard.getObjective(entry.getKey());
                     if (scoreobjective == null) {
                        return false;
                     }

                     if (!scoreboard.entityHasObjective(s1, scoreobjective)) {
                        return false;
                     }

                     Score score = scoreboard.getOrCreateScore(s1, scoreobjective);
                     int i = score.getScorePoints();
                     if (!entry.getValue().test(i)) {
                        return false;
                     }
                  }

                  return true;
               });
            }

            p_197457_0_.setHasScores(true);
         }, (p_202033_0_) -> {
            return !p_202033_0_.hasScores();
         }, new TranslationTextComponent("argument.entity.options.scores.description"));
         register("advancements", (p_197453_0_) -> {
            StringReader stringreader = p_197453_0_.getReader();
            Map<ResourceLocation, Predicate<AdvancementProgress>> map = Maps.newHashMap();
            stringreader.expect('{');
            stringreader.skipWhitespace();

            while(stringreader.canRead() && stringreader.peek() != '}') {
               stringreader.skipWhitespace();
               ResourceLocation resourcelocation = ResourceLocation.read(stringreader);
               stringreader.skipWhitespace();
               stringreader.expect('=');
               stringreader.skipWhitespace();
               if (stringreader.canRead() && stringreader.peek() == '{') {
                  Map<String, Predicate<CriterionProgress>> map1 = Maps.newHashMap();
                  stringreader.skipWhitespace();
                  stringreader.expect('{');
                  stringreader.skipWhitespace();

                  while(stringreader.canRead() && stringreader.peek() != '}') {
                     stringreader.skipWhitespace();
                     String s = stringreader.readUnquotedString();
                     stringreader.skipWhitespace();
                     stringreader.expect('=');
                     stringreader.skipWhitespace();
                     boolean flag1 = stringreader.readBoolean();
                     map1.put(s, (p_197444_1_) -> {
                        return p_197444_1_.isObtained() == flag1;
                     });
                     stringreader.skipWhitespace();
                     if (stringreader.canRead() && stringreader.peek() == ',') {
                        stringreader.skip();
                     }
                  }

                  stringreader.skipWhitespace();
                  stringreader.expect('}');
                  stringreader.skipWhitespace();
                  map.put(resourcelocation, (p_197435_1_) -> {
                     for(Entry<String, Predicate<CriterionProgress>> entry : map1.entrySet()) {
                        CriterionProgress criterionprogress = p_197435_1_.getCriterionProgress(entry.getKey());
                        if (criterionprogress == null || !entry.getValue().test(criterionprogress)) {
                           return false;
                        }
                     }

                     return true;
                  });
               } else {
                  boolean flag = stringreader.readBoolean();
                  map.put(resourcelocation, (p_197451_1_) -> {
                     return p_197451_1_.isDone() == flag;
                  });
               }

               stringreader.skipWhitespace();
               if (stringreader.canRead() && stringreader.peek() == ',') {
                  stringreader.skip();
               }
            }

            stringreader.expect('}');
            if (!map.isEmpty()) {
               p_197453_0_.addFilter((p_197441_1_) -> {
                  if (!(p_197441_1_ instanceof ServerPlayerEntity)) {
                     return false;
                  } else {
                     ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)p_197441_1_;
                     PlayerAdvancements playeradvancements = serverplayerentity.getAdvancements();
                     AdvancementManager advancementmanager = serverplayerentity.getServer().getAdvancementManager();

                     for(Entry<ResourceLocation, Predicate<AdvancementProgress>> entry : map.entrySet()) {
                        Advancement advancement = advancementmanager.getAdvancement(entry.getKey());
                        if (advancement == null || !entry.getValue().test(playeradvancements.getProgress(advancement))) {
                           return false;
                        }
                     }

                     return true;
                  }
               });
               p_197453_0_.setIncludeNonPlayers(false);
            }

            p_197453_0_.setHasAdvancements(true);
         }, (p_202032_0_) -> {
            return !p_202032_0_.hasAdvancements();
         }, new TranslationTextComponent("argument.entity.options.advancements.description"));
         register("predicate", (p_229367_0_) -> {
            boolean flag = p_229367_0_.shouldInvertValue();
            ResourceLocation resourcelocation = ResourceLocation.read(p_229367_0_.getReader());
            p_229367_0_.addFilter((p_229366_2_) -> {
               if (!(p_229366_2_.world instanceof ServerWorld)) {
                  return false;
               } else {
                  ServerWorld serverworld = (ServerWorld)p_229366_2_.world;
                  ILootCondition ilootcondition = serverworld.getServer().func_229736_aP_().func_227517_a_(resourcelocation);
                  if (ilootcondition == null) {
                     return false;
                  } else {
                     LootContext lootcontext = (new LootContext.Builder(serverworld)).withParameter(LootParameters.THIS_ENTITY, p_229366_2_).withParameter(LootParameters.POSITION, new BlockPos(p_229366_2_)).build(LootParameterSets.SELECTOR);
                     return flag ^ ilootcondition.test(lootcontext);
                  }
               }
            });
         }, (p_229365_0_) -> {
            return true;
         }, new TranslationTextComponent("argument.entity.options.predicate.description"));
      }
   }

   public static EntityOptions.IFilter get(EntitySelectorParser parser, String id, int cursor) throws CommandSyntaxException {
      EntityOptions.OptionHandler entityoptions$optionhandler = REGISTRY.get(id);
      if (entityoptions$optionhandler != null) {
         if (entityoptions$optionhandler.canHandle.test(parser)) {
            return entityoptions$optionhandler.handler;
         } else {
            throw INAPPLICABLE_ENTITY_OPTION.createWithContext(parser.getReader(), id);
         }
      } else {
         parser.getReader().setCursor(cursor);
         throw UNKNOWN_ENTITY_OPTION.createWithContext(parser.getReader(), id);
      }
   }

   public static void suggestOptions(EntitySelectorParser parser, SuggestionsBuilder builder) {
      String s = builder.getRemaining().toLowerCase(Locale.ROOT);

      for(Entry<String, EntityOptions.OptionHandler> entry : REGISTRY.entrySet()) {
         if ((entry.getValue()).canHandle.test(parser) && entry.getKey().toLowerCase(Locale.ROOT).startsWith(s)) {
            builder.suggest((String)entry.getKey() + '=', (entry.getValue()).tooltip);
         }
      }

   }

   public interface IFilter {
      void handle(EntitySelectorParser p_handle_1_) throws CommandSyntaxException;
   }

   static class OptionHandler {
      public final EntityOptions.IFilter handler;
      public final Predicate<EntitySelectorParser> canHandle;
      public final ITextComponent tooltip;

      private OptionHandler(EntityOptions.IFilter handlerIn, Predicate<EntitySelectorParser> p_i48717_2_, ITextComponent tooltipIn) {
         this.handler = handlerIn;
         this.canHandle = p_i48717_2_;
         this.tooltip = tooltipIn;
      }
   }
}