import java.util.List;

public interface ProblemValidator {
    boolean passes(List<String> input, List<String> answer, Pair inputData);
}
