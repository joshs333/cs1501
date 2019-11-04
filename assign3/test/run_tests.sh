#!/bin/bash
source ../setup.bash
while read -r p; do
    echo "running $p"
    eval time $p
    if (( $? != 0 )); then
        echo "!!!!!FAILED!!!!!!  $p !!!!!!!!"
    fi
done <<< "$(python generate_commands.py)"
