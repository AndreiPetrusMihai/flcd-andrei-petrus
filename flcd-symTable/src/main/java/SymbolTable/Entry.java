package SymbolTable;

public class Entry {
    private String symbol;
    private Entry nextEntry;

    public Entry( String symbol) {
        this.symbol = symbol;
    }


    public String getSymbol() {
        return symbol;
    }

    public Entry getNextEntry() {
        return nextEntry;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setNextEntry(Entry nextEntry) {
        this.nextEntry = nextEntry;
    }
}
