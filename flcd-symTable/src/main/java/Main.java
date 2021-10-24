import Scanner.FileScanner;

public class Main {

    public static void main(String[] args) {
	// write your code here
    // SymbolTable table = new SymbolTable();
        try {
            FileScanner scanner = new FileScanner("src/main/java/p1.txt", "src/main/java/tokens.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
