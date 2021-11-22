package su.nexmedia.sunlight.modules.homes;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.config.api.ILangMsg;
import su.nexmedia.engine.config.api.ILangTemplate;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.sunlight.SunLight;

public class HomeLang extends ILangTemplate {

    public ILangMsg Command_DeleteHome_Desc       = new ILangMsg(this, "Delete home.");
    public ILangMsg Command_DeleteHome_Usage      = new ILangMsg(this, "[home] &7or &e/%cmd% [player] [home]");
    public ILangMsg Command_DeleteHome_Error      = new ILangMsg(this, "{message: ~prefix: false;}&cCould not delete home. Home with such id does not exist.");
    public ILangMsg Command_DeleteHome_Done_Own   = new ILangMsg(this, "{message: ~prefix: false; ~type: TITLES; ~fadeIn: 20; ~stay: 50; ~fadeOut:20;}&c&lHome Removed\n&7You deleted &c%home_id% &7home.");
    public ILangMsg Command_DeleteHome_Done_Other = new ILangMsg(this, "{message: ~prefix: false; ~type: TITLES; ~fadeIn: 20; ~stay: 50; ~fadeOut:20;}&c&lHome Removed\n&7You deleted &c%home_owner%&7's &c%home_id% &7home.");
    public ILangMsg Command_Home_Desc          = new ILangMsg(this, "Teleport to home.");
    public ILangMsg Command_Home_Usage         = new ILangMsg(this, "[home] &7or &e/%cmd% [player] [home]");
    public ILangMsg Command_Home_Error_Invalid = new ILangMsg(this, "{message: ~prefix: false;}&7Home does not exist.");
    public ILangMsg Command_Home_Done_Own      = new ILangMsg(this, "{message: ~prefix: false; ~type: TITLES; ~fadeIn: 20; ~stay: 50; ~fadeOut:20;}&e&lHome\n&7You teleported to &e%home_name% &7home.");
    public ILangMsg Command_Home_Done_Other    = new ILangMsg(this, "{message: ~prefix: false; ~type: TITLES; ~fadeIn: 20; ~stay: 50; ~fadeOut:20;}&e&lHome\n&7You teleported to &e%home_owner%&7's &e%home_name% &7home.");
    public ILangMsg Command_Homes_Desc  = new ILangMsg(this, "Manage your homes.");
    public ILangMsg Command_Homes_Usage = new ILangMsg(this, "[player]");
    public ILangMsg Command_SetHome_Desc         = new ILangMsg(this, "Set home location.");
    public ILangMsg Command_SetHome_Usage        = new ILangMsg(this, "[home]");
    public ILangMsg Command_SetHome_Error_Limit  = new ILangMsg(this, "{message: ~prefix: false;}&cYou have reached the limit of homes. You can not set more.");
    public ILangMsg Command_SetHome_Error_World  = new ILangMsg(this, "{message: ~prefix: false;}&cYou can not set home in this world!");
    public ILangMsg Command_SetHome_Error_Region = new ILangMsg(this, "{message: ~prefix: false;}&cYou can not set home in this region!");
    public ILangMsg Command_SetHome_Done         = new ILangMsg(this, "{message: ~prefix: false; ~type: TITLES; ~fadeIn: 20; ~stay: 50; ~fadeOut:20;}&a&lHome Set!\n&7Teleport: &a/home %home_id% &8| &7Menu: &a/homes");
    public ILangMsg Editor_Enter_InvitedPlayer = new ILangMsg(this, "&7Enter &aplayer name&7...");
    public ILangMsg Editor_Enter_Name          = new ILangMsg(this, "&7Enter &ahome name&7...");
    public HomeLang(@NotNull SunLight plugin, @NotNull JYML config) {
        super(plugin, config);
    }

}
