#include "validation.h"

using std::array;
using std::cerr;
using std::cout;
using std::endl;
using std::string;

constexpr int LEN     = 5;
const int MAX_GUESSES = 6;

bool is_prime(int p) {
	for(int i = 2; i * i <= p; ++i) {
		if(p % i == 0) return false;
	}
	return true;
}

string answer(int p, int g) {
	array<int, LEN> prime, guess;
	for(int i = 0; i < LEN; ++i) {
		prime[LEN - 1 - i] = p % 10;
		p /= 10;
		guess[LEN - 1 - i] = g % 10;
		g /= 10;
	}
	string answer = "wwwww";
	// check green
	for(int i = 0; i < LEN; ++i) {
		if(guess[i] == prime[i]) {
			answer[i] = 'g';
		}
	}
	// check yellow
	// TODO: Randomize the order here, so that a random cell can become yellow when there are
	// multiple options.
	for(int i = 0; i < LEN; ++i) {
		// i: index in prime.
		// j: index in guess we consider making yellow.
		if(answer[i] == 'g') continue;
		for(int j = 0; j < LEN; ++j) {
			// Pos j doesn't have the right char.
			if(guess[j] != prime[i]) continue;
			// Pos j already 'used'?
			if(answer[j] != 'w') continue;
			answer[j] = 'y';
			break;
		}
	}
	return answer;
}

int main(int argc, char** argv) {
	// The testcase .in file.
	std::ifstream in(argv[1]);
	OutputValidator v(argc, argv);

	int n;
	in >> n;
	cout << n << endl;

	int max_guesses   = 0;
	int total_guesses = 0;

	for(int i = 0; i < n; ++i) {
		int p;
		in >> p;

		int guesses = 0;
		while(true) {
			guesses += 1;
			total_guesses += 1;
			v.check(guesses <= MAX_GUESSES, "Used too many guesses");

			int guess = v.read_integer("guess", 10000, 100000);
			v.check(is_prime(guess), "Guess ", guess, " is not a prime!");

			if(guess == p) {
				cout << "ggggg" << endl;
				break;
			}

			cout << answer(p, guess) << endl;
		}
		max_guesses = std::max(max_guesses, guesses);
	}
	cerr << "Max guesses: " << max_guesses << " \t"
	     << "Avg guesses: " << double(total_guesses) / n << endl;
	fflush(stderr); // since we mix stdout and stderr
}
