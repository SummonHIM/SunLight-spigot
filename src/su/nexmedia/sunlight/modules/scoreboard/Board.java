package su.nexmedia.sunlight.modules.scoreboard;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.core.Version;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.utils.StringUT;
import su.nexmedia.engine.utils.random.Rnd;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.utils.SimpleTextAnimator;
import su.nexmedia.sunlight.nms.SunNMS;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class Board {

    private static Object HEALTH_DISPLAY_VALUE;

    static {
        try {
            HEALTH_DISPLAY_VALUE = Class.forName(getDisplayClass()).getEnumConstants()[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final ScoreboardManager boardManager;
    private final Sideline          sideline;
    private final BoardConfig       boardConfig;

    public Board(@NotNull Player owner, @NotNull ScoreboardManager boardManager, @NotNull BoardConfig boardConfig) {
        this.boardManager = boardManager;
        this.boardConfig = boardConfig;
        this.sideline = new Sideline(new Sidebar(owner));
        this.sideline.getSidebar().setName(boardConfig.getTitle());
        this.update(owner);
    }

    @NotNull
    private static String getDisplayClass() {
        String cls = "IScoreboardCriteria$EnumScoreboardHealthDisplay";
        if (Version.CURRENT.isHigher(Version.V1_16_R3)) {
            return "net.minecraft.world.scores.criteria." + cls;
        }
        return "net.minecraft.server." + Version.CURRENT.name().toLowerCase() + "." + cls;
    }

    @NotNull
    public BoardConfig getBoardConfig() {
        return boardConfig;
    }

    public synchronized void update(@NotNull Player player) {
        List<String> lines = this.getBoardConfig().getLines().stream().map(line -> {
            if (Hooks.hasPlaceholderAPI()) {
                line = PlaceholderAPI.setPlaceholders(player, line);
            }
            for (SimpleTextAnimator animation : this.boardManager.animationMap.values()) {
                line = animation.replace(line);
            }
            return line;
        }).collect(Collectors.toList());

        lines.forEach(this.sideline::add);
        this.sideline.flush();
    }

    public void remove() {
        this.sideline.getSidebar().createRemove(1);
    }

    public synchronized void send(@NotNull String replace) {
        this.sideline.getSidebar().setName(replace);
    }

    public static class Sidebar {

        private final Player               player;
        private final Map<String, Integer> linesA;
        private final Map<String, Integer> linesB;
        private       Boolean              a;

        public Sidebar(@NotNull Player player) {
            this.a = true;
            this.player = player;
            this.linesA = new HashMap<>();
            this.linesB = new HashMap<>();
            this.createRemove(0);
        }

        private String getBuffer() {
            return this.a ? "A" : "B";
        }

        private Map<String, Integer> linesBuffer() {
            return this.a ? this.linesA : this.linesB;
        }

        private Map<String, Integer> linesDisplayed() {
            return this.a ? this.linesB : this.linesA;
        }

        private void swapBuffer() {
            this.a = !this.a;
        }

        /**
         * @param index Create = 0, remove = 1
         */
        public void createRemove(int index) {
            boolean isCreate = index == 0;

            for (String packetId : new String[]{"A", "B"}) {
                PacketContainer container = new PacketContainer(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
                container.getStrings().write(0, packetId);
                container.getChatComponents().write(0, WrappedChatComponent.fromText(""));
                container.getIntegers().write(0, index);
                if (isCreate) container.getModifier().write(2, HEALTH_DISPLAY_VALUE);

                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, container);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        public void send() {
            PacketContainer display = new PacketContainer(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE);
            display.getIntegers().write(0, 1);
            display.getStrings().write(0, this.getBuffer());
            this.swapBuffer();

            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(this.player, display);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            for (String text : this.linesDisplayed().keySet()) {
                if (this.linesBuffer().containsKey(text) && this.linesBuffer().get(text).equals(this.linesDisplayed().get(text))) {
                    continue;
                }
                this.setLine(text, this.linesDisplayed().get(text));
            }

            for (String text : new ArrayList<>(this.linesBuffer().keySet())) {
                if (!this.linesDisplayed().containsKey(text)) {
                    this.removeLine(text);
                }
            }
        }

        public void clear() {
            for (String text : new ArrayList<>(this.linesBuffer().keySet())) {
                this.removeLine(text);
            }
        }

        public void setLine(@NotNull String text, Integer line) {
            if (StringUT.colorRaw(text).length() > 40) return;
            if (this.linesBuffer().containsKey(text)) {
                this.removeLine(text);
            }

            PacketContainer set = new PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE);
            set.getStrings().write(0, text).write(1, this.getBuffer());
            set.getIntegers().write(0, line);
            set.getScoreboardActions().write(0, EnumWrappers.ScoreboardAction.CHANGE);

            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(this.player, set);
                this.linesBuffer().put(text, line);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        public void removeLine(@NotNull String text) {
            if (StringUT.colorRaw(text).length() > 40) return;
            if (!this.linesBuffer().containsKey(text)) {
                return;
            }

            Integer line = this.linesBuffer().get(text);
            PacketContainer reset = new PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE);
            reset.getStrings().write(0, text).write(1, this.getBuffer());
            reset.getIntegers().write(0, line);
            reset.getScoreboardActions().write(0, EnumWrappers.ScoreboardAction.REMOVE);

            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(this.player, reset);
                this.linesBuffer().remove(text);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        public void setName(@NotNull String displayName) {
            if (StringUT.colorOff(displayName).length() > 32) return;

            SunNMS nms = SunLight.getInstance().getSunNMS();

            PacketContainer nameA = new PacketContainer(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
            nameA.getStrings().write(0, "A");
            nameA.getChatComponents().write(0, WrappedChatComponent.fromHandle(nms.getHexedChatComponent(displayName)));
            nameA.getIntegers().write(0, 2);
            nameA.getModifier().write(2, HEALTH_DISPLAY_VALUE);

            PacketContainer nameB = new PacketContainer(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
            nameB.getStrings().write(0, "B");
            nameB.getChatComponents().write(0, WrappedChatComponent.fromHandle(nms.getHexedChatComponent(displayName)));
            nameB.getIntegers().write(0, 2);
            nameB.getModifier().write(2, HEALTH_DISPLAY_VALUE);

            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(this.player, nameA);
                ProtocolLibrary.getProtocolManager().sendServerPacket(this.player, nameB);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Sideline {

        Sidebar       sidebar;
        Deque<String> buffer;

        public Sideline(@NotNull Sidebar sidebar) {
            this.buffer = new ArrayDeque<>();
            this.sidebar = sidebar;
        }

        public void add(@NotNull String line) {
            this.buffer.add(line);
        }

        public void flush() {
            this.sidebar.clear();
            int index = 0;
            Iterator<String> iterator = this.buffer.descendingIterator();
            while (iterator.hasNext()) {
                String line = iterator.next();
                this.sidebar.setLine(makeUnique(line), ++index);
            }
            this.buffer.clear();
            this.sidebar.send();
        }

        @NotNull
        private String makeUnique(@NotNull String str) {
            if (Version.CURRENT.isHigher(Version.V1_15_R1)) {
                return StringUT.color(str + "#" + Rnd.get(100, 999) + Rnd.get(100, 999));
            }
            return str;
        }

        @NotNull
        public Sidebar getSidebar() {
            return this.sidebar;
        }
    }
}
