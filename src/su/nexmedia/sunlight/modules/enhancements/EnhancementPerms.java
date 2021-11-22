package su.nexmedia.sunlight.modules.enhancements;

import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.modules.enhancements.module.chairs.command.ChairsCommand;
import su.nexmedia.sunlight.modules.enhancements.module.chairs.command.SitCommand;
import su.nexmedia.sunlight.modules.enhancements.module.chestsort.command.ChestSortCommand;

public class EnhancementPerms {

    private static final String PREFIX = SunPerms.PREFIX + "enhancements.";

    public static final String CHAIRS_CMD_CHAIRS = PREFIX + "chairs.cmd." + ChairsCommand.NAME;
    public static final String CHAIRS_CMD_SIT    = PREFIX + "chairs.cmd." + SitCommand.NAME;

    public static final String CHESTSORT_CMD_CHESTSORT = PREFIX + "chestsort.cmd." + ChestSortCommand.NAME;

    public static final String SIGNS_COLOR  = PREFIX + "signs.color";
    public static final String ANVILS_COLOR = PREFIX + "anvils.color";
}
