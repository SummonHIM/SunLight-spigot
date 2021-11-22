package su.nexmedia.sunlight.modules.chat.rule;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.ILoadable;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.modules.chat.ChatManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

public class ChatRuleManager implements ILoadable {

    private final SunLight    plugin;
    private final ChatManager chatManager;

    private Map<String, ChatRuleConfig> ruleConfigs;

    public ChatRuleManager(@NotNull ChatManager chatManager) {
        this.plugin = chatManager.plugin();
        this.chatManager = chatManager;
    }

    @Override
    public void setup() {
        this.plugin.getConfigManager().extractFullPath(this.chatManager.getFullPath() + "rules");

        this.ruleConfigs = new HashMap<>();
        for (JYML cfg : JYML.loadAll(this.chatManager.getFullPath() + "/rules/", true)) {
            try {
                ChatRuleConfig ruleConfig = new ChatRuleConfig(this.chatManager, cfg);
                String id = cfg.getFile().getName().replace(".yml", "");
                this.ruleConfigs.put(id, ruleConfig);
            } catch (Exception e) {
                this.chatManager.error("Could not load rule config: '" + cfg.getFile().getName() + "' !");
                e.printStackTrace();
            }
        }

        this.chatManager.info("Loaded " + this.ruleConfigs.size() + " chat rules!");
    }

    @Override
    public void shutdown() {
        if (this.ruleConfigs != null) {
            this.ruleConfigs.clear();
            this.ruleConfigs = null;
        }
    }

    @NotNull
    public String checkRules(@NotNull Player player, @NotNull String msgReal, @NotNull String msgRaw, @NotNull Event e) {
        Set<ChatRuleConfig> punishes = new HashSet<>();

        Label_Rule:
        for (ChatRuleConfig ruleConfig : this.ruleConfigs.values()) {
            for (ChatRule rule : ruleConfig.getRules().values()) {
                Matcher matcher = rule.getMatcher(msgRaw.toLowerCase());
                if (matcher == null) continue;

                Label_Matcher:
                while (matcher.find()) {
                    String find = matcher.group(0).trim();

                    boolean skip = rule.getIgnoredWords().stream().anyMatch(word -> {
                        return msgRaw.contains(word) && (find.contains(word) || word.contains(find));
                    });
                    if (skip) continue;

                    punishes.add(ruleConfig);

                    switch (rule.getAction()) {
                        case DENY -> {
                            ((Cancellable) e).setCancelled(true);
                            break Label_Rule;
                        }
                        case REPLACE -> {
                            msgReal = msgRaw.replace(find, rule.getReplacer());
                            break Label_Matcher;
                        }
                        case REPLACE_FULL -> {
                            msgReal = rule.getReplacer();
                            break Label_Rule;
                        }
                    }
                }
            }
        }

        punishes.forEach(punish -> {
            plugin.getServer().getScheduler().runTask(plugin, () -> punish.punish(player));
        });

        return msgReal;
    }
}
