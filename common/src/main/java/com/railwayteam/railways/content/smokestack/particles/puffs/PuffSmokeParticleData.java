package com.railwayteam.railways.content.smokestack.particles.puffs;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.railwayteam.railways.registry.CRParticleTypes;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public abstract class PuffSmokeParticleData<T extends PuffSmokeParticleData<T>> implements ParticleOptions, ICustomParticleDataWithSprite<T> {
	
	@FunctionalInterface
	protected interface Constructor<T extends PuffSmokeParticleData<T>> {
		@Contract("_, _, _, _ -> new")
		T create(boolean stationary, float red, float green, float blue);
	}

	protected static <T extends PuffSmokeParticleData<T>> Codec<T> makeCodec(Constructor<T> constructor) {
		return RecordCodecBuilder.create(i -> i
			.group(Codec.BOOL.fieldOf("stationary")
					.forGetter(p -> p.stationary),
				Codec.FLOAT.fieldOf("red") // -1, -1, -1 indicates un-dyed
					.forGetter(p -> p.red),
				Codec.FLOAT.fieldOf("green")
					.forGetter(p -> p.green),
				Codec.FLOAT.fieldOf("blue")
					.forGetter(p -> p.blue))
			.apply(i, constructor::create));
	}

	@SuppressWarnings("deprecation")
	protected static <T extends PuffSmokeParticleData<T>> Deserializer<T> makeDeserializer(Constructor<T> constructor) {
		return new Deserializer<>() {
            public @NotNull T fromCommand(@NotNull ParticleType<T> particleTypeIn,
										  @NotNull StringReader reader) throws CommandSyntaxException {
                reader.expect(' ');
                boolean stationary = reader.readBoolean();
                reader.expect(' ');
                float red = reader.readFloat();
                reader.expect(' ');
                float green = reader.readFloat();
                reader.expect(' ');
                float blue = reader.readFloat();
                return constructor.create(stationary, red, green, blue);
            }

            public @NotNull T fromNetwork(@NotNull ParticleType<T> particleTypeIn,
										  @NotNull FriendlyByteBuf buffer) {
                return constructor.create(buffer.readBoolean(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
            }
        };
	}
	
	boolean stationary;
	float red;
	float green;
	float blue;

	protected PuffSmokeParticleData() {
		this(false);
	}

	protected PuffSmokeParticleData(float red, float green, float blue) {
		this(false, red, green, blue);
	}

	protected PuffSmokeParticleData(boolean stationary) {
		this(stationary, -1);
	}

	protected PuffSmokeParticleData(boolean stationary, float brightness) {
		this(stationary, brightness, brightness, brightness);
	}

	protected PuffSmokeParticleData(boolean stationary, float red, float green, float blue) {
		this.stationary = stationary;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	protected abstract @NotNull CRParticleTypes getParticleType();

	@Override
	public @NotNull ParticleType<?> getType() {
		return getParticleType().get();
	}

	@Override
	public void writeToNetwork(FriendlyByteBuf buffer) {
		buffer.writeBoolean(stationary);
		buffer.writeFloat(red);
		buffer.writeFloat(green);
		buffer.writeFloat(blue);
	}

	@Override
	public @NotNull String writeToString() {
		return String.format(Locale.ROOT, "%s %b %f %f %f", getParticleType().parameter(), stationary, red, green, blue);
	}

	@SuppressWarnings("deprecation")
	@Override
	public abstract Deserializer<T> getDeserializer();

	@Override
	public abstract Codec<T> getCodec(ParticleType<T> type);

	@Override
	public abstract ParticleEngine.SpriteParticleRegistration<T> getMetaFactory();

	public abstract float getQuadSize();

	public static PuffSmokeParticleData<?> create(boolean small, boolean stationary, float red, float green, float blue) {
		if (small) {
			return new Small(stationary, red, green, blue);
		} else {
			return new Medium(stationary, red, green, blue);
		}
	}

	public static PuffSmokeParticleData<?> create(boolean small, boolean stationary) {
		if (small) {
			return new Small(stationary);
		} else {
			return new Medium(stationary);
		}
	}

	public static class Small extends PuffSmokeParticleData<Small> {
		public static final Codec<Small> CODEC = makeCodec(Small::new);

		@SuppressWarnings("deprecation")
		public static final Deserializer<Small> DESERIALIZER = makeDeserializer(Small::new);

		public Small() {}

		public Small(float red, float green, float blue) {
			super(red, green, blue);
		}

		public Small(boolean stationary) {
			super(stationary);
		}

		public Small(boolean stationary, float brightness) {
			super(stationary, brightness);
		}

		public Small(boolean stationary, float red, float green, float blue) {
			super(stationary, red, green, blue);
		}

		@Override
		protected @NotNull CRParticleTypes getParticleType() {
			return CRParticleTypes.SMOKE_PUFF_SMALL;
		}

		@SuppressWarnings("deprecation")
		@Override
		public Deserializer<Small> getDeserializer() {
			return DESERIALIZER;
		}

		@Override
		public Codec<Small> getCodec(ParticleType<Small> type) {
			return CODEC;
		}

		@Override
		public ParticleEngine.SpriteParticleRegistration<Small> getMetaFactory() {
			return PuffSmokeParticle.Factory::new;
		}

		@Override
		public float getQuadSize() {
			return 0.5f;
		}
	}

	public static class Medium extends PuffSmokeParticleData<Medium> {
		public static final Codec<Medium> CODEC = makeCodec(Medium::new);

		@SuppressWarnings("deprecation")
		public static final Deserializer<Medium> DESERIALIZER = makeDeserializer(Medium::new);

		public Medium() {}

		public Medium(float red, float green, float blue) {
			super(red, green, blue);
		}

		public Medium(boolean stationary) {
			super(stationary);
		}

		public Medium(boolean stationary, float brightness) {
			super(stationary, brightness);
		}

		public Medium(boolean stationary, float red, float green, float blue) {
			super(stationary, red, green, blue);
		}

		@Override
		protected @NotNull CRParticleTypes getParticleType() {
			return CRParticleTypes.SMOKE_PUFF_MEDIUM;
		}

		@SuppressWarnings("deprecation")
		@Override
		public Deserializer<Medium> getDeserializer() {
			return DESERIALIZER;
		}

		@Override
		public Codec<Medium> getCodec(ParticleType<Medium> type) {
			return CODEC;
		}

		@Override
		public ParticleEngine.SpriteParticleRegistration<Medium> getMetaFactory() {
			return PuffSmokeParticle.Factory::new;
		}

		@Override
		public float getQuadSize() {
			return 1;
		}
	}
}