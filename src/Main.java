import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class Main {

    private static final String YEAR = "2023";
    private static final String DATA_PATH = "./data" + YEAR + '/';
    private static final String INPUT_SUFFIX = "/data/secret/";
    private static final String INPUT_EXT = ".in";
    private static final String ANSWER_EXT = ".ans";
    private static final String TIME_LIMIT_FILE = ".timelimit";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[] dataDir = Objects.requireNonNull(new File(DATA_PATH).list());
        Character endLetter = Arrays.stream(dataDir).max(Comparator.naturalOrder()).orElse("").charAt(0);
        System.out.printf("Enter the first letter of the problem (%s, a-%c): ", YEAR, endLetter);
        String problemLetter = scanner.next().toLowerCase();
        try {
            String problemDirectory = getProblemDirectory(problemLetter);
            if (problemDirectory == null) {
                System.out.println("Problem not found.");
                return;
            }
            ProblemSolver solver = getSolverForProblem(problemDirectory);
            String tlPath = DATA_PATH + problemDirectory + "/" + TIME_LIMIT_FILE;
            long timeLimitSeconds = Long.parseLong(Files.readAllLines(Paths.get(tlPath)).get(0).trim()) * (long) 1e9;

            ExecutorService executor = Executors.newSingleThreadExecutor();
            File testDir = new File(DATA_PATH + problemDirectory + INPUT_SUFFIX);
            File[] testFiles = testDir.listFiles((dir, name) -> name.endsWith(INPUT_EXT));
            if (testFiles == null || testFiles.length == 0) {
                System.out.println("No test cases found.");
                return;
            }
            long startTime = System.nanoTime();
            int total = 0, success = 0;
            for (File testFile : testFiles) {
                String inputPath = testFile.getPath();
                String testName = testFile.getName().replace(INPUT_EXT, "");
                String answerPath = testFile.getParent() + "/" + testName + ANSWER_EXT;

                List<String> inputLines = Files.readAllLines(Paths.get(inputPath));
                List<String> answerLines = Files.readAllLines(Paths.get(answerPath));
                Future<Pair> future = executor.submit(() -> solver.solve(inputLines));
                total++;
                try {
                    long elapsedTime = (System.nanoTime() - startTime);
                    long remainingTime = Math.max(0, timeLimitSeconds - elapsedTime);

                    Pair solverResult = future.get(remainingTime, TimeUnit.NANOSECONDS);
                    if (checkSolution(problemDirectory, inputLines, answerLines, solverResult)) {
                        System.out.println("Test case " + testName + ": Passed");
                        success++;
                    } else {
                        System.out.println("Test case " + testName + ": Failed");
                        System.out.println("Expected: " + answerLines);
                        System.out.println("Got: " + solverResult);
                    }
                } catch (TimeoutException e) {
                    System.out.println("Test case " + testName + ": Timed out");
                    future.cancel(true);
                } catch (Exception e) {
                    System.out.println("Test case " + testName + ": Error - " + e.getMessage());
                }
            }
            System.out.printf("Total passed: %d / %d \n", success, total);
            System.out.println("Took " + (System.nanoTime() - startTime) / 1e9 + "s.");
            executor.shutdown();
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    private static String getProblemDirectory(String problemLetter) {
        File dataDir = new File(DATA_PATH);
        String[] problemDirs = dataDir.list((dir, name) -> name.startsWith(problemLetter));
        if (problemDirs != null && problemDirs.length > 0) return problemDirs[0];
        return null;
    }

    private static ProblemSolver getSolverForProblem(String problemDirectory) throws Exception {
        char upperCaseProblemLetter = (char) (problemDirectory.charAt(0) - 32);
        String n = "Problem" + upperCaseProblemLetter + YEAR + "Solver";
        return (ProblemSolver) Class.forName(n).getDeclaredConstructor().newInstance();
    }

    private static boolean checkSolution(String probDir, List<String> in, List<String> ans, Pair solverResult) {
        try {
            char upperCaseProblemLetter = (char) (probDir.charAt(0) - 32);
            String n = "Problem" + upperCaseProblemLetter + YEAR + "Validator";
            ProblemValidator v = (ProblemValidator) Class.forName(n).getDeclaredConstructor().newInstance();
            return v.passes(in, ans, solverResult);
        } catch (Exception e) {
            if (ans.size() > 1) return ans.equals(solverResult.resStr());
            try {
                double d = Double.parseDouble(ans.get(0));
                if (solverResult.res() < 1) return Math.abs(d - solverResult.res()) <= 1e-6;
                return Math.abs(d - solverResult.res()) / solverResult.res() <= 1e-6;
            } catch (Exception ee) {
                return ans.equals(solverResult.resStr());
            }
        }
    }
}