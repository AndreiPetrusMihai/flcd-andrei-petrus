package Scanner;

import Domain.PifEntry;
import SymbolTable.SymbolTable;
import SymbolTable.Entry;

import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class FileScanner {

    private String filePath;
    private String tokensPath;
    private List<String> reserved = new ArrayList<>();

    private SymbolTable symbolTable = new SymbolTable();
    private List<PifEntry> pifTable = new ArrayList<>();


    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public FileScanner(String tokensPath) throws Exception {
        this.tokensPath = tokensPath;
        generateReservedList();
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
    }

    private boolean isValidString(String token){
        return token.matches("^[\\w_]*$");
    }

    private boolean isValidIdentifier(String token){
        return token.matches("^\\p{Alpha}\\w*$");
    }

    private boolean isValidInteger(String token){
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
                return;
            }
        }
        if(isValidConstant(token) || isValidIdentifier(token)){
            Pair<Integer, Integer> st_pos = symbolTable.pos(token);
            pifTable.add(new PifEntry(token, st_pos));
            return;
        }
        throw new Exception("Invalid sequence of charachters.");
    }

    public void processFile() throws Exception {
        symbolTable = new SymbolTable();
        pifTable = new ArrayList<>();

        java.util.Scanner scanner;
        File file = new File(filePath);
        try{

            scanner = new java.util.Scanner(file);
            //Split the fiels line by line
            int lineNumber = 0;
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                //Process a line
                //Split by " in order to detect sting consts and chars.
                List<String> stringSplits = Arrays.stream(line.split("\"")).filter(str -> str.length() > 0).toList();
                //Throw error if the string is not closed.
                if(stringSplits.size() % 2 == 0){
                    System.out.println("Lexical error");
                    System.out.println("Error on line " + lineNumber + ". Unescaped string");
                    return;
                }
                int tokenNumber = 0;
                for(int index = 0; index < stringSplits.size() ; index++){

                    String section = stringSplits.get(index);
                    if(index%2 == 1){
                        //We are in a string constant and we should just put it in the pif/ST
                        if(isValidString(section)){
                            Pair<Integer, Integer> st_pos = symbolTable.pos(section);
                            pifTable.add(new PifEntry(section, st_pos));
                        } else {
                            System.out.println("Lexical error");
                            System.out.println("Error on line " + lineNumber + ", token " + tokenNumber);
                            return;
                        }

                    } else {
                        //Continue processing
                        List<String>  sectionTokens = Arrays.stream(section.split(" ")).filter(str -> str.length() > 0).toList();
                        for(String token : sectionTokens){
                            //Check if we detect a reserved token at the beginning
                            try{
                                processToken(token);

                            } catch(Exception e){
                                System.out.println("Lexical error");
                                System.out.println("Error on line " + lineNumber + ", token " + tokenNumber);
                                return;
                            }
                        }
                    }
                    tokenNumber++;
                }
                lineNumber++;

            }
            scanner.close();

        } catch(FileNotFoundException e){
            e.printStackTrace();
        }
        System.out.println("Lexically correct");
    }

    public void generateFile() throws IOException {
        String[] filePathSegemnts = filePath.split("\\\\");
        String fileName = filePathSegemnts[filePathSegemnts.length-1].split("\\.")[0];
        BufferedWriter pifWriter = new BufferedWriter(new FileWriter(fileName + "_PIF.out"));
        pifWriter.write("Token    ST_pos\n");
        for(PifEntry entry : pifTable){
            pifWriter.write(entry.token + "   " + entry.stPos.toString() + "\n");
        }
        pifWriter.flush();
        pifWriter.close();

        BufferedWriter stWriter = new BufferedWriter(new FileWriter(fileName + "_ST.out"));
        stWriter.write("ST_pos    Symbol\n");
        Vector<Entry> entryList = symbolTable.getEntryList();
        int lengthIndex = 0;
        for(Entry entry : entryList){
            Entry currentEntry = entry;
            int depthIndex = 0;
            while(currentEntry!=null){
                stWriter.write(lengthIndex + "-" + depthIndex +"   " + currentEntry.getSymbol() +  "\n");
                depthIndex++;
                currentEntry = currentEntry.getNextEntry();
            }
            lengthIndex++;
        }
        stWriter.flush();
        stWriter.close();
    }
}
