import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@SuppressWarnings("unused")
class ProblemA2023Solver implements ProblemSolver {
    public Pair solve(List<String> inputData) {
        Scanner sc = new Scanner(inputData.get(0));
        int h = sc.nextInt(), w = sc.nextInt();
        char[][] piece = new char[h][w];
        for (int i = 0; i < h; i++) for (int j = 0; j < w; j++) piece[i][j] = inputData.get(i + 1).charAt(j);
        boolean l = IntStream.range(0, h).map(i -> piece[i][0]).allMatch(c -> c == '#'),
                r = IntStream.range(0, h).map(i -> piece[i][w - 1]).allMatch(c -> c == '#'),
                t = IntStream.range(0, w).map(i -> piece[0][i]).allMatch(c -> c == '#'),
                b = IntStream.range(0, w).map(i -> piece[h - 1][i]).allMatch(c -> c == '#');
        if (!(l || r || t || b)) return new Pair(null, List.of("impossible"));
        char[][] res;
        if (t) {
            List<String> p = tryRes(piece);
            if (p != null) return new Pair(null, p);
        }
        if (b) {
            List<String> p = tryRes(rotate90(rotate90(piece)));
            if (p != null) return new Pair(null, p);
        }
        if (l) {
            List<String> p = tryRes(rotate90(piece));
            if (p != null) return new Pair(null, p);
        }
        List<String> p = tryRes(rotate90(rotate90(rotate90(piece))));
        if (p != null) return new Pair(null, p);
        return new Pair(null, List.of("impossible"));
    }

    public char[][] rotate90(char[][] arr) {
        char[][] res = new char[arr[0].length][arr.length];
        for (int i = 0; i < arr.length; i++)
            for (int j = 0; j < arr[0].length; j++)
                res[j][arr.length - i - 1] = arr[i][j];
        return res;
    }

    public List<String> tryRes(char[][] arr) {
        for (int j = 0; j < arr[0].length; j++) {
            boolean f = false;
            for (char[] chars : arr) {
                if (chars[j] == '#' && f) return null;
                if (chars[j] == '.') f = true;
            }
        }
        List<String> res = new ArrayList<>();
        res.add(arr.length + " " + arr[0].length);
        for (char[] r : arr) {
            for (int j = 0; j < arr[0].length; j++) r[j] = (r[j] == '#') ? '.' : '#';
            res.add(String.valueOf(r));
        }
        return res;
    }
}

@SuppressWarnings("unused")
class ProblemB2023Solver implements ProblemSolver {
    public Pair solve(List<String> inputData) {
        int n = Integer.parseInt(inputData.get(0));
        Integer[] first = Arrays.stream(inputData.get(1).split(" ")).map(Integer::parseInt).toArray(Integer[]::new);
        Integer[] second = Arrays.stream(inputData.get(2).split(" ")).map(Integer::parseInt).toArray(Integer[]::new);
        double c = 0, c2 = 0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                if (first[i] > second[j]) c++;
                else if (first[i] < second[j]) c2++;
            }
        return new Pair(null, List.of((c > c2) ? "first" : (c < c2) ? "second" : "tie"));
    }
}

@SuppressWarnings("unused")
class ProblemD2023Solver implements ProblemSolver {
    public Pair solve(List<String> inputData) {
        return null;
    }
}

@SuppressWarnings("unused")
class ProblemE2023Solver implements ProblemSolver {
    int n;
    Map<Integer, Map<Integer, Route>> graph;

    public Pair solve(List<String> inputData) {
        Scanner sc = new Scanner(inputData.get(0));
        n = sc.nextInt();
        if (n == 5) {
            System.out.flush();
        }
        int m = sc.nextInt(), t = sc.nextInt();
        graph = new HashMap<>();
        double maxSpeed = 0;
        for (int i = 1; i <= n; i++) graph.put(i, new HashMap<>());
        for (int i = 0; i < m; i++) {
            Integer[] nums = Arrays.stream(inputData.get(i + 1).split(" "))
                    .map(Integer::parseInt).toArray(Integer[]::new);
            Route r = new Route(nums[0], nums[1], nums[2], nums[3]);
            maxSpeed = Math.max(maxSpeed, nums[2]);
            graph.get(nums[0]).put(nums[1], r);
            graph.get(nums[1]).put(nums[0], r);
        }
        if (dijkstra(0.0) <= t) return new Pair(0.0, null);
        double low = 0, high = maxSpeed;
        do {
            double newRate = (high - low) / 2;
            if (dijkstra(newRate) <= t) high = newRate;
            else low = newRate;
        } while (high - low > 10e-6);
        return new Pair(low, null);
    }

    public double dijkstra(double rate) {
        Queue<Node> q = new PriorityQueue<>(Comparator.comparing(r -> r.cost));
        Map<Integer, Double> dist = new HashMap<>();
        dist.put(1, 0.0);
        q.add(new Node(1, 0));
        while (!q.isEmpty()) {
            Node curr = q.poll();
            if (curr.idx == n) break;
            for (Map.Entry<Integer, Route> e : graph.get(curr.idx).entrySet()) {
                int next = e.getKey();
                Route r = e.getValue();
                double newCost = curr.cost + r.l / (r.v + rate);
                if (newCost < dist.getOrDefault(next, Double.MAX_VALUE)) {
                    dist.put(next, newCost);
                    q.add(new Node(next, newCost));
                }
            }
        }
        return dist.getOrDefault(n, 0.0);
    }

    record Route(int a, int b, double l, double v) {
    }

    static class Node {
        int idx;
        double cost;

        public Node(int idx, double cost) {
            this.idx = idx;
            this.cost = cost;
        }
    }
}

@SuppressWarnings("unused")
class ProblemF2023Solver implements ProblemSolver {
    public Pair solve(List<String> inputData) {
        return new Pair((double) Arrays.stream(inputData.get(1).split(" ")).map(Integer::parseInt)
                .sorted().limit(2).reduce(Integer::sum).orElse(0), null);
    }
}

@SuppressWarnings("unused")
class ProblemG2023Solver implements ProblemSolver {
    public Pair solve(List<String> inputData) {
        return null;
    }
}

@SuppressWarnings("unused")
class ProblemH2023Solver implements ProblemSolver {
    public Pair solve(List<String> inputData) {
        int n = Integer.parseInt(inputData.get(0));
        Map<Character, List<Character>> ch = new HashMap<>();
        for (int i = 1; i <= n - 1; i++) {
            String f = inputData.get(i);
            String s = inputData.get(i + 1);
            boolean match = true;
            for (int j = 0; j < Math.min(f.length(), s.length()); j++) {
                char fc = f.charAt(j), sc = s.charAt(j);
                if (fc != sc) {
                    match = false;
                    if (!ch.containsKey(fc)) ch.put(fc, new ArrayList<>());
                    if (!ch.containsKey(sc)) ch.put(sc, new ArrayList<>());
                    ch.get(fc).add(sc);
                    break;
                }
            }
            if (match && s.length() < f.length()) return new Pair(null, List.of("impossible"));
        }
        return topoOrder(ch);
    }

    public Pair topoOrder(Map<Character, List<Character>> ch) {
        int[] deg = new int[26];
        for (Map.Entry<Character, List<Character>> e : ch.entrySet()) for (Character c : e.getValue()) deg[c - 97]++;
        Queue<Character> q = new ArrayDeque<>();
        for (int i = 0; i < 26; i++) if (deg[i] == 0) q.add((char) (i + 97));
        List<Character> ord = new ArrayList<>();
        while (!q.isEmpty()) {
            Character c = q.poll();
            ord.add(c);
            for (Character next : ch.getOrDefault(c, new ArrayList<>())) if (--deg[next - 97] == 0) q.add(next);
        }
        if (ord.size() != 26) return new Pair(null, List.of("impossible"));
        return new Pair(null, List.of(ord.stream().map(String::valueOf).collect(Collectors.joining())));
    }
}

@SuppressWarnings("unused")
class ProblemI2023Solver implements ProblemSolver {
    public Pair solve(List<String> inputData) {
        Scanner sc = new Scanner(inputData.get(0));
        int n = sc.nextInt(), m = sc.nextInt(), res = 0, curr = 0;
        Integer[] jobs = Arrays.stream(inputData.get(1).split(" ")).map(Integer::parseInt).toArray(Integer[]::new);
        for (int i = 0; i < Math.max(0, n - m) + 1; i++) {
            if (jobs[i] <= 0) continue;
            for (int j = i + 1; j < Math.min(n, i + m); j++) jobs[j] -= jobs[i];
            for (int j = i; j < Math.min(n, i + m); j++) res = Math.max(res, jobs[j]);
        }
        return new Pair((double) res, null);
    }
}

@SuppressWarnings("unused")
class ProblemJ2023Solver implements ProblemSolver {
    public Pair solve(List<String> inputData) {
        int n = Integer.parseInt(inputData.get(0));
        int[] first = inputData.get(1).chars().map(c -> c - 65).toArray();
        int[] second = inputData.get(2).chars().map(c -> c - 65).toArray();
        return new Pair((double) IntStream.range(0, n).map(i ->
                (Math.min(first[i], second[i]) + 26 - Math.max(first[i], second[i])) % 26).sum(), null);
    }
}

@SuppressWarnings("unused")
class ProblemK2023Solver implements ProblemSolver {
    public Pair solve(List<String> inputData) {
        int n = Integer.parseInt(inputData.get(0));
        Point[] ps = new Point[n];
        for (int i = 0; i < n; i++) {
            String[] pts = inputData.get(i + 1).split(" ");
            ps[i] = new Point(Integer.parseInt(pts[0]), Integer.parseInt(pts[1]));
        }
        Map<Point, Tuple> map = new HashMap<>();
        for (Point p : ps) map.put(p, new Tuple(n, new double[n]));
        for (int i = 0; i < n; i++)
            for (int j = i + 1; j < n; j++) {
                double d = Math.sqrt(Math.pow(ps[i].x - ps[j].x, 2) + Math.pow(ps[i].y - ps[j].y, 2));
                Tuple f = map.get(ps[i]);
                f.ds[f.i--] = d;
                Tuple s = map.get(ps[j]);
                s.ds[s.i--] = d;
            }
        return new Pair(map.values().stream()
                .map(doubles -> Arrays.stream(doubles.ds).reduce(Double::sum).orElse(0.0) / (n - 1))
                .min(Comparator.naturalOrder()).orElse(0.0), null);
    }

    record Point(int x, int y) {
    }

    static class Tuple {
        int i;
        double[] ds;

        public Tuple(int i, double[] ds) {
            this.i = i - 1;
            this.ds = ds;
        }
    }
}

@SuppressWarnings("unused")
class ProblemL2023Solver implements ProblemSolver {
    public Pair solve(List<String> inputData) {
        return null;
    }
}

@SuppressWarnings("unused")
class ProblemM2023Solver implements ProblemSolver {
    public Pair solve(List<String> inputData) {
        return null;
    }
}