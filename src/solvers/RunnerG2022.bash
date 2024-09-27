#!/bin/bash

ex() {
  rm ProblemG2022Solver.class
  exit "$1"
}

javac ProblemG2022Solver.java
if [[ "$1" == "-a" || "$1" == "--all" ]]; then
    for file in ../../data2022/guessingprimes/data/secret/*.in; do
        py ../../data2022/guessingprimes/attachments/testing_tool.py \
            -f "$file" java ProblemG2022Solver
    done
else
    if [ -z "$1" ]; then
        echo "Error: No file argument provided."
        ex 1
    fi
    file="../../data2022/guessingprimes/data/secret/$1.in"
    if [ ! -f "$file" ]; then
        echo "Error: File not found."
        ex 1
    fi
    py ../../data2022/guessingprimes/attachments/testing_tool.py \
        -f "$1" java ProblemG2022Solver
fi
ex 0
