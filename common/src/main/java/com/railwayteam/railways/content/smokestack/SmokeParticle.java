package com.railwayteam.railways.content.smokestack;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import com.railwayteam.railways.config.CRConfigs;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jetbrains.annotations.NotNull;

public class SmokeParticle extends SimpleAnimatedParticle {

	public enum SmokeQuality {
		LOW("smoke_16"),
		MEDIUM("smoke_32"),
		HIGH("smoke_64"),
		ULTRA("smoke");
		public final String name;


		SmokeQuality(String name) {
			this.name = name;
		}
	}

	public static final ParticleRenderType TRANSPARENT_SMOKE = new ParticleRenderType() {
		@Override
		public void begin(BufferBuilder builder, TextureManager manager) {
			RenderSystem.depthMask(false);
			RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
			RenderSystem.enableBlend();
			RenderSystem.enableDepthTest();
			RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
		}

		@Override
		public void end(Tesselator tesselator) {
			tesselator.end();
			RenderSystem.depthMask(true);
			RenderSystem.disableBlend();
			RenderSystem.defaultBlendFunc();
		}

		@Override
		public String toString() {
			return "TRANSPARENT_SMOKE";
		}
	};

	private LerpedFloat ascendScale = LerpedFloat.linear().startWithValue(1.0);
	private double baseYd;
	private final boolean stationarySource;

	protected SmokeParticle(ClientLevel world, SmokeParticleData data, double x, double y, double z, double dx,
							double dy, double dz, SpriteSet sprite) {
		super(world, x, y, z, sprite, world.random.nextFloat() * .5f);
		double scale = 0.1;
		xd = dx*scale;
		yd = dy*scale + (double)(this.random.nextFloat() / 500.0F);;
		baseYd = yd;
		zd = dz*scale;
		this.gravity = 3.0E-6f;
		quadSize = .375f*4;
		setLifetime(data.stationary ? 400 : CRConfigs.client().smokeLifetime.get());
		setPos(x, y, z);
		roll = oRoll = world.random.nextFloat() * Mth.PI;
		//this.setSpriteFromAge(sprite);
		this.setSprite(sprite.get(CRConfigs.client().smokeQuality.get().ordinal(), SmokeQuality.values().length - 1));
		alpha = data.stationary ? 0.25f : 0.1f;
		ascendScale.chase(data.stationary ? 0.3 : 0.1, data.stationary ? 0.001 : 0.03, LerpedFloat.Chaser.EXP);
		rCol = data.red;
		gCol = data.green;
		bCol = data.blue;
		stationarySource = data.stationary;
	}

	@Override
	public @NotNull ParticleRenderType getRenderType() {
		return TRANSPARENT_SMOKE;
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		if (this.age++ >= this.lifetime || this.alpha <= 0.0f) {
			this.remove();
			return;
		}
		float diffusionScale = stationarySource ? 800.0f : 500.0f;
		if (this.age > 350) {
			diffusionScale = 5000.0f;
		} else if (this.age > 300) {
			diffusionScale = Mth.lerp((age-300) / 50.0f, stationarySource ? 800.0f : 500.0f, 5000.0f);
		}
		this.xd += (double)(this.random.nextFloat() / diffusionScale * (float)(this.random.nextBoolean() ? 1 : -1));
		this.zd += (double)(this.random.nextFloat() / diffusionScale * (float)(this.random.nextBoolean() ? 1 : -1));
		//this.yd -= (double)this.gravity;
		this.yd = this.baseYd * ascendScale.getValue();
		ascendScale.tickChaser();
		this.move(this.xd, this.yd, this.zd);
		if (this.age >= this.lifetime - 100 && this.alpha > 0.01f) {
			this.alpha -= 0.015f;
		}
	}

	@Override
	public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
		Vec3 vec3 = renderInfo.getPosition();
		float f = (float)(Mth.lerp((double)partialTicks, this.xo, this.x) - vec3.x());
		float g = (float)(Mth.lerp((double)partialTicks, this.yo, this.y) - vec3.y());
		float h = (float)(Mth.lerp((double)partialTicks, this.zo, this.z) - vec3.z());
		Quaternionf quaternion;
		if (this.roll == 0.0F) {
			quaternion = renderInfo.rotation();
		} else {
			quaternion = new Quaternionf(renderInfo.rotation());
			float i = Mth.lerp(partialTicks, this.oRoll, this.roll);
			quaternion.mul(Axis.ZP.rotation(i));
		}

        /*quaternion.mul(Axis.XP.rotationDegrees(((this.random.nextFloat()*2) - 1) * 3));
		quaternion.mul(Axis.YP.rotationDegrees(((this.random.nextFloat()*2) - 1) * 3));
		quaternion.mul(Axis.ZP.rotationDegrees(((this.random.nextFloat()*2) - 1) * 3));*/

		Vector3f vector3f = new Vector3f(-1.0F, -1.0F, 0.0F);
		vector3f.rotate(quaternion);
		Vector3f[] vector3fs = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
		float j = this.getQuadSize(partialTicks);

		for(int k = 0; k < 4; ++k) {
			Vector3f vector3f2 = vector3fs[k];
			vector3f2.rotate(quaternion);
			vector3f2.mul(j);
			vector3f2.add(f, g, h);
		}

		float l = this.getU0();
		float m = this.getU1();
		float n = this.getV0();
		float o = this.getV1();
		int p = this.getLightColor(partialTicks);
		buffer.vertex((double)vector3fs[0].x(), (double)vector3fs[0].y(), (double)vector3fs[0].z()).uv(m, o).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(p).endVertex();
		buffer.vertex((double)vector3fs[1].x(), (double)vector3fs[1].y(), (double)vector3fs[1].z()).uv(m, n).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(p).endVertex();
		buffer.vertex((double)vector3fs[2].x(), (double)vector3fs[2].y(), (double)vector3fs[2].z()).uv(l, n).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(p).endVertex();
		buffer.vertex((double)vector3fs[3].x(), (double)vector3fs[3].y(), (double)vector3fs[3].z()).uv(l, o).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(p).endVertex();
	}

    /*	@Override
	public @NotNull ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
		Vec3 vec3 = pRenderInfo.getPosition();
		float f = (float) (x - vec3.x);
		float f1 = (float) (y - vec3.y);
		float f2 = (float) (z - vec3.z);
		float f3 = Mth.lerp(pPartialTicks, this.oRoll, this.roll);
		float f7 = this.getU0();
		float f8 = this.getU1();
		float f5 = this.getV0();
		float f6 = this.getV1();
		float f4 = this.getQuadSize(pPartialTicks);

		for (int i = 0; i < 4; i++) {
			Quaternion quaternion = new Quaternion(Axis.YP, yaw, false);
			quaternion.mul(Axis.XP.rotation(pitch));
			quaternion.mul(Axis.YP.rotation(f3 + Mth.PI / 2 * i + roll));
			Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
			vector3f1.transform(quaternion);

			Vector3f[] avector3f = new Vector3f[] { new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F),
				new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F) };

			for (int j = 0; j < 4; ++j) {
				Vector3f vector3f = avector3f[j];
				vector3f.add(0, 1, 0);
				vector3f.transform(quaternion);
				vector3f.mul(f4);
				vector3f.add(f, f1, f2);
			}

			int j = this.getLightColor(pPartialTicks);
			pBuffer.vertex((double) avector3f[0].x(), (double) avector3f[0].y(), (double) avector3f[0].z())
				.uv(f8, f6)
				.color(this.rCol, this.gCol, this.bCol, this.alpha)
				.uv2(j)
				.endVertex();
			pBuffer.vertex((double) avector3f[1].x(), (double) avector3f[1].y(), (double) avector3f[1].z())
				.uv(f8, f5)
				.color(this.rCol, this.gCol, this.bCol, this.alpha)
				.uv2(j)
				.endVertex();
			pBuffer.vertex((double) avector3f[2].x(), (double) avector3f[2].y(), (double) avector3f[2].z())
				.uv(f7, f5)
				.color(this.rCol, this.gCol, this.bCol, this.alpha)
				.uv2(j)
				.endVertex();
			pBuffer.vertex((double) avector3f[3].x(), (double) avector3f[3].y(), (double) avector3f[3].z())
				.uv(f7, f6)
				.color(this.rCol, this.gCol, this.bCol, this.alpha)
				.uv2(j)
				.endVertex();

		}
	}*/

	@Override
	public int getLightColor(float partialTick) {
		BlockPos blockpos = BlockPos.containing(this.x, this.y, this.z);
		return this.level.isLoaded(blockpos) ? LevelRenderer.getLightColor(level, blockpos) : 0;
	}

	public static class Factory implements ParticleProvider<SmokeParticleData> {
		private final SpriteSet spriteSet;

		public Factory(SpriteSet animatedSprite) {
			this.spriteSet = animatedSprite;
		}

		public Particle createParticle(SmokeParticleData data, ClientLevel worldIn, double x, double y, double z,
									   double xSpeed, double ySpeed, double zSpeed) {
			return new SmokeParticle(worldIn, data, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
		}
	}

}
