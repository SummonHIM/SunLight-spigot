package su.nexmedia.sunlight.modules.scoreboard.config;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.config.api.ILangMsg;
import su.nexmedia.engine.config.api.ILangTemplate;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.sunlight.SunLight;

public class ScoreboardLang extends ILangTemplate {

    public ILangMsg Command_Scoreboard_Desc   = new ILangMsg(this, "Toggle scoreboard.");
    public ILangMsg Command_Scoreboard_Toggle = new ILangMsg(this, "{message: ~prefix: false;}&7Scoreboard: &e%state%");
    public ScoreboardLang(@NotNull SunLight plugin, @NotNull JYML config) {
        super(plugin, config);
    }
}
