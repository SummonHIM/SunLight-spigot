package su.nexmedia.sunlight.modules.menu;

import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.modules.ModuleId;
import su.nexmedia.sunlight.modules.menu.command.MenuCommand;

public class MenuPerms {

    private static final String PREFIX = SunPerms.PREFIX + ModuleId.MENU + ".";

    public static final String MENU            = PREFIX + "menu.";
    public static final String CMD_MENU        = PREFIX + "cmd." + MenuCommand.NAME;
    public static final String CMD_MENU_OTHERS = PREFIX + "cmd." + MenuCommand.NAME + ".others";
}
