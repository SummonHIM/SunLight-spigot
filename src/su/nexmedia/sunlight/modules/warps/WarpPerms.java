package su.nexmedia.sunlight.modules.warps;

import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.modules.warps.command.DeleteWarpCommand;
import su.nexmedia.sunlight.modules.warps.command.SetWarpCommand;
import su.nexmedia.sunlight.modules.warps.command.WarpCommand;
import su.nexmedia.sunlight.modules.warps.command.WarpEditorCommand;

public class WarpPerms {

    private static final String PREFIX = SunPerms.PREFIX + "warps.";

    public static final String WARP = PREFIX + "warp.";

    public static final String CMD_WARP           = PREFIX + "cmd." + WarpCommand.NAME;
    public static final String CMD_WARP_OTHERS    = PREFIX + "cmd." + WarpCommand.NAME + ".others";
    public static final String CMD_WARP_EDITOR    = PREFIX + "cmd." + WarpEditorCommand.NAME;
    public static final String CMD_SETWARP        = PREFIX + "cmd." + SetWarpCommand.NAME;
    public static final String CMD_DELWARP        = PREFIX + "cmd." + DeleteWarpCommand.NAME;
    public static final String CMD_DELWARP_OTHERS = PREFIX + "cmd." + DeleteWarpCommand.NAME + ".others";

    public static final String EDITOR_OTHERS              = PREFIX + "editor.others";
    public static final String EDITOR_TELEPORT_COST_MONEY = PREFIX + "editor.teleport.cost.money";

    public static final String BYPASS_EDITOR_DESCRIPTION_LIMIT = PREFIX + "bypass.editor.description.limit";
    public static final String BYPASS_WARP_TELEPORT_COST       = PREFIX + "bypass.teleport.cost";
    public static final String BYPASS_UNSAFE                   = PREFIX + "bypass.teleport.unsafe";
}
