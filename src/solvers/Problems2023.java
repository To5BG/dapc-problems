import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@SuppressWarnings("all")
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
        int[] f = Arrays.stream(inputData.get(1).split(" ")).mapToInt(Integer::parseInt).toArray();
        int[] s = Arrays.stream(inputData.get(2).split(" ")).mapToInt(Integer::parseInt).toArray();
        double c = 0, c2 = 0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                if (f[i] > s[j]) c++;
                else if (f[i] < s[j]) c2++;
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
    public Pair solve(List<String> inputData) {
        Scanner scanner = new Scanner(inputData.get(0));
        int n = scanner.nextInt(), m = scanner.nextInt(), t = scanner.nextInt();
        List<List<Route>> graph = new ArrayList<>();
        for (int i = 0; i <= n; i++) graph.add(new ArrayList<>());
        for (int i = 0; i < m; i++) {
            int[] nums = Arrays.stream(inputData.get(i + 1).split(" ")).mapToInt(Integer::parseInt).toArray();
            int a = nums[0], b = nums[1], l = nums[2], v = nums[3];
            graph.get(a).add(new Route(b, l, v));
            graph.get(b).add(new Route(a, l, v));
        }
        double dist = dijkstra(graph, n, 0);
        if (dist <= t) return new Pair(0.0, null);
        double low = 0, high = 1e9, mid;
        do {
            mid = (low + high) / 2;
            if (dijkstra(graph, n, mid) <= t) high = mid;
            else low = mid;
        } while ((high - low) >= 1e-6);
        return new Pair(mid, null);
    }

    public double dijkstra(List<List<Route>> graph, int n, double rate) {
        double[] dist = new double[n + 1];
        Arrays.fill(dist, Double.MAX_VALUE);
        dist[1] = 0.0;
        Queue<Node> q = new PriorityQueue<>(Comparator.comparing(i -> i.cost));
        q.add(new Node(1, 0));
        while (!q.isEmpty()) {
            Node curr = q.poll();
            if (curr.cost != dist[curr.idx]) continue;
            if (curr.idx == n) break;
            for (Route e : graph.get(curr.idx)) {
                double newDist = curr.cost + e.l / (e.v + rate);
                if (newDist < dist[e.to]) {
                    dist[e.to] = newDist;
                    q.add(new Node(e.to, newDist));
                }
            }
        }
        return dist[n];
    }

    record Route(int to, double l, double v) {
    }

    record Node(int idx, double cost) {
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
        Scanner sc = new Scanner(inputData.get(0));
        int n = sc.nextInt(), m = sc.nextInt();
        Tuple[] ts = new Tuple[n];
        for (int i = 0; i < n; i++) ts[i] = new Tuple(i + 1);
        for (int i = 0; i < m; i++) {
            int[] nums = Arrays.stream(inputData.get(i + 1).split(" ")).mapToInt(Integer::parseInt).toArray();
            for (int j = 0; j < n; j++) {
                Tuple curr = ts[nums[j] - 1];
                curr.s += j;
                curr.n++;
            }
        }
        List<Integer> res = Arrays.stream(ts).sorted(Comparator.comparing(t -> t.s / t.n)).map(t -> t.idx).toList();
        String resStr = res.stream().map(String::valueOf).collect(Collectors.joining(" "));
        return new Pair(null, List.of(resStr));
    }

    static class Tuple {
        int idx;
        double s, n;

        public Tuple(int idx) {
            this.idx = idx;
            this.s = this.n = 0;
        }
    }
}

@SuppressWarnings("unused")
class ProblemH2023Solver implements ProblemSolver {
    public Pair solve(List<String> inputData) {
        int n = Integer.parseInt(inputData.get(0));
        Map<Character, List<Character>> ch = new HashMap<>();
        for (int i = 0; i < 26; i++) ch.put((char) (i + 97), new ArrayList<>());
        for (int i = 1; i <= n - 1; i++) {
            String f = inputData.get(i);
            String s = inputData.get(i + 1);
            boolean match = true;
            for (int j = 0; j < Math.min(f.length(), s.length()); j++) {
                char fc = f.charAt(j), sc = s.charAt(j);
                if (fc != sc) {
                    match = false;
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
        for (Map.Entry<Character, List<Character>> e : ch.entrySet())
            for (Character c : e.getValue()) deg[c - 97]++;
        Queue<Character> q = new PriorityQueue<>();
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
class ProblemH2023Validator implements ProblemValidator {
    public boolean passes(List<String> input, List<String> answer, Pair inputData) {
        if (inputData.resStr() == null || inputData.resStr().size() != 1) return false;
        String fin = inputData.resStr().get(0);
        if (answer.get(0).equals("impossible")) return fin.equals("impossible");
        char[] sInput = fin.toCharArray();
        Arrays.sort(sInput);
        Map<Character, Integer> inv = new HashMap<>();
        for (int i = 0; i < 26; i++) {
            if (sInput[i] != (char) (i + 97)) return false;
            inv.put(fin.charAt(i), i);
        }
        for (int i = 1; i < input.size() - 1; i++) {
            String f = input.get(i), s = input.get(i + 1);
            for (int j = 0; ; j++) {
                if (j >= f.length()) break;
                if (j >= s.length()) return false;
                int comp = inv.get(f.charAt(j)) - inv.get(s.charAt(j));
                if (comp > 0) return false;
                if (comp < 0) break;
            }
        }
        return true;
    }
}

@SuppressWarnings("unused")
class ProblemI2023Solver implements ProblemSolver {
    public Pair solve(List<String> inputData) {
        Scanner sc = new Scanner(inputData.get(0));
        int n = sc.nextInt(), m = sc.nextInt(), res = 0, curr = 0;
        int[] jobs = Arrays.stream(inputData.get(1).split(" ")).mapToInt(Integer::parseInt).toArray();
        Queue<Integer> machines = new PriorityQueue<>();
        for (int i = 0; i < m; i++) machines.add(0);
        int idx = 0;
        while (!machines.isEmpty()) {
            Integer t = machines.remove();
            res = Math.max(res, t - curr);
            curr = t;
            if (idx != jobs.length) machines.add(curr + jobs[idx++]);
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
                Math.min((first[i] - second[i] + 26) % 26, (second[i] - first[i] + 26) % 26)).sum(), null);
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