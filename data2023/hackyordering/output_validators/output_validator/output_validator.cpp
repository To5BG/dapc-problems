#include "validation.h"

std::string& lowercase(std::string& s) {
	transform(s.begin(), s.end(), s.begin(), [](unsigned char c) { return std::tolower(c); });
	return s;
}

int main(int argc, char* argv[]) {
	// Set up the input and answer streams.
	std::ifstream in(argv[1]);
	std::ifstream ans(argv[2]); // Only for custom checker.
	OutputValidator v(argc, argv);

	// Read input
	int n;
	in >> n;
	std::vector<std::string> strings(n);
	for(int i = 0; i < n; i++) in >> strings[i];

	// Compare possible/impossible with true answer
	std::string true_answer;
	ans >> true_answer;
	if(true_answer == "impossible") {
		v.test_string(true_answer);

	// Check if the permutation is correct
	} else {
		std::string perm = v.read_string("perm", 26, 26);

		// Check if permutation
		std::string sorted = perm;
		std::sort(sorted.begin(), sorted.end());
		v.check(lowercase(sorted) == "abcdefghijklmnopqrstuvwxyz", "not a permutation");

		// Calculate inverse permutation
		std::map<char, int> inv_perm;
		int i = 0;
		for(char c : perm) inv_perm[c] = i++;

		// Check if strings are now sorted
		for(int i = 0; i < n - 1; i++) {
			std::string first = strings[i], second = strings[i + 1];
			for(unsigned long int j = 0;; j++) {
				if(j >= first.length()) break; // First string is prefix of second, sorted
				if(j >= second.length())
					v.WA("strings ", first, " and ", second, " are not sorted");
				// Second string is strict prefix of first, not sorted
				int comparison = inv_perm[first[j]] - inv_perm[second[j]];
				if(comparison > 0) v.WA("strings ", first, " and ", second, " are not sorted");
				// j-th character of first string occurs later, not sorted
				if(comparison < 0) break; // j-th character of first string occurs earlier, sorted
				// else: j-th character is equal, continue to j+1-th character
			}
		}
	}

	v.newline();
}
