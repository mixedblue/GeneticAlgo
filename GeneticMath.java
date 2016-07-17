import java.util.*;
import java.io.*;
import java.lang.*;
public class GeneticMath {
    public static void main(String args[]) {
        int maxTries = 0;
        int goal = 0;
        int populationSize = 0;
        int iterations = 0;
        while(maxTries < 100) {
            try {
                Scanner scan = new Scanner(System.in);
                System.out.println("Enter goal number: ");
                goal = scan.nextInt();
                maxTries = 100;
            }
            catch(Exception e) {
                System.out.println("Wrong input for goal");
                maxTries++;
            }
        }
        maxTries = 0;
        while(maxTries < 100) {
            try {
                Scanner scan = new Scanner(System.in);
                System.out.println("Enter number of iterations: ");
                iterations = scan.nextInt();
                maxTries = 100;
            }
            catch(Exception e) {
                System.out.println("Wrong input for iterations");
                maxTries++;
            }
        }
        maxTries = 0;
        while(maxTries < 100) {
            try {
                Scanner scan = new Scanner(System.in);
                System.out.println("Enter population size: ");
                populationSize = scan.nextInt();
                if(populationSize > 10) {
                    maxTries = 100;
                }
                else {
                    maxTries++;
                    System.out.println("Must be greater than 10");
                }
            }
            catch(Exception e) {
                System.out.println("Wrong input for populationSize");
                maxTries++;
            }
        }
        String answer = genetic(goal, iterations, populationSize);
        System.out.println("After "+iterations+" iterations and "+populationSize+" population size, the solution is: \n" + answer);
    }
    public static String genetic(int goal, int iterations, int populationSize) {
        Map<Integer, String> map = new HashMap<Integer, String>();
        for(int i = 0; i <= 9; i++) {
            map.put(binary(i), String.valueOf(i));
        }
        map.put(1010, "+");
        map.put(1011, "-");
        map.put(1100, "*");
        map.put(1101, "/");
        ArrayList<int[]> chromosomes = new ArrayList<int[]>();
        for(int i = 0; i < populationSize; i++) {
            int[] created = create();
            if(chromosomes.contains(created)) {
                i--;
            }
            else {
                chromosomes.add(create());
            }
        }
        for(int i = 0; i < iterations; i++) {
            chromosomes = iterate(chromosomes, map, populationSize, goal);
            if(chromosomes.size() == 1) {
                return returnString(chromosomes, map, goal);
            }
        }
        return returnString(chromosomes, map, goal);
    }
    public static String returnString(ArrayList<int[]> chromosomes, Map<Integer, String> map, int goal) {
        double maxFit = -1.0;
        int[] bestChromo = new int[0];
        double bestDecode = 0.0;
        for(int q = 0; q < chromosomes.size(); q++) {
            double d = decode(chromosomes.get(q), map);
            double f = fitness(d, goal);
            if(f > maxFit) {
                maxFit = f;
                bestChromo = chromosomes.get(q);
                bestDecode = d;
            }
        }
        String translated = "";
        ArrayList<String> str = decodeHelp(bestChromo, map);
        for(int i = 0; i < str.size(); i++) {
            translated += str.get(i);
        }
        String chro = "";
        for(int i = 0; i < bestChromo.length; i++) {
            chro += bestChromo[i];
        }
        return "Result: " + translated + " = " + bestDecode + " with chromosome: " + chro;
    }
    public static ArrayList<int[]> iterate(ArrayList<int[]> newList, Map<Integer, String> map, int populationSize, int goal) {
        Map<int[], Double> fitMap = new HashMap<int[], Double>();
        for(int w = 0; w < populationSize; w++) {
            fitMap.put(newList.get(w), fitness(decode(newList.get(w), map), goal));
        }
        if(fitMap.containsValue(0.0)) {
            ArrayList<int[]> goalArr = new ArrayList<int[]>();
            Set<int[]> keys = fitMap.keySet();
            int[] bestChromo = new int[0];
            for(int[] key: keys) {
                if(fitMap.get(key) == 0.0) {
                    bestChromo = key;
                }
            }
            goalArr.add(bestChromo);
            return goalArr;
        }
        ArrayList<int[]> sortedFitList = new ArrayList<int[]>();
        fitMap.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).forEachOrdered(entry -> sortedFitList.add(entry.getKey()));
        ArrayList<int[]> topTenList = new ArrayList<int[]>();
        int length =  (int)Math.ceil(sortedFitList.size()/10.0);
        int i = 0;
        int c = 0;
        while(c < length) {
            if(Math.random() > 0.25) {
                topTenList.add(sortedFitList.get(i));
                c++;
            }
            i++;
            if(i == sortedFitList.size()) {
                i = 0;
            }
        }
        int cc = 0;
        int ii = 1;
        int enough = 0;
        ArrayList<int[]> newPopulation = new ArrayList<int[]>();
        while(cc < (populationSize - length) && enough < populationSize*5) {
            int j = 0;
            while(cc < (populationSize - length) && j < ii && enough < populationSize*5) {
                int[] crossed = crossover(topTenList.get(ii), topTenList.get(j));
                enough++;
                if(!newPopulation.contains(crossed)) {
                    newPopulation.add(crossed);
                    cc++;
                }
                j++;
            }
            ii++;
            if(ii == topTenList.size()) {
                ii = 0;
            }
        }
        if(enough == populationSize*5) {
            while(newPopulation.size() < populationSize) {
                newPopulation.add(create());
            }
        }
        for(int q = 0; q < newPopulation.size(); q++) {
            if(Math.random() < .001) {
                int ind = (int) Math.floor(newPopulation.get(q).length*Math.random());
                if(newPopulation.get(q)[ind] == 0) {
                    newPopulation.get(q)[ind] = 1;
                }
                else {
                    newPopulation.get(q)[ind] = 0;
                }
            }
        }
        return newPopulation;
    }
    public static int[] crossover(int[] a, int[] b) {
        int[] cross = new int[0];
        if(a.length > b.length) {
            cross = a;
            int crossIndex = (int) Math.floor(b.length*Math.random());
            while(crossIndex < b.length && crossIndex < cross.length) {
                cross[crossIndex] = b[crossIndex];
                crossIndex++;
            }
        }
        else {
            cross = b;
            int crossIndex = (int) Math.floor((a.length-1)*Math.random());
            while(crossIndex < a.length && crossIndex < cross.length) {
                cross[crossIndex] = a[crossIndex];
                crossIndex++;
            }
        }
        return cross;
    }
    public static int[] create() {
        int length = (int)(200*Math.random()) + 4;
        int[] array = new int[length];
        for(int i = 0; i < length; i++) {
            double random = Math.random();
            if(random < 0.5) {
                array[i] = 0;
            }
            else {
                array[i] = 1;
            }
        }
        return array;
    }
    public static double fitness(double n, int goal) {
        if(n == goal) {
            return 0.0;
        }
        double fitness = 1.0/(goal-n);
        return fitness;
    }
    public static ArrayList<String> decodeHelp(int[] random, Map<Integer, String> map) {
        ArrayList<String> list = new ArrayList<String>();
        for(int i = 0; i < random.length-3; i+=4) {
            int bit = random[i]*1000 + random[i+1]*100 + random[i+2]*10 + random[i+3];
            if(map.containsKey(bit)) {
                list.add(map.get(bit));
            }
        }
        boolean lookingForNumber = true;
        for(int i = 0; i < list.size(); i++) {
            if(lookingForNumber) {
                try {
                    Integer.parseInt(list.get(i));
                    lookingForNumber = false;
                }
                catch(NumberFormatException e) {
                    list.remove(i);
                    i--;
                }
            }
            else {
                try {
                    Integer.parseInt(list.get(i));
                    list.remove(i);
                    i--;
                }
                catch(NumberFormatException e) {
                    lookingForNumber = true;
                }
            }
        }
        if(lookingForNumber && list.size() > 0) {
            list.remove(list.size()-1);
        }
        for(int i = 0; i < list.size()-1; i++) {
            if(list.get(i).equals("/") && list.get(i+1).equals("0")) {
                list.remove(i);
                list.remove(i);
                i--;
            }
        }
        return list;
    }
    public static double decode(int[] random, Map<Integer, String> map) {
        ArrayList<String> list = decodeHelp(random, map);
        if(list.size() == 0) {
            return -Double.MAX_VALUE;
        }
        while(list.size() > 2) {
            double c;
            double a = Double.parseDouble(list.get(0));
            double b = Double.parseDouble(list.get(2));
            if(list.get(1).equals("+")) {
                c = a+b;
            }
            else if(list.get(1).equals("-")) {
                c = a-b;
            }
            else if(list.get(1).equals("*")) {
                c = a*b;
            }
            else {
                c = a/b;
            }
            list.remove(0);
            list.remove(0);
            list.remove(0);
            list.add(0, String.valueOf(c));
        }
        return Double.parseDouble(list.get(0));
    }
    public static int binary(int n) {
        int binary = 0;
        double temp = n;
        while(temp > 0) {
            int index = (int)Math.floor(Math.log(temp)/Math.log(2));
            binary += Math.pow(10, index);
            temp -= Math.pow(2, index);
        }
        return binary;
    }   
}