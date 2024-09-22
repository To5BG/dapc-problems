#!/usr/bin/env python3
import sys


# Source: https://stackoverflow.com/a/48116944
def rot_90(l): return [list(reversed(x)) for x in zip(*l)]


block = open(sys.argv[1], "r").read().strip().splitlines()[1:]
answer = open(sys.argv[2], "r").read().strip().splitlines()

if answer[0] == "impossible":
    exit(42 + (input() != "impossible"))

team_h, team_w, *team_answer = sys.stdin.read().strip().split()
team_h = int(team_h)
team_w = int(team_w)

if all("." not in line for line in team_answer):
    print("No empty space found in team answer", file=sys.stderr)
    exit(43)

first_filled_line = next((i for i, line in enumerate(team_answer) if "#" in line), None)
if first_filled_line is not None and any("#" not in line for line in team_answer[first_filled_line:]):
    print("Found empty line between filled lines in team answer", file=sys.stderr)
    exit(43)

# print("\n".join("".join(lt) for lt in team_answer), file=sys.stderr)
# print(file=sys.stderr)

for _ in range(4):
    block = rot_90(block)

    bottom_block = [i for i, c in enumerate(block[-1]) if c == "#"]
    bottom_team = [i for i, c in enumerate(team_answer[-1]) if c == "."]
    # print(bottom_block, bottom_team, file=sys.stderr)
    if len(bottom_block) != len(bottom_team):
        continue
    x_offsets = set(t - b for b, t in zip(bottom_block, bottom_team))
    if len(x_offsets) != 1:
        continue
    x_offset = x_offsets.pop()  # Should be non-negative, because len(bottom_team) >= len(bottom_block)

    # print("\n".join("".join(line) for line in block), file=sys.stderr)
    # print(file=sys.stderr)

    # The top rows should contain only dots, the bottom rows should fit the block perfectly (c != d)
    if all("#" not in lt for lt in team_answer[:-len(block)]) and \
            all(all(c != d for c, d in zip(["."] * x_offset + lb + ["."] * (len(lt) - len(lb) - x_offset), lt))
                for lb, lt in zip(block, team_answer[-len(block):])):
        exit(42)

print("No rotation of the input block fits in the team's grid", file=sys.stderr)
exit(43)
