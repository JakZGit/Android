package com.google.engedu.ghost;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.lang.*;
public class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line = null;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH)
              words.add(line.trim());
        }

        Collections.sort(words);
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {
        if(prefix.length()==0){
            Random rand = new Random();
            int n = rand.nextInt(words.size()-1);
            return words.get(n);
        }
        else if(BinarySearch(prefix,0,words.size()-1)!=""){
            Log.d("IT IS NOTHING", "getAnyWordStartingWith: " + BinarySearch(prefix,0,words.size()));
            return BinarySearch(prefix,0,words.size());
        }
        else return null;
    }


    public String BinarySearch(String s,int first, int last) {
        int middle = (first + last) / 2;
        if(last < first) {
            return "";
        }
        if (words.get(middle).startsWith(s)) {
            return words.get(middle);//.substring(0, s.length()+1);
        } else if (s.compareTo(words.get(middle)) < 0) {
            return BinarySearch(s, first, middle-1);
        } else {
            return BinarySearch(s, middle+1, last);
        }

    }




    @Override
    public String getGoodWordStartingWith(String prefix) {

        String s = BinarySearch(prefix, 0, words.size() - 1);
        if (prefix.length() == 0) {
            Random rand = new Random();
            int n = rand.nextInt(words.size() - 1);
            return words.get(n);
        }

//        if(s == "")
//            return "";
        else if (s != "") {
            ArrayList<String> temp = new ArrayList<String>();
            int index = words.indexOf(s);
            temp.add(s);
            while (index >= 0 && words.get(index).startsWith(prefix)) {
                temp.add(words.get(index));
                index--;
            }
            index = words.indexOf(s);
            while (index < words.size() && words.get(index).startsWith(prefix)) {
                temp.add(words.get(index));
                index++;
            }
//       for(int i = 0;i<temp.size();i++){
//            System.out.println(temp.get(i));
//       }
            Random rand = new Random();
            int n = rand.nextInt(temp.size() - 1);
            //System.out.println("Size of list: "+ temp.size()+ " .Index: " + n+ " .String " +temp.get(n));
            return temp.get(n);
        }
        else return null;
    }
}
