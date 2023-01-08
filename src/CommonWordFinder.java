/**
 * Ben Alexander UNI: baa2165
 *
 * A program to count word frequency in a text document.
 *
 */

import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UnknownFormatConversionException;


public class CommonWordFinder {

    //The word-frequency pair that occurs most frequently
    static Pair<String, Integer> mostFrequent = new Pair("", 0);
    static int _rank = 1;
    static int _limit = 20000;
    static int _longestWordLength = 0;
    static int _maxDigits = 1;

    /**
     * Puts all the words into a hashmap
     * Then iterates through the hashmap
     * to print the words out in order of most to least frequent.
     * @param hashMap a hashMap data structure
     */
    public static void hashWordFrequency(MyHashMap hashMap, BufferedWriter writer) {

        System.out.println("Total unique words: " + hashMap.size());

        int rank = 1;
        int size = hashMap.size();
        _maxDigits = (int)Math.log10(size);
        int lengthOfLimitString = String.valueOf(_limit).length();
        if (_maxDigits > lengthOfLimitString) {
            //prepended spaces for cosmetic purposes
            _maxDigits = lengthOfLimitString;
        }
        while (rank <= size) {

            Entry highestCandidate = new Entry<String, Integer>(null, 0);
            Iterator itr;
            itr = hashMap.iterator();
            //iterate through the entire hashmap to find the most frequent element
            while (itr.hasNext()) {
                Entry entry = (Entry) itr.next();
                if ((int)entry.value > (int)highestCandidate.value) {
                    highestCandidate = entry;
                }
            }
            //iterate again through the entire hashmap to find all of the most frequent elements
            // and then remove them as they are added to an array to be sorted
            itr = hashMap.iterator();
            String[] sorter = new String[10];
            int i = 0;
            int freq = 0;
            while (itr.hasNext() && _limit > 0) {
                Entry entry = (Entry) itr.next();
                if ((int) entry.value == (int) highestCandidate.value) {
                    String wordKey = ((String) entry.key);
                    freq = (int)entry.value;
                    hashMap.remove((String) entry.key);
                    rank++;
                    _limit--;


                    sorter[i] = wordKey;
                    i++;
                    if (i >= sorter.length) {
                        sorter = Arrays.copyOf(sorter, sorter.length * 2);
                    }

                    if (_limit == 0) {
                        size = 0;
                        break;
                    }

                }
            }
            int k = -1;
            for (String elem : sorter) {
                if (elem == null) {
                    break;
                }
                k++;
            }

            if (k > 0) {
                //sort the elements before adding them so that they are in alphabetical order.
                quickSort(sorter, 0, k);
            }

            //print the contents of sorter adding spaces as needed
            for (int j = 0; j < sorter.length && sorter[j] != null; j++) {
                int spacesAmt = _maxDigits - String.valueOf(_rank).length();
                String preSpaces = "";
                while (spacesAmt >= 0) {
                    preSpaces += " ";
                    spacesAmt--;
                }
                String postSpaces = "";
                spacesAmt = _longestWordLength - sorter[j].length();
                while (spacesAmt > 0) {
                    postSpaces += " ";
                    spacesAmt--;
                }
                try {
                    System.out.format(preSpaces + _rank + ". " + sorter[j] + postSpaces + " " + freq + System.lineSeparator());
                    try {
                        writer.append(preSpaces + _rank + ". " + sorter[j] + postSpaces + " " + freq + "\n");
                    } catch (IOException e) {
                        System.err.println("File not written to.");
                    }
                    _rank++;
                } catch (UnknownFormatConversionException e) {

                }

            }
        }
    }

    /*
    * A standard quicksort src: https://www.geeksforgeeks.org/quick-sort/
     */
    public static void quickSort(String[] array, int start, int end) {
        if (start >= end) {
            return;
        }


        String pivot = array[end];
        int partitionIndex = start;


        for (int i = start; i < end; i++) {

            if (array[i] != null && array[i].compareTo(pivot) < 0) {
                swap(array, i, partitionIndex);
                partitionIndex++;
            }
        }


        swap(array, partitionIndex, end);

        quickSort(array, start, partitionIndex - 1);
        quickSort(array, partitionIndex + 1, end);
    }

    public static void swap(String[] array, int i, int j) {
        String temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    public static void main(String[] args) {
        boolean character_mode = true;
        int num_args = args.length;
        if (num_args < 1 || num_args > 4) {
            System.err.println("Usage: java CommonWordFinder <filename> <optional filename> [char|word] [limit]");
            System.exit(1);
        }
        String fileName = args[0];
        String outputFile = "out.txt";
        String type = args[1];
        int limit = 20000;
        if (args.length == 4) {
            outputFile = args[1];
            if (args[2].equals("word")) {
                character_mode = false;
            }
            try {
                limit = Integer.parseInt(args[3]);
                _limit = limit;
            } catch (NumberFormatException e) {
                System.err.println("Error: Invalid limit '" + args[2] + "' received.");
                System.exit(1);
            }
        }
        else if (args.length == 3) {
            if (args[1].equals("word")) {
                character_mode = false;
            } else if (args[1].equals("char")) {
                character_mode = true;
            } else {
                outputFile = args[1];
            }
            try {
                limit = Integer.parseInt(args[2]);
                _limit = limit;
            } catch (NumberFormatException e) {
                if (args[2].equals("word")) {
                    character_mode = false;
                } else if (args[2].equals("char")) {
                    character_mode = true;
                } else {
                    System.err.println("Error: Invalid limit '" + args[2] + "' received.");
                    System.exit(1);
                }
            }

        } else if (args.length == 2) {
            _limit = 10;
            if (args[1].equals("word")) {
                character_mode = false;
            } else if (args[1].equals("char")) {
                character_mode = true;
            } else {
                try {
                    limit = Integer.parseInt(args[1]);
                    _limit = limit;
                } catch (NumberFormatException e) {
                    outputFile = args[1];

                }
            }

        } else if (args.length == 1) {
            _limit = 10;
        }

        MyMap dataStruct;


        try {

            FileReader fr = new FileReader(args[0]);
            BufferedReader br = new BufferedReader(fr);

            int b;
            StringBuilder word = new StringBuilder();

            dataStruct = new MyHashMap<String, Integer>();

            if (character_mode == false) {

                while ((b = br.read()) != -1) {
                    //System.out.print((char)c);
                    if (b >= 'A' && b <= 'Z') {
                        b += 32; //use lowercase
                    }
                    boolean isSpaceChar = (b == ' ' || b == '　' || b == '\t' || b == '\n' || b == '.' || b == ',' || b == '「' || b == '」' || b == '？' ||
                            b == '!' || b == '！' || b == '?' || b == '。' || b == '、' || b == ':' || b == '：' || b == '，' || b == ';' || b == '；' ||
                            b == '(' || b == ')' || b == '[' || b == ']' || b == '【' || b == '】' || b == '\r'); //true if among allowable characters


                    if (!isSpaceChar) {
                        word.append((char) b);
                    } else if (isSpaceChar && !word.isEmpty() || b == '\0') {

                        String w = word.toString(); //gets string from word (byte array)
                        if (w.length() > _longestWordLength) {
                            _longestWordLength = w.length(); //keep track of longest word for pretty printing.
                        }
                        while (w.length() >= 1 && w.charAt(0) == '-') {
                            //removes leading hyphens
                            w = w.substring(1);
                        }
                        Object f = dataStruct.get(w); //word frequency
                        if (f == null) {
                            f = 0;
                        }
                        int freq = (int) f;
                        if (w.length() > 0) {
                            f = dataStruct.put(w, freq + 1);
                        }
                        word = new StringBuilder();
                    }
                }
            } else {

                while ((b = br.read()) != -1) {
                    //System.out.print((char)c);
                    if (b >= 'A' && b <= 'Z') {
                        b += 32; //use lowercase
                    }
                    boolean isSpaceChar = (b == ' ' || b == '　' || b == '\t' || b == '\n' || b == '.' || b == ',' || b == '「' || b == '」' || b == '？' ||
                            b == '!' || b == '！' || b == '?' || b == '。' || b == '、' || b == ':' || b == '：' || b == '，' || b == ';' || b == '；' ||
                            b == '(' || b == ')' || b == '[' || b == ']' || b == '【' || b == '】' || b == '\r'); //true if among allowable characters

                    if (!isSpaceChar) {
                        Object f = dataStruct.get("" + (char) b); //word frequency
                        if (f == null) {
                            f = 0;
                        }
                        int freq = (int) f;
                        dataStruct.put("" + (char) b, freq + 1);
                    }
                }
            }


            br.close();
            fr.close();

            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

            hashWordFrequency((MyHashMap) dataStruct, writer);

            writer.close();

        } catch (IOException e) {
            System.err.println("Error: Cannot open file '" + args[0] + "' for input.");
        } catch (IOError e) {
            System.err.println("Error: An I/O error occurred reading '" + args[0] + "'.");
        }

    }
}
