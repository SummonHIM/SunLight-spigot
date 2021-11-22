package su.nexmedia.sunlight.modules.worlds.config;

import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.config.api.ILangMsg;
import su.nexmedia.engine.config.api.ILangTemplate;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.sunlight.SunLight;

public class WorldsLang extends ILangTemplate {

    public ILangMsg Command_Goto_Usage = new ILangMsg(this, "<world>");
    public ILangMsg Command_Goto_Desc  = new ILangMsg(this, "Teleport in specified world.");
    public ILangMsg Command_Goto_Done  = new ILangMsg(this, "Whoosh!");
    public ILangMsg Command_Move_Usage = new ILangMsg(this, "<player> <world>");
    public ILangMsg Command_Move_Desc  = new ILangMsg(this, "Teleport player in specified world.");
    public ILangMsg Command_Move_Done  = new ILangMsg(this, "{message: ~prefix: false;}&e%player% &7moved to world &e%world%&7!");
    public ILangMsg Command_WorldManager_Desc = new ILangMsg(this, "World Manager");
    public ILangMsg Command_WorldManager_Create_Usage = new ILangMsg(this, "<name>");
    public ILangMsg Command_WorldManager_Create_Desc  = new ILangMsg(this, "Create a new world config.");
    public ILangMsg Command_WorldManager_Create_Error = new ILangMsg(this, "World with this name already exists!");
    public ILangMsg Command_WorldManager_Create_Done  = new ILangMsg(this, "{message: ~prefix: false;}&7Created world config: &a%world_id%&7!");
    public ILangMsg Command_WorldManager_Delete_Usage = new ILangMsg(this, "<world> [withFolder]");
    public ILangMsg Command_WorldManager_Delete_Desc  = new ILangMsg(this, "Delete specified world and config.");
    public ILangMsg Command_WorldManager_Delete_Error = new ILangMsg(this, "{message: ~prefix: false;}&cCould not delete the world! World is loaded or file access error.");
    public ILangMsg Command_WorldManager_Delete_Done  = new ILangMsg(this, "{message: ~prefix: false;}&7Deleted &a%world_id% &7world!");
    public ILangMsg Command_WorldManager_Editor_Desc = new ILangMsg(this, "Open world editor.");
    public ILangMsg Command_WorldManager_Load_Usage = new ILangMsg(this, "<world>");
    public ILangMsg Command_WorldManager_Load_Desc  = new ILangMsg(this, "Load specified world into the server.");
    public ILangMsg Command_WorldManager_Load_Error = new ILangMsg(this, "{message: ~prefix: false;}&cWorld is already loaded or world settings are invalid!");
    public ILangMsg Command_WorldManager_Load_Done  = new ILangMsg(this, "{message: ~prefix: false;}&7Loaded world: &a%world_id%&7!");
    public ILangMsg Command_WorldManager_Unload_Usage = new ILangMsg(this, "<world>");
    public ILangMsg Command_WorldManager_Unload_Desc  = new ILangMsg(this, "Unload specified world from the server.");
    public ILangMsg Command_WorldManager_Unload_Error = new ILangMsg(this, "{message: ~prefix: false;}&cWorld is already unloaded or internal server error!");
    public ILangMsg Command_WorldManager_Unload_Done  = new ILangMsg(this, "{message: ~prefix: false;}&7Unloaded world: &a%world_id%&7!");
    public ILangMsg Worlds_Error_BadCommand = new ILangMsg(this, "{message: ~prefix: false;}&cYou can't use that command here!");
    public ILangMsg Editor_Enter_Seed      = new ILangMsg(this, "&7Enter world &aseed&7...");
    public ILangMsg Editor_Enter_Generator = new ILangMsg(this, "&7Enter &agenerator &7name...");


    public WorldsLang(@NotNull SunLight plugin, @NotNull JYML config) {
        super(plugin, config);
    }

    @Override
    public void setup() {
        super.setup();
        this.setupEnum(WorldType.class);
        this.setupEnum(World.Environment.class);
        this.setupEnum(Difficulty.class);
    }
}
