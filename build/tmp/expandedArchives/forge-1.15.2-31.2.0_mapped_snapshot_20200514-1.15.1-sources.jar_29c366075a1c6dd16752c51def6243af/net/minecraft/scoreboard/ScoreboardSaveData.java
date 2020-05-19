package net.minecraft.scoreboard;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.storage.WorldSavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScoreboardSaveData extends WorldSavedData {
   private static final Logger LOGGER = LogManager.getLogger();
   private Scoreboard scoreboard;
   private CompoundNBT delayedInitNbt;

   public ScoreboardSaveData() {
      super("scoreboard");
   }

   public void setScoreboard(Scoreboard scoreboardIn) {
      this.scoreboard = scoreboardIn;
      if (this.delayedInitNbt != null) {
         this.read(this.delayedInitNbt);
      }

   }

   /**
    * reads in data from the NBTTagCompound into this MapDataBase
    */
   public void read(CompoundNBT nbt) {
      if (this.scoreboard == null) {
         this.delayedInitNbt = nbt;
      } else {
         this.readObjectives(nbt.getList("Objectives", 10));
         this.scoreboard.func_197905_a(nbt.getList("PlayerScores", 10));
         if (nbt.contains("DisplaySlots", 10)) {
            this.readDisplayConfig(nbt.getCompound("DisplaySlots"));
         }

         if (nbt.contains("Teams", 9)) {
            this.readTeams(nbt.getList("Teams", 10));
         }

      }
   }

   protected void readTeams(ListNBT tagList) {
      for(int i = 0; i < tagList.size(); ++i) {
         CompoundNBT compoundnbt = tagList.getCompound(i);
         String s = compoundnbt.getString("Name");
         if (s.length() > 16) {
            s = s.substring(0, 16);
         }

         ScorePlayerTeam scoreplayerteam = this.scoreboard.createTeam(s);
         ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(compoundnbt.getString("DisplayName"));
         if (itextcomponent != null) {
            scoreplayerteam.setDisplayName(itextcomponent);
         }

         if (compoundnbt.contains("TeamColor", 8)) {
            scoreplayerteam.setColor(TextFormatting.getValueByName(compoundnbt.getString("TeamColor")));
         }

         if (compoundnbt.contains("AllowFriendlyFire", 99)) {
            scoreplayerteam.setAllowFriendlyFire(compoundnbt.getBoolean("AllowFriendlyFire"));
         }

         if (compoundnbt.contains("SeeFriendlyInvisibles", 99)) {
            scoreplayerteam.setSeeFriendlyInvisiblesEnabled(compoundnbt.getBoolean("SeeFriendlyInvisibles"));
         }

         if (compoundnbt.contains("MemberNamePrefix", 8)) {
            ITextComponent itextcomponent1 = ITextComponent.Serializer.fromJson(compoundnbt.getString("MemberNamePrefix"));
            if (itextcomponent1 != null) {
               scoreplayerteam.setPrefix(itextcomponent1);
            }
         }

         if (compoundnbt.contains("MemberNameSuffix", 8)) {
            ITextComponent itextcomponent2 = ITextComponent.Serializer.fromJson(compoundnbt.getString("MemberNameSuffix"));
            if (itextcomponent2 != null) {
               scoreplayerteam.setSuffix(itextcomponent2);
            }
         }

         if (compoundnbt.contains("NameTagVisibility", 8)) {
            Team.Visible team$visible = Team.Visible.getByName(compoundnbt.getString("NameTagVisibility"));
            if (team$visible != null) {
               scoreplayerteam.setNameTagVisibility(team$visible);
            }
         }

         if (compoundnbt.contains("DeathMessageVisibility", 8)) {
            Team.Visible team$visible1 = Team.Visible.getByName(compoundnbt.getString("DeathMessageVisibility"));
            if (team$visible1 != null) {
               scoreplayerteam.setDeathMessageVisibility(team$visible1);
            }
         }

         if (compoundnbt.contains("CollisionRule", 8)) {
            Team.CollisionRule team$collisionrule = Team.CollisionRule.getByName(compoundnbt.getString("CollisionRule"));
            if (team$collisionrule != null) {
               scoreplayerteam.setCollisionRule(team$collisionrule);
            }
         }

         this.loadTeamPlayers(scoreplayerteam, compoundnbt.getList("Players", 8));
      }

   }

   protected void loadTeamPlayers(ScorePlayerTeam playerTeam, ListNBT tagList) {
      for(int i = 0; i < tagList.size(); ++i) {
         this.scoreboard.addPlayerToTeam(tagList.getString(i), playerTeam);
      }

   }

   protected void readDisplayConfig(CompoundNBT compound) {
      for(int i = 0; i < 19; ++i) {
         if (compound.contains("slot_" + i, 8)) {
            String s = compound.getString("slot_" + i);
            ScoreObjective scoreobjective = this.scoreboard.getObjective(s);
            this.scoreboard.setObjectiveInDisplaySlot(i, scoreobjective);
         }
      }

   }

   protected void readObjectives(ListNBT nbt) {
      for(int i = 0; i < nbt.size(); ++i) {
         CompoundNBT compoundnbt = nbt.getCompound(i);
         ScoreCriteria.func_216390_a(compoundnbt.getString("CriteriaName")).ifPresent((p_215164_2_) -> {
            String s = compoundnbt.getString("Name");
            if (s.length() > 16) {
               s = s.substring(0, 16);
            }

            ITextComponent itextcomponent = ITextComponent.Serializer.fromJson(compoundnbt.getString("DisplayName"));
            ScoreCriteria.RenderType scorecriteria$rendertype = ScoreCriteria.RenderType.byId(compoundnbt.getString("RenderType"));
            this.scoreboard.addObjective(s, p_215164_2_, itextcomponent, scorecriteria$rendertype);
         });
      }

   }

   public CompoundNBT write(CompoundNBT compound) {
      if (this.scoreboard == null) {
         LOGGER.warn("Tried to save scoreboard without having a scoreboard...");
         return compound;
      } else {
         compound.put("Objectives", this.objectivesToNbt());
         compound.put("PlayerScores", this.scoreboard.func_197902_i());
         compound.put("Teams", this.teamsToNbt());
         this.fillInDisplaySlots(compound);
         return compound;
      }
   }

   protected ListNBT teamsToNbt() {
      ListNBT listnbt = new ListNBT();

      for(ScorePlayerTeam scoreplayerteam : this.scoreboard.getTeams()) {
         CompoundNBT compoundnbt = new CompoundNBT();
         compoundnbt.putString("Name", scoreplayerteam.getName());
         compoundnbt.putString("DisplayName", ITextComponent.Serializer.toJson(scoreplayerteam.getDisplayName()));
         if (scoreplayerteam.getColor().getColorIndex() >= 0) {
            compoundnbt.putString("TeamColor", scoreplayerteam.getColor().getFriendlyName());
         }

         compoundnbt.putBoolean("AllowFriendlyFire", scoreplayerteam.getAllowFriendlyFire());
         compoundnbt.putBoolean("SeeFriendlyInvisibles", scoreplayerteam.getSeeFriendlyInvisiblesEnabled());
         compoundnbt.putString("MemberNamePrefix", ITextComponent.Serializer.toJson(scoreplayerteam.getPrefix()));
         compoundnbt.putString("MemberNameSuffix", ITextComponent.Serializer.toJson(scoreplayerteam.getSuffix()));
         compoundnbt.putString("NameTagVisibility", scoreplayerteam.getNameTagVisibility().internalName);
         compoundnbt.putString("DeathMessageVisibility", scoreplayerteam.getDeathMessageVisibility().internalName);
         compoundnbt.putString("CollisionRule", scoreplayerteam.getCollisionRule().name);
         ListNBT listnbt1 = new ListNBT();

         for(String s : scoreplayerteam.getMembershipCollection()) {
            listnbt1.add(StringNBT.valueOf(s));
         }

         compoundnbt.put("Players", listnbt1);
         listnbt.add(compoundnbt);
      }

      return listnbt;
   }

   protected void fillInDisplaySlots(CompoundNBT compound) {
      CompoundNBT compoundnbt = new CompoundNBT();
      boolean flag = false;

      for(int i = 0; i < 19; ++i) {
         ScoreObjective scoreobjective = this.scoreboard.getObjectiveInDisplaySlot(i);
         if (scoreobjective != null) {
            compoundnbt.putString("slot_" + i, scoreobjective.getName());
            flag = true;
         }
      }

      if (flag) {
         compound.put("DisplaySlots", compoundnbt);
      }

   }

   protected ListNBT objectivesToNbt() {
      ListNBT listnbt = new ListNBT();

      for(ScoreObjective scoreobjective : this.scoreboard.getScoreObjectives()) {
         if (scoreobjective.getCriteria() != null) {
            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.putString("Name", scoreobjective.getName());
            compoundnbt.putString("CriteriaName", scoreobjective.getCriteria().getName());
            compoundnbt.putString("DisplayName", ITextComponent.Serializer.toJson(scoreobjective.getDisplayName()));
            compoundnbt.putString("RenderType", scoreobjective.getRenderType().getId());
            listnbt.add(compoundnbt);
         }
      }

      return listnbt;
   }
}