package pt.ulisboa.tecnico.classes.namingserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerEntry {
    private String host;
    private int port;
    private List<String> qualifiers;

    public ServerEntry(String host, int port, List<String> qualifiers) {
        this.host = host;
        this.port = port;
        this.qualifiers = new ArrayList<>(qualifiers);
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public String getUri() {
        return getHost()+":"+getPort();
    }

    public List<String> getQualifiers() {
        return qualifiers;
    }

    /**
     * Verifies if the received qualifiers list matches the qualifiers of the server
     * If the received list is empty it will return true in order to simulate matching all existing qualifiers
     *
     * @param qualifiers    list of strings with the qualifiers
     * @return              if qualifiers match with the qualifiers of the server
     */
    public boolean checkQualifiers(List<String> qualifiers){
        if (qualifiers.isEmpty())
            return true;
        return !Collections.disjoint(qualifiers, getQualifiers());
    }

    public static String getUri(String host, int port) {
        return host+":"+port;
    }
}
