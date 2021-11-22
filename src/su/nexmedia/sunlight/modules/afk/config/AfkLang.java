package su.nexmedia.sunlight.modules.afk.config;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.config.api.ILangMsg;
import su.nexmedia.engine.config.api.ILangTemplate;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.sunlight.SunLight;

public class AfkLang extends ILangTemplate {

    public ILangMsg Command_Afk_Desc = new ILangMsg(this, "Toggle AFK mode.");
    public ILangMsg Afk_Enter      = new ILangMsg(this, "{message: ~prefix: false;}&e*** &6%player% &enow is AFK... ***");
    public ILangMsg Afk_Exit       = new ILangMsg(this, "{message: ~prefix: false;}&e*** &6%player% &ecomes back after &6%time% &e***");
    public ILangMsg Afk_Kick       = new ILangMsg(this, "&eYou have been force kicked for being AFK more than &6%time%&e!");
    public ILangMsg Afk_TellNotify = new ILangMsg(this, "{message: ~prefix: false;}&e*** &6%player% &eis AFK and may not reply the messages. &e***");
    public AfkLang(@NotNull SunLight plugin, @NotNull JYML config) {
        super(plugin, config);
    }
}
