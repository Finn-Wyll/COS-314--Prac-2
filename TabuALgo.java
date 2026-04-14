
/**
 * TabuALgo
 */
import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;
import java.util.Random;
import java.io.FileNotFoundException;

public class TabuALgo {

	private ArrayList<Item> knapsack = new ArrayList<>();
	private int maxWeight;
	private int size;
	private Random generator = new Random();
	private int MAX_ITERATIONS = 1000;
	private int iterations = 0;
	private ArrayList<Integer> currentSolution = new ArrayList<>();
	private ArrayList<ArrayList<Integer>> tabuList = new ArrayList<>();
	private int tabuLength = 0;
	private int MAX_TABU_LENGTH = 10;

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

	private void initialize() {
		// set the initial population
		ArrayList<Integer> entity = new ArrayList<>();
		for (int j = 0; j < size; j++) {
			entity.add(generator.nextInt(2));
		}
		currentSolution = entity;
	}

	private int bitFlip(int bit) {
		if (bit == 0) {
			return 1;
		}
		return 0;

	}

	private float evaluate(ArrayList<Integer> solution) {
		int totalValue = 0;
		int totalWeight = 0;
		for (int i = 0; i < solution.size(); i++) {
			if (solution.get(i) == 1) {
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

	private void iterate() {
		int pos = generator.nextInt(size);
		int flippedbit = bitFlip(currentSolution.get(pos));
		ArrayList<Integer> neighbourSolution = new ArrayList<>(currentSolution);
		neighbourSolution.set(pos, flippedbit);
		if (!tabuList.contains(neighbourSolution)) {
			if (tabuLength > MAX_TABU_LENGTH) {
				tabuList.removeFirst();

			}
			tabuList.addLast(neighbourSolution);

		}

		if (evaluate(neighbourSolution) > evaluate(currentSolution)) {
			currentSolution = neighbourSolution;
		}

	}

	public void run() {
		initialize();
		while (iterations < MAX_ITERATIONS) {
			iterate();
			iterations++;
		}
		System.out.println(currentSolution);
	}

}
