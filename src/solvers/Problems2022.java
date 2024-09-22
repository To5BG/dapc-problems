import java.util.*;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
class ProblemA2022Solver implements ProblemSolver {
    // Abbreviated Aliases - given aliases, calculate total length of shortest unique prefixes
    // Add all elements to a modified Trie -> each node contains all elements that contain same prefix so far
    // e.g. "ab", "ac", "aban" will form the Trie (root -> "a" -> leaf {"b","c","ban"})
    // When adding prefix, check if leaf nodes has it (e.g. "abac" will lead to new nodes "b" -> leaf {"ac", "an"})
    // Alternative: sort aliases, then compare each to its prev and next one to find its unique alias
    public Pair solve(List<String> inputData) {
        int n = Integer.parseInt(inputData.get(0).split(" ")[0]);
        Trie t = new Trie();
        for (int i = 1; i <= n; i++) t.addChild(new StringBuilder(inputData.get(i)), 0);
        int totalPrefixLength = IntStream.rangeClosed(1, n).map(i -> traverse(t, inputData.get(i), 0)).sum();
        return new Pair((double) totalPrefixLength, null);
    }

    public int traverse(Trie t, String s, int i) {
        if (i == s.length() || t == null) return i;
        return traverse(t.children[s.charAt(i) - 97], s, i + 1);
    }

    static class Trie {
        Trie[] children = new Trie[26];
        List<StringBuilder> ctexts = new ArrayList<>();

        public void addChild(StringBuilder word, int pos) {
            if (pos == word.length()) return;
            char c = word.charAt(pos);
            if (children[c - 97] != null) children[c - 97].addChild(word, pos + 1);
            else {
                boolean f = false;
                for (int i = 0; i < ctexts.size(); i++) {
                    StringBuilder sb = ctexts.get(i);
                    if (sb.charAt(0) == c) {
                        f = true;
                        if (children[c - 97] == null) children[c - 97] = new Trie();
                        children[c - 97].ctexts.add(sb.deleteCharAt(0));
                        ctexts.remove(i--);
                    }
                }
                if (f) children[c - 97].addChild(word, pos + 1);
                else ctexts.add(new StringBuilder(word.substring(pos)));
            }
        }
    }
}

@SuppressWarnings("unused")
class ProblemB2022Solver implements ProblemSolver {
    // Bubble-bubble sort - how many iterations of custom bubble-bubble sort, to sort the array of size n?
    // sort algo - sort each continuous subarray k from 0 to (n - k), start over until whole array is sorted
    // Fast enough to be simulated
    // Alternative: smart remark -> large elements move to the right, small ones move k - 1 to the left,
    // aka you can also find the largest distance between a value and its sorted position
    public Pair solve(List<String> inputData) {
        Scanner sc = new Scanner(inputData.get(0));
        int n = sc.nextInt(), k = sc.nextInt();
        sc = new Scanner(inputData.get(1));
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) nums[i] = sc.nextInt();
        double res = 0;
        for (; res < 1000000; res++) {
            boolean moved = false;
            for (int bubble = 0; bubble <= n - k; bubble++)
                // Insertion sort for sorting subarrays
                for (int j = bubble; j < k + bubble; j++) {
                    int l = j;
                    while (l > bubble && nums[l - 1] > nums[l]) {
                        moved = true;
                        int t = nums[l];
                        nums[l] = nums[l - 1];
                        nums[(l--) - 1] = t;
                    }
                }
            if (!moved) return new Pair(res, null);
        }
        return new Pair(res, null);
    }
}

@SuppressWarnings("unused")
class ProblemC2022Solver implements ProblemSolver {
    // Cookbook composition - order recipes by accessibility (beginner vs expert time)
    // Each recipe has steps and each step may have dependencies, all of which are linearized (shown before used)
    // Beginner is sum of all steps, expert is simple DP -> mem[i] = step[i].time + max(mem[j] for all dependencies)
    public Pair solve(List<String> inputData) {
        List<Recipe> recipes = new ArrayList<>();
        int l = 1;
        for (int i = 0; i < Integer.parseInt(inputData.get(0)); i++) {
            Scanner sc = new Scanner(inputData.get(l));
            String recipeName = sc.next();
            int stepsN = sc.nextInt();
            Step[] steps = new Step[stepsN];
            l++;
            for (int j = 0; j < stepsN; j++) {
                sc = new Scanner(inputData.get(l + j));
                String stepName = sc.next();
                int t = sc.nextInt(), depNum = sc.nextInt();
                String[] deps = new String[depNum];
                for (int k = 0; k < depNum; k++) deps[k] = sc.next();
                steps[j] = new Step(stepName, t, deps);
            }
            l += stepsN;
            recipes.add(new Recipe(recipeName, steps));
        }
        recipes.sort(Comparator.comparing(r -> r.name));
        recipes.sort(Comparator.comparingDouble(Recipe::calculateRatio));
        return new Pair(null, recipes.stream().map(r -> r.name).toList());
    }

    public static class Recipe {
        String name;
        Step[] steps;
        Double ratio = null;

        public Recipe(String name, Step[] steps) {
            this.name = name;
            this.steps = steps;
        }

        public double calculateRatio() {
            if (ratio != null) return ratio;
            double[] mem = new double[steps.length];
            mem[0] = steps[0].time;
            for (int i = 1; i < steps.length; i++) {
                double maxDep = 0;
                for (String s : steps[i].dependencies)
                    for (int k = 0; k <= i; k++)
                        if (steps[k].name.equals(s)) maxDep = Math.max(maxDep, mem[k]);
                mem[i] = steps[i].time + maxDep;
            }
            double expertTime = Arrays.stream(mem).max().orElse(1.0);
            return (ratio = (Arrays.stream(steps).mapToInt(s -> s.time).sum() / expertTime));
        }
    }

    public record Step(String name, int time, String[] dependencies) {
    }
}

@SuppressWarnings("unused")
class ProblemD2022Solver implements ProblemSolver {
    // Dimensional debugging - given n algos that work with input < H, how many can you verify for large inputs > L?
    // Graph floodfill -> two algos share edge if one's L < the other's H, for all dimensions (supersede or overlap)
    public Pair solve(List<String> inputData) {
        Scanner sc = new Scanner(inputData.get(0));
        int n = sc.nextInt(), dim = sc.nextInt();
        Box[] boxes = new Box[n];
        for (int i = 0; i < n; i++)
            boxes[i] = new Box(
                    Arrays.stream(inputData.get(2 * i + 1).split(" ")).mapToInt(Integer::parseInt).toArray(),
                    Arrays.stream(inputData.get(2 * (i + 1)).split(" ")).mapToInt(Integer::parseInt).toArray()
            );
        Optional<Box> start = Arrays.stream(boxes).filter(l -> Arrays.stream(l.low).allMatch(i -> i == 0)).findFirst();
        if (start.isEmpty()) return new Pair(0.0, null);
        Map<Box, List<Box>> graph = new HashMap<>();
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                if (i == j) continue;
                if (boxes[i].intersects(boxes[j])) {
                    graph.putIfAbsent(boxes[i], new ArrayList<>());
                    graph.get(boxes[i]).add(boxes[j]);
                }
            }
        Queue<Box> q = new ArrayDeque<>();
        Set<Box> vis = new HashSet<>();
        q.add(start.get());
        while (!q.isEmpty()) {
            Box box = q.poll();
            if (vis.contains(box)) continue;
            vis.add(box);
            q.addAll(graph.getOrDefault(box, new ArrayList<>()));
        }
        return new Pair((double) vis.size(), null);
    }

    public record Box(int[] low, int[] high) {
        public boolean intersects(Box o) {
            for (int i = 0; i < low.length; i++) if (o.low[i] > high[i]) return false;
            return true;
        }
    }
}

@SuppressWarnings("unused")
class ProblemE2022Solver implements ProblemSolver {
    // Extended braille -> Given n braille characters, determine how many are unique up to translation
    // For each character, sort points, and move lowest to (0,0) to standardize
    // Put all in a hashset and find final size
    public Pair solve(List<String> inputData) {
        int n = Integer.parseInt(inputData.get(0));
        Braille[] chars = new Braille[n];
        int l = 1;
        for (int i = 0; i < n; i++) {
            int nc = Integer.parseInt(inputData.get(l++));
            Point[] pts = new Point[nc];
            for (int j = 0; j < nc; j++) {
                String[] strs = inputData.get(l++).split(" ");
                pts[j] = new Point(Integer.parseInt(strs[0]), Integer.parseInt(strs[1]));
            }
            Arrays.sort(pts, ((a, b) -> {
                if (a.x != b.x) return a.x - b.x;
                return a.y - b.y;
            }));
            Point first = pts[0];
            for (Point p : pts) {
                p.x = p.x - first.x;
                p.y = p.y - first.y;
            }
            chars[i] = new Braille(pts);
        }
        Set<Braille> charSet = new HashSet<>(Arrays.asList(chars));
        return new Pair((double) charSet.size(), null);
    }

    record Braille(Point[] pts) {
    }

    static class Point {
        int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}

@SuppressWarnings("unused")
class ProblemF2022Solver implements ProblemSolver {
    // Fastestest function - given that foo took x% of runtime before, and y% after, find factor of improvement
    // Derivation: oldT / (oldT + otherT) = x , newT / (newT + otherT) = y
    // (oldT + otherT) / oldT = 1 / x  <=>  1 + otherT / oldT = 1 / x  <=>  otherT / oldT = 1 / x - 1
    // Analogously, replacing oldT with newT and x with y:  otherT / newT = 1 / y - 1
    // Divide the two:  (otherT / newT) / (otherT / oldT) = (oldT / newT) = (1 / y - 1) / (1 / x - 1)
    public Pair solve(List<String> inputData) {
        Scanner sc = new Scanner(inputData.get(0));
        double x = sc.nextInt() / 100d, y = sc.nextInt() / 100d;
        return new Pair((1d / y - 1d) / (1d / x - 1d), null);
    }
}

@SuppressWarnings("unused")
class ProblemH2022Solver implements ProblemSolver {
    // Heavy Hauling - given n 1D boxes, and moving a box costs d^2 for dist d, find min to make all positions distinct
    // First take care of individual stacks (boxes of same x-coordinate) -> they map to an interval
    // The cost of moving a single box is quadratic to its original dist -> C(x) = (x_box - x)^2
    // Aka model each box as a quadratic, ax^2 + bx + c. A single box's solution is its original position x_box.
    // -b / 2a = x_box AND a{x_box}^2 + b{x_box} + c = 0  <=>  a = 1, b = -2 * {x_box}, c = {x_box}^2
    // To combine two boxes, translate one of them, e.g. -> C(x) = ({x_box_1} - x)^2 + ({x_box_2 - x} + 1)^2
    // Two boxes need to be combined iff their ranges overlap. The size of a group is equal to a (combining quadratics)
    // Thus, two groups/ranges overlap iff {x_solution_1} + a >= {x_solution_2}. Closed form to combine groups:
    // ax^2 + bx + c + {a2}{x2}^2 + {b2}{x2} + {c2} = ax^2 + bx + c + {a2}(x + a)^2 + {b2}(x + a) + {c2} =
    // (a + {a2})x^2 + (b + 2a{a2} + {b2})x + (c + a^2{a2} + a{b2} + {c2})
    public Pair solve(List<String> inputData) {
        int n = Integer.parseInt(inputData.get(0));
        Long[] boxes = Arrays.stream(inputData.get(1).split(" ")).map(Long::parseLong).toArray(Long[]::new);
        Deque<Group> stack = new ArrayDeque<>();
        for (Long box : boxes) {
            Group next = new Group(1, -2 * box, box * box);
            while (!stack.isEmpty() && stack.peek().intersects(next)) next = next.merge(stack.pop());
            stack.push(next);
        }
        return new Pair((double) stack.stream().map(Group::cost).reduce(Long::sum).orElse(0L), null);
    }

    static class Group {
        long a, b, c;

        public Group(long a, long b, long c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }

        public long minX() {
            return (long) Math.floor((double) -b / (2L * a) + 0.5);
        }

        public long cost() {
            long min = minX();
            return a * min * min + b * min + c;
        }

        public Group merge(Group o) {
            return new Group(a + o.a, b + 2 * a * o.a + o.b, c + a * a * o.a + o.b * a + o.c);
        }

        public boolean intersects(Group o) {
            return minX() + a >= o.minX();
        }
    }
}

@SuppressWarnings("unused")
class ProblemI2022Solver implements ProblemSolver {
    // Inked inscriptions - copy n psalms in at most 2 * n * sqrt(n) page flips.
    // Each psalm copy can be modelled as a point (old_book_page, new_book_page).
    // To go from one psalm to another, we need (Manhattan distance) page flips, aka we now minimize Hamiltonian path.
    // Empirically, greedy approach works here... One smart approach - divide the space into bands of height sqrt(n)
    // We go from left-to-right on one band, then right-to-left on the next one, etc.
    // Each band -> n horizontal, (1+2+...+n) approx n/2 vertical, and 2 * n total to transition between bands
    // Aka this method uses at most sqrt(n) * (n + n/2) + 2 * n = 1.5 * n * sqrt(n) + 2 * n page flips.
    public Pair solve(List<String> inputData) {
        int n = Integer.parseInt(inputData.get(0));
        List<Point> psalms = new ArrayList<>();
        String[] strs = inputData.get(1).split(" ");
        for (int i = 0; i < n; i++) psalms.add(new Point(i + 1, Integer.parseInt(strs[i])));
        List<String> res = new ArrayList<>();
        Point curr = new Point(1, 1);
        while (!psalms.isEmpty()) {
            int minDist = Integer.MAX_VALUE;
            Point bestNext = null;
            for (Point next : psalms) {
                int currDist = Math.abs(curr.x - next.x) + Math.abs(curr.y - next.y);
                if (currDist < minDist) {
                    minDist = currDist;
                    bestNext = next;
                }
            }
            curr = bestNext;
            assert curr != null;
            res.add(curr.x + " " + curr.y);
            psalms.remove(curr);
        }
        return new Pair(null, res);
    }

    record Point(int x, int y) {
    }
}

@SuppressWarnings("unused")
class ProblemJ2022Solver implements ProblemSolver {
    // Jabbing jets - Given n concentric circles, find num of points s.t. dist between any two is > e
    // It is given that any two concentric circles are of dist > e, aka each circle is independent of another
    // Derivation: maximal case -> find the smallest angle possible to achieve maximal polygon, with side e
    // Pick two points, and they create an arc, along the polygon, each with distance r to center and e between them
    // split section in middle -> hypotenuse r, base e / 2 -> sin(a / 2) = e / (r * 2)  <=>  a = 2 * asin(e / (r * 2))
    // The number of such sections in our maximal case is m = floor(2 * pi / a) = floor(pi / asin(e / (r * 2))
    // Finally, in base case we can always jab at least 1 point, so we have 1 when e > 2 * r
    public Pair solve(List<String> inputData) {
        Scanner sc = new Scanner(inputData.get(0));
        int n = sc.nextInt(), e = sc.nextInt();
        return new Pair(Arrays.stream(inputData.get(1).split(" ")).map(Integer::parseInt)
                .map(r -> circlePoints(r, e)).reduce(Double::sum).orElse(0.0), null);
    }

    public double circlePoints(int r, int e) {
        if (e > 2 * r) return 1;
        return (int) (Math.PI / Math.asin((double) e / (2 * r)) + 0.00001);
    }
}

@SuppressWarnings("unused")
class ProblemK2022Solver implements ProblemSolver {
    // Knitting patterns - given a pattern and costs to {back strand, front strand/use, start/end use}, find min cost
    // Each color independent -> if appears on pattern must be used (front strand), only decision is on gaps
    // for each gap in the pattern, pick min between (length_gap) * back_strand < 2 * start/end use
    int costBack, costFront, costChange;

    public Pair solve(List<String> inputData) {
        Scanner sc = new Scanner(inputData.get(0));
        costBack = sc.nextInt();
        costFront = sc.nextInt();
        costChange = sc.nextInt();
        List<String> res = inputData.get(1).chars().boxed().map(c -> calculateCost(inputData.get(2), c)).toList();
        return new Pair(null, res);
    }

    public String calculateCost(String knit, int c) {
        int cost = 0;
        int lastKnit = -10_000;
        for (int i = 0; i < knit.length(); i++)
            if (knit.charAt(i) == c) {
                if (lastKnit != i - 1) cost += Math.min((i - lastKnit - 1) * costBack, 2 * costChange);
                cost += costFront;
                lastKnit = i;
            }
        return String.valueOf(cost);
    }
}

@SuppressWarnings("unused")
class ProblemL2022Solver implements ProblemSolver {
    // Lots of Liquid - find a length of a cube with enough volume to contain all other n cubes
    // Simply cube root of sum of all cube volumes (side^3)
    public Pair solve(List<String> inputData) {
        return new Pair(Arrays.stream(inputData.get(1).split(" "))
                .map(Double::parseDouble).map(d -> d * d * d).reduce(Double::sum)
                .map(d -> Math.pow(d, 1 / 3.0d)).orElse(0.0), null);
    }
}