#!/bin/bash

process_file() {
    file=$1
    second_line=$(sed -n '2p' "$file")
    rounds=$(echo "$second_line" | grep -oE '[0-9]+')

    py ../../data2023/cheapflying/attachments/testing_tool.py \
        -f "$file" -r "$rounds" java ProblemC2023Solver
}

ex() {
  rm ProblemC2023Solver.class
  exit "$1"
}

javac ProblemC2023Solver.java
if [[ "$1" == "-a" || "$1" == "--all" ]]; then
    for file in ../../data2023/cheapflying/data/secret/*.in; do
        process_file "$file"
    done
else
    if [ -z "$1" ]; then
        echo "Error: No file argument provided."
        ex 1
    fi
    file="../../data2023/cheapflying/data/secret/$1.in"
    if [ ! -f "$file" ]; then
        echo "Error: File not found."
        ex 1
    fi
    process_file "$file"
fi
ex 0