package edu.uwb.css143b2020fall.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SearcherImpl implements Searcher {
    public List<Integer> search(String keyPhrase, Map<String, List<List<Integer>>> index) {

        System.out.println("keyPhrase = " + keyPhrase + ", index = " + index);
        //split the keyphrase into words by space
        keyPhrase = keyPhrase.trim();
        
        //if the query is empty, return an empty list
        if(keyPhrase.length()==0){
            return new ArrayList<Integer>();
        }
        String[] phraseArray = keyPhrase.split("\\s+");

        //if any of the words in the query don't show up in any of the documents at all, return an empty list
        if(!allWordsAppear(index, phraseArray)){
            return new ArrayList<Integer>();
        }

        //documents contains one integer list for each word. The integer list is comprised of the document locations for that word
        List<List<Integer>> documents = getDocuments(index, phraseArray);

        //get the documents that have all the words in the query
        List<Integer> commonDocuments = getCommonDocs(documents);

        //get the locations of each word in the documents where all words are found
        HashMap<Integer, List<List<Integer>>> locationLists = getLocationLists(index, phraseArray, commonDocuments);

        List<Integer> result = findValidDocs(locationLists, commonDocuments);
        /*

        //for the each document in the common documents list
        //  go down the query (starting from the first word)
        //  get its location in the document, add it to an int list
        //  if all the documents are have a location difference of one ex. (3,4,5). then it is the correct order

        determine whether search words are in the correct order


        to find intersections, add all unique values to a hashmap
         */
        return result;
    }

    private boolean allWordsAppear(Map<String, List<List<Integer>>> index, String[] phraseArray){
        for(int i =0; i<phraseArray.length; i++){
            if(!index.containsKey(phraseArray[i])){
                return false;
            }
        }
        return true;
    }

    private List<Integer> findValidDocs(HashMap<Integer, List<List<Integer>>> locationLists, List<Integer> commonDocuments) {
        List<Integer> result = new ArrayList<>();
        //the following nested loop does the integer math for the documents
        //each loop iteration is one document
        for (int documentIndex = 0; documentIndex < locationLists.size(); documentIndex++) {
            List<List<Integer>> document = locationLists.get(commonDocuments.get(documentIndex));
            //each loop iteration cycles through all the words (int lists), of a document
            for (int queryIndex = 0; queryIndex < document.size(); queryIndex++) {
                List<Integer> aWordIndexes = document.get(queryIndex);

                //each loop iteration subtracts the current index in the list from the location value of that word in a document
                //after this subtraction, of any document has a common value for all words, then we know that this document is a valid result for the search query
                for (int indexIndex = 0; indexIndex < aWordIndexes.size(); indexIndex++) {
                    int index = aWordIndexes.get(indexIndex);
                    aWordIndexes.set(indexIndex, index - queryIndex);
                }
            }
        }
        for(int doc: commonDocuments){
            if(isValidDoc(locationLists,doc)){
                result.add(doc);
            }
        }
        return result;
    }

    private boolean isValidDoc(HashMap<Integer, List<List<Integer>>> locationLists, int docNum) {
        List<Integer> validNumList = locationLists.get(docNum).get(0); //get the first word's numbers
        List<List<Integer>> document = locationLists.get(docNum);
        boolean validSoFar = true;
        for (int num : validNumList) {
            validSoFar=true;
            //starting at 1, because we know word 0 has the possible values we are looking for
            for (int currentWordIndex = 1; currentWordIndex < document.size(); currentWordIndex++) {
                if (!document.get(currentWordIndex).contains(num)) {
                    //if any word doesnt contain a valid index, stop checking this num.
                    validSoFar = false;
                    break;
                }
            }
        }
        return validSoFar;
    }

    private HashMap<Integer, List<List<Integer>>> getLocationLists(Map<String, List<List<Integer>>> index, String[] phrase, List<Integer> commonDocuments) {
        //<Document number, {{word 1 indexes}, {word 2 indexes}, etc.}
        HashMap<Integer, List<List<Integer>>> locationLists = new HashMap<Integer, List<List<Integer>>>();

        //for each word in phrase
        for (String word : phrase) {
            System.out.println("word = " + word);
            List<List<Integer>> thisWord = index.get(word);
            System.out.println(word+ " = " + thisWord);
            //for each doc in common docs
            for (int thisDocumentID : commonDocuments) {
                System.out.println("thisDocumentID = " + thisDocumentID);
                if (!locationLists.containsKey(thisDocumentID)) {
                    locationLists.put(thisDocumentID, new ArrayList<List<Integer>>());
                }
                List<List<Integer>> thisDocLocationList = locationLists.get(thisDocumentID);
                thisDocLocationList.add(new ArrayList<Integer>(thisWord.get(thisDocumentID)));
                System.out.println("locationLists.get(thisDocumentID) = " + locationLists.get(thisDocumentID));
            }
        }
        System.out.println("locationLists = " + locationLists);
        return locationLists;
    }

    private List<Integer> getCommonDocs(List<List<Integer>> documents) {
        Set<Integer> common = new HashSet<Integer>();
        //if we have any words in the query, add the first word's locations to the set
        if(documents.size()>0){
            common.addAll(new ArrayList<Integer>(documents.get(0)));
        }
        //cycles through the documents (each int list is one word, each val in the int list is the document location for that word)
        for (int outer = 1; outer < documents.size(); outer++) {
            List<Integer> innerDoc = documents.get(outer);
            //get the intersection with the next word
            common.retainAll(new ArrayList<Integer>(innerDoc));
        }
        //add all nums from our set to the an ArrayList of common doc numbers
        List<Integer> commonDocIds = new ArrayList<Integer>(common);
        System.out.println("commonDocIds = " + commonDocIds);
        return commonDocIds;
    }

    private List<List<Integer>> getDocuments(Map<String, List<List<Integer>>> index, String[] phraseArray) {
        //first list corresponds to the word, inner list corresponds to the documents that each word is in
        List<List<Integer>> documents = new ArrayList<List<Integer>>();

        //make documents the length of the number of words in the query
        for (int queryLength = 0; queryLength < phraseArray.length; queryLength++) {
            documents.add(new ArrayList<Integer>());
        }

        //for each word in the keyPhrase
        for (int phraseWordIndex = 0; phraseWordIndex < phraseArray.length; phraseWordIndex++) {
            //TODO: only do this .size once (before this for loop)
            //TODO: OR only call index.get once per word in the query
            //System.out.println(index.toString());
            //System.out.println("phraseWordIndex = " + phraseWordIndex);
            System.out.println("phraseArray[PhraseWordIndex] = " + phraseArray[phraseWordIndex]);
            int numDocuments = index.get(phraseArray[phraseWordIndex]).size();
            //for each document position list for this word in the index hashtable
            for (int documentIndex = 0; documentIndex < numDocuments; documentIndex++) {
                //get the current document position list
                List<Integer> documentPosList = index.get(phraseArray[phraseWordIndex]).get(documentIndex);
                //if this position list has at least one instance of the word
                if (docHasWord(phraseArray[phraseWordIndex], documentPosList)) {
                    //add it to the integer list for this word
                    List<Integer> thisWordDocuments = documents.get(phraseWordIndex);
                    thisWordDocuments.add(documentIndex);
                }
            }
        }
        return documents;
    }

    private boolean docHasWord(String word, List<Integer> document) {
        return document.size() > 0;
    }
}