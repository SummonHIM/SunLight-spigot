package su.nexmedia.sunlight.command.list;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.utils.MsgUT;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.SunPerms;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomTextCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "ctext";

    private final List<String> text;

    public CustomTextCommand(@NotNull SunLight plugin, @NotNull File file) {
        super(plugin, new ArrayList<>(), SunPerms.CMD_CTEXT + "." + StringUT.noSpace(file.getName().replace(".txt", "")).toLowerCase());
        this.text = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                this.text.add(StringUT.color(sCurrentLine));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // A Hack of Engine Command to define aliases.
        this.aliases = new String[]{StringUT.noSpace(file.getName().replace(".txt", ""))};
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    @NotNull
    public String getDescription() {
        return "";
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        Player player = sender instanceof Player ? (Player) sender : null;
        boolean papi = Hooks.hasPlaceholderAPI();

        this.text.forEach(line -> {
            if (player != null && papi) {
                line = PlaceholderAPI.setPlaceholders(player, line);
            }
            MsgUT.sendWithJSON(sender, line.replace("%player%", sender.getName()));
        });
    }
}
