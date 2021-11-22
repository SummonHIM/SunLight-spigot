package su.nexmedia.sunlight.command.list;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.engine.utils.NumberUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.engine.utils.TimeUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;
import su.nexmedia.sunlight.config.CommandConfig;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.List;

public class SystemCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "system";
    public static final String CPU_CORES    = String.valueOf(Runtime.getRuntime().availableProcessors());
    public static final String JAVA_VERSION = System.getProperty("java.version");
    public static final String OS_NAME      = System.getProperty("os.name");
    public static final String OS_ARCH      = System.getProperty("os.arch");
    public static       String CPU_LOAD     = "N/A";
    private final List<String> format;
    public SystemCommand(@NotNull SunLight plugin) {
        super(plugin, CommandConfig.getAliases(NAME), SunPerms.CMD_SYSTEM);

        JYML cfg = CommandConfig.getConfig();
        String path = "Settings." + NAME + ".";

        this.format = StringUT.color(cfg.getStringList(path + "Format"));
    }

    private static void getCPULoad() {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
            AttributeList list = mbs.getAttributes(name, new String[]{"ProcessCpuLoad"});

            if (list == null || list.isEmpty()) return;

            Attribute att = (Attribute) list.get(0);
            Double value = (Double) att.getValue();
            if (value == -1.0) return;

            CPU_LOAD = NumberUT.format((value * 1000D) / 10D);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.lang().Command_System_Desc.getMsg();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        Runtime runtime = Runtime.getRuntime();

        String upTime = TimeUT.formatTimeLeft(System.currentTimeMillis(), ManagementFactory.getRuntimeMXBean().getStartTime());
        String tps = NumberUT.format(plugin.getSunNMS().getTPS());
        String ramMax = String.valueOf(runtime.maxMemory() / 1024L / 1024L);
        String ramTotal = String.valueOf(runtime.totalMemory() / 1024L / 1024);
        String ramFree = String.valueOf(runtime.freeMemory() / 1024L / 1024L);

        getCPULoad();

        for (String lineSys : this.format) {

            if (lineSys.contains("%world%")) {
                for (World world : plugin.getServer().getWorlds()) {
                    int tiles = 0;
                    int ents = world.getEntities().size();
                    int chunks = world.getLoadedChunks().length;
                    for (Chunk chunk : world.getLoadedChunks()) {
                        tiles += chunk.getTileEntities().length;
                    }

                    lineSys = lineSys
                        .replace("%tiles%", String.valueOf(tiles))
                        .replace("%chunks%", String.valueOf(chunks))
                        .replace("%entities%", String.valueOf(ents))
                        .replace("%world%", world.getName());
                    sender.sendMessage(lineSys);
                }
                continue;
            }

            lineSys = lineSys
                .replace("%cpu_load%", CPU_LOAD)
                .replace("%cpu_cores%", CPU_CORES)
                .replace("%os_arch%", OS_ARCH)
                .replace("%os_name%", OS_NAME)
                .replace("%java_version%", JAVA_VERSION)
                .replace("%uptime%", upTime)
                .replace("%tps%", tps)
                .replace("%ram_max%", ramMax)
                .replace("%ram_total%", ramTotal)
                .replace("%ram_free%", ramFree);
            sender.sendMessage(lineSys);
        }
    }
}
