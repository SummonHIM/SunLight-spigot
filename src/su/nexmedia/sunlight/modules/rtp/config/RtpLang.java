package su.nexmedia.sunlight.modules.rtp.config;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.config.api.ILangMsg;
import su.nexmedia.engine.config.api.ILangTemplate;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.sunlight.SunLight;

public class RtpLang extends ILangTemplate {

    public ILangMsg Command_RTP_Desc = new ILangMsg(this, "Teleport to a random place.");
    public ILangMsg Teleport_Error_World     = new ILangMsg(this, "{message: ~prefix: false;}&cRandom teleport is not allowed in this world.");
    public ILangMsg Teleport_Error_AlreadyIn = new ILangMsg(this, "{message: ~prefix: false;}&cYou're already in RTP!");
    public ILangMsg Teleport_Notify_Done     = new ILangMsg(this, "{message: ~type: TITLES; ~fadeIn: 10; ~stay: 60; ~fadeOut: 10;}&a&lSuccessful Teleport! \n &7&lYou're here: &8[&7X: &b%x%&7] &8[&7Y: &b%y%&7] &8[&7Z: &b%z%&7]");
    public ILangMsg Teleport_Notify_Search   = new ILangMsg(this, "{message: ~type: TITLES; ~fadeIn: 10; ~stay: 100; ~fadeOut: 10;}&e&lSearching location... \n &7&lProcess: &8[&6%attempt_current%&7/&6%attempt_max%&8]");
    public ILangMsg Teleport_Notify_Failure  = new ILangMsg(this, "{message: ~type: TITLES; ~fadeIn: 10; ~stay: 60; ~fadeOut: 10;}&c&lLocation not found: \n &7&lUnable to find location.");
    public RtpLang(@NotNull SunLight plugin, @NotNull JYML config) {
        super(plugin, config);
    }

}
