package io.redstudioragnarok.mysticbows.items;

import io.redstudioragnarok.mysticbows.config.MysticBowsConfig;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.Queue;

public class BurstBow extends ItemBow {

    private boolean firstArrow = true;

    private int delay = 0;

    private float arrowVelocity;

    private final Queue<ItemStack> arrowQueue = new LinkedList<>();

    public BurstBow() {
        setCreativeTab(CreativeTabs.COMBAT);

        maxStackSize = 1;

        if (MysticBowsConfig.common.burstBow.durability > 1)
            setMaxDamage(MysticBowsConfig.common.burstBow.durability - 1);
        else if (MysticBowsConfig.common.burstBow.durability == 1)
            setMaxDamage(1);

        addPropertyOverride(new ResourceLocation("pull"), (ItemStack bow, World world, EntityLivingBase entity) -> {
            if (entity == null)
                return 0;

            float drawTime = 20 * MysticBowsConfig.common.burstBow.drawTimeMult;

            return (72000 - entity.getItemInUseCount()) / drawTime;
        });
    }

    public void updateConfig() {
        if (MysticBowsConfig.common.burstBow.durability > 1)
            setMaxDamage(MysticBowsConfig.common.burstBow.durability - 1);
        else if (MysticBowsConfig.common.burstBow.durability == 1)
            setMaxDamage(1);

        addPropertyOverride(new ResourceLocation("pull"), (ItemStack bow, World world, EntityLivingBase entity) -> {
            if (entity == null)
                return 0;

            float drawTime = 20 * MysticBowsConfig.common.burstBow.drawTimeMult;

            return (72000 - entity.getItemInUseCount()) / drawTime;
        });
    }

    /**
     * Called when the player stops using an Item (stops holding the right mouse button).
     */
    @Override
    public void onPlayerStoppedUsing(ItemStack itemStack, World world, EntityLivingBase entityLiving, int timeInUse) {
        if (!(entityLiving instanceof EntityPlayer))
            return;

        final EntityPlayer player = (EntityPlayer) entityLiving;
        final boolean infiniteArrows = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, itemStack) > 0;
        ItemStack arrow = this.findAmmo(player);

        final float chargeDivider = 1 * MysticBowsConfig.common.burstBow.drawTimeMult;

        int charge = (int) ((this.getMaxItemUseDuration(itemStack) - timeInUse) / chargeDivider);
        charge = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(itemStack, world, player, charge, !arrow.isEmpty() || infiniteArrows);

        if (charge < 0)
            return;

        if (!arrow.isEmpty() || infiniteArrows) {
            if (arrow.isEmpty())
                arrow = new ItemStack(Items.ARROW);

            arrowVelocity = getArrowVelocity(charge);

            if ((double) arrowVelocity >= 0.1) {
                boolean arrowInfinite = player.capabilities.isCreativeMode || (arrow.getItem() instanceof ItemArrow && ((ItemArrow) arrow.getItem()).isInfinite(arrow, itemStack, player));

                final int arrowToShoot = infiniteArrows ? MysticBowsConfig.common.burstBow.arrowPerShot : MathHelper.clamp(arrow.getCount(), 1, MysticBowsConfig.common.burstBow.arrowPerShot);

                if (arrowQueue.isEmpty())
                    firstArrow = true;

                for (int i = 0; i < arrowToShoot; i++) {
                    if (!world.isRemote) {
                        ItemArrow itemArrow = (ItemArrow) (arrow.getItem() instanceof ItemArrow ? arrow.getItem() : Items.ARROW);

                        final EntityArrow entityArrow = itemArrow.createArrow(world, arrow, player);

                        if (firstArrow) {
                            entityArrow.shoot(player, player.rotationPitch, player.rotationYaw, 0, (arrowVelocity * 3) * MysticBowsConfig.common.burstBow.velocityMult, MysticBowsConfig.common.burstBow.inaccuracy);

                            if (arrowVelocity == 1)
                                entityArrow.setIsCritical(true);

                            entityArrow.setDamage(entityArrow.getDamage() * MysticBowsConfig.common.burstBow.damageMult);

                            final int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, itemStack);

                            if (power > 0)
                                entityArrow.setDamage(entityArrow.getDamage() + (double) power * 0.5 + 0.5);

                            final int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, itemStack);

                            if (punch > 0)
                                entityArrow.setKnockbackStrength(punch);

                            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, itemStack) > 0)
                                entityArrow.setFire(MysticBowsConfig.common.burstBow.flameTime);
                        }

                        if (MysticBowsConfig.common.burstBow.durability == 1)
                            itemStack.damageItem(2, player);
                        else if (MysticBowsConfig.common.burstBow.durability > 0)
                            itemStack.damageItem(1, player);

                        if (arrowInfinite || player.capabilities.isCreativeMode && (arrow.getItem() == Items.SPECTRAL_ARROW || arrow.getItem() == Items.TIPPED_ARROW))
                            entityArrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;

                        if (!firstArrow)
                            arrowQueue.add(arrow);
                        else {
                            world.spawnEntity(entityArrow);
                            world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1, 1 / (itemRand.nextFloat() * 0.4F + 1.2F) + arrowVelocity * 0.5F);
                            delay =  MysticBowsConfig.common.burstBow.delay;

                            firstArrow = false;
                        }
                    }

                    if (!arrowInfinite && !player.capabilities.isCreativeMode) {
                        arrow.shrink(1);

                        if (arrow.isEmpty())
                            player.inventory.deleteStack(arrow);
                    }
                }

                player.addStat(StatList.getObjectUseStats(this));
            }
        }
    }

    @Override
    public void onUpdate(ItemStack itemStack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (!world.isRemote && !arrowQueue.isEmpty()) {
            if (delay == 0) {
                final ItemStack arrow = arrowQueue.poll();

                ItemArrow itemArrow = (ItemArrow) (arrow.getItem() instanceof ItemArrow ? arrow.getItem() : Items.ARROW);

                EntityArrow entityArrow = itemArrow.createArrow(world, arrow,((EntityLivingBase) entity));

                entityArrow.shoot(entity, entity.rotationPitch, entity.rotationYaw, 0, (arrowVelocity * 3) * MysticBowsConfig.common.burstBow.velocityMult, MysticBowsConfig.common.burstBow.inaccuracy);

                if (arrowVelocity == 1)
                    entityArrow.setIsCritical(true);

                entityArrow.setDamage(entityArrow.getDamage() * MysticBowsConfig.common.burstBow.damageMult);

                final int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, itemStack);

                if (power > 0)
                    entityArrow.setDamage(entityArrow.getDamage() + (double) power * 0.5 + 0.5);

                final int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, itemStack);

                if (punch > 0)
                    entityArrow.setKnockbackStrength(punch);

                if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, itemStack) > 0)
                    entityArrow.setFire(MysticBowsConfig.common.burstBow.flameTime);

                world.spawnEntity(entityArrow);
                world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1, 1 / (itemRand.nextFloat() * 0.4F + 1.2F) + arrowVelocity * 0.5F);
                delay =  MysticBowsConfig.common.burstBow.delay;
            } else {
                delay--;
            }
        }
    }
}
