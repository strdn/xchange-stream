package info.bitrich.xchangestream.dsx.dto;

/**
 * Created by Pavel Chertalev
 */
public class DsxWebSocketBaseTransaction {

    protected final String channel;

    public DsxWebSocketBaseTransaction(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }

}
