package io.redstudioragnarok.mysticbows.items;

import com.elenai.elenaidodge2.api.FeathersHelper;
import io.redstudioragnarok.mysticbows.config.MysticBowsConfig;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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

public class FeatherBow extends ItemBow {

    public FeatherBow() {
        setCreativeTab(CreativeTabs.COMBAT);

        maxStackSize = 1;

        if (MysticBowsConfig.common.featherBow.durability > 1)
            setMaxDamage(MysticBowsConfig.common.featherBow.durability - 1);
        else if (MysticBowsConfig.common.featherBow.durability == 1)
            setMaxDamage(1);

        addPropertyOverride(new ResourceLocation("pull"), (ItemStack bow, World world, EntityLivingBase entity) -> {
            if (entity == null)
                return 0;

            float drawTime = 20 * MysticBowsConfig.common.featherBow.drawTimeMult;

            return (72000 - entity.getItemInUseCount()) / drawTime;
        });
    }

    public void updateConfig() {
        if (MysticBowsConfig.common.featherBow.durability > 1)
            setMaxDamage(MysticBowsConfig.common.featherBow.durability - 1);
        else if (MysticBowsConfig.common.featherBow.durability == 1)
            setMaxDamage(1);

        addPropertyOverride(new ResourceLocation("pull"), (ItemStack bow, World world, EntityLivingBase entity) -> {
            if (entity == null)
                return 0;

            float drawTime = 20 * MysticBowsConfig.common.featherBow.drawTimeMult;

            return (72000 - entity.getItemInUseCount()) / drawTime;
        });
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        final ItemStack itemStack = player.getHeldItem(hand);

        if (!player.isCreative() && (!world.isRemote && !(FeathersHelper.getFeatherLevel((EntityPlayerMP) player) >= MysticBowsConfig.common.featherBow.featherConsumption) || world.isRemote && !(FeathersHelper.getFeatherLevel((EntityPlayerSP) player) >= MysticBowsConfig.common.featherBow.featherConsumption)))
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
        final ItemStack arrow = new ItemStack(Items.ARROW);

        final float chargeDivider = 1 * MysticBowsConfig.common.featherBow.drawTimeMult;

        int charge = (int) ((this.getMaxItemUseDuration(itemStack) - timeInUse) / chargeDivider);
        charge = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(itemStack, world, player, charge, true);

        if (charge < 0)
            return;

        final float arrowVelocity = getArrowVelocity(charge);

        if ((double) arrowVelocity >= 0.1) {
            if (!world.isRemote) {
                ItemArrow itemArrow = (ItemArrow) (arrow.getItem() instanceof ItemArrow ? arrow.getItem() : Items.ARROW);

                final EntityArrow entityArrow = itemArrow.createArrow(world, arrow, player);

                entityArrow.shoot(player, player.rotationPitch, player.rotationYaw, 0, (arrowVelocity * 3) * MysticBowsConfig.common.featherBow.velocityMult, MysticBowsConfig.common.featherBow.inaccuracy);

                if (arrowVelocity == 1)
                    entityArrow.setIsCritical(true);

                entityArrow.setDamage(entityArrow.getDamage() * MysticBowsConfig.common.featherBow.damageMult);

                final int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, itemStack);

                if (power > 0)
                    entityArrow.setDamage(entityArrow.getDamage() + (double) power * 0.5 + 0.5);

                final int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, itemStack);

                if (punch > 0)
                    entityArrow.setKnockbackStrength(punch);

                if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, itemStack) > 0) {
                    entityArrow.setFire(MysticBowsConfig.common.featherBow.flameTime);

                    world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 0.9F, 1.35F / ((itemRand.nextFloat() * 0.4F) + 1.2F) + arrowVelocity * 0.5F);
                }

                if (MysticBowsConfig.common.featherBow.durability == 1)
                    itemStack.damageItem(2, player);
                else if (MysticBowsConfig.common.featherBow.durability > 0)
                    itemStack.damageItem(1, player);

                entityArrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;

                world.spawnEntity(entityArrow);

                FeathersHelper.decreaseFeathers((EntityPlayerMP) player, MysticBowsConfig.common.featherBow.featherConsumption);
            }

            world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1, 1 / (itemRand.nextFloat() * 0.4F + 1.2F) + arrowVelocity * 0.5F);

            player.addStat(StatList.getObjectUseStats(this));
        }
    }
}
