package su.nexmedia.sunlight.modules.chat.config;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.config.api.ILangMsg;
import su.nexmedia.engine.config.api.ILangTemplate;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.sunlight.SunLight;

public class ChatLang extends ILangTemplate {

    public ILangMsg Chat_AntiSpam_Similar_Msg = new ILangMsg(this, "{message: ~prefix: false;}&cDo not spam messages!");
    public ILangMsg Chat_AntiSpam_Similar_Cmd = new ILangMsg(this, "{message: ~prefix: false;}&cDo not spam commands!");
    public ILangMsg Chat_AntiSpam_Delay_Msg   = new ILangMsg(this, "{message: ~prefix: false;}&cYou have to wait &c%time% &cbefore you can send another message.");
    public ILangMsg Chat_AntiSpam_Delay_Cmd   = new ILangMsg(this, "{message: ~prefix: false;}&cYou have to wait &c%time% &cbefore you can use another command.");
    public ILangMsg Command_ShortChannel_Desc  = new ILangMsg(this, "Switch chat channel or send a message to channel");
    public ILangMsg Command_ShortChannel_Usage = new ILangMsg(this, "[message]");
    public ILangMsg Command_Channel_Desc        = new ILangMsg(this, "Manage your chat channels.");
    public ILangMsg Command_Channel_Join_Desc   = new ILangMsg(this, "Join the channel.");
    public ILangMsg Command_Channel_Join_Usage  = new ILangMsg(this, "<channel>");
    public ILangMsg Command_Channel_Leave_Desc  = new ILangMsg(this, "Leave the channel.");
    public ILangMsg Command_Channel_Leave_Usage = new ILangMsg(this, "<channel>");
    public ILangMsg Command_Channel_Set_Desc    = new ILangMsg(this, "Set the channel by default.");
    public ILangMsg Command_Channel_Set_Usage   = new ILangMsg(this, "<channel>");
    public ILangMsg Channel_Join_Success            = new ILangMsg(this, "{message: ~prefix: false;}&7You joined the &a%channel_name% &7chat channel.");
    public ILangMsg Channel_Join_Error_NoPermission = new ILangMsg(this, "{message: ~prefix: false;}&cYou don't have permissions to join this channel.");
    public ILangMsg Channel_Join_Error_AlreadyIn    = new ILangMsg(this, "{message: ~prefix: false;}&cYou already joined this channel!");
    public ILangMsg Channel_Leave_Success           = new ILangMsg(this, "{message: ~prefix: false;}&7You have left the &c%channel_name%&7 chat channel.");
    public ILangMsg Channel_Leave_Error_NotIn       = new ILangMsg(this, "{message: ~prefix: false;}&cYou're are not in the channel!");
    public ILangMsg Channgel_Set_Success            = new ILangMsg(this, "{message: ~prefix: false;}&7Set default channel: &e%channel_name%&7.");
    public ILangMsg Channel_Error_Invalid = new ILangMsg(this, "{message: ~prefix: false;}&cInvalid channel!");

    public ChatLang(@NotNull SunLight plugin, @NotNull JYML config) {
        super(plugin, config);
    }

    //public ILangMsg Channel_Speak_NoPermission = new ILangMsg(this, "&cYou don't have permission to speak in this chat channel!");
}
