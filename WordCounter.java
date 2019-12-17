
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
public class WordCounter {
    public static final Path FOLDER_OF_TEXT_FILES  = Paths.get("WordCounterFiles"); // path to the folder where input text files are located
    public static final Path WORD_COUNT_TABLE_FILE = Paths.get("..."); // path to the output plain-text (.txt) file
    public static final int  NUMBER_OF_THREADS     = 2;                // max. number of threads to spawn
    public static void main(String... args) {
        try {
            int numThreads = NUMBER_OF_THREADS;
            File fileDirectory = FOLDER_OF_TEXT_FILES.toFile();
            File[] listOfFiles = fileDirectory.listFiles();
            List<TreeMap<String, Integer>> wordCounts = new ArrayList<TreeMap<String, Integer>>();

            if(NUMBER_OF_THREADS > listOfFiles.length){
                numThreads = listOfFiles.length;
            }

            ArrayList<Threader> listofThreads = new ArrayList<Threader>(numThreads);
            for(int i = 0; i < listOfFiles.length; i++) {
                Threader thread = new Threader(listOfFiles[i]);
                listofThreads.add(thread);
                thread.start();
            }
            for(Threader thread: listofThreads) {
                try {
                    thread.join();
                    wordCounts.add(thread.getWc());
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
            TreeMap<String, Integer> totalWC = new TreeMap<String, Integer>();
            for(TreeMap<String,Integer> wcount: wordCounts) {
                for(Map.Entry<String, Integer> entry: wcount.entrySet()) {
                    String iteratedWord = entry.getKey();
                    int iteratedCount = entry.getValue();
                    Integer count = totalWC.get(iteratedWord);
                    totalWC.put(iteratedWord, count == null ? iteratedCount : count + iteratedCount);
                }

            }
            int longestKey = 0;
            for(String key : totalWC.keySet()){
                if (key.length() > longestKey){
                    longestKey = key.length();
                }
            }
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < longestKey + 1; i++){
                sb.append(" ");
            }
            for(int i = 0; i < listOfFiles.length; i++){
                sb.append(listOfFiles[i].getName());
                sb.append(" ");
            }
            sb.append("Total");
            sb.append("\n");
            for(String key : totalWC.keySet()){
                sb.append(key);
                for(int i = 0; i < longestKey + 1 - key.length(); i++){
                    sb.append(" ");
                }
                for(int i = 0; i < wordCounts.size(); i++){
                    TreeMap<String, Integer> yeet = wordCounts.get(i);
                    sb.append(yeet.getOrDefault(key,0));
                    for(int j = 0; j < listOfFiles[i].getName().length() + 1 - yeet.getOrDefault(key, 0).toString().length(); j++){
                        sb.append(" ");
                    }
                }
                sb.append(totalWC.get(key));
                sb.append("\n");
            }
            PrintWriter pw = new PrintWriter(WORD_COUNT_TABLE_FILE.toFile());
            pw.print(sb.toString());
            pw.close();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }

    }
       static class Threader extends Thread{
            File filename;
            TreeMap<String, Integer> wc;
            public Threader(File filename){
                this.filename = filename;
                this.wc = new TreeMap<String, Integer>();
            }

            public TreeMap<String, Integer> getWc(){
                return this.wc;
            }
            public void run(){
                try{
                    BufferedReader br = new BufferedReader(new FileReader(filename));
                    String line;
                    while((line = br.readLine())!= null){
                        for(String word: line.split("\\s+")){
                            word = word.replaceAll("[^a-zA-Z ]", "").toLowerCase();
                            Integer count = wc.get(word);
                            wc.put(word, count == null ? 1 : count + 1);
                        }
                    }
                    br.close();
                }
                catch (Exception ex){
                    System.out.println("IOException");
                }
            }
        }
    }
