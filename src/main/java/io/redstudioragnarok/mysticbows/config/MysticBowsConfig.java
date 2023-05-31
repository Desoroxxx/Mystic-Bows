package io.redstudioragnarok.mysticbows.config;

import io.redstudioragnarok.mysticbows.MysticBows;
import io.redstudioragnarok.mysticbows.items.Bow;
import io.redstudioragnarok.mysticbows.items.BurstBow;
import io.redstudioragnarok.mysticbows.items.FeatherBow;
import io.redstudioragnarok.mysticbows.items.ShotBow;
import io.redstudioragnarok.mysticbows.utils.ModReference;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static io.redstudioragnarok.mysticbows.MysticBows.isElenaiDodge2Loaded;

@SuppressWarnings("CanBeFinal")
@Config(modid = ModReference.id, name = ModReference.name)
public class MysticBowsConfig {

    public static final Common common = new Common();

    public static class Common {

        public final BowConfig bow = new BowConfig(385, 1, 1, 1, 1, 100);
        public final BowConfig crudeBow = new BowConfig(163, 1, 1, 1, 2.7F, 20);
        public final BowConfig quickBow = new BowConfig(535, 0.6F, 0.75F, 0.6F, 0.6F, 100);
        public final BowConfig heavyBow = new BowConfig(215, 2, 2, 2, 1.3F, 100);

        public final LightningBowConfig lightningBow = new LightningBowConfig();

        public final FlameBowConfig flameBow = new FlameBowConfig();

        public final ShotBowConfig shotBow = new ShotBowConfig();

        public final RangedBowConfig rangedBow = new RangedBowConfig();

        public final BurstBowConfig burstBow = new BurstBowConfig();

        public final FeatherBowConfig featherBow = new FeatherBowConfig();

        public static class BowConfig {

            @Config.LangKey(ModReference.id + ".general.common." + "durability")
            public int durability;
            @Config.LangKey(ModReference.id + ".general.common." + "flameTime")
            public int flameTime;
            
            @Config.LangKey(ModReference.id + ".general.common." + "damageMult")
            public float damageMult;
            @Config.LangKey(ModReference.id + ".general.common." + "velocityMult")
            public float velocityMult;
            @Config.LangKey(ModReference.id + ".general.common." + "drawTimeMult")
            public float drawTimeMult;
            @Config.LangKey(ModReference.id + ".general.common." + "inaccuracy")
            public float inaccuracy;

            public BowConfig(final int durability, final float damageMult, final float velocityMult, final float drawTimeMult, final float inaccuracy, final int flameTime) {
                this.durability = durability;
                this.damageMult = damageMult;
                this.velocityMult = velocityMult;
                this.drawTimeMult = drawTimeMult;
                this.inaccuracy = inaccuracy;
                this.flameTime = flameTime;
            }
        }

        public static class LightningBowConfig {

            public boolean strikeEntitiesOnly = false;

            @Config.LangKey(ModReference.id + ".general.common." + "durability")
            public int durability = 385;
            @Config.LangKey(ModReference.id + ".general.common." + "flameTime")
            public int flameTime = 100;
            public int lightningChance = 100;
            public int strikes = 3;
            public int radius = 2;

            @Config.LangKey(ModReference.id + ".general.common." + "damageMult")
            public float damageMult = 1;
            @Config.LangKey(ModReference.id + ".general.common." + "velocityMult")
            public float velocityMult = 1;
            @Config.LangKey(ModReference.id + ".general.common." + "drawTimeMult")
            public float drawTimeMult = 3;
            @Config.LangKey(ModReference.id + ".general.common." + "inaccuracy")
            public float inaccuracy = 1;
        }

        public static class FlameBowConfig {

            public boolean igniteBlocks = true;

            @Config.LangKey(ModReference.id + ".general.common." + "durability")
            public int durability = 385;
            @Config.LangKey(ModReference.id + ".general.common." + "flameTime")
            public int flameTime = 200;

            @Config.LangKey(ModReference.id + ".general.common." + "damageMult")
            public float damageMult = 1;
            @Config.LangKey(ModReference.id + ".general.common." + "velocityMult")
            public float velocityMult = 1;
            @Config.LangKey(ModReference.id + ".general.common." + "drawTimeMult")
            public float drawTimeMult = 1.2F;
            @Config.LangKey(ModReference.id + ".general.common." + "inaccuracy")
            public float inaccuracy = 1;
        }

        public static class ShotBowConfig {

            @Config.LangKey(ModReference.id + ".general.common." + "durability")
            public int durability = 225;
            @Config.LangKey(ModReference.id + ".general.common." + "flameTime")
            public int flameTime = 100;
            public int arrowPerShot = 5;
            public int arrowConsumption = 1;

            @Config.LangKey(ModReference.id + ".general.common." + "damageMult")
            public float damageMult = 0.8F;
            @Config.LangKey(ModReference.id + ".general.common." + "velocityMult")
            public float velocityMult = 1;
            @Config.LangKey(ModReference.id + ".general.common." + "drawTimeMult")
            public float drawTimeMult = 1.6F;
            @Config.LangKey(ModReference.id + ".general.common." + "inaccuracy")
            public float inaccuracy = 3;
        }

        public static class RangedBowConfig {

            @Config.LangKey(ModReference.id + ".general.common." + "durability")
            public int durability = 360;
            @Config.LangKey(ModReference.id + ".general.common." + "flameTime")
            public int flameTime = 100;

            @Config.LangKey(ModReference.id + ".general.common." + "damageMult")
            public float damageMult = 1;
            @Config.LangKey(ModReference.id + ".general.common." + "velocityMult")
            public float velocityMult = 5;
            @Config.LangKey(ModReference.id + ".general.common." + "drawTimeMult")
            public float drawTimeMult = 1.5F;
            @Config.LangKey(ModReference.id + ".general.common." + "inaccuracy")
            public float inaccuracy = 0;
            public float fovMult = 4.5F;
        }

        public static class BurstBowConfig {

            @Config.LangKey(ModReference.id + ".general.common." + "durability")
            public int durability = 225;
            @Config.LangKey(ModReference.id + ".general.common." + "flameTime")
            public int flameTime = 100;
            public int arrowPerShot = 3;
            public int delay = 6;

            @Config.LangKey(ModReference.id + ".general.common." + "damageMult")
            public float damageMult = 0.7F;
            @Config.LangKey(ModReference.id + ".general.common." + "velocityMult")
            public float velocityMult = 1;
            @Config.LangKey(ModReference.id + ".general.common." + "drawTimeMult")
            public float drawTimeMult = 1.8F;
            @Config.LangKey(ModReference.id + ".general.common." + "inaccuracy")
            public float inaccuracy = 1;
        }

        public static class FeatherBowConfig {

            @Config.LangKey(ModReference.id + ".general.common." + "durability")
            public int durability = 385;
            @Config.LangKey(ModReference.id + ".general.common." + "flameTime")
            public int flameTime = 100;
            public int featherConsumption = 1;

            @Config.LangKey(ModReference.id + ".general.common." + "damageMult")
            public float damageMult = 1;
            @Config.LangKey(ModReference.id + ".general.common." + "velocityMult")
            public float velocityMult = 1;
            @Config.LangKey(ModReference.id + ".general.common." + "drawTimeMult")
            public float drawTimeMult = 1;
            @Config.LangKey(ModReference.id + ".general.common." + "inaccuracy")
            public float inaccuracy = 1;
        }
    }

    @Mod.EventBusSubscriber(modid = ModReference.id)
    private static class EventHandler {
        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent onConfigChangedEvent) {
            if (onConfigChangedEvent.getModID().equals(ModReference.id))
                ConfigManager.sync(ModReference.id, Config.Type.INSTANCE);

            ((Bow) MysticBows.bow).updateConfig(common.bow.durability, common.bow.damageMult, common.bow.velocityMult, common.bow.drawTimeMult, common.bow.inaccuracy, common.bow.flameTime, 1, false);
            ((Bow) MysticBows.crudeBow).updateConfig(common.crudeBow.durability, common.crudeBow.damageMult, common.crudeBow.velocityMult, common.crudeBow.drawTimeMult, common.crudeBow.inaccuracy, common.crudeBow.flameTime, 1, false);
            ((Bow) MysticBows.quickBow).updateConfig(common.quickBow.durability, common.quickBow.damageMult, common.quickBow.velocityMult, common.quickBow.drawTimeMult, common.quickBow.inaccuracy, common.quickBow.flameTime, 1, false);
            ((Bow) MysticBows.heavyBow).updateConfig(common.heavyBow.durability, common.heavyBow.damageMult, common.heavyBow.velocityMult, common.heavyBow.drawTimeMult, common.heavyBow.inaccuracy, common.heavyBow.flameTime, 1, false);
            ((Bow) MysticBows.lightningBow).updateConfig(common.lightningBow.durability, common.lightningBow.damageMult, common.lightningBow.velocityMult, common.lightningBow.drawTimeMult, common.lightningBow.inaccuracy, common.lightningBow.flameTime, common.lightningBow.lightningChance, common.lightningBow.strikeEntitiesOnly, common.lightningBow.strikes);
            ((Bow) MysticBows.flameBow).updateConfig(common.flameBow.durability, common.flameBow.damageMult, common.flameBow.velocityMult, common.flameBow.drawTimeMult, common.flameBow.inaccuracy, common.flameBow.flameTime, common.flameBow. igniteBlocks);
            ((ShotBow) MysticBows.shotBow).updateConfig();
            ((Bow) MysticBows.rangedBow).updateConfig(common.rangedBow.durability, common.rangedBow.damageMult, common.rangedBow.velocityMult, common.rangedBow.drawTimeMult, common.rangedBow.inaccuracy, common.rangedBow.flameTime, 1, true);
            ((BurstBow) MysticBows.burstBow).updateConfig();

            if (!isElenaiDodge2Loaded)
                return;

            ((FeatherBow) MysticBows.featherBow).updateConfig();
        }
    }
}
