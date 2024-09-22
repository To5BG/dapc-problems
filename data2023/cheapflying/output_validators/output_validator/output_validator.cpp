#include "validation.h"

int main(int argc, char** argv) {
	// Set up the input and answer streams.
	std::ifstream in(argv[1]);
	OutputValidator v(argc, argv);

	int a, b, c;
	int interactions;
	in >> a >> b >> c;
	in >> interactions;
	std::cout << a << " " << b << " " << c << std::endl;

	bool bought = false;

	long long costRentOnly = 0;
	long long buyDirect    = b;
	long long actualCost   = 0;

	for(int i = 0; i < interactions; i++) {
		std::cout << "flight" << std::endl;
		costRentOnly += a;
		buyDirect += c;
		std::string command = v.test_strings({"self", "buy", "airline"}, "op");
		v.newline();

		if(command == "buy") {
			if(bought) {
				std::cout << "end" << std::endl;
				v.WA("iteration ", i + 1, ": bought the airplane twice.");
			} else {
				bought = true;
				actualCost += b + c; // buy and fly
			}
		} else if(command == "self") {
			if(bought) {
				actualCost += c;
			} else {
				std::cout << "end" << std::endl;
				v.WA("iteration ", i + 1, ": Uses self, but has not bought the airplane yet.");
			}
		} else if(command == "airline") {
			actualCost += a;
		} else {
			v.WA("Unknown command: ", command);
		}

		auto optimum = std::min(costRentOnly, buyDirect);

		if(actualCost > 2 * optimum) {
			std::cout << "end" << std::endl;
			v.WA("iteration ", i + 1, ": exceeded twice the optimimum: ", actualCost, " > ",
			     2 * optimum);
		}
	}

	std::cout << "end" << std::endl;
}
