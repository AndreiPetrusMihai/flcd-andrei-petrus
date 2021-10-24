package Scanner;

import Domain.PifEntry;
import SymbolTable.SymbolTable;
import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileScanner {

    private String filePath;
    private String tokensPath;
    private List<String> reserved = new ArrayList<>();

    private SymbolTable symbolTable = new SymbolTable();
    private List<PifEntry> pifTable = new ArrayList<>();


    public FileScanner(String filePath, String tokensPath) throws Exception {
        this.filePath = filePath;
        this.tokensPath = tokensPath;
        generateReservedList();
        processFile();
    }

    private void generateReservedList(){
        BufferedReader reader;
        try{
            reader = new BufferedReader(new FileReader(tokensPath));
            String token = reader.readLine();
            while(token != null){
                reserved.add(token);
                token = reader.readLine();
            }
            reader.close();
        } catch(IOException e){
            e.printStackTrace();
        }
        System.out.println(reserved.toString());
    }

    private boolean isValidString(String token){
        return token.matches("^[\\w_]*$");
    }

    private boolean isValidIdentifier(String token){
        return token.matches("^\\p{Alpha}\\w*$");
    }

    private boolean isValidInteger(String token){
        System.out.println(token);
        return token.matches("(^[1-9]\\d*$)|(^0$)");
    }

    private boolean isValidFloat(String token){
        return token.matches("(^[1-9]\\d*\\.\\d\\d*$)|(^0$)");
    }

    private boolean isValidConstant(String token){
        return isValidInteger(token) || isValidFloat(token);
    }


    private void processToken(String token) throws Exception {
        for(String reservedToken : reserved){
            if(token.equals(reservedToken)){
                //Removed the token
                pifTable.add(new PifEntry(reservedToken, new Pair<>(-1,-1)));
                System.out.println("Reserved: " + reservedToken);
                //remove it
                return;
            }
        }
        if(isValidConstant(token) || isValidIdentifier(token)){
            System.out.println("Identifier | constant: " + token);
            Pair<Integer, Integer> st_pos = symbolTable.pos(token);
            pifTable.add(new PifEntry(token, st_pos));
            return;
        }
        throw new Exception("Invalid sequence of charachters.");
    }

    public void processFile() throws Exception {
        java.util.Scanner scanner;
        File file = new File(filePath);
        try{

            scanner = new java.util.Scanner(file);
            String line = scanner.nextLine();
            //Split the fiels line by line
            while(scanner.hasNextLine()){
                //Process a line
                //Split by " in order to detect sting consts and chars.
                List<String> stringSplits = Arrays.stream(line.split("\"")).filter(str -> str.length() > 0).toList();
                //Throw error if the string is not closed.
                if(stringSplits.size() % 2 == 0)
                    throw new Exception("Error regarding unescaped strings");

                for(int index = 0; index < stringSplits.size() ; index++){

                    String section = stringSplits.get(index);
                    if(index%2 == 1){
                        //We are in a string constant and we should just put it in the pif/ST
                        if(isValidString(section)){
                            System.out.println("Identifier | constant: " + section);
                            Pair<Integer, Integer> st_pos = symbolTable.pos(section);
                            pifTable.add(new PifEntry(section, st_pos));
                        } else {
                            throw new Exception("Invalid string constant");
                        }

                    } else {
                        //Continue processing
                        List<String>  sectionTokens = Arrays.stream(section.split(" ")).filter(str -> str.length() > 0).toList();
                        for(String token : sectionTokens){
                            //Check if we detect a reserved token at the beginning
                            processToken(token);
                        }
                    }

                }
                line = scanner.nextLine();
            }
            scanner.close();

        } catch(FileNotFoundException e){
            e.printStackTrace();
        }

    }
}
