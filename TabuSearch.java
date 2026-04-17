import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class TabuSearch
{
    private ArrayList<Item> knapsack = new ArrayList<>();
    private double maxWeight;
    private int size;

    private final int MAX_ITERATIONS = 1000;
    private int TABU_TENURE;

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

    public double run(String filePath, long seed)
    {
        this.generator = new Random(seed);
        knapsack.clear();
        load(filePath);

        TABU_TENURE = Math.max(5, (int) Math.sqrt(size));

        long startTime = System.currentTimeMillis();

        int[] tabuList = new int[size];

        ArrayList<Integer> current = generateInitialSolution();
        ArrayList<Integer> best = new ArrayList<>(current);
        double bestFitness = getFitness(best);

        for (int iter = 0; iter < MAX_ITERATIONS; iter++)
        {
            int bestMoveIndex = -1;
            double bestNeighborFitness = Double.NEGATIVE_INFINITY;

            for (int i = 0; i < size; i++)
            {
                ArrayList<Integer> neighbor = new ArrayList<>(current);
                neighbor.set(i, neighbor.get(i) == 0 ? 1 : 0);
                double neighborFitness = getFitness(neighbor);

                boolean isTabu = tabuList[i] > 0;
                boolean aspirationMet = neighborFitness > bestFitness;

                if (!isTabu || aspirationMet)
                {
                    if (neighborFitness > bestNeighborFitness)
                    {
                        bestNeighborFitness = neighborFitness;
                        bestMoveIndex = i;
                    }
                }
            }

            if (bestMoveIndex == -1)
            {
                int minTabu = Integer.MAX_VALUE;
                for (int i = 0; i < size; i++)
                {
                    if (tabuList[i] < minTabu)
                    {
                        minTabu = tabuList[i];
                        bestMoveIndex = i;
                    }
                }
            }

            current = new ArrayList<>(current);
            current.set(bestMoveIndex, current.get(bestMoveIndex) == 0 ? 1 : 0);

            tabuList[bestMoveIndex] = TABU_TENURE;

            for (int i = 0; i < size; i++)
            {
                if (tabuList[i] > 0) tabuList[i]--;
            }

            double currentFitness = getFitness(current);
            if (currentFitness > bestFitness)
            {
                bestFitness = currentFitness;
                best = new ArrayList<>(current);
            }
        }

        long endTime = System.currentTimeMillis();
        double runtime = (endTime - startTime) / 1000.0;

        System.out.printf("TS  | File: %-25s | Seed: %d | Best: %.2f | Time: %.3fs%n",
                filePath, seed, bestFitness, runtime);

        return bestFitness;
    }
}
