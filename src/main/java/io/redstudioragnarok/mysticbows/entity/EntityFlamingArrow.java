package io.redstudioragnarok.mysticbows.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityFlamingArrow extends EntityTippedArrow {

    public EntityFlamingArrow(final World world) {
        super(world);
    }

    public EntityFlamingArrow(final World world, final EntityLivingBase shooter) {
        super(world, shooter);
    }

    @Override
    protected void onHit(RayTraceResult raytraceResult) {
        super.onHit(raytraceResult);

        if (raytraceResult.typeOfHit != RayTraceResult.Type.BLOCK)
            return;

        final BlockPos blockHit = raytraceResult.getBlockPos().up();

        if (world.isAirBlock(blockHit))
            world.setBlockState(blockHit, Blocks.FIRE.getDefaultState(), 11);
    }
}
