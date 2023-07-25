package com.railwayteam.railways.content.smokestack;

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

import java.util.Locale;

public class SmokeParticleData implements ParticleOptions, ICustomParticleDataWithSprite<SmokeParticleData> {

	public static final Codec<SmokeParticleData> CODEC = RecordCodecBuilder.create(i -> i
		.group(Codec.BOOL.fieldOf("stationary")
			.forGetter(p -> p.stationary),
			Codec.FLOAT.fieldOf("red")
				.forGetter(p -> p.red),
			Codec.FLOAT.fieldOf("green")
				.forGetter(p -> p.green),
			Codec.FLOAT.fieldOf("blue")
				.forGetter(p -> p.blue))
		.apply(i, SmokeParticleData::new));

	public static final Deserializer<SmokeParticleData> DESERIALIZER =
		new Deserializer<SmokeParticleData>() {
			public SmokeParticleData fromCommand(ParticleType<SmokeParticleData> particleTypeIn,
												 StringReader reader) throws CommandSyntaxException {
				reader.expect(' ');
				boolean stationary = reader.readBoolean();
				reader.expect(' ');
				float red = reader.readFloat();
				reader.expect(' ');
				float green = reader.readFloat();
				reader.expect(' ');
				float blue = reader.readFloat();
				return new SmokeParticleData(stationary, red, green, blue);
			}

			public SmokeParticleData fromNetwork(ParticleType<SmokeParticleData> particleTypeIn,
												 FriendlyByteBuf buffer) {
				return new SmokeParticleData(buffer.readBoolean(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
			}
		};

	boolean stationary;
	float red;
	float green;
	float blue;

	public SmokeParticleData() {
		this(false);
	}

	public SmokeParticleData(float red, float green, float blue) {
		this(false, red, green, blue);
	}

	public SmokeParticleData(boolean stationary) {
		this(stationary, stationary ? 0.3f : 0.1f);
	}

	public SmokeParticleData(boolean stationary, float brightness) {
		this(stationary, brightness, brightness, brightness);
	}

	public SmokeParticleData(boolean stationary, float red, float green, float blue) {
		this.stationary = stationary;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	@Override
	public ParticleType<?> getType() {
		return CRParticleTypes.SMOKE.get();
	}

	@Override
	public void writeToNetwork(FriendlyByteBuf buffer) {
		buffer.writeBoolean(stationary);
		buffer.writeFloat(red);
		buffer.writeFloat(green);
		buffer.writeFloat(blue);
	}

	@Override
	public String writeToString() {
		return String.format(Locale.ROOT, "%s %b %f %f %f", CRParticleTypes.SMOKE.parameter(), stationary, red, green, blue);
	}

	@Override
	public Deserializer<SmokeParticleData> getDeserializer() {
		return DESERIALIZER;
	}

	@Override
	public Codec<SmokeParticleData> getCodec(ParticleType<SmokeParticleData> type) {
		return CODEC;
	}

	@Override
	public ParticleEngine.SpriteParticleRegistration<SmokeParticleData> getMetaFactory() {
		return SmokeParticle.Factory::new;
	}
}