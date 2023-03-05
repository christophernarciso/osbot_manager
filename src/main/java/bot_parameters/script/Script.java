package bot_parameters.script;

import bot_parameters.interfaces.BotParameter;
import bot_parameters.interfaces.Copyable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import sun.java2d.pipe.SpanShapeRenderer;

import java.io.*;

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

    public final String getNickname() {
      if(nickname != null) {
        return nickname.get();
      }
      return "";
    }
    public final String getScriptIdentifier() {
        return scriptIdentifier.get();
    }

    public final String getParameters() {
        return parameters.get();
    }

    public final void setNickname(final String nickname) {
      this.nickname.set(nickname);
    }
    public final void setScriptIdentifier(final String scriptIdentifier) {
        this.scriptIdentifier.set(scriptIdentifier);
    }

    public final void setParameters(final String parameters) {
        this.parameters.set(parameters);
    }

    public final boolean isLocal() { return isLocal.get(); }

    public void setIsLocal(final boolean isLocal) { this.isLocal.set(isLocal); }

    @Override
    public final String[] toParameter() {
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
        } catch (OptionalDataException e) {

        }
    }

    @Override
    public final String toString() {
      if(nickname != null && !nickname.get().isEmpty()) {
        return nickname.get() + " (" + scriptIdentifier.get() + ")";
      }
      return scriptIdentifier.get();
    }

    @Override
    public Script createCopy() {
      if(nickname != null) {
        return new Script(getScriptIdentifier(), getParameters(), isLocal(), getNickname());
      }
      return new Script(getScriptIdentifier(), getParameters(), isLocal());
    }
}
