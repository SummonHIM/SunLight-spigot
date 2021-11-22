package su.nexmedia.sunlight.modules.worlds;

import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.modules.worlds.commands.GotoCommand;
import su.nexmedia.sunlight.modules.worlds.commands.MoveCommand;
import su.nexmedia.sunlight.modules.worlds.commands.worldmanager.*;

public class WorldPerms {

    private static final String PREFIX = SunPerms.PREFIX + "worlds.";

    public static final String CMD_GOTO                = PREFIX + "cmd." + GotoCommand.NAME;
    public static final String CMD_MOVE                = PREFIX + "cmd." + MoveCommand.NAME;
    public static final String CMD_WORLDMANAGER        = PREFIX + "cmd." + WorldManagerCommand.NAME;
    public static final String CMD_WORLDMANAGER_CREATE = PREFIX + "cmd." + WorldManagerCommand.NAME + "." + CreateSubCommand.NAME;
    public static final String CMD_WORLDMANAGER_DELETE = PREFIX + "cmd." + WorldManagerCommand.NAME + "." + DeleteSubCommand.NAME;
    public static final String CMD_WORLDMANGER_LOAD    = PREFIX + "cmd." + WorldManagerCommand.NAME + "." + LoadSubCommand.NAME;
    public static final String CMD_WORLDMANAGER_UNLOAD = PREFIX + "cmd." + WorldManagerCommand.NAME + "." + UnloadSubCommand.NAME;
    public static final String CMD_WORLDMANAGER_EDITOR = PREFIX + "cmd." + WorldManagerCommand.NAME + "." + EditorSubCommand.NAME;

    public static final String WORLDS_BYPASS_COMMANDS = PREFIX + "bypass.commands";
}
