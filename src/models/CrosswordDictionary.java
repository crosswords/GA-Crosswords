package models;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by tomek on 29.04.14.
 */
public class CrosswordDictionary implements ICrosswordDictionary {

    private String[] filenames = new String[] {"datasets/bestNouns.csv"};

    private static Set<IEntry> entries;

    public ArrayList<String> usableEntries;

    @Override
    public Set<IEntry> allEntries() {
        return getEntries();

    }

    public Set<IEntry> getEntries() {
        if(entries == null){
            loadEntries();
        }
        return entries;
    }

    public void loadEntries() {
        entries = new HashSet<IEntry>();
        for(String p: filenames){
            try {
                List<String> strings = Files.readAllLines(FileSystems.getDefault().getPath(p), Charset.defaultCharset());
                for(String line: strings){
                    if(line.matches(".*,.*")){
                        Entry entry = new Entry();
                        entry.setWord(line.split(",")[0]);
                        entry.setClues(
                                Arrays.asList(
                                        line.substring(entry.getWord().length()).split("\",\"")
                                )
                        );
                        entries.add(entry);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        usableEntries = new ArrayList<>();
        usableEntries.addAll(entries.stream().map(IEntry::getWord).collect(Collectors.toList()));
    }
}
