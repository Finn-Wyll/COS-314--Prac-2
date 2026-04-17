import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter seed value: ");
        long seed = sc.nextLong();
        sc.close();

        String[] instances = {
            "f1_l-d_kp_10_269",
            "f2_l-d_kp_20_878",
            "f3_l-d_kp_4_20",
            "f4_l-d_kp_4_11",
            "f5_l-d_kp_15_375",
            "f6_l-d_kp_10_60",
            "f7_l-d_kp_7_50",
            "f8_l-d_kp_23_10000",
            "f9_l-d_kp_5_80",
            "f10_l-d_kp_20_879"
        };

        System.out.println("\n=== Genetic Algorithm ===");
        GeneticAlgo ga = new GeneticAlgo();
        for (String instance : instances) {
            ga.run(instance, seed);
        }

        System.out.println("\n=== Iterated Local Search (Tabu) ===");
        IteratedLocalSearch ils = new IteratedLocalSearch();
        for (String instance : instances) {
            ils.run(instance, seed);
        }
    }
}
