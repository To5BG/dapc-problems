#include "validation.h"

#define ld long double

using namespace std;

// Output: a list of recipe names, ordered by accessibility (lowest beginner/expert time ratio first).
// Recipes with equal accessibility can be ordered arbitrarily.

int main(int argc, char **argv) {
    // Set up the input and answer streams.
    ifstream cin(argv[1]); // Warning! Shadowing `cin` on purpose, to make it easier to copy/paste from solution.
    ifstream ans(argv[2]);
    OutputValidator v(argc, argv);

    int n;
    cin >> n;

    vector<string> recipes;
    unordered_map<string, ld> accessibility;

    // Loop copied from maarten.cpp
    for (int i = 0; i < n; ++i) {
        string recipe;
        cin >> recipe;
        recipes.push_back(recipe);

        int s;
        cin >> s;

        unordered_map<string, int> end;
        int beginnerTime = 0, expertTime = 0;
        for (int j = 0; j < s; ++j) {
            string step;
            int t, d;
            cin >> step >> t >> d;

            beginnerTime += t;

            int depEnd = 0;
            for (int k = 0; k < d; ++k) {
                string dep;
                cin >> dep;
                if (depEnd < end[dep])
                    depEnd = end[dep];
            }
            end[step] = t + depEnd;
            if (expertTime < t + depEnd)
                expertTime = t + depEnd;
        }
        accessibility[recipe] = (ld) beginnerTime / (ld) expertTime;
    }

    set<string> seen;
    ld prev = 0;
    for (int i = 0; i < n; ++i) {
        string recipe = v.read_string("recipe", 1, 10, "abcdefghijklmnopqrstuvwxyz");
        v.newline();
        v.check(seen.find(recipe) == seen.end(), "Recipe ", recipe, " is duplicate in team output!");
        v.check(accessibility[recipe] >= prev, "Accessibility order is wrong in team output!");
        prev = accessibility[recipe];
        seen.insert(recipe);
    }
    v.check(seen.size() == n, "Not all recipes were listed in team output!");
}
