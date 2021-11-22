package su.nexmedia.sunlight.modules.warps.config;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.config.api.ILangMsg;
import su.nexmedia.engine.config.api.ILangTemplate;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.warps.type.WarpSortType;
import su.nexmedia.sunlight.modules.warps.type.WarpType;

public class WarpLang extends ILangTemplate {

    public ILangMsg Command_DeleteWarp_Desc  = new ILangMsg(this, "Delete specified warp.");
    public ILangMsg Command_DeleteWarp_Usage = new ILangMsg(this, "<warp>");
    public ILangMsg Command_DeleteWarp_Done  = new ILangMsg(this, "{message: ~prefix: false; ~type: TITLES; ~fadeIn: 20; ~stay: 80; ~fadeOut: 20;}&c&lWarp Deleted.\n&7Warp Name: &c%warp_name% &7| Warp Id: &c%warp_id%");
    public ILangMsg Command_SetWarp_Desc          = new ILangMsg(this, "Set a new warp.");
    public ILangMsg Command_SetWarp_Usage         = new ILangMsg(this, "<warp>");
    public ILangMsg Command_SetWarp_Done_New      = new ILangMsg(this, "{message: ~prefix: false; ~type: TITLES; ~fadeIn: 20; ~stay: 80; ~fadeOut: 20;}&a&lWarp Created!\n&7Warp Id: &a%warp_id% &7| Edit it in &a/warps");
    public ILangMsg Command_SetWarp_Done_Location = new ILangMsg(this, "{message: ~prefix: false; ~type: TITLES; ~fadeIn: 20; ~stay: 80; ~fadeOut: 20;}&a&lWarp Changed!\n&7Warp location is updated.");
    public ILangMsg Command_SetWarp_Error_Limit   = new ILangMsg(this, "{message: ~prefix: false; ~type: TITLES; ~fadeIn: 20; ~stay: 80; ~fadeOut: 20;}&c&lWarp Error!\n&7You have reached your warps limit.");
    public ILangMsg Command_SetWarp_Error_World   = new ILangMsg(this, "{message: ~prefix: false; ~type: TITLES; ~fadeIn: 20; ~stay: 80; ~fadeOut: 20;}&c&lWarp Error!\n&7You can not create warps in this world.");
    public ILangMsg Command_SetWarp_Error_Exists  = new ILangMsg(this, "{message: ~prefix: false; ~type: TITLES; ~fadeIn: 20; ~stay: 80; ~fadeOut: 20;}&c&lWarp Error!\n&7Warp with such name already exists!");
    public ILangMsg Command_Warp_Desc         = new ILangMsg(this, "Browse warps or teleport to a specified warp.");
    public ILangMsg Command_Warp_Usage        = new ILangMsg(this, "<warp> [player]");
    public ILangMsg Command_Warps_Done_Self   = new ILangMsg(this, "{message: ~prefix: false; ~type: TITLES; ~fadeIn: 20; ~stay: 60; ~fadeOut: 20;}&e&l%warp_name%\n%warp_welcome_message%");
    public ILangMsg Command_Warps_Done_Others = new ILangMsg(this, "{message: ~prefix: false;}Player &a%player% &7teleported on warp &a%warp_name%&7...");
    public ILangMsg Command_WarpEditor_Desc = new ILangMsg(this, "Open warp editor.");
    public ILangMsg Warp_Error_Invalid                 = new ILangMsg(this, "{message: ~prefix: false;}&cWarp &e%warp_id% &cdoes not exists!");
    public ILangMsg Warp_Error_NoPerm                  = new ILangMsg(this, "{message: ~prefix: false; ~type: TITLES; ~fadeIn: 20; ~stay: 80; ~fadeOut: 20;}&c&lWarp Error!\n&7You don't have permission for this warp!");
    public ILangMsg Warp_Teleport_Error_NotEnoughFunds = new ILangMsg(this, "{message: ~prefix: false; ~type: TITLES; ~fadeIn: 20; ~stay: 80; ~fadeOut: 20;}&c&lWarp Error!\n&7You need &c$%warp_teleport_cost_money%&7 to teleport on this warp!");
    public ILangMsg Warp_Teleport_Error_Unsafe         = new ILangMsg(this, "{message: ~prefix: false; ~type: TITLES; ~fadeIn: 20; ~stay: 80; ~fadeOut: 20;}&e&lUnsafe Warp!\n&7Please, notify the server staff.");
    public ILangMsg Editor_Error_Description_Length = new ILangMsg(this, "Line lenght can not be longer than &c%length% chars&7.");
    public ILangMsg Editor_Error_Description_Size   = new ILangMsg(this, "Lines amount can not be more than &c%size%&7.");
    public ILangMsg Editor_Enter_Welcome            = new ILangMsg(this, "&7Enter welcome message...");
    public ILangMsg Editor_Enter_CostMoney          = new ILangMsg(this, "&7Enter teleportation cost...");
    public ILangMsg Editor_Enter_Name               = new ILangMsg(this, "&7Enter new name...");
    public ILangMsg Editor_Enter_Description        = new ILangMsg(this, "&7Enter description...");
    public WarpLang(@NotNull SunLight plugin, @NotNull JYML config) {
        super(plugin, config);
    }

    @Override
    public void setup() {
        super.setup();
        this.setupEnum(WarpType.class);
        this.setupEnum(WarpSortType.class);
    }
}
