#!/usr/bin/env python3
#
# Testing tool for the Guessing Primes problem
#
# Usage:
#
#   python3 testing_tool.py -f inputfile <program invocation>
#
#
# Use the -f parameter to specify the input file, e.g. 1.in.
# Format of the input file:
# - One line with one integer, the number of rounds.
# - One line per round, the 5-digit primes to be guessed.
# e.g.:
# 2
# 31517
# 13457
#
#
# You can compile and run your solution as follows.
# - You may have to replace 'python3' by just 'python'.
# - On Windows, you may have to to replace '/' by '\'.

# C++:
#   g++ solution.cpp
#   python3 testing_tool.py -f 1.in ./a.out

# Java
#   javac solution.java
#   python3 testing_tool.py -f 1.in java solution

# Python3
#   python3 testing_tool.py -f 1.in python3 ./solution.py


# The tool is provided as-is, and you should feel free to make
# whatever alterations or augmentations you like to it.
#
# The tool attempts to detect and report common errors, but it is not an
# exhaustive test. It is not guaranteed that a program that passes this testing
# tool will be accepted.


import argparse
import subprocess
import traceback


def write(p, line):
    assert p.poll() is None, "Program terminated early"
    print(f"Write: {line}", flush=True)
    p.stdin.write(f"{line}\n")
    p.stdin.flush()


def read(p):
    assert p.poll() is None, "Program terminated early"
    line = p.stdout.readline().strip()
    assert line != "", "Read empty line or closed output pipe"
    print(f"Read: {line}", flush=True)
    return line


parser = argparse.ArgumentParser(description="Testing tool for problem Guessing Primes.")
parser.add_argument(
    "-f",
    dest="inputfile",
    metavar="inputfile",
    default=None,
    type=argparse.FileType("r"),
    required=True,
    help="The input file to use.",
)
parser.add_argument("program", nargs="+", help="Invocation of your solution")

args = parser.parse_args()

secrets = None
with args.inputfile as f:
    lines = f.readlines()
    assert len(lines) > 0
    rounds = int(lines[0])
    secrets = [str(int(l)) for l in lines[1:]]
    assert len(secrets) == rounds

assert secrets is not None


def is_prime(x):
    for i in range(2, x):
        if x % i == 0:
            return False
        if i * i > x:
            return True


with subprocess.Popen(
    " ".join(args.program),
    shell=True,
    stdout=subprocess.PIPE,
    stdin=subprocess.PIPE,
    universal_newlines=True,
) as p:
    try:
        write(p, f"{rounds}")

        for secret in secrets:
            queries = 0
            while True:
                guess = read(p)
                queries += 1
                assert queries <= 6, "Used too many queries!"
                if guess == secret:
                    write(p, "ggggg")
                    print(f"\nQueries used: {queries}\n")
                    break

                assert len(guess) == 5, "You may only guess 5 digit numbers"

                assert is_prime(
                    int(guess)
                ), f"You may only guess primes. {guess} is not."

                # Make the reply
                reply = ["w", "w", "w", "w", "w"]
                # First make green characters.
                for i in range(5):
                    if guess[i] == secret[i]:
                        reply[i] = "g"
                # Make yellow characters.
                for i in range(5):
                    if reply[i] == "g":
                        continue
                    for j in range(5):
                        if guess[j] == secret[i]:
                            if reply[j] != "w":
                                continue
                            reply[j] = "y"
                            break
                write(p, "".join(reply))

        assert (
            p.stdout.readline() == ""
        ), "Your submission printed extra data after finding a solution."
        assert p.wait() == 0, "Your submission did not exit cleanly after finishing."

    except AssertionError as e:
        print()
        print(f"Error: {e}")
        print()
        try:
            p.wait(timeout=2)
        except subprocess.TimeoutExpired:
            print("Killing your submission after 2 second timeout.")
            p.kill()

    except Exception as e:
        print()
        traceback.print_exc()
        print()
        try:
            p.wait(timeout=2)
        except subprocess.TimeoutExpired:
            print("Killing your submission after 2 second timeout.")
            p.kill()
        raise e
    finally:
        print(f"Exit code: {p.wait()}\n", flush=True)
