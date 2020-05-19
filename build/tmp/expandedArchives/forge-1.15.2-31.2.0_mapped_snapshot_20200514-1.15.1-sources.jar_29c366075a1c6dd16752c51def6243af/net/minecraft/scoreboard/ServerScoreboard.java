package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SDisplayObjectivePacket;
import net.minecraft.network.play.server.SScoreboardObjectivePacket;
import net.minecraft.network.play.server.STeamsPacket;
import net.minecraft.network.play.server.SUpdateScorePacket;
import net.minecraft.server.MinecraftServer;

public class ServerScoreboard extends Scoreboard {
   private final MinecraftServer server;
   private final Set<ScoreObjective> addedObjectives = Sets.newHashSet();
   private Runnable[] dirtyRunnables = new Runnable[0];

   public ServerScoreboard(MinecraftServer mcServer) {
      this.server = mcServer;
   }

   public void onScoreChanged(Score scoreIn) {
      super.onScoreChanged(scoreIn);
      if (this.addedObjectives.contains(scoreIn.getObjective())) {
         this.server.getPlayerList().sendPacketToAllPlayers(new SUpdateScorePacket(ServerScoreboard.Action.CHANGE, scoreIn.getObjective().getName(), scoreIn.getPlayerName(), scoreIn.getScorePoints()));
      }

      this.markSaveDataDirty();
   }

   public void onPlayerRemoved(String scoreName) {
      super.onPlayerRemoved(scoreName);
      this.server.getPlayerList().sendPacketToAllPlayers(new SUpdateScorePacket(ServerScoreboard.Action.REMOVE, (String)null, scoreName, 0));
      this.markSaveDataDirty();
   }

   public void onPlayerScoreRemoved(String scoreName, ScoreObjective objective) {
      super.onPlayerScoreRemoved(scoreName, objective);
      if (this.addedObjectives.contains(objective)) {
         this.server.getPlayerList().sendPacketToAllPlayers(new SUpdateScorePacket(ServerScoreboard.Action.REMOVE, objective.getName(), scoreName, 0));
      }

      this.markSaveDataDirty();
   }

   /**
    * 0 is tab menu, 1 is sidebar, 2 is below name
    */
   public void setObjectiveInDisplaySlot(int objectiveSlot, @Nullable ScoreObjective objective) {
      ScoreObjective scoreobjective = this.getObjectiveInDisplaySlot(objectiveSlot);
      super.setObjectiveInDisplaySlot(objectiveSlot, objective);
      if (scoreobjective != objective && scoreobjective != null) {
         if (this.getObjectiveDisplaySlotCount(scoreobjective) > 0) {
            this.server.getPlayerList().sendPacketToAllPlayers(new SDisplayObjectivePacket(objectiveSlot, objective));
         } else {
            this.sendDisplaySlotRemovalPackets(scoreobjective);
         }
      }

      if (objective != null) {
         if (this.addedObjectives.contains(objective)) {
            this.server.getPlayerList().sendPacketToAllPlayers(new SDisplayObjectivePacket(objectiveSlot, objective));
         } else {
            this.addObjective(objective);
         }
      }

      this.markSaveDataDirty();
   }

   public boolean addPlayerToTeam(String p_197901_1_, ScorePlayerTeam p_197901_2_) {
      if (super.addPlayerToTeam(p_197901_1_, p_197901_2_)) {
         this.server.getPlayerList().sendPacketToAllPlayers(new STeamsPacket(p_197901_2_, Arrays.asList(p_197901_1_), 3));
         this.markSaveDataDirty();
         return true;
      } else {
         return false;
      }
   }

   /**
    * Removes the given username from the given ScorePlayerTeam. If the player is not on the team then an
    * IllegalStateException is thrown.
    */
   public void removePlayerFromTeam(String username, ScorePlayerTeam playerTeam) {
      super.removePlayerFromTeam(username, playerTeam);
      this.server.getPlayerList().sendPacketToAllPlayers(new STeamsPacket(playerTeam, Arrays.asList(username), 4));
      this.markSaveDataDirty();
   }

   public void onObjectiveAdded(ScoreObjective objective) {
      super.onObjectiveAdded(objective);
      this.markSaveDataDirty();
   }

   public void onObjectiveChanged(ScoreObjective objective) {
      super.onObjectiveChanged(objective);
      if (this.addedObjectives.contains(objective)) {
         this.server.getPlayerList().sendPacketToAllPlayers(new SScoreboardObjectivePacket(objective, 2));
      }

      this.markSaveDataDirty();
   }

   public void onObjectiveRemoved(ScoreObjective objective) {
      super.onObjectiveRemoved(objective);
      if (this.addedObjectives.contains(objective)) {
         this.sendDisplaySlotRemovalPackets(objective);
      }

      this.markSaveDataDirty();
   }

   public void onTeamAdded(ScorePlayerTeam playerTeam) {
      super.onTeamAdded(playerTeam);
      this.server.getPlayerList().sendPacketToAllPlayers(new STeamsPacket(playerTeam, 0));
      this.markSaveDataDirty();
   }

   public void onTeamChanged(ScorePlayerTeam playerTeam) {
      super.onTeamChanged(playerTeam);
      this.server.getPlayerList().sendPacketToAllPlayers(new STeamsPacket(playerTeam, 2));
      this.markSaveDataDirty();
   }

   public void onTeamRemoved(ScorePlayerTeam playerTeam) {
      super.onTeamRemoved(playerTeam);
      this.server.getPlayerList().sendPacketToAllPlayers(new STeamsPacket(playerTeam, 1));
      this.markSaveDataDirty();
   }

   public void addDirtyRunnable(Runnable runnable) {
      this.dirtyRunnables = Arrays.copyOf(this.dirtyRunnables, this.dirtyRunnables.length + 1);
      this.dirtyRunnables[this.dirtyRunnables.length - 1] = runnable;
   }

   protected void markSaveDataDirty() {
      for(Runnable runnable : this.dirtyRunnables) {
         runnable.run();
      }

   }

   public List<IPacket<?>> getCreatePackets(ScoreObjective objective) {
      List<IPacket<?>> list = Lists.newArrayList();
      list.add(new SScoreboardObjectivePacket(objective, 0));

      for(int i = 0; i < 19; ++i) {
         if (this.getObjectiveInDisplaySlot(i) == objective) {
            list.add(new SDisplayObjectivePacket(i, objective));
         }
      }

      for(Score score : this.getSortedScores(objective)) {
         list.add(new SUpdateScorePacket(ServerScoreboard.Action.CHANGE, score.getObjective().getName(), score.getPlayerName(), score.getScorePoints()));
      }

      return list;
   }

   public void addObjective(ScoreObjective objective) {
      List<IPacket<?>> list = this.getCreatePackets(objective);

      for(ServerPlayerEntity serverplayerentity : this.server.getPlayerList().getPlayers()) {
         for(IPacket<?> ipacket : list) {
            serverplayerentity.connection.sendPacket(ipacket);
         }
      }

      this.addedObjectives.add(objective);
   }

   public List<IPacket<?>> getDestroyPackets(ScoreObjective p_96548_1_) {
      List<IPacket<?>> list = Lists.newArrayList();
      list.add(new SScoreboardObjectivePacket(p_96548_1_, 1));

      for(int i = 0; i < 19; ++i) {
         if (this.getObjectiveInDisplaySlot(i) == p_96548_1_) {
            list.add(new SDisplayObjectivePacket(i, p_96548_1_));
         }
      }

      return list;
   }

   public void sendDisplaySlotRemovalPackets(ScoreObjective p_96546_1_) {
      List<IPacket<?>> list = this.getDestroyPackets(p_96546_1_);

      for(ServerPlayerEntity serverplayerentity : this.server.getPlayerList().getPlayers()) {
         for(IPacket<?> ipacket : list) {
            serverplayerentity.connection.sendPacket(ipacket);
         }
      }

      this.addedObjectives.remove(p_96546_1_);
   }

   public int getObjectiveDisplaySlotCount(ScoreObjective p_96552_1_) {
      int i = 0;

      for(int j = 0; j < 19; ++j) {
         if (this.getObjectiveInDisplaySlot(j) == p_96552_1_) {
            ++i;
         }
      }

      return i;
   }

   public static enum Action {
      CHANGE,
      REMOVE;
   }
}