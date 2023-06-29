package io.redstudioragnarok.mysticbows;

import io.redstudioragnarok.mysticbows.config.MysticBowsConfig;
import io.redstudioragnarok.mysticbows.handler.ClientEventHandler;
import io.redstudioragnarok.mysticbows.items.Bow;
import io.redstudioragnarok.mysticbows.items.BurstBow;
import io.redstudioragnarok.mysticbows.items.FeatherBow;
import io.redstudioragnarok.mysticbows.items.ShotBow;
import io.redstudioragnarok.mysticbows.utils.ModReference;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

//   /$$      /$$                       /$$     /$$                 /$$$$$$$
//  | $$$    /$$$                      | $$    |__/                | $$__  $$
//  | $$$$  /$$$$ /$$   /$$  /$$$$$$$ /$$$$$$   /$$  /$$$$$$$      | $$  \ $$  /$$$$$$  /$$  /$$  /$$  /$$$$$$$
//  | $$ $$/$$ $$| $$  | $$ /$$_____/|_  $$_/  | $$ /$$_____/      | $$$$$$$  /$$__  $$| $$ | $$ | $$ /$$_____/
//  | $$  $$$| $$| $$  | $$|  $$$$$$   | $$    | $$| $$            | $$__  $$| $$  \ $$| $$ | $$ | $$|  $$$$$$
//  | $$\  $ | $$| $$  | $$ \____  $$  | $$ /$$| $$| $$            | $$  \ $$| $$  | $$| $$ | $$ | $$ \____  $$
//  | $$ \/  | $$|  $$$$$$$ /$$$$$$$/  |  $$$$/| $$|  $$$$$$$      | $$$$$$$/|  $$$$$$/|  $$$$$/$$$$/ /$$$$$$$/
//  |__/     |__/ \____  $$|_______/    \___/  |__/ \_______/      |_______/  \______/  \_____/\___/ |_______/
//                /$$  | $$
//               |  $$$$$$/
//                \______/
@Mod(modid = ModReference.ID, name = ModReference.NAME, version = ModReference.VERSION)
@Mod.EventBusSubscriber
public class MysticBows {

    public static final boolean IS_ELENAI_DODGE_2_LOADED = Loader.isModLoaded("elenaidodge2");

    public static Item bow, crudeBow, quickBow, heavyBow, lightningBow, flameBow, shotBow, rangedBow, burstBow, featherBow;

    @Mod.EventHandler
    @SideOnly(Side.CLIENT)
    public void init(FMLInitializationEvent initializationEvent) {
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
    }

    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> itemRegistryEvent) {
        bow = new Bow(MysticBowsConfig.common.bow.durability, MysticBowsConfig.common.bow.damageMult, MysticBowsConfig.common.bow.velocityMult, MysticBowsConfig.common.bow.drawTimeMult, MysticBowsConfig.common.bow.inaccuracy, MysticBowsConfig.common.bow.flameTime, 1, false).setTranslationKey("bow").setRegistryName(ModReference.ID, "bow");
        crudeBow = new Bow(MysticBowsConfig.common.crudeBow.durability, MysticBowsConfig.common.crudeBow.damageMult, MysticBowsConfig.common.crudeBow.velocityMult, MysticBowsConfig.common.crudeBow.drawTimeMult, MysticBowsConfig.common.crudeBow.inaccuracy, MysticBowsConfig.common.crudeBow.flameTime, 1, false).setTranslationKey("crude_bow").setRegistryName(ModReference.ID, "crude_bow");
        quickBow = new Bow(MysticBowsConfig.common.quickBow.durability, MysticBowsConfig.common.quickBow.damageMult, MysticBowsConfig.common.quickBow.velocityMult, MysticBowsConfig.common.quickBow.drawTimeMult, MysticBowsConfig.common.quickBow.inaccuracy, MysticBowsConfig.common.quickBow.flameTime, 1, false).setTranslationKey("quick_bow").setRegistryName(ModReference.ID, "quick_bow");
        heavyBow = new Bow(MysticBowsConfig.common.heavyBow.durability, MysticBowsConfig.common.heavyBow.damageMult, MysticBowsConfig.common.heavyBow.velocityMult, MysticBowsConfig.common.heavyBow.drawTimeMult, MysticBowsConfig.common.heavyBow.inaccuracy, MysticBowsConfig.common.heavyBow.flameTime, 1, false).setTranslationKey("heavy_bow").setRegistryName(ModReference.ID, "heavy_bow");
        lightningBow = new Bow(MysticBowsConfig.common.lightningBow.durability, MysticBowsConfig.common.lightningBow.damageMult, MysticBowsConfig.common.lightningBow.velocityMult, MysticBowsConfig.common.lightningBow.drawTimeMult, MysticBowsConfig.common.lightningBow.inaccuracy, MysticBowsConfig.common.lightningBow.flameTime, MysticBowsConfig.common.lightningBow.lightningChance, MysticBowsConfig.common.lightningBow.strikeEntitiesOnly, MysticBowsConfig.common.lightningBow.strikes).setTranslationKey("lightning_bow").setRegistryName(ModReference.ID, "lightning_bow");
        flameBow = new Bow(MysticBowsConfig.common.flameBow.durability, MysticBowsConfig.common.flameBow.damageMult, MysticBowsConfig.common.flameBow.velocityMult, MysticBowsConfig.common.flameBow.drawTimeMult, MysticBowsConfig.common.flameBow.inaccuracy, MysticBowsConfig.common.flameBow.flameTime, MysticBowsConfig.common.flameBow.igniteBlocks).setTranslationKey("flame_bow").setRegistryName(ModReference.ID, "flame_bow");
        shotBow = new ShotBow().setTranslationKey("shot_bow").setRegistryName(ModReference.ID, "shot_bow");
        rangedBow = new Bow(MysticBowsConfig.common.rangedBow.durability, MysticBowsConfig.common.rangedBow.damageMult, MysticBowsConfig.common.rangedBow.velocityMult, MysticBowsConfig.common.rangedBow.drawTimeMult, MysticBowsConfig.common.rangedBow.inaccuracy, MysticBowsConfig.common.rangedBow.flameTime, 1, true).setTranslationKey("ranged_bow").setRegistryName(ModReference.ID, "ranged_bow");
        burstBow = new BurstBow().setTranslationKey("burst_bow").setRegistryName(ModReference.ID, "burst_bow");

        itemRegistryEvent.getRegistry().registerAll(bow, crudeBow, quickBow, heavyBow, lightningBow, flameBow, shotBow, rangedBow, burstBow);

        if (!IS_ELENAI_DODGE_2_LOADED)
            return;

        featherBow = new FeatherBow().setTranslationKey("feather_bow").setRegistryName(ModReference.ID, "feather_bow");

        itemRegistryEvent.getRegistry().register(featherBow);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerRenders(final ModelRegistryEvent modelRegistryEvent) {
        ModelLoader.setCustomModelResourceLocation(bow, 0, new ModelResourceLocation(bow.delegate.name(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(crudeBow, 0, new ModelResourceLocation(crudeBow.delegate.name(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(quickBow, 0, new ModelResourceLocation(quickBow.delegate.name(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(heavyBow, 0, new ModelResourceLocation(heavyBow.delegate.name(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(lightningBow, 0, new ModelResourceLocation(lightningBow.delegate.name(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(flameBow, 0, new ModelResourceLocation(flameBow.delegate.name(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(shotBow, 0, new ModelResourceLocation(shotBow.delegate.name(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(rangedBow, 0, new ModelResourceLocation(rangedBow.delegate.name(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(burstBow, 0, new ModelResourceLocation(burstBow.delegate.name(), "inventory"));

        if (!IS_ELENAI_DODGE_2_LOADED)
            return;

        ModelLoader.setCustomModelResourceLocation(featherBow, 0, new ModelResourceLocation(featherBow.delegate.name(), "inventory"));
    }
}
