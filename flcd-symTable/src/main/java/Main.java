import Scanner.FileScanner;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        String[] fileList = {
//                "W:\\Year3Sem1\\FLCD\\flcd-andrei-petrus\\programs\\p1.txt",
//                "W:\\Year3Sem1\\FLCD\\flcd-andrei-petrus\\programs\\p2.txt",
                "W:\\Year3Sem1\\FLCD\\flcd-andrei-petrus\\programs\\p3.txt",
                //"W:\\Year3Sem1\\FLCD\\flcd-andrei-petrus\\programs\\p1err.txt"
                };

        String separatorsPath = "W:\\Year3Sem1\\FLCD\\flcd-andrei-petrus\\separators.txt";
        String reservedWordsPath = "W:\\Year3Sem1\\FLCD\\flcd-andrei-petrus\\reservedWords.txt";

        try {
            FileScanner scanner = new FileScanner( separatorsPath, reservedWordsPath);
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
