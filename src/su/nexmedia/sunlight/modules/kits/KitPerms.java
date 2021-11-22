package su.nexmedia.sunlight.modules.kits;

import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.modules.kits.command.KitCommand;
import su.nexmedia.sunlight.modules.kits.command.KitEditorCommand;
import su.nexmedia.sunlight.modules.kits.command.KitPreviewCommand;

public class KitPerms {

    private static final String PREFIX = SunPerms.PREFIX + "kits.";

    public static final String KITS_KIT               = PREFIX + "kit.";
    public static final String KITS_CMD_KIT           = PREFIX + "cmd." + KitCommand.NAME;
    public static final String KITS_CMD_KIT_OTHERS    = PREFIX + "cmd." + KitCommand.NAME + ".others";
    public static final String KITS_CMD_KIT_EDITOR    = PREFIX + "cmd." + KitEditorCommand.NAME;
    public static final String KITS_CMD_KIT_PREVIEW   = PREFIX + "cmd." + KitPreviewCommand.NAME;
    public static final String KITS_BYPASS_COST_MONEY = PREFIX + "bypass.cost.money";
    public static final String KITS_BYPASS_COOLDOWN   = PREFIX + "bypass.cooldown";
}
