#!/usr/bin/env python3

import os
import sys

prefix = sys.argv[3]

with open(sys.argv[1], "r") as handle:
    with open(sys.argv[2], "w") as out:
        for l in handle:
            line = l.strip()

            parts = os.path.split(line)

            basedir = parts[0] if len(parts) > 1 else ""

            print(line + "\t" + os.path.join(prefix, basedir), file=out)




