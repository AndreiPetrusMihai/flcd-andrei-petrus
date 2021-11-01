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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class FileScanner {


    private String separatorsPath;
    private List<String> separators = new ArrayList<>();

    private String reservedWordsPath;
    private List<String> reservedWords = new ArrayList<>();

    private List<String> reserved = new ArrayList<>();

    Pattern reservedTokenPattern;

    private String filePath;

    private SymbolTable symbolTable = new SymbolTable();
    private List<PifEntry> pifTable = new ArrayList<>();

    public FileScanner(String separatorsPath, String reservedWordsPath) {
        this.separatorsPath = separatorsPath;
        this.reservedWordsPath = reservedWordsPath;
        generateReservedLists();
        generatePattern();
    }

    private void generateReservedLists(){
        fromFileToArray(separators,separatorsPath);
        fromFileToArray(reservedWords, reservedWordsPath);
        reserved.addAll(reservedWords);
        reserved.addAll(separators);
    }

    private void fromFileToArray(List<String> array, String filePath){
        BufferedReader reader;
        try{
            reader = new BufferedReader(new FileReader(filePath));
            String token = reader.readLine();
            while(token != null){
                array.add(token);
                token = reader.readLine();
            }
            reader.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private void generatePattern(){
        String separatorsGroup = "(";
        for(String separator : separators){
            separatorsGroup = separatorsGroup.concat("("+ Pattern.quote(separator)  +")|");
        }
        separatorsGroup = separatorsGroup.substring(0, separatorsGroup.length()-1);
        separatorsGroup+= ")";
        reservedTokenPattern = Pattern.compile(separatorsGroup);
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

    private boolean isValidNonStringConstant(String token){
        return isValidInteger(token) || isValidFloat(token);
    }

    private void processStringToken(String sToken,Integer lNumber,Integer tNumber){
        if(isValidString(sToken)){
            Pair<Integer, Integer> st_pos = symbolTable.pos(sToken);
            pifTable.add(new PifEntry("constant", st_pos));
        } else {
            System.out.println("Lexical error");
            System.out.println("Error on line " + lNumber + ", token " + tNumber);
            return;
        }
    }

    private void processToken(String token) throws Exception {
        for(String reservedToken : reserved){
            if(token.equals(reservedToken)){
                pifTable.add(new PifEntry(reservedToken, new Pair<>(-1,-1)));
                return;
            }
        }
        if(isValidIdentifier(token)){
            Pair<Integer, Integer> st_pos = symbolTable.pos(token);
            pifTable.add(new PifEntry("identifier", st_pos));
            return;
        }
        if(isValidNonStringConstant(token)){
            Pair<Integer, Integer> st_pos = symbolTable.pos(token);
            pifTable.add(new PifEntry("constant", st_pos));
            return;
        }
        throw new Exception("Invalid sequence of charachters.");
    }

    private List<String> generateTokensList(String section){
        List<String> tokenList = new ArrayList<>();
        //Split by space in order to simplify the process.
        List<String>  concatenatedTokens = Arrays.stream(section.split(" ")).filter(str -> str.length() > 0).toList();
        for(String concatToken : concatenatedTokens){
            Matcher matcher = reservedTokenPattern.matcher(concatToken);
            int lastMatchIndex = 0;
            while(matcher.find()){
                if(lastMatchIndex != matcher.start()){
                    //if the next reserved token doesn't start where the last one ended, we have an indentifier or a constant
                    tokenList.add(concatToken.substring(lastMatchIndex,matcher.start()));
                }
                lastMatchIndex = matcher.end();
                tokenList.add(matcher.group());
            }
            if(concatToken.length()-1 >= lastMatchIndex){
                tokenList.add(concatToken.substring(lastMatchIndex,concatToken.length()));
            }
        }
        return tokenList;
    }

    public void processFile() {
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
                List<String> stringSplits = Arrays.stream(line.split("\"")).toList();
                //Print error if the string is not closed.
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
                        processStringToken(section, lineNumber, tokenNumber);
                        tokenNumber++;

                    } else {
                        //COnstruct a list that contains the separated tokens
                        List<String> tokens = generateTokensList(section);

                        for(String token : tokens){
                            try{
                                //Classify and process each token
                                processToken(token.trim());
                            } catch(Exception e){
                                System.out.println("Lexical error");
                                System.out.println("Error on line " + lineNumber + ", token " + tokenNumber);
                                return;
                            }
                            tokenNumber++;
                        }
                    }
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
        stWriter.write("A HashMap was used for the symbol table implementation.\n");

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


    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

}
