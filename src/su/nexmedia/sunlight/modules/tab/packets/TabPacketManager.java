package su.nexmedia.sunlight.modules.tab.packets;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.tab.TabManager;

import java.util.List;

public abstract class TabPacketManager {

    protected static final int TEAM_CREATED    = 0;
    protected static final int TEAM_REMOVED    = 1;
    protected static final int TEAM_UPDATED    = 2;
    protected static final int PLAYERS_ADDED   = 3;
    protected static final int PLAYERS_REMOVED = 4;
    protected final ProtocolManager manager;
    protected       SunLight        plugin;
    protected       TabManager      tabManager;

    public TabPacketManager(@NotNull SunLight plugin, @NotNull TabManager tabManager) {
        this.plugin = plugin;
        this.tabManager = tabManager;
        this.manager = ProtocolLibrary.getProtocolManager();
    }

    public abstract void constructTeam();

    public abstract void constructList(@NotNull Player pHolder, @NotNull List<Player> sorted);
}
