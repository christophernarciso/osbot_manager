package bot_parameters.script;

import bot_parameters.interfaces.BotParameter;
import bot_parameters.interfaces.Copyable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Optional;

public final class Script implements BotParameter, Copyable<Script>, Serializable {

    private static final long serialVersionUID = -3697946363287646016L;

    private SimpleStringProperty author;
    private SimpleStringProperty nickname;
    private SimpleStringProperty scriptIdentifier;
    private SimpleStringProperty parameters;
    private SimpleBooleanProperty isLocal;

    public Script(final String scriptIdentifier, final String parameters, final boolean isLocal) {
        this.scriptIdentifier = new SimpleStringProperty(scriptIdentifier);
        this.parameters = new SimpleStringProperty(parameters);
        this.isLocal = new SimpleBooleanProperty(isLocal);
    }

    public Script(final String scriptIdentifier, final String parameters, final boolean isLocal, final String nickname) {
        this.scriptIdentifier = new SimpleStringProperty(scriptIdentifier);
        this.parameters = new SimpleStringProperty(parameters);
        this.isLocal = new SimpleBooleanProperty(isLocal);
        this.nickname = new SimpleStringProperty(nickname);
    }

    public String getNickname() {
        return Optional.ofNullable(nickname.get()).orElse(StringUtils.EMPTY);
    }

    public void setNickname(final String nickname) {
        this.nickname.set(nickname);
    }

    public String getScriptIdentifier() {
        return scriptIdentifier.get();
    }

    public void setScriptIdentifier(final String scriptIdentifier) {
        this.scriptIdentifier.set(scriptIdentifier);
    }

    public String getParameters() {
        return parameters.get();
    }

    public void setParameters(final String parameters) {
        this.parameters.set(parameters);
    }

    public boolean isLocal() {
        return isLocal.get();
    }

    public void setIsLocal(final boolean isLocal) {
        this.isLocal.set(isLocal);
    }

    @Override
    public String[] toParameter() {
        if (isLocal()) {
            return new String[]{"-script", String.format("\\\"%s\\\":\\\"%s\\\"", scriptIdentifier.get(), parameters.get())};
        } else {
            return new String[]{"-script", String.format("%s:\\\"%s\\\"", scriptIdentifier.get(), parameters.get())};
        }
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.writeObject(scriptIdentifier.get());
        stream.writeObject(parameters.get());
        stream.writeBoolean(isLocal.get());
        stream.writeObject(nickname.get());
    }

    private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        scriptIdentifier = new SimpleStringProperty((String) stream.readObject());
        parameters = new SimpleStringProperty((String) stream.readObject());
        isLocal = new SimpleBooleanProperty(stream.readBoolean());
        try {
            nickname = new SimpleStringProperty((String) stream.readObject());
        } catch (Exception e) {
            nickname = new SimpleStringProperty(StringUtils.EMPTY);
        }
    }

    @Override
    public String toString() {
        return getNickname() + " (" + scriptIdentifier.get() + ")";
    }

    @Override
    public Script createCopy() {
        return new Script(getScriptIdentifier(), getParameters(), isLocal(), getNickname());
    }
}
