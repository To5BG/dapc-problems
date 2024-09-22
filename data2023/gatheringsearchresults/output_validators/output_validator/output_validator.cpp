#include "validation.h"

// Checks the output of the team. In particular, this checks the following properties:
// - The reported permutation is actually a permutation
// - The reported permutation has the correct cost

typedef long long ll;

int main(int argc, char **argv) {
	// Set up the input and answer streams.
	std::ifstream in(argv[1]);
	std::ifstream ans(argv[2]); // Only for custom checker.
	OutputValidator v(argc, argv);

	int n, k;
	in >> n >> k;
	std::vector<std::vector<ll>> inp(k, std::vector<ll>(n));
	for (ll i = 0; i < k; i++) {
		for (ll j = 0; j < n; j++) {
			ll p;
			in >> p;
			inp[i][p-1] = j;
		}
	}

	std::cerr << "Input read correctly" << std::endl;
	std::cerr << "n = " << n << std::endl;

	// Read team answer and jury answer.
	std::vector<ll> tau_team = v.read_integers("tau", n, 1, n, Unique), tau_jury(n);
	for (int i = 0; i < n; i++) {
		ans >> tau_jury[i];
	}
	std::vector<ll> tau_team_inv(n), tau_jury_inv(n);
	for (int i = 0; i < n; i++) {
	    tau_team_inv[tau_team[i]-1] = i;
	    tau_jury_inv[tau_jury[i]-1] = i;
	}

	// Compute the costs of both permutations, and check whether they match.
	ll team_cost = 0, jury_cost = 0;
	for (int i = 0; i < n; i++) {
		for (int j = 0; j < k; j++) {
			team_cost += (tau_team_inv[i] - inp[j][i])*(tau_team_inv[i] - inp[j][i]);
			jury_cost += (tau_jury_inv[i] - inp[j][i])*(tau_jury_inv[i] - inp[j][i]);
		}
	}
	if (team_cost > jury_cost) {
		v.WA("Team submission has cost ", team_cost, " while jury submission has cost ", jury_cost);
	}
	if (team_cost < jury_cost) {
		std::cerr << "Team submission has cost " << team_cost << " while jury submission has cost " << jury_cost << ". THIS SHOULD NOT HAPPEN!" << std::endl;
		return 7;
	}
}
