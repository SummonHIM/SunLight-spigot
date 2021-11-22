package su.nexmedia.sunlight.modules.chat.rule;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.utils.regex.RegexUT;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatRule {

    private final String        id;
    private final String        regex;
    private final ChatRule.Type action;
    private final String        replacer;
    private final Set<String>   ignoredWords;

    private final Pattern pattern;

    public ChatRule(
        @NotNull String id,
        @NotNull String regex,
        @NotNull ChatRule.Type action,
        @NotNull String replacer,
        @NotNull Set<String> ignoredWords
    ) {
        this.id = id.toLowerCase();
        this.regex = regex;
        this.action = action;
        this.replacer = replacer;
        this.ignoredWords = ignoredWords;

        this.pattern = Pattern.compile(this.getRegex());
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    @NotNull
    public String getRegex() {
        return this.regex;
    }

    @NotNull
    public ChatRule.Type getAction() {
        return this.action;
    }

    @NotNull
    public String getReplacer() {
        return this.replacer;
    }

    @NotNull
    public Set<String> getIgnoredWords() {
        return this.ignoredWords;
    }

    @NotNull
    public Pattern getPattern() {
        return pattern;
    }

    @Nullable
    public Matcher getMatcher(@NotNull String msg) {
        return RegexUT.getMatcher(this.getPattern(), msg);
    }

    public enum Type {
        DENY, REPLACE, REPLACE_FULL,
    }
}
