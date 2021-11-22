package su.nexmedia.sunlight.modules.spawn.config;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.config.api.ILangMsg;
import su.nexmedia.engine.config.api.ILangTemplate;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.sunlight.SunLight;

public class SpawnLang extends ILangTemplate {

    public ILangMsg Command_DelSpawn_Desc  = new ILangMsg(this, "Delete specified spawn.");
    public ILangMsg Command_DelSpawn_Usage = new ILangMsg(this, "<name>");
    public ILangMsg Command_DelSpawn_Done  = new ILangMsg(this, "{message: ~prefix: false; ~type: TITLES; ~fadeIn: 10; ~stay: 50; ~fadeOut: 10;}&c&lSpawn Deleted\n&7Spawn Id: &c%spawn_id%");
    public ILangMsg Command_SetSpawn_Desc  = new ILangMsg(this, "Create spawn point.");
    public ILangMsg Command_SetSpawn_Usage = new ILangMsg(this, "[name]");
    public ILangMsg Command_SetSpawn_Done  = new ILangMsg(this, "{message: ~prefix: false; ~type: TITLES; ~fadeIn: 10; ~stay: 50; ~fadeOut: 10;}&a&lSpawn Set!\n&7Spawn Id: &a%spawn_id% &7| Editor: &a/spawneditor");
    public ILangMsg Command_Spawn_Desc        = new ILangMsg(this, "Teleport on specified spawn.");
    public ILangMsg Command_Spawn_Usage       = new ILangMsg(this, "[spawn] &7or &f[player] [spawn]");
    public ILangMsg Command_Spawn_Done_Self   = new ILangMsg(this, "{message: ~prefix: false; ~type: TITLES; ~fadeIn: 10; ~stay: 80; ~fadeOut: 10;}&e&lSpawn\n&7You teleported on the &e%spawn_name% &7spawn.");
    public ILangMsg Command_Spawn_Done_Others = new ILangMsg(this, "{message: ~prefix: false;}&7Teleporting &e%player% &7on &e%spawn_id%&7 spawn...");
    public ILangMsg Command_Spawn_Error_Empty = new ILangMsg(this, "{message: ~prefix: false;}&7Spawn &c%spawn_id% &7does not exists.");
    public ILangMsg Command_SpawnEditor_Desc = new ILangMsg(this, "Open spawn editor.");
    public ILangMsg Spawn_Editor_Tip_Name     = new ILangMsg(this, "&7Enter spawn &aname&7...");
    public ILangMsg Spawn_Editor_Tip_Priority = new ILangMsg(this, "&7Enter spawn &apriority&7...");
    public ILangMsg Spawn_Editor_Tip_AddGroup = new ILangMsg(this, "&7Enter &agroup &7name...");
    public SpawnLang(@NotNull SunLight plugin, @NotNull JYML config) {
        super(plugin, config);
    }

}
