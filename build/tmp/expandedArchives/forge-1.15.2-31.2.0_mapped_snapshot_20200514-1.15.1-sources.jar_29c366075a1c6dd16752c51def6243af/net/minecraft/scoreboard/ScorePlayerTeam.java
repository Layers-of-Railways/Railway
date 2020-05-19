package net.minecraft.scoreboard;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ScorePlayerTeam extends Team {
   private final Scoreboard scoreboard;
   private final String name;
   private final Set<String> membershipSet = Sets.newHashSet();
   private ITextComponent displayName;
   private ITextComponent prefix = new StringTextComponent("");
   private ITextComponent suffix = new StringTextComponent("");
   private boolean allowFriendlyFire = true;
   private boolean canSeeFriendlyInvisibles = true;
   private Team.Visible nameTagVisibility = Team.Visible.ALWAYS;
   private Team.Visible deathMessageVisibility = Team.Visible.ALWAYS;
   private TextFormatting color = TextFormatting.RESET;
   private Team.CollisionRule collisionRule = Team.CollisionRule.ALWAYS;

   public ScorePlayerTeam(Scoreboard scoreboardIn, String name) {
      this.scoreboard = scoreboardIn;
      this.name = name;
      this.displayName = new StringTextComponent(name);
   }

   /**
    * Retrieve the name by which this team is registered in the scoreboard
    */
   public String getName() {
      return this.name;
   }

   /**
    * Gets the display name for this team.
    */
   public ITextComponent getDisplayName() {
      return this.displayName;
   }

   /**
    * Get name to display in command feedback
    */
   public ITextComponent getCommandName() {
      ITextComponent itextcomponent = TextComponentUtils.wrapInSquareBrackets(this.displayName.deepCopy().applyTextStyle((p_211543_1_) -> {
         p_211543_1_.setInsertion(this.name).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(this.name)));
      }));
      TextFormatting textformatting = this.getColor();
      if (textformatting != TextFormatting.RESET) {
         itextcomponent.applyTextStyle(textformatting);
      }

      return itextcomponent;
   }

   /**
    * Sets the display name for this team.
    */
   public void setDisplayName(ITextComponent name) {
      if (name == null) {
         throw new IllegalArgumentException("Name cannot be null");
      } else {
         this.displayName = name;
         this.scoreboard.onTeamChanged(this);
      }
   }

   public void setPrefix(@Nullable ITextComponent p_207408_1_) {
      this.prefix = (ITextComponent)(p_207408_1_ == null ? new StringTextComponent("") : p_207408_1_.deepCopy());
      this.scoreboard.onTeamChanged(this);
   }

   public ITextComponent getPrefix() {
      return this.prefix;
   }

   public void setSuffix(@Nullable ITextComponent p_207409_1_) {
      this.suffix = (ITextComponent)(p_207409_1_ == null ? new StringTextComponent("") : p_207409_1_.deepCopy());
      this.scoreboard.onTeamChanged(this);
   }

   public ITextComponent getSuffix() {
      return this.suffix;
   }

   /**
    * Gets a collection of all members of this team.
    */
   public Collection<String> getMembershipCollection() {
      return this.membershipSet;
   }

   public ITextComponent format(ITextComponent p_200540_1_) {
      ITextComponent itextcomponent = (new StringTextComponent("")).appendSibling(this.prefix).appendSibling(p_200540_1_).appendSibling(this.suffix);
      TextFormatting textformatting = this.getColor();
      if (textformatting != TextFormatting.RESET) {
         itextcomponent.applyTextStyle(textformatting);
      }

      return itextcomponent;
   }

   public static ITextComponent formatMemberName(@Nullable Team p_200541_0_, ITextComponent p_200541_1_) {
      return p_200541_0_ == null ? p_200541_1_.deepCopy() : p_200541_0_.format(p_200541_1_);
   }

   /**
    * Checks whether friendly fire (PVP between members of the team) is allowed.
    */
   public boolean getAllowFriendlyFire() {
      return this.allowFriendlyFire;
   }

   /**
    * Sets whether friendly fire (PVP between members of the team) is allowed.
    */
   public void setAllowFriendlyFire(boolean friendlyFire) {
      this.allowFriendlyFire = friendlyFire;
      this.scoreboard.onTeamChanged(this);
   }

   /**
    * Checks whether members of this team can see other members that are invisible.
    */
   public boolean getSeeFriendlyInvisiblesEnabled() {
      return this.canSeeFriendlyInvisibles;
   }

   /**
    * Sets whether members of this team can see other members that are invisible.
    */
   public void setSeeFriendlyInvisiblesEnabled(boolean friendlyInvisibles) {
      this.canSeeFriendlyInvisibles = friendlyInvisibles;
      this.scoreboard.onTeamChanged(this);
   }

   /**
    * Gets the visibility flags for player name tags.
    */
   public Team.Visible getNameTagVisibility() {
      return this.nameTagVisibility;
   }

   /**
    * Gets the visibility flags for player death messages.
    */
   public Team.Visible getDeathMessageVisibility() {
      return this.deathMessageVisibility;
   }

   /**
    * Sets the visibility flags for player name tags.
    */
   public void setNameTagVisibility(Team.Visible visibility) {
      this.nameTagVisibility = visibility;
      this.scoreboard.onTeamChanged(this);
   }

   /**
    * Sets the visibility flags for player death messages.
    */
   public void setDeathMessageVisibility(Team.Visible visibility) {
      this.deathMessageVisibility = visibility;
      this.scoreboard.onTeamChanged(this);
   }

   /**
    * Gets the rule to be used for handling collisions with members of this team.
    */
   public Team.CollisionRule getCollisionRule() {
      return this.collisionRule;
   }

   /**
    * Sets the rule to be used for handling collisions with members of this team.
    */
   public void setCollisionRule(Team.CollisionRule rule) {
      this.collisionRule = rule;
      this.scoreboard.onTeamChanged(this);
   }

   /**
    * Gets a bitmask containing the friendly fire and invisibles flags.
    */
   public int getFriendlyFlags() {
      int i = 0;
      if (this.getAllowFriendlyFire()) {
         i |= 1;
      }

      if (this.getSeeFriendlyInvisiblesEnabled()) {
         i |= 2;
      }

      return i;
   }

   /**
    * Sets friendly fire and invisibles flags based off of the given bitmask.
    */
   @OnlyIn(Dist.CLIENT)
   public void setFriendlyFlags(int flags) {
      this.setAllowFriendlyFire((flags & 1) > 0);
      this.setSeeFriendlyInvisiblesEnabled((flags & 2) > 0);
   }

   /**
    * Sets the color for this team. The team color is used mainly for team kill objectives and team-specific setDisplay
    * usage; it does _not_ affect all situations (for instance, the prefix is used for the glowing effect).
    */
   public void setColor(TextFormatting color) {
      this.color = color;
      this.scoreboard.onTeamChanged(this);
   }

   /**
    * Gets the color for this team. The team color is used mainly for team kill objectives and team-specific setDisplay
    * usage; it does _not_ affect all situations (for instance, the prefix is used for the glowing effect).
    */
   public TextFormatting getColor() {
      return this.color;
   }
}