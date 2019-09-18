#!/usr/bin/env python
import subprocess
import os
import glob
import argparse
import shutil
import shlex

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--input", help="Directory to take input from", default="./input")
    parser.add_argument("--control", help="Directory containing control output to compare", default="./control")
    parser.add_argument("--output", help="Directory to place output", default="./output")
    args = parser.parse_args()
    if os.path.exists(args.output):
        shutil.rmtree(args.output)
    os.mkdir(args.output)

    input_data_raw = glob.glob(os.path.join(args.input, "*.in"))
    control_data_raw = glob.glob(os.path.join(args.control, "*.out"))
    tests = []
    for file in input_data_raw:
        base = os.path.split(file[:-3])[1]
        out_name = os.path.join(args.control,base + ".out")
        if out_name in control_data_raw:
            tests.append(base)
        else:
            print("No control data found for test: %s"%(os.path.split(base)[1]))

    successes = []
    failures = []
    for test in tests:
        output = ""
        failure = False
        input_file = os.path.join(args.input, test + ".in")
        control_file = os.path.join(args.control, test + ".out")
        output_file = os.path.join(args.output, test + ".test")
        command = "lexer %s %s"%(input_file, output_file)
        test_command = "diff %s %s"%(control_file, output_file)

        output += "*** Running test: %s ****\n"%(test)
        output += "Using command: %s\n"%(command)
        try:
            subprocess.check_output(shlex.split(command))
        except Exception as err:
            output += "!!! Command failed.\n"
            failure = True
        try:
            output += "Testing output with command: %s\n"%(test_command)
            subprocess.check_output(shlex.split(test_command))
            successes.append(test)
        except Exception as err:
            output += "!!! Test Failed.\n"
            failures.append(test)
        if failure:
            print(output)
            print("")

    if len(successes) != 0:
        print("%s Tests Succeeded."%(len(successes)))
        for test in successes:
            print("    %s"%(test))
    else:
        print("No Tests Succeeded.")

    if len(failures) != 0:
        print("%s Tests Failed."%(len(failures)))
        for test in failures:
            print("    %s"%(test))
    else:
        print("No Tests Failed.")

if __name__ == "__main__":
    main()
