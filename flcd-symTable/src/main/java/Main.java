import Scanner.FileScanner;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        String[] fileList = {
                "W:\\Year3Sem1\\FLCD\\flcd-andrei-petrus\\p1.txt",
                "W:\\Year3Sem1\\FLCD\\flcd-andrei-petrus\\p2.txt",
                "W:\\Year3Sem1\\FLCD\\flcd-andrei-petrus\\p3.txt",
                "W:\\Year3Sem1\\FLCD\\flcd-andrei-petrus\\p1err.txt"};

        try {
            FileScanner scanner = new FileScanner( "W:\\Year3Sem1\\FLCD\\flcd-andrei-petrus\\tokens.txt");
            for(String file : fileList){
                scanner.setFilePath(file);
                scanner.processFile();
                scanner.generateFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
