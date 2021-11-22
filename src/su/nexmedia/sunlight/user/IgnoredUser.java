package su.nexmedia.sunlight.user;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.ICleanable;
import su.nexmedia.engine.api.manager.IEditable;
import su.nexmedia.sunlight.SunLight;
import su.nexmedia.sunlight.user.editor.EditorUserIgnoredSettings;

public class IgnoredUser implements IEditable, ICleanable {

    private final String  name;
    private       boolean ignoreChatMessages;
    private       boolean ignorePrivateMessages;
    private       boolean ignoreTeleportRequests;

    private transient EditorUserIgnoredSettings editor;

    public IgnoredUser(@NotNull String name) {
        this.name = name;
        this.setIgnoreChatMessages(false);
        this.setIgnorePrivateMessages(true);
        this.setIgnoreTeleportRequests(true);
    }

    @Override
    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
    }

    @Override
    @NotNull
    public EditorUserIgnoredSettings getEditor() {
        if (this.editor == null) {
            this.editor = new EditorUserIgnoredSettings(SunLight.getInstance(), this);
        }
        return this.editor;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public boolean isIgnoreChatMessages() {
        return this.ignoreChatMessages;
    }

    public void setIgnoreChatMessages(boolean ignoreChatMessages) {
        this.ignoreChatMessages = ignoreChatMessages;
    }

    public boolean isIgnorePrivateMessages() {
        return this.ignorePrivateMessages;
    }

    public void setIgnorePrivateMessages(boolean ignorePrivateMessages) {
        this.ignorePrivateMessages = ignorePrivateMessages;
    }

    public boolean isIgnoreTeleportRequests() {
        return this.ignoreTeleportRequests;
    }

    public void setIgnoreTeleportRequests(boolean ignoreTeleportRequests) {
        this.ignoreTeleportRequests = ignoreTeleportRequests;
    }
}
