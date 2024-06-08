/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.railwayteam.railways.content.smokestack.particles.chimneypush;

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
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public abstract class ChimneyPushParticleData<T extends ChimneyPushParticleData<T>> implements ParticleOptions, ICustomParticleDataWithSprite<T> {
	
	@FunctionalInterface
	protected interface Constructor<T extends ChimneyPushParticleData<T>> {
		@Contract("_, _, _, _ -> new")
		T create(boolean stationary, float red, float green, float blue);
	}

	protected static <T extends ChimneyPushParticleData<T>> Codec<T> makeCodec(Constructor<T> constructor) {
		return RecordCodecBuilder.create(i -> i
			.group(Codec.BOOL.fieldOf("leadOnly")
					.forGetter(p -> p.leadOnly),
				Codec.FLOAT.fieldOf("red") // -1, -1, -1 indicates un-dyed
					.forGetter(p -> p.red),
				Codec.FLOAT.fieldOf("green")
					.forGetter(p -> p.green),
				Codec.FLOAT.fieldOf("blue")
					.forGetter(p -> p.blue))
			.apply(i, constructor::create));
	}

	@SuppressWarnings("deprecation")
	protected static <T extends ChimneyPushParticleData<T>> Deserializer<T> makeDeserializer(Constructor<T> constructor) {
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
	
	boolean leadOnly;
	float red;
	float green;
	float blue;

	protected ChimneyPushParticleData() {
		this(false);
	}

	protected ChimneyPushParticleData(float red, float green, float blue) {
		this(false, red, green, blue);
	}

	protected ChimneyPushParticleData(boolean leadOnly) {
		this(leadOnly, -1);
	}

	protected ChimneyPushParticleData(boolean leadOnly, float brightness) {
		this(leadOnly, brightness, brightness, brightness);
	}

	protected ChimneyPushParticleData(boolean leadOnly, float red, float green, float blue) {
		this.leadOnly = leadOnly;
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
		buffer.writeBoolean(leadOnly);
		buffer.writeFloat(red);
		buffer.writeFloat(green);
		buffer.writeFloat(blue);
	}

	@Override
	public @NotNull String writeToString() {
		return String.format(Locale.ROOT, "%s %b %f %f %f", getParticleType().parameter(), leadOnly, red, green, blue);
	}

	@SuppressWarnings("deprecation")
	@Override
	public abstract Deserializer<T> getDeserializer();

	@Override
	public abstract Codec<T> getCodec(ParticleType<T> type);

	@Override
	public abstract ParticleEngine.SpriteParticleRegistration<T> getMetaFactory();

	public abstract float getQuadSize();

	public static ChimneyPushParticleData<?> create(boolean small, boolean leadOnly, @NotNull DyeColor color) {
		float idx = -(2 + color.getId());
		return create(small, leadOnly, idx, idx, idx);
	}

	public static ChimneyPushParticleData<?> create(boolean small, boolean leadOnly, float red, float green, float blue) {
		if (small) {
			return new Small(leadOnly, red, green, blue);
		} else {
			return new Medium(leadOnly, red, green, blue);
		}
	}

	public static ChimneyPushParticleData<?> create(boolean small, boolean leadOnly) {
		if (small) {
			return new Small(leadOnly);
		} else {
			return new Medium(leadOnly);
		}
	}

	public static class Small extends ChimneyPushParticleData<Small> {
		public static final Codec<Small> CODEC = makeCodec(Small::new);

		@SuppressWarnings("deprecation")
		public static final Deserializer<Small> DESERIALIZER = makeDeserializer(Small::new);

		public Small() {}

		public Small(float red, float green, float blue) {
			super(red, green, blue);
		}

		public Small(boolean leadOnly) {
			super(leadOnly);
		}

		public Small(boolean leadOnly, float brightness) {
			super(leadOnly, brightness);
		}

		public Small(boolean leadOnly, float red, float green, float blue) {
			super(leadOnly, red, green, blue);
		}

		@Override
		protected @NotNull CRParticleTypes getParticleType() {
			return CRParticleTypes.CHIMNEYPUSH_SMALL;
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
			return ChimneyPushParticle.Factory::new;
		}

		@Override
		public float getQuadSize() {
			return 0.5f;
		}
	}

	public static class Medium extends ChimneyPushParticleData<Medium> {
		public static final Codec<Medium> CODEC = makeCodec(Medium::new);

		@SuppressWarnings("deprecation")
		public static final Deserializer<Medium> DESERIALIZER = makeDeserializer(Medium::new);

		public Medium() {}

		public Medium(float red, float green, float blue) {
			super(red, green, blue);
		}

		public Medium(boolean leadOnly) {
			super(leadOnly);
		}

		public Medium(boolean leadOnly, float brightness) {
			super(leadOnly, brightness);
		}

		public Medium(boolean leadOnly, float red, float green, float blue) {
			super(leadOnly, red, green, blue);
		}

		@Override
		protected @NotNull CRParticleTypes getParticleType() {
			return CRParticleTypes.CHIMNEYPUSH_MEDIUM;
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
			return ChimneyPushParticle.Factory::new;
		}

		@Override
		public float getQuadSize() {
			return 1.25f;
		}
	}
}