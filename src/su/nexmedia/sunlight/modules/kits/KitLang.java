package su.nexmedia.sunlight.modules.kits;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.config.api.ILangMsg;
import su.nexmedia.engine.config.api.ILangTemplate;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.sunlight.SunLight;

public class KitLang extends ILangTemplate {

    public ILangMsg Command_Kit_Usage        = new ILangMsg(this, "<kit> [player]");
    public ILangMsg Command_Kit_Desc         = new ILangMsg(this, "Get specified kit.");
    public ILangMsg Command_KitEditor_Desc   = new ILangMsg(this, "Open kit editor.");
    public ILangMsg Command_KitPreview_Usage = new ILangMsg(this, "<kit>");
    public ILangMsg Command_KitPreview_Desc  = new ILangMsg(this, "Preview content of the specified kit.");
    public ILangMsg Kit_Error_NoKits             = new ILangMsg(this, "{message: ~prefix: false;}&cNo kits are available.");
    public ILangMsg Kit_Error_InvalidKit         = new ILangMsg(this, "{message: ~prefix: false;}&cKit &e%kit_id% &cdoes not exists!");
    public ILangMsg Kit_Error_NoPermission       = new ILangMsg(this, "{message: ~prefix: false;}&cYou don't have permissions to use this kit!");
    public ILangMsg Kit_Error_NotEnoughFunds     = new ILangMsg(this, "{message: ~prefix: false;}&cYou can't afford this kit! You need &c$%kit_cost_money%&7.");
    public ILangMsg Kit_Error_Cooldown_Expirable = new ILangMsg(this, "{message: ~prefix: false;}&cYou have to wait &e%kit_user_cooldown% &cbefore you can use this kit again.");
    public ILangMsg Kit_Error_Cooldown_OneTimed  = new ILangMsg(this, "{message: ~prefix: false;}&cThis kit is one-timed. You already used this kit.");
    public ILangMsg Kit_Notify_Give_Self   = new ILangMsg(this, "{message: ~prefix: false;}&7Recieved kit: &a%kit_name% &8(%kit_id%)&7!");
    public ILangMsg Kit_Notify_Give_Others = new ILangMsg(this, "{message: ~prefix: false;}&7Given kit &a%kit_name% &8(%kit_id%) &7to &a%player%&7!");
    public ILangMsg Editor_Enter_KitId         = new ILangMsg(this, "&7Enter &aunique &7kit &aidentifier...");
    public ILangMsg Editor_Enter_Command       = new ILangMsg(this, "&7Enter a &acommand&7...");
    public ILangMsg Editor_Enter_Cooldown      = new ILangMsg(this, "&7Enter &acooldown&7... &a(in seconds)");
    public ILangMsg Editor_Enter_Cost          = new ILangMsg(this, "&7Enter &amoney cost&7...");
    public ILangMsg Editor_Enter_Name          = new ILangMsg(this, "&7Enter new &aname&7...");
    public ILangMsg Editor_Enter_Description   = new ILangMsg(this, "&7Enter description &atext&7...");
    public ILangMsg Editor_Enter_Priority      = new ILangMsg(this, "&7Enter new &apriority&7...");
    public ILangMsg Editor_Error_AlreadyExists = new ILangMsg(this, "&cKit already exists!");
    public KitLang(@NotNull SunLight plugin, @NotNull JYML config) {
        super(plugin, config);
    }

}
