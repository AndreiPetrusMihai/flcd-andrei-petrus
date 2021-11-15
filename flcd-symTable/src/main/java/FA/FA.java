package FA;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class FA {
    List<String> states;
    List<String> startStates;
    List<String> finalStates;
    List<String> alphabet;

    List<String> bruteTransitions = new ArrayList<>();

    HashMap<String, HashMap<String, HashMap<String, Boolean>>> transitions;

    String filePath;
    String separator;
    String transitionSeparator;

    public FA(String filePath, String separator, String transitionSeparator, boolean interactiveMode) {
        this.filePath = filePath;
        this.separator = separator;
        this.transitionSeparator = transitionSeparator;
        init();
        if (interactiveMode)
            start();
    }

    private void start() {
        Scanner scanner = new Scanner(System.in);
        boolean shouldStop = false;
        while (true) {
            printOptions();
            int option;
            try {
                String userInput = scanner.nextLine();
                option = Integer.parseInt(userInput);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid input");
                continue;
            }
            switch (option) {
                case 0 -> shouldStop = true;
                case 1 -> System.out.println(this.states.toString());
                case 2 -> System.out.println(this.finalStates.toString());
                case 3 -> System.out.println(this.alphabet.toString());
                case 4 -> printTransitions();
                case 5 -> processUserSequence(scanner);

                default -> System.out.println("Option doesn't match a command.");
            }
            System.out.println();
            if (shouldStop) break;
        }
    }

    private void unpackAlphabet() {
        List<String> unpackedAlphabet = new ArrayList();
        for (String seq : alphabet) {
            if (seq.equals("a...z")) {
                for (int c = 97; c <= 122; c++) {
                    char ch = (char) c;
                    unpackedAlphabet.add(String.valueOf(ch));
                }
                continue;
            }
            if (seq.equals("A...Z")) {
                for (int c = 65; c <= 90; c++) {
                    char ch = (char) c;
                    unpackedAlphabet.add(String.valueOf(ch));
                }
                continue;
            }
            if (seq.equals("0...9")) {
                for (int n = 0; n <= 9; n++) {
                    unpackedAlphabet.add(String.valueOf(n));
                }
                continue;
            }
            unpackedAlphabet.add(seq);
        }
        alphabet = unpackedAlphabet;
    }

    public void initTransitionMap() {
        transitions = new HashMap<>();
        for (String stateOne : states) {
            transitions.put(stateOne, new HashMap<>());
            for (String stateTwo : states) {
                transitions.get(stateOne).put(stateTwo, new HashMap<>());
            }
        }
    }

    public void processTransition(String startState, String endState, String transitionValue) {
        if (transitionValue.equals("a...z")) {
            for (int c = 97; c <= 122; c++) {
                char ch = (char) c;


                transitions.get(startState).get(endState).put(String.valueOf(ch), true);
            }
            return;
        }
        if (transitionValue.equals("A...Z")) {
            for (int c = 65; c <= 90; c++) {
                char ch = (char) c;
                transitions.get(startState).get(endState).put(String.valueOf(ch), true);
            }
            return;
        }
        if (transitionValue.equals("0...9")) {
            for (int n = 0; n <= 9; n++) {
                transitions.get(startState).get(endState).put(String.valueOf(n), true);
            }
            return;
        }

        transitions.get(startState).get(endState).put(transitionValue, true);
    }

    private void init() {
        Scanner scanner;
        String quotedSeparator = "\\Q" + separator + "\\E";
        String quotedTransitionSeparator = "\\Q" + transitionSeparator + "\\E";

        File file = new File(filePath);
        try {
            scanner = new Scanner(file);

            String statesLine = scanner.nextLine();
            this.states = Arrays.stream(statesLine.split(quotedSeparator)).toList();

            String startStatesLine = scanner.nextLine();
            this.startStates = Arrays.stream(startStatesLine.split(quotedSeparator)).toList();

            String finalStatesLine = scanner.nextLine();
            this.finalStates = Arrays.stream(finalStatesLine.split(quotedSeparator)).toList();

            String alphabetLine = scanner.nextLine();
            this.alphabet = Arrays.stream(alphabetLine.split(quotedSeparator)).toList();

            unpackAlphabet();

            initTransitionMap();
            while (scanner.hasNextLine()) {
                String transition = scanner.nextLine();
                this.bruteTransitions.add(transition);
                String[] transitionElements = transition.split(quotedTransitionSeparator);

                String startState = transitionElements[0];
                String endState = transitionElements[2];

                String transitionValuesSection = transitionElements[1];

                String[] transitionValues = transitionValuesSection.split(separator);
                for (String transitionValue : transitionValues) {
                    processTransition(startState, endState, transitionValue);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void printOptions() {
        System.out.println("0.Exit");
        System.out.println("1.Set of states");
        System.out.println("2.Final states");
        System.out.println("3.The alphabet");
        System.out.println("4.The transitions");
        System.out.println("5.Check sequence");
    }

    public boolean verifySequence(String sequence) {
        if (sequence.length() == 0)
            return false;

        char[] chars = sequence.toCharArray();

        char startChar = chars[0];
        List<String> validStartStates = new ArrayList<>();

        for (String startState : this.startStates) {
            Set<String> endStatesSet = this.transitions.get(startState).keySet();
            for (String endState : endStatesSet) {
                if (this.transitions.get(startState).get(endState).get(String.valueOf(startChar)) != null) {
                    validStartStates.add(startState);
                }
            }
        }

        for (String startState : validStartStates) {
            String lastState = startState;
            boolean noContinuation = true;
            for (char currentChar : chars) {
                Set<String> endStatesSet = this.transitions.get(lastState).keySet();
                noContinuation = true;

                for (String endState : endStatesSet) {
                    if (this.transitions.get(lastState).get(endState).get(String.valueOf(currentChar)) != null) {
                        lastState = endState;
                        noContinuation = false;
                        break;
                    }
                }
                if (noContinuation)
                    break;
            }
            if (noContinuation) {
                continue;
            }
            if (this.finalStates.contains(lastState))
                return true;

        }
        return false;
    }

    private void processUserSequence(Scanner inputScanner) {
        System.out.println("Provide the sequence that we should check.");
        String userSequence = inputScanner.nextLine();
        if (verifySequence(userSequence)) {
            System.out.println("Valid sequence");
        } else {
            System.out.println("Invalid sequence");
        }
    }

    private void printTransitions(){
        for(String transition : this.bruteTransitions){
            System.out.println(transition);
        }
    }

}
