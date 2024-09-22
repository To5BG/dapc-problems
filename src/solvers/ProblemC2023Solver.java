import java.util.Scanner;

public class ProblemC2023Solver {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int a = sc.nextInt(), b = sc.nextInt(), c = sc.nextInt();
        int f = 0, state = 0;
        while (++f > 0) {
            if (sc.next().equals("end")) break;
            if (f * a >= b + f * c && state == 0) state = 1;
            if (state == 0) {
                System.out.println("airline");
            } else if (state == 1) {
                System.out.println("buy");
                state = 2;
            } else System.out.println("self");
        }
    }
}
