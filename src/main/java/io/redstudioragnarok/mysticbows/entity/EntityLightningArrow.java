package io.redstudioragnarok.mysticbows.entity;

import io.redstudioragnarok.mysticbows.config.MysticBowsConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.Random;

public class EntityLightningArrow extends EntityTippedArrow {

    private final boolean strikeEntitiesOnly;

    private final int lightningChance;
    private final int strikes;

    private static final Random random = new Random();

    public EntityLightningArrow(final World world) {
        super(world);

        lightningChance = 0;
        strikes = 0;
        strikeEntitiesOnly = false;
    }

    public EntityLightningArrow(final World world, final EntityLivingBase shooter, final int lightningChance, final int strikes, final boolean strikeEntitiesOnly) {
        super(world, shooter);

        this.lightningChance = lightningChance;
        this.strikes = strikes;
        this.strikeEntitiesOnly = strikeEntitiesOnly;
    }

    @Override
    protected void onHit(RayTraceResult raytraceResultIn) {
        super.onHit(raytraceResultIn);

        final Entity entity = raytraceResultIn.entityHit;

        if (entity == null && strikeEntitiesOnly)
            return;

        if (random.nextInt(100) + 1 <= lightningChance) {
            final int area = MysticBowsConfig.common.lightningBow.radius;

            for (int i = 0; i < strikes; i++) {
                final Random random = new Random();

                final int offsetX = random.nextInt((area * 2) + 1) - area;
                final int offsetZ = random.nextInt((area * 2) + 1) - area;

                world.addWeatherEffect(new EntityLightningBolt(world, posX + offsetX, posY, posZ + offsetZ, false));
            }
        }
    }
}
