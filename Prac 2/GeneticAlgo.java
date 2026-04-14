import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.io.FileNotFoundException;

public class GeneticAlgo {
	private ArrayList<Item> knapsack = new ArrayList<>();
	private int MAX_ITERATIONS = 1000;
	private int iterations = 0;
	private int maxWeight;
	private int size;
	private final int POP_SIZE = 16;
	private final float MUTATION_RATE = 0.2f;
	private final float CROSSOVER_RATE = 0.7f;
	private Random generator = new Random();
	private ArrayList<ArrayList<Integer>> population = new ArrayList<>();

	private void load(String filePath) {
		File loadFile = new File(filePath);
		try (Scanner loader = new Scanner(loadFile)) {
			this.size = loader.nextInt();
			this.maxWeight = loader.nextInt();
			while (loader.hasNextInt()) {
				int weight = loader.nextInt();
				int value = loader.nextInt();
				knapsack.add(new Item(value, weight));
			}

		} catch (FileNotFoundException e) {
			System.err.println("could not load file");
		}
	}

	private ArrayList<Integer> crossover(ArrayList<Integer> parent1, ArrayList<Integer> parent2) {

		if (generator.nextFloat() <= CROSSOVER_RATE) {
			ArrayList<Integer> child = new ArrayList<>();
			int splitPoint = generator.nextInt(parent1.size());
			for (int i = 0; i < splitPoint; i++) {
				child.add(parent1.get(i));
			}
			for (int i = splitPoint; i < parent2.size(); i++) {
				child.add(parent2.get(i));
			}
			return child;
		}
		return parent1;

	}

	private ArrayList<Integer> mutate(ArrayList<Integer> child) {
		if (generator.nextFloat() <= MUTATION_RATE) {
			int mutationPoint = generator.nextInt(child.size());
			ArrayList<Integer> newChild = new ArrayList<Integer>(child);
			int current = child.get(mutationPoint);
			newChild.set(mutationPoint, current == 0 ? 1 : 0);
			return newChild;

		}
		return child;
	}

	private float getFitness(ArrayList<Integer> child) {
		int totalValue = 0;
		int totalWeight = 0;
		for (int i = 0; i < child.size(); i++) {
			if (child.get(i) == 1) {
				totalValue += knapsack.get(i).value;
				totalWeight += knapsack.get(i).weight;

			}
		}
		if (totalWeight > maxWeight) {
			return 0;

		}
		if (totalWeight == 0)
			return 0;
		return (float) totalValue;
	}

	private ArrayList<Integer> select() {
		ArrayList<Integer> best = null;
		float bestFitness = -1;
		for (int i = 0; i < 3; i++) {
			int randomIndex = generator.nextInt(population.size());
			ArrayList<Integer> candidate = population.get(randomIndex);
			float fitness = getFitness(candidate);
			if (fitness > bestFitness) {
				bestFitness = fitness;
				best = candidate;
			}
		}
		return best;
	}

	private void iterate() {
		ArrayList<ArrayList<Integer>> newPopulation = new ArrayList<>();
		while (newPopulation.size() < POP_SIZE) {
			ArrayList<Integer> parent1 = select();
			ArrayList<Integer> parent2 = select();
			ArrayList<Integer> child = mutate(crossover(parent1, parent2));
			newPopulation.add(child);

		}
		population = newPopulation;
		iterations++;

	}

	private void initialize() {
		// set the initial population
		population.clear();
		for (int i = 0; i < POP_SIZE; i++) {
			ArrayList<Integer> chromosome = new ArrayList<>();
			for (int j = 0; j < size; j++) {
				chromosome.add(generator.nextInt(2));
			}
			population.add(chromosome);
		}
	}

	public void run() {

		load("f8_l-d_kp_23_10000");
		System.out.println("Size: " + size);
		System.out.println("Weight: " + maxWeight);
		initialize();
		while (iterations < MAX_ITERATIONS) {
			this.iterate();

		}
		ArrayList<Integer> best = null;
		float bestFitness = -1;
		for (ArrayList<Integer> chromosome : population) {
			float fitness = getFitness(chromosome);
			if (fitness > bestFitness) {
				bestFitness = fitness;
				best = chromosome;
			}
		}
		System.out.println("Best chromosome: " + best);
		System.out.println("Best fitness: " + bestFitness);

	}
}
