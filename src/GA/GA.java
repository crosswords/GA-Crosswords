package GA;

import controlers.Generator;
import controlers.GeneratorThread;
import models.Crossword;
import models.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GA{
    final boolean debug = true;
    final int emptyFieldPenalty = 1000;
    final int populationSize;
    final int maxIteration = 10;
    final int maxTime = 600;
    final int mutationIndex = 1;
    final double crossingoverIndex = 2.5;
    final int tournamentIndex = 1;
    private int width, height;
    private List<Crossword> crosswords;

    public GA(int width, int height){
        int processors = Runtime.getRuntime().availableProcessors();
        this.populationSize = processors * 7;
        this.width = width;
        this.height = height;
        this.crosswords = new ArrayList<>();
    }

    public char[][] runGA(){
        while (crosswords.size() < populationSize) {
            crosswords.addAll(initialSolution());
        }
        for (int i=0; i< maxIteration; i++){
            assessAll();
            for (int j=0; j< populationSize * crossingoverIndex; j++)
                crossOver();
            assessAll();
            crosswords = selectBest(populationSize * 2);
            applyHillClimber();
            //mutate();
            assessAll();
            crosswords = selectGeneration();
        }
        printDebugInfo();
        return chooseBestSolution();
    }

    private ArrayList<Crossword> initialSolution(){
        GeneratorThread[] gens = new GeneratorThread[0];
        try {
            gens = Generator.generate(0, height, width);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayList<Crossword> cs = new ArrayList<>();
        for (GeneratorThread gen1 : gens) {
            cs.add(new Crossword(gen1.getMatrix(), gen1.getMaxSizeHorizontal(), gen1.getMaxSizeVertical()));
        }
        return cs;
    }

    private void mutate(){

    }

    private void crossOver(){
        ArrayList<Crossword> parents = findParents();
        Crossword child1 = cross(parents.get(0), parents.get(1));
        Crossword child2 = cross(parents.get(1), parents.get(0));
        crosswords.add(child1);
        crosswords.add(child2);
    }

    private Crossword cross(Crossword mother, Crossword father){
        Crossword child = new Crossword(mother.getHeight(), mother.getWidth());
        for (int y =0; y<mother.getHeight()/2; y++){
            System.arraycopy(mother.matrix[y], 0, child.matrix[y], 0, mother.matrix[y].length);
        }
        for (int y = father.getHeight()/2; y<father.getHeight(); y++){
            System.arraycopy(father.matrix[y], 0, child.matrix[y], 0, father.matrix[y].length);
        }
        child.setFather(father);
        child.setMother(mother);
        child.allWords = child.utils.findAllWords(child);
        ArrayList<Word> wrongWords = child.utils.getWrongWords(child);
        for (Word w : wrongWords){
            child.removeWord(w);
            child.tryInsert(w);
        }
        return child;
    }

    private ArrayList<Crossword> findParents(){
        Random r = new Random();
        ArrayList<Integer> indices = new ArrayList<>();
        for(int i=0;i<populationSize;i++) indices.add(i);
        Crossword father = null;
        Crossword mother = null;
        Crossword another;
        Crossword another2;
        while (mother == father) {
            Collections.shuffle(indices);
            mother = crosswords.get(indices.get(0));
            father = crosswords.get(indices.get(1));
//            another = crosswords.get(indices.get(1));
//            another2 = crosswords.get(indices.get(2));
//            if (mother.getFitness() <= another.getFitness() && mother.getFitness() <= another2.getFitness())
//                father = mother;
//            else if (another.getFitness() <= another2.getFitness() && another.getFitness() <= mother.getFitness())
//                father = another;
//            else
//                father = another2;
//            Collections.shuffle(indices);
//            mother = crosswords.get(indices.get(0));
//            another = crosswords.get(indices.get(1));
//            another2 = crosswords.get(indices.get(2));
//            if (mother.getFitness() <= another.getFitness() && mother.getFitness() <= another2.getFitness())
//                mother = mother;
//            else if (another.getFitness() <= another2.getFitness() && another.getFitness() <= mother.getFitness())
//                mother = another;
//            else
//                mother = another2;
        }
        ArrayList<Crossword> array = new ArrayList<>();
        array.add(mother);
        array.add(father);
        return array;
    }

    private ArrayList<Crossword> selectBest(int count){
        Collections.sort(crosswords, (c1, c2) -> Integer.signum((c1.getMother().getFitness() + c1.getFather().getFitness()) - (c2.getMother().getFitness() + c2.getFather().getFitness())));
        ArrayList<Crossword> newList = new ArrayList<>();
        int i=1;
        int selected = 1;
        newList.add(crosswords.get(0));
        while (selected < count && i < crosswords.size()){
            if (crosswords.get(i).getFitness() == crosswords.get(i-1).getFitness()) {
                //crosswords.remove(i);
                newList.add(crosswords.get(i));
                selected++;
            }
            else{
                newList.add(crosswords.get(i));
                selected ++;
                //i++;
            }
            i++;
        }
        return newList;
    }

    private ArrayList<Crossword> selectGeneration(){
        Collections.sort(crosswords, (c2, c1) -> Integer.signum(c2.getFitness()- c1.getFitness()));
        ArrayList<Crossword> newList = new ArrayList<>();
        for (int i=0; i< populationSize; i++){
            newList.add(crosswords.get(i));
        }
        return newList;
    }

    private Crossword tournamentSelect(){
        return new Crossword();
    }

    private void assessAll(){
        for (Crossword c : crosswords){
            c.setFitness(penaltyEmptySpaces(c));
        }
    }

    private void applyHillClimber(){
        for (Crossword cs : crosswords) {
            cs.applyClimber();
        }
    }

    private char[][] chooseBestSolution(){
        Crossword bestOne = crosswords.get(0);
        for (Crossword c : crosswords){
            if (c.getFitness() < bestOne.getFitness()) bestOne = c;
        }
        return bestOne.matrix;
    }

    private int penaltyEmptySpaces(Crossword crossword){
        int percent=0;
        for(int i=0;i<crossword.getWidth();++i)
            for(int j=0;j<crossword.getHeight();++j){
                if(crossword.matrix[i][j]==' '|| crossword.matrix[i][j]=='\u0000')
                    ++percent;
            }
        return percent * emptyFieldPenalty;
    }

    private void printSolution(Crossword cs){
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (cs.matrix[x][y] == '\u0000') System.out.print(' ');
                else
                    System.out.print(cs.matrix[x][y]);
            }
            System.out.println();
        }
    }

    private void printDebugInfo(){
        if (debug){
            System.out.println("Crosswords: " + String.valueOf(crosswords.size()));
            for (Crossword cs : crosswords){
                System.out.println("Solution fitness: " + cs.getFitness());
                printSolution(cs);
            }
        }
    }
}