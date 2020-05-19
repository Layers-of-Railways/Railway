package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ColorArgument;
import net.minecraft.command.arguments.ComponentArgument;
import net.minecraft.command.arguments.ScoreHolderArgument;
import net.minecraft.command.arguments.TeamArgument;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class TeamCommand {
   private static final SimpleCommandExceptionType DUPLICATE_TEAM_NAME = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.add.duplicate"));
   private static final DynamicCommandExceptionType TEAM_NAME_TOO_LONG = new DynamicCommandExceptionType((p_208916_0_) -> {
      return new TranslationTextComponent("commands.team.add.longName", p_208916_0_);
   });
   private static final SimpleCommandExceptionType EMPTY_NO_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.empty.unchanged"));
   private static final SimpleCommandExceptionType NAME_NO_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.option.name.unchanged"));
   private static final SimpleCommandExceptionType COLOR_NO_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.option.color.unchanged"));
   private static final SimpleCommandExceptionType FRIENDLY_FIRE_ALREADY_ON = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.option.friendlyfire.alreadyEnabled"));
   private static final SimpleCommandExceptionType FRIENDLY_FIRE_ALREADY_OFF = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.option.friendlyfire.alreadyDisabled"));
   private static final SimpleCommandExceptionType SEE_FRIENDLY_INVISIBLES_ALREADY_ON = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.option.seeFriendlyInvisibles.alreadyEnabled"));
   private static final SimpleCommandExceptionType SEE_FRIENDLY_INVISIBLES_ALREADY_OFF = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.option.seeFriendlyInvisibles.alreadyDisabled"));
   private static final SimpleCommandExceptionType NAMETAG_VISIBILITY_NO_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.option.nametagVisibility.unchanged"));
   private static final SimpleCommandExceptionType DEATH_MESSAGE_VISIBILITY_NO_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.option.deathMessageVisibility.unchanged"));
   private static final SimpleCommandExceptionType COLLISION_NO_CHANGE = new SimpleCommandExceptionType(new TranslationTextComponent("commands.team.option.collisionRule.unchanged"));

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("team").requires((p_198780_0_) -> {
         return p_198780_0_.hasPermissionLevel(2);
      }).then(Commands.literal("list").executes((p_198760_0_) -> {
         return listTeams(p_198760_0_.getSource());
      }).then(Commands.argument("team", TeamArgument.team()).executes((p_198763_0_) -> {
         return listMembers(p_198763_0_.getSource(), TeamArgument.getTeam(p_198763_0_, "team"));
      }))).then(Commands.literal("add").then(Commands.argument("team", StringArgumentType.word()).executes((p_198767_0_) -> {
         return addTeam(p_198767_0_.getSource(), StringArgumentType.getString(p_198767_0_, "team"));
      }).then(Commands.argument("displayName", ComponentArgument.component()).executes((p_198779_0_) -> {
         return addTeam(p_198779_0_.getSource(), StringArgumentType.getString(p_198779_0_, "team"), ComponentArgument.getComponent(p_198779_0_, "displayName"));
      })))).then(Commands.literal("remove").then(Commands.argument("team", TeamArgument.team()).executes((p_198773_0_) -> {
         return removeTeam(p_198773_0_.getSource(), TeamArgument.getTeam(p_198773_0_, "team"));
      }))).then(Commands.literal("empty").then(Commands.argument("team", TeamArgument.team()).executes((p_198785_0_) -> {
         return emptyTeam(p_198785_0_.getSource(), TeamArgument.getTeam(p_198785_0_, "team"));
      }))).then(Commands.literal("join").then(Commands.argument("team", TeamArgument.team()).executes((p_198758_0_) -> {
         return joinTeam(p_198758_0_.getSource(), TeamArgument.getTeam(p_198758_0_, "team"), Collections.singleton(p_198758_0_.getSource().assertIsEntity().getScoreboardName()));
      }).then(Commands.argument("members", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).executes((p_198755_0_) -> {
         return joinTeam(p_198755_0_.getSource(), TeamArgument.getTeam(p_198755_0_, "team"), ScoreHolderArgument.getScoreHolder(p_198755_0_, "members"));
      })))).then(Commands.literal("leave").then(Commands.argument("members", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_ENTITY_SELECTOR).executes((p_198765_0_) -> {
         return leaveFromTeams(p_198765_0_.getSource(), ScoreHolderArgument.getScoreHolder(p_198765_0_, "members"));
      }))).then(Commands.literal("modify").then(Commands.argument("team", TeamArgument.team()).then(Commands.literal("displayName").then(Commands.argument("displayName", ComponentArgument.component()).executes((p_211919_0_) -> {
         return setDisplayName(p_211919_0_.getSource(), TeamArgument.getTeam(p_211919_0_, "team"), ComponentArgument.getComponent(p_211919_0_, "displayName"));
      }))).then(Commands.literal("color").then(Commands.argument("value", ColorArgument.color()).executes((p_198762_0_) -> {
         return setColor(p_198762_0_.getSource(), TeamArgument.getTeam(p_198762_0_, "team"), ColorArgument.getColor(p_198762_0_, "value"));
      }))).then(Commands.literal("friendlyFire").then(Commands.argument("allowed", BoolArgumentType.bool()).executes((p_198775_0_) -> {
         return setAllowFriendlyFire(p_198775_0_.getSource(), TeamArgument.getTeam(p_198775_0_, "team"), BoolArgumentType.getBool(p_198775_0_, "allowed"));
      }))).then(Commands.literal("seeFriendlyInvisibles").then(Commands.argument("allowed", BoolArgumentType.bool()).executes((p_198770_0_) -> {
         return setCanSeeFriendlyInvisibles(p_198770_0_.getSource(), TeamArgument.getTeam(p_198770_0_, "team"), BoolArgumentType.getBool(p_198770_0_, "allowed"));
      }))).then(Commands.literal("nametagVisibility").then(Commands.literal("never").executes((p_198778_0_) -> {
         return setNameTagVisibility(p_198778_0_.getSource(), TeamArgument.getTeam(p_198778_0_, "team"), Team.Visible.NEVER);
      })).then(Commands.literal("hideForOtherTeams").executes((p_198764_0_) -> {
         return setNameTagVisibility(p_198764_0_.getSource(), TeamArgument.getTeam(p_198764_0_, "team"), Team.Visible.HIDE_FOR_OTHER_TEAMS);
      })).then(Commands.literal("hideForOwnTeam").executes((p_198766_0_) -> {
         return setNameTagVisibility(p_198766_0_.getSource(), TeamArgument.getTeam(p_198766_0_, "team"), Team.Visible.HIDE_FOR_OWN_TEAM);
      })).then(Commands.literal("always").executes((p_198759_0_) -> {
         return setNameTagVisibility(p_198759_0_.getSource(), TeamArgument.getTeam(p_198759_0_, "team"), Team.Visible.ALWAYS);
      }))).then(Commands.literal("deathMessageVisibility").then(Commands.literal("never").executes((p_198789_0_) -> {
         return setDeathMessageVisibility(p_198789_0_.getSource(), TeamArgument.getTeam(p_198789_0_, "team"), Team.Visible.NEVER);
      })).then(Commands.literal("hideForOtherTeams").executes((p_198791_0_) -> {
         return setDeathMessageVisibility(p_198791_0_.getSource(), TeamArgument.getTeam(p_198791_0_, "team"), Team.Visible.HIDE_FOR_OTHER_TEAMS);
      })).then(Commands.literal("hideForOwnTeam").executes((p_198769_0_) -> {
         return setDeathMessageVisibility(p_198769_0_.getSource(), TeamArgument.getTeam(p_198769_0_, "team"), Team.Visible.HIDE_FOR_OWN_TEAM);
      })).then(Commands.literal("always").executes((p_198774_0_) -> {
         return setDeathMessageVisibility(p_198774_0_.getSource(), TeamArgument.getTeam(p_198774_0_, "team"), Team.Visible.ALWAYS);
      }))).then(Commands.literal("collisionRule").then(Commands.literal("never").executes((p_198761_0_) -> {
         return setCollisionRule(p_198761_0_.getSource(), TeamArgument.getTeam(p_198761_0_, "team"), Team.CollisionRule.NEVER);
      })).then(Commands.literal("pushOwnTeam").executes((p_198756_0_) -> {
         return setCollisionRule(p_198756_0_.getSource(), TeamArgument.getTeam(p_198756_0_, "team"), Team.CollisionRule.PUSH_OWN_TEAM);
      })).then(Commands.literal("pushOtherTeams").executes((p_198754_0_) -> {
         return setCollisionRule(p_198754_0_.getSource(), TeamArgument.getTeam(p_198754_0_, "team"), Team.CollisionRule.PUSH_OTHER_TEAMS);
      })).then(Commands.literal("always").executes((p_198790_0_) -> {
         return setCollisionRule(p_198790_0_.getSource(), TeamArgument.getTeam(p_198790_0_, "team"), Team.CollisionRule.ALWAYS);
      }))).then(Commands.literal("prefix").then(Commands.argument("prefix", ComponentArgument.component()).executes((p_207514_0_) -> {
         return setPrefix(p_207514_0_.getSource(), TeamArgument.getTeam(p_207514_0_, "team"), ComponentArgument.getComponent(p_207514_0_, "prefix"));
      }))).then(Commands.literal("suffix").then(Commands.argument("suffix", ComponentArgument.component()).executes((p_207516_0_) -> {
         return setSuffix(p_207516_0_.getSource(), TeamArgument.getTeam(p_207516_0_, "team"), ComponentArgument.getComponent(p_207516_0_, "suffix"));
      }))))));
   }

   /**
    * Removes the listed players from their teams.
    */
   private static int leaveFromTeams(CommandSource source, Collection<String> players) {
      Scoreboard scoreboard = source.getServer().getScoreboard();

      for(String s : players) {
         scoreboard.removePlayerFromTeams(s);
      }

      if (players.size() == 1) {
         source.sendFeedback(new TranslationTextComponent("commands.team.leave.success.single", players.iterator().next()), true);
      } else {
         source.sendFeedback(new TranslationTextComponent("commands.team.leave.success.multiple", players.size()), true);
      }

      return players.size();
   }

   private static int joinTeam(CommandSource source, ScorePlayerTeam teamIn, Collection<String> players) {
      Scoreboard scoreboard = source.getServer().getScoreboard();

      for(String s : players) {
         scoreboard.addPlayerToTeam(s, teamIn);
      }

      if (players.size() == 1) {
         source.sendFeedback(new TranslationTextComponent("commands.team.join.success.single", players.iterator().next(), teamIn.getCommandName()), true);
      } else {
         source.sendFeedback(new TranslationTextComponent("commands.team.join.success.multiple", players.size(), teamIn.getCommandName()), true);
      }

      return players.size();
   }

   private static int setNameTagVisibility(CommandSource source, ScorePlayerTeam teamIn, Team.Visible visibility) throws CommandSyntaxException {
      if (teamIn.getNameTagVisibility() == visibility) {
         throw NAMETAG_VISIBILITY_NO_CHANGE.create();
      } else {
         teamIn.setNameTagVisibility(visibility);
         source.sendFeedback(new TranslationTextComponent("commands.team.option.nametagVisibility.success", teamIn.getCommandName(), visibility.getDisplayName()), true);
         return 0;
      }
   }

   private static int setDeathMessageVisibility(CommandSource source, ScorePlayerTeam teamIn, Team.Visible visibility) throws CommandSyntaxException {
      if (teamIn.getDeathMessageVisibility() == visibility) {
         throw DEATH_MESSAGE_VISIBILITY_NO_CHANGE.create();
      } else {
         teamIn.setDeathMessageVisibility(visibility);
         source.sendFeedback(new TranslationTextComponent("commands.team.option.deathMessageVisibility.success", teamIn.getCommandName(), visibility.getDisplayName()), true);
         return 0;
      }
   }

   private static int setCollisionRule(CommandSource source, ScorePlayerTeam teamIn, Team.CollisionRule rule) throws CommandSyntaxException {
      if (teamIn.getCollisionRule() == rule) {
         throw COLLISION_NO_CHANGE.create();
      } else {
         teamIn.setCollisionRule(rule);
         source.sendFeedback(new TranslationTextComponent("commands.team.option.collisionRule.success", teamIn.getCommandName(), rule.getDisplayName()), true);
         return 0;
      }
   }

   private static int setCanSeeFriendlyInvisibles(CommandSource source, ScorePlayerTeam teamIn, boolean value) throws CommandSyntaxException {
      if (teamIn.getSeeFriendlyInvisiblesEnabled() == value) {
         if (value) {
            throw SEE_FRIENDLY_INVISIBLES_ALREADY_ON.create();
         } else {
            throw SEE_FRIENDLY_INVISIBLES_ALREADY_OFF.create();
         }
      } else {
         teamIn.setSeeFriendlyInvisiblesEnabled(value);
         source.sendFeedback(new TranslationTextComponent("commands.team.option.seeFriendlyInvisibles." + (value ? "enabled" : "disabled"), teamIn.getCommandName()), true);
         return 0;
      }
   }

   private static int setAllowFriendlyFire(CommandSource source, ScorePlayerTeam teamIn, boolean value) throws CommandSyntaxException {
      if (teamIn.getAllowFriendlyFire() == value) {
         if (value) {
            throw FRIENDLY_FIRE_ALREADY_ON.create();
         } else {
            throw FRIENDLY_FIRE_ALREADY_OFF.create();
         }
      } else {
         teamIn.setAllowFriendlyFire(value);
         source.sendFeedback(new TranslationTextComponent("commands.team.option.friendlyfire." + (value ? "enabled" : "disabled"), teamIn.getCommandName()), true);
         return 0;
      }
   }

   private static int setDisplayName(CommandSource source, ScorePlayerTeam teamIn, ITextComponent value) throws CommandSyntaxException {
      if (teamIn.getDisplayName().equals(value)) {
         throw NAME_NO_CHANGE.create();
      } else {
         teamIn.setDisplayName(value);
         source.sendFeedback(new TranslationTextComponent("commands.team.option.name.success", teamIn.getCommandName()), true);
         return 0;
      }
   }

   private static int setColor(CommandSource source, ScorePlayerTeam teamIn, TextFormatting value) throws CommandSyntaxException {
      if (teamIn.getColor() == value) {
         throw COLOR_NO_CHANGE.create();
      } else {
         teamIn.setColor(value);
         source.sendFeedback(new TranslationTextComponent("commands.team.option.color.success", teamIn.getCommandName(), value.getFriendlyName()), true);
         return 0;
      }
   }

   private static int emptyTeam(CommandSource source, ScorePlayerTeam teamIn) throws CommandSyntaxException {
      Scoreboard scoreboard = source.getServer().getScoreboard();
      Collection<String> collection = Lists.newArrayList(teamIn.getMembershipCollection());
      if (collection.isEmpty()) {
         throw EMPTY_NO_CHANGE.create();
      } else {
         for(String s : collection) {
            scoreboard.removePlayerFromTeam(s, teamIn);
         }

         source.sendFeedback(new TranslationTextComponent("commands.team.empty.success", collection.size(), teamIn.getCommandName()), true);
         return collection.size();
      }
   }

   private static int removeTeam(CommandSource source, ScorePlayerTeam teamIn) {
      Scoreboard scoreboard = source.getServer().getScoreboard();
      scoreboard.removeTeam(teamIn);
      source.sendFeedback(new TranslationTextComponent("commands.team.remove.success", teamIn.getCommandName()), true);
      return scoreboard.getTeams().size();
   }

   private static int addTeam(CommandSource source, String name) throws CommandSyntaxException {
      return addTeam(source, name, new StringTextComponent(name));
   }

   private static int addTeam(CommandSource source, String name, ITextComponent displayName) throws CommandSyntaxException {
      Scoreboard scoreboard = source.getServer().getScoreboard();
      if (scoreboard.getTeam(name) != null) {
         throw DUPLICATE_TEAM_NAME.create();
      } else if (name.length() > 16) {
         throw TEAM_NAME_TOO_LONG.create(16);
      } else {
         ScorePlayerTeam scoreplayerteam = scoreboard.createTeam(name);
         scoreplayerteam.setDisplayName(displayName);
         source.sendFeedback(new TranslationTextComponent("commands.team.add.success", scoreplayerteam.getCommandName()), true);
         return scoreboard.getTeams().size();
      }
   }

   private static int listMembers(CommandSource source, ScorePlayerTeam teamIn) {
      Collection<String> collection = teamIn.getMembershipCollection();
      if (collection.isEmpty()) {
         source.sendFeedback(new TranslationTextComponent("commands.team.list.members.empty", teamIn.getCommandName()), false);
      } else {
         source.sendFeedback(new TranslationTextComponent("commands.team.list.members.success", teamIn.getCommandName(), collection.size(), TextComponentUtils.makeGreenSortedList(collection)), false);
      }

      return collection.size();
   }

   private static int listTeams(CommandSource source) {
      Collection<ScorePlayerTeam> collection = source.getServer().getScoreboard().getTeams();
      if (collection.isEmpty()) {
         source.sendFeedback(new TranslationTextComponent("commands.team.list.teams.empty"), false);
      } else {
         source.sendFeedback(new TranslationTextComponent("commands.team.list.teams.success", collection.size(), TextComponentUtils.makeList(collection, ScorePlayerTeam::getCommandName)), false);
      }

      return collection.size();
   }

   private static int setPrefix(CommandSource source, ScorePlayerTeam teamIn, ITextComponent prefix) {
      teamIn.setPrefix(prefix);
      source.sendFeedback(new TranslationTextComponent("commands.team.option.prefix.success", prefix), false);
      return 1;
   }

   private static int setSuffix(CommandSource source, ScorePlayerTeam teamIn, ITextComponent suffix) {
      teamIn.setSuffix(suffix);
      source.sendFeedback(new TranslationTextComponent("commands.team.option.suffix.success", suffix), false);
      return 1;
   }
}