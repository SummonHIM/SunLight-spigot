package su.nexmedia.sunlight.modules.menu;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.config.api.ILangMsg;
import su.nexmedia.engine.config.api.ILangTemplate;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.sunlight.SunLight;

public class MenuLang extends ILangTemplate {

    public ILangMsg Command_Menu_Desc        = new ILangMsg(this, "Open specified menu.");
    public ILangMsg Command_Menu_Usage       = new ILangMsg(this, "<id> [player]");
    public ILangMsg Command_Menu_Others_Done = new ILangMsg(this, "{message: ~prefix: false;}&7Opened &e%menu% menu &7for &e%player%&7.");
    public ILangMsg Menu_Error_Invalid      = new ILangMsg(this, "{message: ~prefix: false;}&cNo such menu!");
    public ILangMsg Menu_Error_NoPermission = new ILangMsg(this, "{message: ~prefix: false;}&cYou don't have permission to use this menu!");
    public MenuLang(@NotNull SunLight plugin, @NotNull JYML config) {
        super(plugin, config);
    }
}
