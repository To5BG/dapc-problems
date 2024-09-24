import java.util.*;

@SuppressWarnings("all")
public class ProblemG2022Solver {
    // Guessing primes - Guess the hidden 5-digit number in at most 6 guesses
    // Naively guessing will occasionally use more than 6 guesses, so we start with two guesses that use all digits
    // Generate all primes in a hashset, and reduce the space based on the white/yellow/green indicators
    // One option is to store the guesses as strings, and use regex to reduce the space
    // We have both 'necessary' and 'forbidden' patterns, and check each number on all of these
    // More performant -> Store primes in a map of contained digits to set of numbers
    // Update candidate digits at each position separately after each guess
    static String[] firstGuesses = new String[]{"10597", "24683"};
    static final int DIGITS = 5;
    static final int TOTAL_ROUNDS = 6;

    public static void main(String[] args) {
        Map<Set<Character>, Set<String>> primes = new HashMap<>();
        for (int i = (int) Math.pow(10, DIGITS - 1); i <= (int) Math.pow(10, DIGITS); i++)
            if (isPrime(i)) {
                Set<Character> chars = new HashSet<>();
                String number = String.valueOf(i);
                for (int d = 0; d < DIGITS; d++) chars.add(number.charAt(d));
                if (!primes.containsKey(chars)) primes.put(chars, new HashSet<>());
                primes.get(chars).add(number);
            }
        Scanner sc = new Scanner(System.in);
        int rounds = sc.nextInt();
        round:
        for (int r = 0; r < rounds; r++) {
            List<Set<Character>> charSet = new ArrayList<>();
            charSet.add(new HashSet<>(Set.of('1', '2', '3', '4', '5', '6', '7', '8', '9')));
            for (int i = 0; i < DIGITS - 2; i++)
                charSet.add(new HashSet<>(Set.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')));
            charSet.add(new HashSet<>(Set.of('1', '3', '5', '7', '9')));
            Set<Character> possibleChars = new HashSet<>();
            for (int i = 0; i < firstGuesses.length; i++) {
                String guess = firstGuesses[i];
                System.out.println(guess);
                System.out.flush();
                String res = sc.next();
                if (res.equals("ggggg")) continue round;
                for (int j = 0; j < DIGITS; j++) if (res.charAt(j) != 'w') possibleChars.add(guess.charAt(j));
                updateSet(charSet, guess, res);
            }
            Set<String> candidates = new HashSet<>(primes.get(possibleChars));
            updateCandidates(candidates, charSet);
            for (int j = 0; j < TOTAL_ROUNDS - firstGuesses.length; j++) {
                String nextGuess = candidates.iterator().next();
                System.out.println(nextGuess);
                System.out.flush();
                String res = sc.next();
                if (res.equals("ggggg")) break;
                updateSet(charSet, nextGuess, res);
                updateCandidates(candidates, charSet);
            }
        }
    }

    public static boolean isPrime(int n) {
        for (int i = 2; i <= Math.sqrt(n); i++) if (n % i == 0) return false;
        return true;
    }

    public static void updateSet(List<Set<Character>> charSet, String guess, String res) {
        int[] doesNotContainDigitFlag = new int[10];
        for (int i = 0; i < DIGITS; i++) {
            char c = res.charAt(i), d = guess.charAt(i);
            if (c == 'w') {
                if (doesNotContainDigitFlag[d - 48] == 0) doesNotContainDigitFlag[d - 48] = 1;
                charSet.get(i).remove(d);
            } else if (c == 'y') {
                doesNotContainDigitFlag[d - 48] = -1;
                charSet.get(i).remove(d);
            } else {
                doesNotContainDigitFlag[d - 48] = -1;
                for (char j = 48; j < 58; j++) if (j != d) charSet.get(i).remove(j);
            }
        }
        for (int i = 0; i < 10; i++)
            if (doesNotContainDigitFlag[i] == 1)
                for (int j = 0; j < DIGITS; j++) charSet.get(j).remove(i);
    }

    public static void updateCandidates(Set<String> candidates, List<Set<Character>> charSet) {
        List<String> remove = new ArrayList<>();
        for (String c : candidates) {
            for (int i = 0; i < DIGITS; i++) {
                if (!charSet.get(i).contains(c.charAt(i))) {
                    remove.add(c);
                    break;
                }
            }
        }
        for (String c : remove) candidates.remove(c);
    }
}
