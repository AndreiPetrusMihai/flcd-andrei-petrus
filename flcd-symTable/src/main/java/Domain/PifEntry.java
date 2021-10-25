package Domain;

import javafx.util.Pair;

public class PifEntry {
    public String token;
    public Pair<Integer, Integer> stPos;

    public PifEntry(String token, Pair<Integer, Integer> stPos) {
        this.token = token;
        this.stPos = stPos;
    }
}
