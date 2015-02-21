package GA;

import models.*;

import java.util.ArrayList;
import java.util.Objects;

public class WordUtils {
    public CrosswordDictionary dict;

    public WordUtils(){
        this.dict = new CrosswordDictionary();
        dict.loadEntries();
    }

    public ArrayList<Word> getWrongWords(Crossword crossword){
        ArrayList<Word> wrongWords = new ArrayList<>();
        boolean found = false;
        for (Word w : crossword.allWords){
            found = false;
            for (IEntry e : dict.allEntries()){
                if (Objects.equals(e.getWord(), w.getWord())){
                    found = true;
                    break;
                }
            }
            if (!found) wrongWords.add(w);
        }
        return wrongWords;
    }

    public ArrayList<Word> findAllWords(Crossword crossword){
        ArrayList<Word> allWords = new ArrayList<>();
        int x=0, y=0;
        boolean[][] checkedVerticalMatrix = new boolean[crossword.getWidth()][crossword.getHeight()];
        boolean[][] checkedHorizontalMatrix = new boolean[crossword.getWidth()][crossword.getHeight()];
        while (x*y<crossword.getHeight() * crossword.getWidth()){
            if(crossword.matrix[x][y]!=' '&& crossword.matrix[x][y]!='\u0000') {
                if (!checkedHorizontalMatrix[x][y]) {
                    Word w = getWord(crossword, x, y, Direction.Horizontal);
                    allWords.add(w);
                    for (int i=x; i<x+w.getWord().length(); i++) checkedHorizontalMatrix[i][y] = true;
                }
                if (!checkedVerticalMatrix[x][y]) {
                    Word w = getWord(crossword, x, y, Direction.Vertical);
                    allWords.add(w);
                    for (int i=y; i<y+w.getWord().length(); i++) checkedVerticalMatrix[x][i] = true;
                }
            }
            if (x == crossword.getWidth()-1){
                y++;
                x = 0;
            } else x++;
            if (y==crossword.getHeight()) break;
        }
        return allWords;
    }

    public Word getWord(Crossword cs, int x, int y, Direction direction){
        StringBuilder sb = new StringBuilder();
        if (direction == Direction.Horizontal){
            int i=x;
            while (i< cs.getWidth() && cs.matrix[i][y] != ' ' && cs.matrix[i][y]!='\u0000'){
                sb.append(cs.matrix[i][y]);
                i++;
            }
        } else{
            int i=y;
            while (i<cs.getHeight() && cs.matrix[x][i] != ' ' && cs.matrix[x][i]!='\u0000'){
                sb.append(cs.matrix[x][i]);
                i++;
            }
        }
        return new Word(sb.toString(), "", direction, x, y);
    }
}
