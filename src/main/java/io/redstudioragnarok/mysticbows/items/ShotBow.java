package io.redstudioragnarok.mysticbows.items;

import io.redstudioragnarok.mysticbows.config.MysticBowsConfig;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
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
import net.minecraft.util.*;
import net.minecraft.world.World;

public class ShotBow extends ItemBow {

    public ShotBow() {
        setCreativeTab(CreativeTabs.COMBAT);

        maxStackSize = 1;

        if (MysticBowsConfig.common.shotBow.durability > 1)
            setMaxDamage(MysticBowsConfig.common.shotBow.durability - 1);
        else if (MysticBowsConfig.common.shotBow.durability == 1)
            setMaxDamage(1);

        addPropertyOverride(new ResourceLocation("pull"), (ItemStack bow, World world, EntityLivingBase entity) -> {
            if (entity == null)
                return 0;

            float drawTime = 20 * MysticBowsConfig.common.shotBow.drawTimeMult;

            return (72000 - entity.getItemInUseCount()) / drawTime;
        });
    }

    public void updateConfig() {
        if (MysticBowsConfig.common.shotBow.durability > 1)
            setMaxDamage(MysticBowsConfig.common.shotBow.durability - 1);
        else if (MysticBowsConfig.common.shotBow.durability == 1)
            setMaxDamage(1);

        addPropertyOverride(new ResourceLocation("pull"), (ItemStack bow, World world, EntityLivingBase entity) -> {
            if (entity == null)
                return 0;

            float drawTime = 20 * MysticBowsConfig.common.shotBow.drawTimeMult;

            return (72000 - entity.getItemInUseCount()) / drawTime;
        });
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        final ItemStack itemStack = player.getHeldItem(hand);

        if (!player.isCreative() && (!world.isRemote && !(findAmmo(player).getCount() >= MysticBowsConfig.common.shotBow.arrowConsumption) || world.isRemote && !(findAmmo(player).getCount() >= MysticBowsConfig.common.shotBow.arrowConsumption)))
            return new ActionResult<>(EnumActionResult.FAIL, itemStack);

        final ActionResult<ItemStack> arrowNockEvent = net.minecraftforge.event.ForgeEventFactory.onArrowNock(itemStack, world, player, hand, true);

        if (arrowNockEvent != null)
            return arrowNockEvent;

        player.setActiveHand(hand);

        return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
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

        final float chargeDivider = 1 * MysticBowsConfig.common.shotBow.drawTimeMult;

        int charge = (int) ((this.getMaxItemUseDuration(itemStack) - timeInUse) / chargeDivider);
        charge = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(itemStack, world, player, charge, !arrow.isEmpty() || infiniteArrows);

        if (charge < 0)
            return;

        if (arrow.getCount() >= MysticBowsConfig.common.shotBow.arrowConsumption || infiniteArrows) {
            if (arrow.isEmpty())
                arrow = new ItemStack(Items.ARROW);

            final float arrowVelocity = getArrowVelocity(charge);

            if ((double) arrowVelocity >= 0.1) {
                boolean arrowInfinite = player.capabilities.isCreativeMode || (arrow.getItem() instanceof ItemArrow && ((ItemArrow) arrow.getItem()).isInfinite(arrow, itemStack, player));

                for (int i = 0; i < (charge >= 18 ? MysticBowsConfig.common.shotBow.arrowPerShot : 1); i++) {
                    if (!world.isRemote) {
                        ItemArrow itemArrow = (ItemArrow) (arrow.getItem() instanceof ItemArrow ? arrow.getItem() : Items.ARROW);

                        final EntityArrow entityArrow = itemArrow.createArrow(world, arrow, player);

                        entityArrow.shoot(player, player.rotationPitch, player.rotationYaw, 0, (arrowVelocity * 3) * MysticBowsConfig.common.shotBow.velocityMult, MysticBowsConfig.common.shotBow.inaccuracy);

                        if (arrowVelocity == 1)
                            entityArrow.setIsCritical(true);

                        entityArrow.setDamage(entityArrow.getDamage() * MysticBowsConfig.common.shotBow.damageMult);

                        final int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, itemStack);

                        if (power > 0)
                            entityArrow.setDamage(entityArrow.getDamage() + (double) power * 0.5 + 0.5);

                        final int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, itemStack);

                        if (punch > 0)
                            entityArrow.setKnockbackStrength(punch);

                        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, itemStack) > 0) {
                            entityArrow.setFire(MysticBowsConfig.common.shotBow.flameTime);

                            world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 0.9F, 1.35F / ((itemRand.nextFloat() * 0.4F) + 1.2F) + arrowVelocity * 0.5F);
                        }

                        if (MysticBowsConfig.common.shotBow.durability == 1)
                            itemStack.damageItem(2, player);
                        else if (MysticBowsConfig.common.shotBow.durability > 0)
                            itemStack.damageItem(1, player);

                        if (charge >= 18)
                            entityArrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;

                        world.spawnEntity(entityArrow);
                    }
                }

                if (!arrowInfinite && !player.capabilities.isCreativeMode) {
                    arrow.shrink(MysticBowsConfig.common.shotBow.arrowConsumption);

                    if (arrow.isEmpty())
                        player.inventory.deleteStack(arrow);
                }

                world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1, 1 / (itemRand.nextFloat() * 0.4F + 1.2F) + arrowVelocity * 0.5F);

                player.addStat(StatList.getObjectUseStats(this));
            }
        }
    }
}
