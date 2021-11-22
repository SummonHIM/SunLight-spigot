package su.nexmedia.sunlight.modules.tab.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.EnumChatFormat;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.scores.ScoreboardTeamBase.EnumNameTagVisibility;
import net.minecraft.world.scores.ScoreboardTeamBase.EnumTeamPush;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.utils.Reflex;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.tab.TabManager;
import su.nexmedia.sunlight.modules.tab.TabNametag;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class V1_17_R1 extends TabPacketManager {

    public V1_17_R1(@NotNull SunLight plugin, @NotNull TabManager tabManager) {
        super(plugin, tabManager);
    }

    @Override
    public void constructList(@NotNull Player pHolder, @NotNull List<Player> sorted) {
        List<EntityPlayer> list = sorted.stream().map(pServer -> ((CraftPlayer) pServer).getHandle())
            .collect(Collectors.toList());

        PacketPlayOutPlayerInfo playerRemove = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.e, list);
        this.plugin.getPacketManager().sendPacket(pHolder, playerRemove);

        PacketPlayOutPlayerInfo playerAdd = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.a, list);
        this.plugin.getPacketManager().sendPacket(pHolder, playerAdd);
    }

    @Override
    public void constructTeam() {
        Collection<? extends Player> online = plugin.getServer().getOnlinePlayers();

        // Prepare map with all online player's teams and data.
        Map<Player, TabNametag> playerTeams = new HashMap<>();
        for (Player playerOnline : online) {
            TabNametag teamInfo = this.tabManager.getPlayerNametag(playerOnline);
            playerTeams.put(playerOnline, teamInfo);
        }

        // Now, recreate ScoreboardTeam packet for each online player with a new packet for each server player.
        // So, we create a fake team for each player (for PlaceholderAPI support).
        for (Player playerReceiver : online) {
            playerTeams.forEach((playerTeam, teamInfo) -> {
                String teamId = teamInfo.getTeamId() + playerTeam.getName();
                if (teamId.length() > 16) teamId = teamId.substring(0, 16);

                String teamPrefix = teamInfo.getPrefix();
                String teamSuffix = teamInfo.getSuffix();
                ChatColor teamColor = teamInfo.getColor();
                EnumChatFormat teamColorNMS = EnumChatFormat.valueOf(teamColor.name());

                if (Hooks.hasPlaceholderAPI()) {
                    teamPrefix = PlaceholderAPI.setPlaceholders(playerTeam, teamPrefix);
                    teamSuffix = PlaceholderAPI.setPlaceholders(playerTeam, teamSuffix);
                }

                PacketContainer pRemove = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
                Collection<String> entities = new ArrayList<>(Stream.of(playerTeam).map(HumanEntity::getName).toList());
                pRemove.getStrings().write(0, teamId); // Name
                pRemove.getSpecificModifier(Collection.class).write(0, entities);
                pRemove.getIntegers().write(0, TEAM_REMOVED); // Mode

                try {
                    this.manager.sendServerPacket(playerReceiver, pRemove);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                PacketContainer pCreate = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
                entities = new ArrayList<>(Stream.of(playerTeam).map(HumanEntity::getName).toList());
                pCreate.getStrings().write(0, teamId); // Name
                pCreate.getIntegers().write(0, TEAM_CREATED); // Mode
                pCreate.getSpecificModifier(Collection.class).write(0, entities);

                // Optional<PacketPlayOutScoreboardTeam.b>
                Optional<?> opt = pCreate.getSpecificModifier(Optional.class).read(0);
                Object b = opt.orElse(null);
                if (b != null) {
                    Reflex.setFieldValue(b, "b", plugin.getSunNMS().getHexedChatComponent(teamPrefix));       // 'playerPrefix'
                    Reflex.setFieldValue(b, "c", plugin.getSunNMS().getHexedChatComponent(teamSuffix));       // 'playerSuffix'
                    Reflex.setFieldValue(b, "a", new ChatComponentText(teamId));   // 'displayName'
                    Reflex.setFieldValue(b, "f", teamColorNMS);                    // 'color'
                    Reflex.setFieldValue(b, "d", EnumNameTagVisibility.a.name());  // 'nametagVisibility'
                    Reflex.setFieldValue(b, "e", EnumTeamPush.a.name());           // 'collisionRule'
                    Reflex.setFieldValue(b, "g", 0);                         // 'options'
                    pCreate.getSpecificModifier(Optional.class).write(0, Optional.of(b));
                }

                try {
                    this.manager.sendServerPacket(playerReceiver, pCreate);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
