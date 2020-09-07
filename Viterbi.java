/**
 @author Jeff Cho and Gia Kim Fall 2019
 */
import java.util.*;
import java.io.*;

public class Viterbi {

    /**
     *
     * Method to run Viterbi tagging on one line
     *
     */

    public static String toVitLine(String lineIn, HashMap<String, HashMap<String, Double>> trans, HashMap<String, HashMap<String, Double>> observs){
        // default start value
        String startVal = "#";
        // unseen penalty of -100
        double unseen = -100.0;
        //initialize blank line to be rerturned
        String lineOut = "";
        String lastTag = "";
        //set highest score to negative infinity so that it gets beaten certainly
        double highScore = Double.NEGATIVE_INFINITY;
        String[] wordArray = lineIn.split(" ");
        //List<Map<String,String>> backtracking = new ArrayList<Map<String,String>>();
        ArrayList<TreeMap<String,String>> backtracking = new ArrayList<TreeMap<String,String>>();

        Stack<String> printStrings = new Stack<String>();
        Set<String> previousStates = new TreeSet<>();
        previousStates.add(startVal);
        //Map<String, Double> previousScores = new TreeMap<>();
        TreeMap<String, Double> previousScores = new TreeMap<>();

        previousScores.put(startVal, 0.0);
        // iterate through the words
        for (int j = 0; j<wordArray.length; j++) {
            Set<String> nextStates = new TreeSet<>();
            //Map<String,Double> nextScores = new TreeMap<>();
            //Map<String,String> backPoint = new TreeMap<>();
            TreeMap<String,Double> nextScores = new TreeMap<>();
            TreeMap<String,String> backPoint = new TreeMap<>();
            double score;
            String currMin = "";
            //iterate through the previous states
            for (String st : previousStates){
                //check for the state in the transitions
                if(trans.containsKey(st) && !trans.get(st).isEmpty()){
                    for (String tr : trans.get(st).keySet()){
                        nextStates.add(tr);
                        //change the score, with a penalty if it's not in observations
                        if(observs.containsKey(tr) && observs.get(tr).containsKey(wordArray[j])){
                            score = previousScores.get(st) + trans.get(st).get(tr) + observs.get(tr).get(wordArray[j]);
                        }
                        else {
                            score = previousScores.get(st) + trans.get(st).get(tr) + unseen;
                        }
                        if(!nextScores.containsKey(tr) || score > nextScores.get(tr)){
                            nextScores.put(tr, score);
                            backPoint.put(tr, st);
                            if(backtracking.size() > j){
                                backtracking.remove(j);
                            }
                            backtracking.add(backPoint);
                        }
                    }
                }
            }
            // move on to next scores and states
            previousScores = nextScores;
            previousStates = nextStates;
        }
        // iterate through previous scores, finding the highest score among them
        for(String sco : previousScores.keySet()){
            if(previousScores.get(sco) > highScore){
                highScore = previousScores.get(sco);
                lastTag = sco;
            }
        }

        printStrings.push(lastTag);
        // go backwards through the wordArray
        for(int i = wordArray.length - 1; i > 0; i--){
            printStrings.push(backtracking.get(i).get(printStrings.peek()));
        }
        //pop everything in the stack, if it contains anything
        while(!printStrings.isEmpty()){
            lineOut += printStrings.pop() + " ";
        }

        // return the tagged line
        return lineOut;
    }


}