package su.nexmedia.sunlight.modules.enhancements;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.config.api.ILangMsg;
import su.nexmedia.engine.config.api.ILangTemplate;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.sunlight.SunLight;

public class EnhancementLang extends ILangTemplate {

    public ILangMsg Command_ChestSort_Desc   = new ILangMsg(this, "Toggle chest sorting.");
    public ILangMsg Command_ChestSort_Toggle = new ILangMsg(this, "{message: ~prefix: false;}&7Chest sort: &e%state%");
    public ILangMsg Command_Chairs_Desc   = new ILangMsg(this, "Toggle chairs function.");
    public ILangMsg Command_Chairs_Toggle = new ILangMsg(this, "{message: ~prefix: false;}&7Chairs: &e%state%");
    public ILangMsg Command_Sit_Desc      = new ILangMsg(this, "Sit on the block you're standing on.");
    public EnhancementLang(@NotNull SunLight plugin, @NotNull JYML config) {
        super(plugin, config);
    }
}
