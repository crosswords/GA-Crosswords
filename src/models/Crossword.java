package models;

import GA.WordUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Random;

public class Crossword{
    int height, width;
    final int emptyFieldPenalty = 1000;
    public char[][] matrix;
    public ArrayList<Word> allWords;
    Crossword mother, father;
    int fitness;
    public WordUtils utils;

    public Crossword(){
        this.fitness = -1;
        allWords = new ArrayList<>();
        utils = new WordUtils();
        this.mother = this;
        this.father = this;
    }

    public Crossword(char[][] matrix, int w, int h){
        this.matrix = matrix;
        this.setHeight(h);
        this.setWidth(w);
        this.fitness = -1;
        this.utils = new WordUtils();
        allWords = utils.findAllWords(this);
        this.mother = this;
        this.father = this;
    }

    public Crossword(int h, int w){
        this.setHeight(h);
        this.setWidth(w);
        this.fitness = -1;
        this.utils = new WordUtils();
        this.matrix = new char[w][h];
        this.mother = this;
        this.father = this;
    }

    public void removeWord(Word word){
        Direction d = word.getDirection();
        if (d.equals(Direction.Horizontal)){
            for (int i=0; i< word.getWord().length(); i++)
                this.matrix[word.getPoint().x + i][word.getPoint().y] = ' ';
        } else{
            for (int i=0; i< word.getWord().length(); i++)
                this.matrix[word.getPoint().x][word.getPoint().y + i] = ' ';
        }
        allWords.remove(word);
    }

    public void addWord(Word word){
        Direction d = word.getDirection();
        if (d.equals(Direction.Horizontal)){
            for (int i=0; i< word.getWord().length(); i++)
                this.matrix[word.getPoint().x + i][word.getPoint().y] = word.getWord().charAt(i);
        } else{
            for (int i=0; i< word.getWord().length(); i++)
                this.matrix[word.getPoint().x][word.getPoint().y + i] = word.getWord().charAt(i);
        }
        allWords.add(word);
    }

    public void tryInsert(Word w){
        Word newOne = findWordInParent(w);
        if (newOne.getWord() != "") {
            Boolean inserted = checkIfValidInsert(newOne);
            if (inserted) return;
        }
    }

    private Word findWordInParent(Word w){
        for (Word wo : father.allWords){
            if ((wo.getWord().startsWith(w.getWord())) || (wo.getWord().endsWith(w.getWord())))
                return wo;
        }
        for (Word wo : mother.allWords){
            if ((wo.getWord().startsWith(w.getWord())) || (wo.getWord().endsWith(w.getWord())))
                return wo;
        }
        return new Word("", "");
    }

    private boolean checkIfValidInsert(Word w) {
        int x = w.getPoint().x;
        int y = w.getPoint().y;
        Direction d = w.getDirection();
        String word = w.getWord();
        for (int i = 0; i < word.length(); i++) {
            if (d == Direction.Horizontal) {
                if (x+i >= width)
                    return false;
                if (!((matrix[x + i][y] == ' ') || (matrix[x+i][y] == '\u0000') || (matrix[x + i][y] == word.charAt(i))))
                    return false;
            } else {
                if (y+i >= height)
                    return false;
                if (!((matrix[x][y + i] == ' ') || (matrix[x][y + i] == '\u0000') || (matrix[x][y + i] == word.charAt(i))))
                    return false;
            }
        }
        addWord(w);
        return true;
    }

    public void applyClimber(){
        int x,y;
        Random r = new Random();
        String regex;
        String tempRegex;
        for (int j = 0; j < 10; j++) {
            x = 0;
            y = 0;
            while (matrix[x][y] != ' ' && matrix[x][y] != '\u0000') {
                x = r.nextInt(width);
                y = r.nextInt(height);
            }
            ArrayList<Integer> wordLengths = new ArrayList<>();
            for (int i = 3; i < 9; i++)
                wordLengths.add(i);
            Collections.shuffle(wordLengths);
            for (Integer wordLength : wordLengths) {
                regex = findIntersections(x, y, wordLength, Direction.Horizontal);
                if (regex == "")
                    continue;
                for (String s : utils.dict.usableEntries) {
                    tempRegex = regex.substring(0, Math.min(s.length(), regex.length()));
                    tempRegex += "\\w*";
                    if ((s.length() == wordLength) && (s.matches(tempRegex))) {
                        checkIfValidInsert(new Word(s, "", Direction.Horizontal, x, y));
                        return;
                    }
                }
                regex = findIntersections(x, y, wordLength, Direction.Vertical);
                if (regex == "")
                    continue;
                for (String s : utils.dict.usableEntries) {
                    tempRegex = regex.substring(0, Math.min(s.length(), regex.length()));
                    tempRegex += "\\w*";
                    if ((s.length() == wordLength) && (s.matches(tempRegex))) {
                        checkIfValidInsert(new Word(s, "", Direction.Vertical, x, y));
                        return;
                    }
                }
            }
        }
    }

    private String findIntersections(int x, int y, int length, Direction d){
        String regex = "";
        for (int i=0; i< length; i++){
            if (d == Direction.Horizontal) {
                if (x+i >= width)
                    return "";
                if (!((matrix[x + i][y] == ' ') || (matrix[x+i][y] == '\u0000')))
                    regex = regex.concat(String.valueOf(matrix[x+i][y]));
                else
                    regex = regex.concat(".");
            } else {
                if (y+i >= height)
                    return "";
                if (!((matrix[x][y + i] == ' ') || (matrix[x][y + i] == '\u0000')))
                    regex = regex.concat(String.valueOf(matrix[x][y+i]));
                else
                    regex = regex.concat(".");
            }
        }
        if (d == Direction.Horizontal){
            if (x+length >= width)
                return regex;
            if (!((matrix[x + length][y] == ' ') || (matrix[x+length][y] == '\u0000')))
                regex = "";
        }
        if (d == Direction.Vertical){
            if (y+length >= height)
                return regex;
            if (!((matrix[x][y+ length] == ' ') || (matrix[x][y+ length] == '\u0000')))
                regex = "";
        }
        return regex;
    }

    public Crossword getMother() {
        return mother;
    }

    public void setMother(Crossword mother) {
        this.mother = mother;
    }

    public Crossword getFather() {
        return father;
    }

    public void setFather(Crossword father) {
        this.father = father;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getFitness() {
        if (this.fitness >= 0)
            return fitness;
        else{
            return penaltyEmptySpaces();
        }
    }

    private int penaltyEmptySpaces(){
        int percent=0;
        for(int i=0;i<this.getWidth();++i)
            for(int j=0;j<this.getHeight();++j){
                if(this.matrix[i][j]==' '|| this.matrix[i][j]=='\u0000')
                    ++percent;
            }
        return percent * emptyFieldPenalty;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }
}