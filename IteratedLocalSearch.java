import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

public class IteratedLocalSearch
{
    private ArrayList<Item> knapsack = new ArrayList<>();
    private double maxWeight;
    private int size;

    private final int ILS_ITERATIONS = 200;
    private final int TABU_ITERATIONS = 100;
    private final int TABU_LIST_SIZE  = 7;
    private final int PERTURBATION_STRENGTH = 3;

    private Random generator;

    private void load(String filePath)
    {
        File loadFile = new File(filePath);
        try (Scanner loader = new Scanner(loadFile))
        {
            this.size = loader.nextInt();
            this.maxWeight = loader.nextDouble();
            while (loader.hasNextDouble())
            {
                double value = loader.nextDouble();
                double weight = loader.nextDouble();
                knapsack.add(new Item(value, weight));
            }
        }
        catch (FileNotFoundException e)
        {
            System.err.println("Could not load file: " + filePath);
        }
    }

    private double getFitness(ArrayList<Integer> solution)
    {
        double totalValue = 0;
        double totalWeight = 0;
        for (int i = 0; i < solution.size(); i++)
        {
            if (solution.get(i) == 1)
            {
                totalValue += knapsack.get(i).value;
                totalWeight += knapsack.get(i).weight;
            }
        }
        if (totalWeight > maxWeight || totalWeight == 0) return 0;
        return totalValue;
    }

    private ArrayList<Integer> generateInitialSolution()
    {
        ArrayList<Integer> solution = new ArrayList<>();
        for (int i = 0; i < size; i++)
        {
            solution.add(generator.nextInt(2));
        }
        return solution;
    }

    private ArrayList<Integer> localSearch(ArrayList<Integer> startSolution)
    {
        LinkedList<ArrayList<Integer>> L = new LinkedList<>();
        ArrayList<Integer> s = new ArrayList<>(startSolution);

        for (int iter = 0; iter < TABU_ITERATIONS; iter++)
        {
            ArrayList<Integer> sPrime = new ArrayList<>(s);
            int idx = generator.nextInt(size);
            sPrime.set(idx, sPrime.get(idx) == 0 ? 1 : 0);

            if (!L.contains(sPrime))
            {
                if (L.size() >= TABU_LIST_SIZE)
                {
                    L.removeFirst();
                }
                L.addLast(new ArrayList<>(sPrime));
            }

            if (getFitness(sPrime) > getFitness(s))
            {
                s = sPrime;
            }
        }

        return s;
    }

    private ArrayList<Integer> perturbation(ArrayList<Integer> sStar)
    {
        ArrayList<Integer> sPrime = new ArrayList<>(sStar);
        for (int i = 0; i < PERTURBATION_STRENGTH; i++)
        {
            int idx = generator.nextInt(size);
            sPrime.set(idx, sPrime.get(idx) == 0 ? 1 : 0);
        }
        return sPrime;
    }

    private ArrayList<Integer> acceptanceCriterion(ArrayList<Integer> sStar,
                                                    ArrayList<Integer> sStarStar)
    {
        if (getFitness(sStarStar) >= getFitness(sStar))
        {
            return sStarStar;
        }
        return sStar;
    }

    public double run(String filePath, long seed)
    {
        this.generator = new Random(seed);
        knapsack.clear();
        load(filePath);

        long startTime = System.currentTimeMillis();

        ArrayList<Integer> s1 = generateInitialSolution();
        ArrayList<Integer> sStar = localSearch(s1);

        ArrayList<Integer> best = new ArrayList<>(sStar);
        double bestFitness = getFitness(best);

        for (int iter = 0; iter < ILS_ITERATIONS; iter++)
        {
            ArrayList<Integer> sPrime = perturbation(sStar);
            ArrayList<Integer> sStarStar = localSearch(sPrime);
            sStar = acceptanceCriterion(sStar, sStarStar);

            if (getFitness(sStar) > bestFitness)
            {
                bestFitness = getFitness(sStar);
                best = new ArrayList<>(sStar);
            }
        }

        long endTime = System.currentTimeMillis();
        double runtime = (endTime - startTime) / 1000.0;

        System.out.printf("ILS | File: %-25s | Seed: %d | Best: %.2f | Time: %.3fs%n",
                filePath, seed, bestFitness, runtime);

        return bestFitness;
    }
}
