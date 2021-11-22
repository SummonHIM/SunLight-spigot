package su.nexmedia.sunlight.modules.spawn;

import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.modules.ModuleId;
import su.nexmedia.sunlight.modules.spawn.command.DeleteSpawnCommand;
import su.nexmedia.sunlight.modules.spawn.command.SetSpawnCommand;
import su.nexmedia.sunlight.modules.spawn.command.SpawnCommand;
import su.nexmedia.sunlight.modules.spawn.command.SpawnEditorCommand;

public class SpawnPerms {

    private static final String PREFIX = SunPerms.PREFIX + ModuleId.SPAWN + ".";

    public static final String SPAWN            = PREFIX + "spawn.";
    public static final String CMD_DELSPAWN     = PREFIX + "cmd." + DeleteSpawnCommand.NAME;
    public static final String CMD_SETSPAWN     = PREFIX + "cmd." + SetSpawnCommand.NAME;
    public static final String CMD_SPAWN        = PREFIX + "cmd." + SpawnCommand.NAME;
    public static final String CMD_SPAWN_OTHERS = PREFIX + "cmd." + SpawnCommand.NAME + ".others";
    public static final String CMD_SPAWN_EDITOR = PREFIX + "cmd." + SpawnEditorCommand.NAME;
}
