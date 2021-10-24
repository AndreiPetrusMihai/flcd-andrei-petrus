package SymbolTable;

import javafx.util.Pair;

import java.util.Vector;

public class SymbolTable {
    private Vector<Entry> entryList;
    private int MAX_SIZE = 16;
    private int CURRENT_SIZE = 0;

    public SymbolTable() {
        this.entryList = new Vector<>(MAX_SIZE);
        entryList.setSize(MAX_SIZE);
    }

    public SymbolTable(int maxCapacity){
        this.MAX_SIZE = maxCapacity;
        this.entryList = new Vector<>(MAX_SIZE);
        entryList.setSize(MAX_SIZE);
    }

    private int hash(String symbol){
        int sum = 0;
        for(int i = 0; i < symbol.length(); i++)
        {
            char asciiValue = symbol.charAt(i);
            sum = sum + asciiValue;
        }
        return sum % MAX_SIZE;
    }

    public Pair<Integer,Integer> pos(String symbol ){
        int pos = hash(symbol);
        if(entryList.get(pos) == null){
            entryList.set(pos,new Entry(symbol));
            return new Pair<>(pos,0);
        } else {
            Entry currentEntry = entryList.get(pos);
            int depth = 0;
            while(currentEntry.getNextEntry() != null) {
                if(currentEntry.getSymbol().equals(symbol))
                    return new Pair<>(pos,depth);

                depth++;
                currentEntry = currentEntry.getNextEntry();
            }

            if(currentEntry.getSymbol().equals(symbol))
                return new Pair<>(pos,depth);

            CURRENT_SIZE++;
            currentEntry.setNextEntry(new Entry(symbol));
            return new Pair<>(pos,depth+1);
        }
    }

    public String getPos(String symbol){
        int pos = hash(symbol);

        Entry currentEntry = entryList.get(pos);

        while(currentEntry != null) {
            if(currentEntry.getSymbol() == symbol){
                return currentEntry.getSymbol();
            }
            currentEntry = currentEntry.getNextEntry();
        }
        return null;
    }

    public void print(){
        for(Entry entry : this.entryList){
            System.out.println("--- --- --- ---");
            Entry currentEntry = entry;
            while(currentEntry!=null){
                System.out.print(
                        currentEntry.getSymbol() + " "
                );
                currentEntry = currentEntry.getNextEntry();
            }
            System.out.println();

        }
    }

    public Vector<Entry> getEntryList() {
        return entryList;
    }
}
