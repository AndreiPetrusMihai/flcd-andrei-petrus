import SymbolTable.SymbolTable;

public class Main {

    public static void main(String[] args) {
	// write your code here
        SymbolTable table = new SymbolTable();
        System.out.println(table.pos("a"));
        System.out.println(table.pos("5"));
        System.out.println(table.pos("b"));
        System.out.println(table.pos("a"));
        System.out.println(table.pos("2"));



        table.print();
    }
}
