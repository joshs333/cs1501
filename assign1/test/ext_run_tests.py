#!/usr/bin/env python
import subprocess
import os
import glob
import argparse
import shutil
import shlex
import time
import random

algorithm_type = "NotDLB"
log_file = None

class Board(object):
    letters = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z']
    def __init__(self, size):
        self.board = []
        self.size = size
        for i in range(size):
            self.board.append([])
            for j in range(size):
                self.board[i].append("+")

    def set_alpha(self, num):
        positions = []
        for i in range(self.size):
            for j in range(self.size):
                position = (i, j)
                positions.append(position)
        fill_positions = random.sample(positions, num)
        for pos in fill_positions:
            self.board[pos[0]][pos[1]] = random.sample(self.letters, 1)[0]

    def set_filled(self, num):
        positions = []
        for i in range(self.size):
            for j in range(self.size):
                position = (i, j)
                positions.append(position)
        fill_positions = random.sample(positions, num)
        for pos in fill_positions:
            self.board[pos[0]][pos[1]] = '-'

    def reset(self):
        self.board = []
        for i in range(self.size):
            self.board.append([])
            for j in range(self.size):
                self.board[i].append("+")

    def print_board(self, func=None):
        if func == None:
            print(self.size)
        else:
            func(str(self.size) + '\n')
        for i in range(self.size):
            row = ""
            for j in range(self.size):
                row += self.board[i][j]
            if func == None:
                print(row)
            else:
                func(row + '\n')

def run_test(dict, board_file, run_time):
    # print("** Running on %s **"%(board_file))
    command = "time crossword_solver %s %s %s"%(algorithm_type, dict, board_file)
    start = time.time()
    out = subprocess.Popen(shlex.split(command), stdout=subprocess.PIPE)
    # run_time = 10
    exec_time = 0
    while out.poll() == None:
        exec_time = time.time() - start
        if exec_time > run_time:
            out.kill()
            return (exec_time, False, 0)
        time.sleep(.1)
    exec_time = time.time() - start
    results = out.communicate()[0]
    if results[0] == "-":
        return (exec_time, True, 314159)
    return (exec_time, True, int(results[0]))
    # print("** Finished on %s - %f **"%(board_file, exec_time))

def test_board(size, times, test_file_output, dict, timeout, min_fill, max_fill):
    result_map = {}
    result_map["filled"] = {}
    result_map["preset"] = {}
    b = Board(size)
    for i in range(size * size + 1):
        i = size * size - i
        if i < min_fill or i > max_fill:
            continue
        new_times = times if i != 0 else 1
        for j in range(new_times):
            b.reset()
            b.set_filled(i)
            file_name = "my_test_%d_filled_%d_%d.txt"%(size, i, j)
            file_name = os.path.join(test_file_output, file_name)
            with open(file_name, "w+") as f:
                b.print_board(f.write)
            results = run_test(dict, file_name, timeout)
            exec_time, finished, count = results
            log_file.write("%s\t%f\t%s\t%d\n"%(file_name, exec_time, "True" if finished else "False", count))
            if finished:
                print("%s got %d in %f"%(file_name, count, exec_time))
            else:
                print("%s timed out in %f"%(file_name, exec_time))
            if i not in result_map["filled"]:
                result_map["filled"][i] = []
            result_map["filled"][i].append(results)
        for j in range(new_times):
            b.reset()
            b.set_alpha(i)
            file_name = "my_test_%d_preset_%d_%d.txt"%(size, i, j)
            file_name = os.path.join(test_file_output, file_name)
            # with open(file_name, "w+") as f:
            #    b.print_board(f.write)
            results = run_test(dict, file_name, timeout)
            exec_time, finished, count = results
            log_file.write("%s\t%f\t%s\t%d\n"%(file_name, exec_time, "True" if finished else "False", count))
            if finished:
                print("%s got %d in %f"%(file_name, count, exec_time))
            else:
                print("%s timed out in %f"%(file_name, exec_time))
            if i not in result_map["preset"]:
                result_map["preset"][i] = []
            result_map["preset"][i].append(results)
    return result_map

def print_table(data):
    for key in data:
        exec_avg = 0.0
        result_avg = 0
        count = 0
        for result in data[key]:
            time, fin, sols = result
            if fin:
                exec_avg += time
                count += 1
                result_avg += sols
        if count != 0:
            exec_avg /= count
            result_avg /= count
            print("%d\t%f\t%d"%(key, exec_avg, result_avg))

def run_tests(args):
    results = {}
    combined_results = {}
    combined_results["filled"] = {}
    combined_results["preset"] = {}
    for i in range(args.min_size, args.max_size + 1):
        results[i] = test_board(i, args.times, args.test_file_output, args.dict, args.timeout, args.min_fill, args.max_fill)
        for key in results[i]["filled"]:
            if key not in combined_results["filled"]:
                combined_results["filled"][key] = []
            combined_results["filled"][key].extend(results[i]["filled"][key])
        for key in results[i]["preset"]:
            if key not in combined_results["preset"]:
                combined_results["preset"][key] = []
            combined_results["preset"][key].extend(results[i]["preset"][key])

    # print(combined_results)
    for i in results:
        print("%d Results Filled"%(i))
        print_table(results[i]["filled"])
        print("%d Results Preset"%(i))
        print_table(results[i]["preset"])
    print("Combined Results Filled")
    print_table(combined_results["filled"])
    print("Combined Results Preset")
    print_table(combined_results["preset"])
    # print(results)

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--min_size", help="Minimum size of grid (inclusive)", default=3, type=int)
    parser.add_argument("--max_size", help="Maximum size of grid (inclusive)", default=4, type=int)
    parser.add_argument("--min_fill", help="Minimum size of grid filled (inclusive)", default=0, type=int)
    parser.add_argument("--max_fill", help="Maximum size of grid filled (inclusive)", default=100, type=int)
    parser.add_argument("--times", help="Times per test", default=3, type=int)
    parser.add_argument("--timeout", help="Times to allow a test run", default=2 * 60 * 60, type=int)
    parser.add_argument("--test_file_output", help="Location to write test_files", default="./my_tests")
    parser.add_argument("--dict", help="Directory to take input from", default="dict8.txt")
    parser.add_argument("--output", help="File to write results to", default="ndlb_my_test_results.txt")
    args = parser.parse_args()
    global log_file
    log_file = open(args.output, "w+")
    run_tests(args)
    algorithm_type = "MyDict"
    run_tests(args)
    log_file.close()

if __name__ == "__main__":
    main()
