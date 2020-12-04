package edu.uwb.css143b2020fall.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearcherImpl implements Searcher {
    public List<Integer> search(String keyPhrase, Map<String, List<List<Integer>>> index) {
        List<Integer> result = new ArrayList<>();
        /*
        split the keyphrase into words by space

        check which documents have all the words from the keyPhrase (rule out the ones that dont have all the given words in keyPHrase)

        find the occurance of the first word of an input in a document, check if the next words are the words we need

        for each common document, get the location index of each word

        determine whether search words are in the correct order


        to find intersections, add all unique values to a hashmap
         */
        return result;
    }
}