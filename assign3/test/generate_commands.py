#!/usr/bin/env python
import glob
import os
import sys

if __name__ == "__main__":
    files = [ os.path.split(f)[1] for f in glob.glob("src/*")]
    for file in files:
        compress_command = "run LZWmod - r < %s > %s"%("src/%s"%(file), "comp/%s"%(file))
        uncompress_command = "run LZWmod + < %s > %s"%("comp/%s"%(file), "un/%s"%(file))
        compare_command = "diff %s %s"%("src/%s"%(file), "un/%s"%(file))

        print(compress_command)
        print(uncompress_command)
        print(compare_command)
