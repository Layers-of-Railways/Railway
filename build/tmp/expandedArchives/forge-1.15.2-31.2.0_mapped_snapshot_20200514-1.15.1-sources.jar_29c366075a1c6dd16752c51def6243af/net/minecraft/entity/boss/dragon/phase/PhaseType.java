package net.minecraft.entity.boss.dragon.phase;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;

public class PhaseType<T extends IPhase> {
   private static PhaseType<?>[] phases = new PhaseType[0];
   public static final PhaseType<HoldingPatternPhase> HOLDING_PATTERN = create(HoldingPatternPhase.class, "HoldingPattern");
   public static final PhaseType<StrafePlayerPhase> STRAFE_PLAYER = create(StrafePlayerPhase.class, "StrafePlayer");
   public static final PhaseType<LandingApproachPhase> LANDING_APPROACH = create(LandingApproachPhase.class, "LandingApproach");
   public static final PhaseType<LandingPhase> LANDING = create(LandingPhase.class, "Landing");
   public static final PhaseType<TakeoffPhase> TAKEOFF = create(TakeoffPhase.class, "Takeoff");
   public static final PhaseType<FlamingSittingPhase> SITTING_FLAMING = create(FlamingSittingPhase.class, "SittingFlaming");
   public static final PhaseType<ScanningSittingPhase> SITTING_SCANNING = create(ScanningSittingPhase.class, "SittingScanning");
   public static final PhaseType<AttackingSittingPhase> SITTING_ATTACKING = create(AttackingSittingPhase.class, "SittingAttacking");
   public static final PhaseType<ChargingPlayerPhase> CHARGING_PLAYER = create(ChargingPlayerPhase.class, "ChargingPlayer");
   public static final PhaseType<DyingPhase> DYING = create(DyingPhase.class, "Dying");
   public static final PhaseType<HoverPhase> HOVER = create(HoverPhase.class, "Hover");
   private final Class<? extends IPhase> clazz;
   private final int id;
   private final String name;

   private PhaseType(int idIn, Class<? extends IPhase> clazzIn, String nameIn) {
      this.id = idIn;
      this.clazz = clazzIn;
      this.name = nameIn;
   }

   public IPhase createPhase(EnderDragonEntity dragon) {
      try {
         Constructor<? extends IPhase> constructor = this.getConstructor();
         return constructor.newInstance(dragon);
      } catch (Exception exception) {
         throw new Error(exception);
      }
   }

   protected Constructor<? extends IPhase> getConstructor() throws NoSuchMethodException {
      return this.clazz.getConstructor(EnderDragonEntity.class);
   }

   public int getId() {
      return this.id;
   }

   public String toString() {
      return this.name + " (#" + this.id + ")";
   }

   /**
    * Gets a phase by its ID. If the phase is out of bounds (negative or beyond the end of the phase array), returns
    * {@link #HOLDING_PATTERN}.
    */
   public static PhaseType<?> getById(int idIn) {
      return idIn >= 0 && idIn < phases.length ? phases[idIn] : HOLDING_PATTERN;
   }

   public static int getTotalPhases() {
      return phases.length;
   }

   private static <T extends IPhase> PhaseType<T> create(Class<T> phaseIn, String nameIn) {
      PhaseType<T> phasetype = new PhaseType<>(phases.length, phaseIn, nameIn);
      phases = Arrays.copyOf(phases, phases.length + 1);
      phases[phasetype.getId()] = phasetype;
      return phasetype;
   }
}