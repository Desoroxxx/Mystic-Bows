package io.redstudioragnarok.mysticbows.utils;

import io.redstudioragnarok.mysticbows.Tags;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class defines constants for Mystic Bows.
 * <p>
 * id and version are automatically updated by RFG.
 */
public class ModReference {

    public static final String id = Tags.ID;
    public static final String name = "Mystic Bows";
    public static final String version = Tags.VERSION;
    public static final Logger log = LogManager.getLogger(id);
}
