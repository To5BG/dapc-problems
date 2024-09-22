#!/usr/bin/env python3
#
# Testing tool for the Cheap Flying problem
#
# Usage:
#
#   python3 testing_tool.py -f inputfile -r rounds <program invocation>
#
#
# Use the -f parameter to specify the input file, e.g. 1.in.
# Use the -r parameter to specify how many rounds of flights you will do.
# The actual interactor will determine this number on its own.
# Format of the input file:
# - One line with three integers a, b, and c (cost of one flight, cost of the aircraft, cost of one self-operated flight)
# e.g.:
# 5 50 2
#
#
# You can compile and run your solution as follows:

# C++:
#   g++ solution.cpp
#   python3 testing_tool.py -f 1.in -r 5 ./a.out

# Python3
#   python3 testing_tool.py -f 1.in -r 5 python3 ./solution.py

# Java
#   javac solution.java
#   python3 testing_tool.py -f 1.in -r 5 java solution

# Kotlin
#   kotlinc solution.kt
#   python3 testing_tool.py -f 1.in -r 5 kotlin solutionKt


# The tool is provided as-is, and you should feel free to make
# whatever alterations or augmentations you like to it.
#
# The tool attempts to detect and report common errors, but it is not an exhaustive test.
# It is not guaranteed that a program that passes this testing tool will be accepted.


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


parser = argparse.ArgumentParser(description="Testing tool for problem Cheap Flying.")
parser.add_argument(
    "-f",
    dest="inputfile",
    metavar="inputfile",
    default=None,
    type=argparse.FileType("r"),
    required=True,
    help="The input file to use.",
)
parser.add_argument(
    "-r",
    dest="rounds",
    metavar="rounds",
    default=None,
    type=int,
    required=False,
    help="The number of rounds, i.e. the number of flights",
)
parser.add_argument("program", nargs="+", help="Invocation of your solution")

args = parser.parse_args()

with args.inputfile as f:
    lines = f.readlines()
    assert len(lines) > 0

    a, b, c = map(int, lines[0].split())
    args.rounds = int(lines[1])

with subprocess.Popen(
    " ".join(args.program),
    shell=True,
    stdout=subprocess.PIPE,
    stdin=subprocess.PIPE,
    universal_newlines=True,
) as p:
    try:
        write(p, f"{a} {b} {c}")

        air = 0
        zelf = 0

        for i in range(0, args.rounds):
            write(p, "flight")

            op, *rest = read(p).split()
            if op == "airline":
                air += 1
            elif op == "buy":
                assert zelf == 0, f"You already bought the plane - and tried to do it again."
                zelf = 1
            elif op == "self":
                assert zelf != 0, f"You have not bought the plane yet - and tried to use it."
                zelf += 1
            else:
                assert False, f"Operation '{op}' is not one of 'airline', 'buy', or 'self'."

        write(p, "end")
        onlyAir = args.rounds * a
        onlySelf = args.rounds * c + b
        optimal = min(onlyAir, onlySelf)

        actual = air * a + zelf * c
        if zelf != 0:
            actual += b

        assert actual <= 2 * optimal, \
            f"Optimal cost is {optimal}, your program used {actual}, which is more than twice the optimum"

        print(f"\nSuccess.\n")

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
