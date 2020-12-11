package edu.uwb.css143b2020fall.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IndexerImpl implements Indexer {
    public Map<String, List<List<Integer>>> index(List<String> docs) {
        Map<String, List<List<Integer>>> indexes = new HashMap<>();
        for(int docIndex = 0; docIndex<docs.size(); docIndex++){


            //remove leading and trailing whitespace from document
            String toBeSplit = docs.get(docIndex);
            toBeSplit = toBeSplit.trim();
            //if the document is empty, then continue to the next document
            if(toBeSplit.length()==0){
                continue;
            }

            //split the document into an array with each string being a word from the document
            String[] split = toBeSplit.split("\\s+");

            //outer array traverses each word in the document
            for(int wordIndex = 0; wordIndex<split.length; wordIndex++){
                /*if our indexes hashmap doesnt already contain this word
                make a new list of integer lists
                add as many integer lists to the indexList as there are documents to be indexed
                    (if docs has a length of 5, then indexList will also have a length of 5)
                 */
                if(!indexes.containsKey(split[wordIndex])) {
                    List<List<Integer>> indexList = new ArrayList<List<Integer>>();
                    for (int docSizeCounter = 0; docSizeCounter < docs.size(); docSizeCounter++) {
                        indexList.add(new ArrayList<Integer>());
                    }
                    indexes.put(split[wordIndex], indexList);
                }

                //get the indexList for this word
                List<List<Integer>> keyList = indexes.get(split[wordIndex]);
                //get the position list for this doc
                List<Integer> positionList = keyList.get(docIndex);
                //add this position to that position list
                positionList.add(wordIndex);

            }
        }
        return indexes;
    }
}