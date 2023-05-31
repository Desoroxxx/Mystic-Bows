package io.redstudioragnarok.mysticbows.items;

import io.redstudioragnarok.mysticbows.entity.EntityFlamingArrow;
import io.redstudioragnarok.mysticbows.entity.EntityLightningArrow;
import io.redstudioragnarok.mysticbows.handler.ClientEventHandler;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class Bow extends ItemBow {

    public boolean rangedBow;
    private boolean flaming;
    private boolean strikeEntitiesOnly;
    private boolean igniteBlocks;

    private int durability;
    private int lightningChance;
    private int strikes;
    private int flameTime;
    private int arrowPerShot;
    
    private float damageMult;
    private float velocityMult;
    private float inaccuracy;
    public float drawTimeMult;

    public Bow(final int durability, final float damageMult, final float velocityMult, final float drawTimeMult, final float inaccuracy, final int flameTime, final int arrowPerShot, final boolean rangedBow) {
        setCreativeTab(CreativeTabs.COMBAT);

        maxStackSize = 1;

        this.durability = durability;
        this.damageMult = damageMult;
        this.velocityMult = velocityMult;
        this.drawTimeMult = drawTimeMult;
        this.inaccuracy = inaccuracy;
        this.flameTime = flameTime;
        this.arrowPerShot = arrowPerShot;
        this.rangedBow = rangedBow;

        if (durability > 1)
            setMaxDamage(durability - 1);
        else if (durability == 1)
            setMaxDamage(1);

        addPropertyOverride(new ResourceLocation("pull"), (ItemStack bow, World world, EntityLivingBase entity) -> {
            if (entity == null)
                return 0;

            float drawTime = 20 * drawTimeMult;

            return (72000 - entity.getItemInUseCount()) / drawTime;
        });
    }

    public Bow(final int durability, final float damageMult, final float velocityMult, final float drawTimeMult, final float inaccuracy,  final int flameTime, final int lightningChance, final boolean strikeEntitiesOnly, final int strikes) {
        this(durability, damageMult, velocityMult, drawTimeMult, inaccuracy, flameTime, 1, false);

        this.lightningChance = lightningChance;
        this.strikeEntitiesOnly = strikeEntitiesOnly;
        this.strikes = strikes;
    }

    public Bow(final int durability, final float damageMult, final float velocityMult, final float drawTimeMult, final float inaccuracy,  final int flameTime, final boolean igniteBlocks) {
        this(durability, damageMult, velocityMult, drawTimeMult, inaccuracy, flameTime, 1, false);

        this.flaming = true;
        this.igniteBlocks = igniteBlocks;
    }

    public void updateConfig(final int durability, final float damageMult, final float velocityMult, final float drawTimeMult, final float inaccuracy, final int flameTime, final int arrowPerShot, final boolean rangedBow) {
        this.durability = durability;
        this.damageMult = damageMult;
        this.velocityMult = velocityMult;
        this.drawTimeMult = drawTimeMult;
        this.inaccuracy = inaccuracy;
        this.flameTime = flameTime;
        this.arrowPerShot = arrowPerShot;
        this.rangedBow = rangedBow;

        if (durability > 1)
            setMaxDamage(durability - 1);
        else if (durability == 1)
            setMaxDamage(1);

        addPropertyOverride(new ResourceLocation("pull"), (ItemStack bow, World world, EntityLivingBase entity) -> {
            if (entity == null)
                return 0;

            float drawTime = 16 * drawTimeMult;

            return (72000 - entity.getItemInUseCount()) / drawTime;
        });
    }

    public void updateConfig(final int durability, final float damageMult, final float velocityMult, final float drawTimeMult, final float inaccuracy, final int flameTime, final int lightningChance, final boolean strikeEntitiesOnly, final int strikes) {
        updateConfig(durability, damageMult, velocityMult, drawTimeMult, inaccuracy, flameTime, 1, false);

        this.lightningChance = lightningChance;
        this.strikeEntitiesOnly = strikeEntitiesOnly;
        this.strikes = strikes;
    }

    public void updateConfig(final int durability, final float damageMult, final float velocityMult, final float drawTimeMult, final float inaccuracy, final int flameTime, final boolean igniteBlocks) {
        updateConfig(durability, damageMult, velocityMult, drawTimeMult, inaccuracy, flameTime, 1, false);

        this.igniteBlocks = igniteBlocks;
    }

    /**
     * Called when the player stops using an Item (stops holding the right mouse button).
     */
    @Override
    public void onPlayerStoppedUsing(ItemStack itemStack, World world, EntityLivingBase entityLiving, int timeInUse) {
        if (!(entityLiving instanceof EntityPlayer))
            return;

        if (world.isRemote && rangedBow)
            ClientEventHandler.resetMouseSensitivity();

        final EntityPlayer player = (EntityPlayer) entityLiving;
        final boolean infiniteArrows = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, itemStack) > 0;
        ItemStack arrow = this.findAmmo(player);

        final float chargeDivider = 1 * drawTimeMult;

        int charge = (int) ((this.getMaxItemUseDuration(itemStack) - timeInUse) / chargeDivider);
        charge = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(itemStack, world, player, charge, !arrow.isEmpty() || infiniteArrows);

        if (charge < 0)
            return;

        if ((!arrow.isEmpty() && arrow.getCount() >= arrowPerShot) || infiniteArrows) {
            if (arrow.isEmpty())
                arrow = new ItemStack(Items.ARROW);

            final float arrowVelocity = getArrowVelocity(charge);

            if ((double) arrowVelocity >= 0.1) {
                boolean arrowInfinite = player.capabilities.isCreativeMode || (arrow.getItem() instanceof ItemArrow && ((ItemArrow) arrow.getItem()).isInfinite(arrow, itemStack, player));

                for (int i = 0; i < arrowPerShot; i++) {
                    if (!world.isRemote) {
                        ItemArrow itemArrow = (ItemArrow) (arrow.getItem() instanceof ItemArrow ? arrow.getItem() : Items.ARROW);

                        final EntityArrow entityArrow = !(lightningChance > 0) ? (igniteBlocks ? new EntityFlamingArrow(world, player) : itemArrow.createArrow(world, arrow, player)) : new EntityLightningArrow(world, player, charge >= 16 ? lightningChance : 0, strikes, strikeEntitiesOnly);

                        entityArrow.shoot(player, player.rotationPitch, player.rotationYaw, 0, (arrowVelocity * 3) * velocityMult, inaccuracy);

                        if (arrowVelocity == 1)
                            entityArrow.setIsCritical(true);

                        entityArrow.setDamage(entityArrow.getDamage() * damageMult);

                        final int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, itemStack);

                        if (power > 0)
                            entityArrow.setDamage(entityArrow.getDamage() + (double) power * 0.5 + 0.5);

                        final int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, itemStack);

                        if (punch > 0)
                            entityArrow.setKnockbackStrength(punch);

                        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, itemStack) > 0 || flaming) {
                            entityArrow.setFire(flameTime);

                            world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 0.9F, 1.35F / ((itemRand.nextFloat() * 0.4F) + 1.2F) + arrowVelocity * 0.5F);
                        }

                        if (durability == 1)
                            itemStack.damageItem(2, player);
                        else if (durability > 0)
                            itemStack.damageItem(1, player);

                        if (arrowInfinite || player.capabilities.isCreativeMode && (arrow.getItem() == Items.SPECTRAL_ARROW || arrow.getItem() == Items.TIPPED_ARROW))
                            entityArrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;

                        world.spawnEntity(entityArrow);
                    }

                    if (!arrowInfinite && !player.capabilities.isCreativeMode) {
                        arrow.shrink(1);

                        if (arrow.isEmpty())
                            player.inventory.deleteStack(arrow);
                    }
                }

                world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1, 1 / (itemRand.nextFloat() * 0.4F + 1.2F) + arrowVelocity * 0.5F);

                player.addStat(StatList.getObjectUseStats(this));
            }
        }
    }
}
