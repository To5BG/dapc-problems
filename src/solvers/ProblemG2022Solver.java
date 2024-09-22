import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProblemG2022Solver {
    static String[] firstGuesses = new String[]{"10597", "24683"};
    static int fi = 0;

    public static void main(String[] args) {
        Set<String> primes = new HashSet<>();
        for (int i = 10001; i < 99999; i++) if (isPrime(i)) primes.add(String.valueOf(i));

        Scanner sc = new Scanner(System.in);
        int rounds = sc.nextInt();
        for (int r = 0; r < rounds; r++) {
            Set<String> currPrimes = new HashSet<>(primes);
            for (int j = 0; j < 6; j++) {
                String nextGuess = nextGuess(currPrimes);
                System.out.println(nextGuess);
                System.out.flush();
                String res = sc.next();
                if (res.equals("ggggg")) {
                    fi = 0;
                    break;
                }
                updatePrimes(primes, currPrimes, nextGuess, res);
            }
        }
    }

    public static boolean isPrime(int n) {
        for (int i = 2; i <= Math.sqrt(n); i++) if (n % i == 0) return false;
        return true;
    }

    public static void updatePrimes(Set<String> primes, Set<String> currPrimes, String guess, String res) {
        currPrimes.remove(guess);
        List<String> forbiddenStr = new ArrayList<>(), necessaryStr = new ArrayList<>();
        Map<Integer, List<Integer>> green = new HashMap<>();
        Map<Integer, Integer> greenCount = new HashMap<>(), yellowCount = new HashMap<>(), whiteCount = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            char c = res.charAt(i);
            int d = guess.charAt(i) - 48;
            if (c == 'g') {
                if (!green.containsKey(d)) green.put(d, new ArrayList<>());
                greenCount.merge(d, 1, Integer::sum);
                green.get(d).add(i);
            } else if (c == 'y') {
                yellowCount.merge(d, 1, Integer::sum);
                forbiddenStr.add(".".repeat(i) + d + ".".repeat(4 - i));
            } else whiteCount.merge(d, 1, Integer::sum);
        }
        for (Map.Entry<Integer, List<Integer>> e : green.entrySet()) {
            StringBuilder sb = new StringBuilder(".".repeat(5));
            for (int i : e.getValue()) sb.setCharAt(i, (char) (e.getKey() + 48));
            necessaryStr.add(sb.toString());
        }
        for (Map.Entry<Integer, Integer> e : yellowCount.entrySet()) {
            int count = e.getValue() + greenCount.getOrDefault(e.getKey(), 0);
            necessaryStr.add((".*" + e.getKey()).repeat(count) + ".*");
        }
        for (Map.Entry<Integer, Integer> e : whiteCount.entrySet()) {
            int count = e.getValue() + greenCount.getOrDefault(e.getKey(), 0) + yellowCount.getOrDefault(e.getKey(), 0);
            forbiddenStr.add((".*" + e.getKey()).repeat(count) + ".*");
        }
        List<Pattern> forbidden = forbiddenStr.stream().map(Pattern::compile).toList();
        List<Pattern> necessary = necessaryStr.stream().map(Pattern::compile).toList();
        outer:
        for (String n : primes) {
            if (!currPrimes.contains(n)) continue;
            for (Pattern p : necessary) {
                Matcher mat = p.matcher(n);
                if (!mat.find()) {
                    currPrimes.remove(n);
                    continue outer;
                }
            }
            for (Pattern p : forbidden) {
                Matcher mat = p.matcher(n);
                if (mat.find()) {
                    currPrimes.remove(n);
                    break;
                }
            }
        }
    }

    public static String nextGuess(Set<String> currPrimes) {
        if (fi < 2) return firstGuesses[fi++];
        Random rand = new Random();
        int randomIndex = rand.nextInt(currPrimes.size());
        Iterator<String> iter = currPrimes.iterator();
        for (int i = 0; i < randomIndex; i++) iter.next();
        return iter.next();
    }
}
