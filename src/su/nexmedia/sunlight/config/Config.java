package su.nexmedia.sunlight.config;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.config.api.IConfigTemplate;
import su.nexmedia.sunlight.SunLight;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class Config extends IConfigTemplate {

    public static boolean DATA_CONVERSION_ESSENTIALS;
    public static SimpleDateFormat  GENERAL_DATE_FORMAT;
    public static DateTimeFormatter GENERAL_TIME_FORMAT;

    public Config(@NotNull SunLight plugin) {
        super(plugin);
    }

    @Override
    public void load() {
        Config.DATA_CONVERSION_ESSENTIALS = cfg.getBoolean("Data_Conversion.Essentials");

        Config.GENERAL_TIME_FORMAT = DateTimeFormatter.ofPattern(cfg.getString("General.Time_Format", "h:mm:ss a"));
        Config.GENERAL_DATE_FORMAT = new SimpleDateFormat(cfg.getString("General.Date_Format", "dd/MM/yyyy HH:mm"));
    }
}
