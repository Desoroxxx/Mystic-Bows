package io.redstudioragnarok.mysticbows.handler;

import io.redstudioragnarok.mysticbows.config.MysticBowsConfig;
import io.redstudioragnarok.mysticbows.items.Bow;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Client-side event handler for custom bows.
 * <p>
 * This class handles the field of view (FOV) and the mouse sensitivity when a player is drawing a custom bow.
 * The FOV "zooms in" proportionately to the draw of the bow.
 * For ranged bows, mouse sensitivity is also adjusted based on the final FOV.
 * <p>
 * Mouse sensitivity is reset to its original value when the bow is no longer in use.
 *
 * @see Bow
 * @see FOVUpdateEvent
 */
public class ClientEventHandler {

    private static boolean modifiedMouseSensitivity;

    private static float originalMouseSensitivity;

    /**
     * This event handler adjusts the Field of View (FOV) based on the player's use of a custom bow.
     * The FOV "zooms in" proportionately to the draw of the bow.
     *
     * @param fovUpdateEvent The event that updates the FOV.
     */
    @SubscribeEvent
    public void onFovUpdate(FOVUpdateEvent fovUpdateEvent) {
        // Get the active item the player is currently holding
        final Item eventItem = fovUpdateEvent.getEntity().getActiveItemStack().getItem();

        // If the active item is not a bow from the mod, return
        if (!(eventItem instanceof Bow))
            return;

        float finalFov = fovUpdateEvent.getFov();

        // Calculate how long the item has been in use and the current pull of the bow
        final float itemUseCount = 72000 - fovUpdateEvent.getEntity().getItemInUseCount();
        float currentPull = itemUseCount / 20;

        // Cap the currentPull to 1 and square it for a non-linear pull effect
        if (currentPull > 1.0F)
            currentPull = 1.0F;
        else
            currentPull *= currentPull;

        // Adjust the FOV based on the current pull of the bow
        finalFov /= 1.0F - (currentPull * 0.15F);

        // Compute a custom FOV divider based on bow's draw time multiplier
        final float fovDivider = 16 * ((Bow) eventItem).drawTimeMult;

        // Calculate customBow value based on item use count and fovDivider
        float customBow = itemUseCount / fovDivider;

        // Cap the customBow to 1 and square it for a non-linear customBow effect
        if (customBow > 1.0F)
            customBow = 1.0F;
        else
            customBow *= customBow;

        // Adjust the FOV based on the customBow and the type of bow
        finalFov *= 1.0F - (customBow * 0.15F) * (((Bow) eventItem).rangedBow ? MysticBowsConfig.common.rangedBow.fovMult : 1);

        // If the bow is a ranged bow, adjust the mouse sensitivity based on the final FOV
        if (((Bow) eventItem).rangedBow) {
            if (!modifiedMouseSensitivity) {
                originalMouseSensitivity = Minecraft.getMinecraft().gameSettings.mouseSensitivity;
                modifiedMouseSensitivity = true;
            }

            // Modify the mouse sensitivity based on final FOV
            Minecraft.getMinecraft().gameSettings.mouseSensitivity = originalMouseSensitivity * finalFov;
        }

        // Update the FOV
        fovUpdateEvent.setNewfov(finalFov);
    }

    /**
     * Resets the mouse sensitivity back to the original value.
     * To be called when no longer using the bow.
     */
    public static void resetMouseSensitivity() {
        // Restore the original mouse sensitivity
        Minecraft.getMinecraft().gameSettings.mouseSensitivity = originalMouseSensitivity;

        // Set the modifiedMouseSensitivity flag to false as the sensitivity is no longer modified
        modifiedMouseSensitivity = false;
    }
}

