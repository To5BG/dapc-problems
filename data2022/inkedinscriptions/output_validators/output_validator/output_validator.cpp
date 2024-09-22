#include "validation.h"
using std::vector;
using std::pair;
using std::abs;

// Output: a list of `n` page flips from `i` to `j`.

int main(int argc, char **argv) {
    // Set up the input and answer streams.
    std::ifstream in(argv[1]);
    std::ifstream ans(argv[2]); // Only for custom checker.
    OutputValidator v(argc, argv);

    int n;
    in >> n;
    vector<int> a(n);
    vector<bool> b(n, false);
    for (int i=0;i<n;i++)
        in >> a[i];

    int upper_bound = std::ceil(2*n*std::sqrt(n));
    int flips = 0;
    int x=0, y=1;
    for (int t=0;t<n;t++) {
        int i = v.read_integer("i", 1, n);
        i--;
        v.space();
        int j = v.read_integer("j", 1, n);
        v.newline();

        v.check(!b[i], "Duplicate psalm");
        v.check(a[i] == j, "Copied to wrong page");
        b[i] = true;
        flips += abs(x-i) + abs(y-j);
        x = i;
        y = j;
    }
    v.check(flips <= upper_bound, "Number of flips should be ", upper_bound, " or less, but is ", flips);
    std::cerr << "Flips used: " << flips << ", ratio: " << 1.0 * flips / upper_bound << std::endl;
}
