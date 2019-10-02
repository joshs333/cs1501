#!/usr/bin/env python

def main():
    lengths = {}
    even = 0
    odd = 0
    print("testing")
    with open("dict8.txt", "r") as fi:
        lines = fi.read()
        for li in lines.split("\n"):
            if len(li) not in lengths:
                lengths[len(li)] = 0
            lengths[len(li)] += 1
            if len(li) % 2 == 0:
                even += 1
            else:
                odd += 1
    for key in lengths:
        print("%d: %d"%(key, lengths[key]))
    print("Even: %d"%(even))
    print("Odd: %d"%(odd))

if __name__=="__main__":
    main()
