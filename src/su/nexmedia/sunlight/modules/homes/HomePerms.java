package su.nexmedia.sunlight.modules.homes;

import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.modules.ModuleId;
import su.nexmedia.sunlight.modules.homes.command.DeleteHomeCommand;
import su.nexmedia.sunlight.modules.homes.command.HomeCommand;
import su.nexmedia.sunlight.modules.homes.command.HomesCommand;
import su.nexmedia.sunlight.modules.homes.command.SetHomeCommand;

public class HomePerms {

    private static final String PREFIX = SunPerms.PREFIX + ModuleId.HOMES + ".";

    public static final String HOMES_CMD_DELHOME           = PREFIX + "cmd." + DeleteHomeCommand.NAME;
    public static final String HOMES_CMD_DELHOME_OTHERS    = PREFIX + "cmd." + DeleteHomeCommand.NAME + ".others";
    public static final String HOMES_CMD_HOME              = PREFIX + "cmd." + HomeCommand.NAME;
    public static final String HOMES_CMD_HOME_OTHERS       = PREFIX + "cmd." + HomeCommand.NAME + ".others";
    public static final String HOMES_CMD_HOMES             = PREFIX + "cmd." + HomesCommand.NAME;
    public static final String HOMES_CMD_HOMES_OTHERS      = PREFIX + "cmd." + HomesCommand.NAME + ".others";
    public static final String HOMES_CMD_SETHOME           = PREFIX + "cmd." + SetHomeCommand.NAME;
    public static final String HOMES_BYPASS_SET_WORLDS     = PREFIX + "bypass.set.worlds";
    public static final String HOMES_BYPASS_SET_WG_REGIONS = PREFIX + "bypass.set.regions";
}
