#include "validation.h"

using namespace std;

// Output is a grid that would fit the block in the input perfectly:
// - One line with two integers h and w, the height and width of the Tetris block.
// - h lines with w characters, each character being either '#' or '.'.

int main(int argc, char *argv[]) {
    // Set up the input and answer streams.
    ifstream in(argv[1]);
    ifstream ans(argv[2]); // Only for custom checker.
    OutputValidator v(argc, argv);

    string peek;
    ans >> peek;
    if (peek == "impossible") {
        v.test_string("impossible");
        v.newline();
    } else {
        int h = v.read_integer("h_out", 1, 1000);
        v.space();
        int w = v.read_integer("w_out", 1, 1000);
        v.newline();
        vector<string> lines(h);
        for (int y = 0; y < h; y++) {
            lines[y] = v.read_string("grid", w, w, "#.");
            v.check(lines[y].find('.') != string::npos, "Line ", y, " is full, which is not valid");
            v.newline();
        }
    }
}
