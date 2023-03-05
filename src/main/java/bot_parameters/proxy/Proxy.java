package bot_parameters.proxy;

import bot_parameters.interfaces.BotParameter;
import bot_parameters.interfaces.Copyable;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.*;

public class Proxy implements BotParameter, Copyable<Proxy>, Serializable {

    private static final long serialVersionUID = -6367454010350132779L;

    SimpleStringProperty nickname;
    SimpleStringProperty ipAddress;
    SimpleIntegerProperty port;

    public Proxy(final String ipAddress, final int port) {
      this.ipAddress = new SimpleStringProperty(ipAddress);
      this.port = new SimpleIntegerProperty(port);
    }
    public Proxy(final String ipAddress, final int port, final String nickname) {
      this.nickname = new SimpleStringProperty(nickname);
      this.ipAddress = new SimpleStringProperty(ipAddress);
      this.port = new SimpleIntegerProperty(port);
    }

    public final String getNickname() {
        if(nickname != null) {
          return nickname.get();
        }
        return "";
    }

    public final String getIpAddress() {
        return ipAddress.get();
    }

    public final int getPort() {
        return port.get();
    }

    public final void setNickname(final String nickname) { this.nickname.set(nickname); }

    public final void setIP(final String ip) {
        this.ipAddress.set(ip);
    }

    public final void setPort(final int port) { this.port.set(port); }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.writeObject(getIpAddress());
        stream.writeInt(getPort());
        stream.writeObject(getNickname());
    }

    private void readObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        ipAddress = new SimpleStringProperty((String) stream.readObject());
        port = new SimpleIntegerProperty(stream.readInt());
        try {
          nickname = new SimpleStringProperty((String) stream.readObject());
        } catch(OptionalDataException e) {

        }
    }

    @Override
    public String[] toParameter() {
        return new String[] { "-proxy", String.format("%s:%d", ipAddress.get(), port.get()) };
    }

    @Override
    public String toString() {
      if(nickname != null && !nickname.get().isEmpty()) {
        return nickname.get() + " (" + ipAddress.get() + ":" + port.get() + ")";
      }
      return ipAddress.get() + ":" + port.get();
    }

    @Override
    public Proxy createCopy() {
        if(nickname != null) {
          return new Proxy(getIpAddress(), getPort(), getNickname());
        }
        return new Proxy(getIpAddress(), getPort());
    }
}
