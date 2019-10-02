#!/usr/bin/env python
import subprocess
import os
import glob
import argparse
import shutil
import shlex
import time

# ignore_ind = ["9", "8", "7", "6"]
# ignore_ind = ["7a", "6c", "6b", "3a", "6a", "3b", "4d", "4e", "5a", "4f", "8a", "8b"]
ignore_ind = ["3a", "4a", "4b", "4e", "5a", "6a", "6b", "7a", "8a", "8b", "6c"]
implementations = ["MyDict"]

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--input", help="Directory to take input from", default="./raw")
    parser.add_argument("--dict", help="Directory to take input from", default="dict8.txt")
    args = parser.parse_args()
    files = glob.glob(os.path.join(args.input, "*.txt"))
    i = 0
    for imple in implementations:
        results = []
        for file in files:
            ignore = False
            for ind in ignore_ind:
                if ind in file:
                    print("Ignoring %s for index: %s"%(file, ind))
                    ignore = True
            if ignore:
                continue
            print("Running on %s"%(file))
            command = "time crossword_solver %s %s %s"%(imple, args.dict, file)
            start = time.time()
            out = subprocess.Popen(shlex.split(command), stdout=subprocess.PIPE)
            exec_time = time.time() - start
            run_time = 2 * 60 * 60 # 2 hours in seconds
            # run_time = 2
            killed = False
            while out.poll() == None:
                exec_time = time.time() - start
                if exec_time > run_time:
                    killed = True
                    out.kill()
                    break;
                time.sleep(.1)
            result = 0
            if not killed:
                result = out.communicate()[0][:-1]
            results.append("%s\t%s\t%f\t%s\t%s"%(imple, file, exec_time, "True" if not killed else "False", result if not killed else "N/A"))
        for result in results:
            print(result)


if __name__ == "__main__":
    main()
