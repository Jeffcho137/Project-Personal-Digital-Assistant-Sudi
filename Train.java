/**
 @author Jeff Cho and Gia Kim Fall 2019
 Class of methods that "train" the computer to recognize parts of speech, given training files
 */

import java.util.*;
import java.io.*;

public class Train{
    //STATES = parts of speech tags
    //TRANSITIONS = tag to tag, with weights
    //OBSERVATIONS = tag to word, with weights

    /**
     * Method to create a Map keeping track of the number of times of each observation
     *
     * @param wordsPathName    location of file containing the total string of training sentence data.
     * @param posPathName      location of file containing the parts of speech of every word in the sentence file
     * @return    a HashMap of observations in which each Key is a String of a word in the file,
     *           and each Value is another Map, containing all of the parts of speech in the file as
     *           Keys and the Integer of the number of times a given word is used as a given part of speech
     */
    public static HashMap<String, HashMap<String, Double>> keepTrack(String wordsPathName, String posPathName) throws IOException{
        //initialize BufferedReaders and ap to put observations in
        BufferedReader posInput = null;
        BufferedReader wordInput = null;

        HashMap<String, HashMap<String, Double>> obs = new HashMap<String, HashMap<String, Double>>();
        try{
            //try to open files
            posInput = new BufferedReader(new FileReader(posPathName));
            wordInput = new BufferedReader(new FileReader(wordsPathName));
            String pLine = posInput.readLine();
            String wLine = wordInput.readLine();
            //While there are more lines in each of the given files
            while (wLine != null && pLine != null){
                //Lowercase the sentence file, and split both on white space
                wLine = wLine.toLowerCase();
                //posLine = posLine.toLowerCase();
                String[] wordsPerLine = wLine.split(" ");
                String[] posPerLine = pLine.split(" ");
                //Checks for '#' character to see if it is in map
                //checks first word in sentence to see if it is in inner map
                if (obs.containsKey("#")){
                    HashMap<String, Double> wordAndCounts = new HashMap<String, Double>();
                    wordAndCounts = obs.get("#");
                    if (wordAndCounts.containsKey(wordsPerLine[0])){
                        Double num = wordAndCounts.get(wordsPerLine[0]) +1;
                        wordAndCounts.put(wordsPerLine[0], num);
                        obs.put("#", wordAndCounts);
                    }
                    else{
                        wordAndCounts.put(wordsPerLine[0], 1.0);
                        obs.put("#", wordAndCounts);
                    }
                }
                else{
                    HashMap<String, Double> map = new HashMap<String, Double>();
                    map.put(wordsPerLine[0], 1.0);
                    obs.put("#", map);
                }
                for (int i = 0; i < wordsPerLine.length-1; i ++){
                    HashMap<String, Double> wordsCounts = new HashMap<String, Double>();
                    if (obs.containsKey(posPerLine[i])){
                        wordsCounts = obs.get(posPerLine[i]);

                        if (wordsCounts.containsKey(wordsPerLine[i])){
                            Double num = wordsCounts.get(wordsPerLine[i]) + 1;
                            wordsCounts.put(wordsPerLine[i], num);
                        }
                        else{
                            wordsCounts.put(wordsPerLine[i], 1.0);
                        }
                    }
                    else{
                        wordsCounts.put(wordsPerLine[i], 1.0);
                    }
                    obs.put(posPerLine[i], wordsCounts);
                }
                pLine = posInput.readLine();
                wLine = wordInput.readLine();
            }
        }
        //Catch exceptions
        catch (IOException e){
            e.printStackTrace();
        }
        //close files
        finally{
            wordInput.close();
            posInput.close();
        }
        //return created map
        return obs;
    }


    public static HashMap<String, HashMap<String, Double>> keepTrack2(String pathName) throws IOException{
        BufferedReader input = null;
        HashMap<String, HashMap<String, Double>> transitions = new HashMap<String, HashMap<String, Double>>();

        try{
            input = new BufferedReader(new FileReader(pathName));
            String line = input.readLine();
            while (line != null){
                String[] states = line.split(" ");
                for (int i = 0; i < states.length-1; i ++){
                    if (i == 0){
                        if (transitions.containsKey("#")){
                            HashMap<String, Double> current = transitions.get("#");
                            if (current.containsKey(states[0])){
                                double newval = current.get(states[0]) + 1;
                                current.put(states[0], newval);
                                transitions.put("#", current);
                            }
                            else{
                                current.put(states[0], 1.0);
                                transitions.put("#", current);
                            }
                        }
                        else{
                            HashMap<String, Double> val = new HashMap<String, Double>();
                            val.put(states[0], 1.0);
                            transitions.put("#", val);
                        }
                    }
                    //set nextState = to the next state in line
                    String nextState = states[i+1];
                    if (transitions.containsKey(states[i])){
                        HashMap<String, Double> current = transitions.get(states[i]);
                        if (current.containsKey(nextState)){
                            current.put(nextState, current.get(nextState) + 1);
                            transitions.put(states[i], current);
                        }
                        else{
                            current.put(nextState, 1.0);
                            transitions.put(states[i], current);
                        }
                    }
                    else{
                        HashMap<String, Double> val = new HashMap<String, Double>();
                        val.put(nextState, 1.0);
                        transitions.put(states[i], val);
                    }
                }
                line = input.readLine();
            }
        }
        //catch exceptions
        catch (IOException e){
            e.printStackTrace();
        }
        //close the file
        finally{
            input.close();
        }
        //return the map
        return transitions;
    }

    /**
     * Method that converts what was just a simple count of instances of an observation or transitions
     * into a log base e probability

    \  */
    public static HashMap<String, HashMap<String, Double>> logConversion(HashMap<String, HashMap<String, Double>> map){
        for (String key : map.keySet()){
            HashMap<String,Double> innerMap = map.get(key);
            Double total = 0.0;
            for (String key2: innerMap.keySet()){
                total += innerMap.get(key2);
            }
            for (String key2: innerMap.keySet()){
                innerMap.put(key2, Math.log(innerMap.get(key2) / total));
            }
        }
        //return new map
        return map;
    }


}