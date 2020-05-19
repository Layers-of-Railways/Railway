package net.minecraft.scoreboard;

import java.util.Comparator;
import javax.annotation.Nullable;

public class Score {
   public static final Comparator<Score> SCORE_COMPARATOR = (p_210221_0_, p_210221_1_) -> {
      if (p_210221_0_.getScorePoints() > p_210221_1_.getScorePoints()) {
         return 1;
      } else {
         return p_210221_0_.getScorePoints() < p_210221_1_.getScorePoints() ? -1 : p_210221_1_.getPlayerName().compareToIgnoreCase(p_210221_0_.getPlayerName());
      }
   };
   private final Scoreboard scoreboard;
   @Nullable
   private final ScoreObjective objective;
   private final String scorePlayerName;
   private int scorePoints;
   private boolean locked;
   private boolean forceUpdate;

   public Score(Scoreboard scoreboard, ScoreObjective objective, String playerName) {
      this.scoreboard = scoreboard;
      this.objective = objective;
      this.scorePlayerName = playerName;
      this.locked = true;
      this.forceUpdate = true;
   }

   public void increaseScore(int amount) {
      if (this.objective.getCriteria().isReadOnly()) {
         throw new IllegalStateException("Cannot modify read-only score");
      } else {
         this.setScorePoints(this.getScorePoints() + amount);
      }
   }

   public void incrementScore() {
      this.increaseScore(1);
   }

   public int getScorePoints() {
      return this.scorePoints;
   }

   public void reset() {
      this.setScorePoints(0);
   }

   public void setScorePoints(int points) {
      int i = this.scorePoints;
      this.scorePoints = points;
      if (i != points || this.forceUpdate) {
         this.forceUpdate = false;
         this.getScoreScoreboard().onScoreChanged(this);
      }

   }

   @Nullable
   public ScoreObjective getObjective() {
      return this.objective;
   }

   /**
    * Returns the name of the player this score belongs to
    */
   public String getPlayerName() {
      return this.scorePlayerName;
   }

   public Scoreboard getScoreScoreboard() {
      return this.scoreboard;
   }

   public boolean isLocked() {
      return this.locked;
   }

   public void setLocked(boolean locked) {
      this.locked = locked;
   }
}