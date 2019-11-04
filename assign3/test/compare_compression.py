#!/usr/bin/env python
import subprocess
import glob
import os
import shlex
import sys

if __name__ == "__main__":
    files = [os.path.split(f)[1] for f in glob.glob("src/*")]
    for file in files:
        src = "src/%s"%(file)
        comp = "comp/%s"%(file)
        src_size = os.stat(src).st_size
        comp_size = os.stat(comp).st_size
        print("%s\t%3.2f\t%3.2f"%(file, src_size, comp_size))
