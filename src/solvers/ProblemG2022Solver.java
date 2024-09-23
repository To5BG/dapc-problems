import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("all")
public class ProblemG2022Solver {
    // Guessing primes - Guess the hidden 5-digit number in at most 6 guesses
    // Naively guessing will occasionally use more than 6 guesses, so we start with two guesses that use all digits
    // Generate all primes in a hashset, and reduce the space based on the white/yellow/green indicators
    // One option is to store the guesses as strings, and use regex to reduce the space
    // We have both 'necessary' and 'forbidden' patterns, and check each number on all of these
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
        Map<Integer, Integer> greenC = new HashMap<>(), yellowC = new HashMap<>(), whiteC = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            char resultChar = res.charAt(i);
            int guessDigit = guess.charAt(i) - 48;
            if (resultChar == 'g') {
                if (!green.containsKey(guessDigit)) green.put(guessDigit, new ArrayList<>());
                green.get(guessDigit).add(i);
                greenC.merge(guessDigit, 1, Integer::sum);
            } else if (resultChar == 'y') {
                yellowC.merge(guessDigit, 1, Integer::sum);
                // Cannot have the digit over a yellow mark
                forbiddenStr.add(".".repeat(i) + guessDigit + ".".repeat(4 - i));
            } else whiteC.merge(guessDigit, 1, Integer::sum);
        }
        for (Map.Entry<Integer, List<Integer>> e : green.entrySet()) {
            StringBuilder sb = new StringBuilder(".".repeat(5));
            for (int i : e.getValue()) sb.setCharAt(i, (char) (e.getKey() + 48));
            // Must have the digit over green marks
            necessaryStr.add(sb.toString());
        }
        for (Map.Entry<Integer, Integer> e : yellowC.entrySet()) {
            int count = e.getValue() + greenC.getOrDefault(e.getKey(), 0);
            // Must contain the digit as many times as (yellow + green) marks
            necessaryStr.add((".*" + e.getKey()).repeat(count) + ".*");
        }
        for (Map.Entry<Integer, Integer> e : whiteC.entrySet()) {
            int count = e.getValue() + greenC.getOrDefault(e.getKey(), 0) +
                    yellowC.getOrDefault(e.getKey(), 0);
            // Must not have the digit as many times as (white + yellow + green) marks
            // Aka if no green or yellow, just one white, then it is not contained at all (<=> cannot have it once)
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
