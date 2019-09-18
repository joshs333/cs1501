#!/bin/bash
# @file sc.bash
# @brief provides shortcuts and aliasing for running and testing the lexical analyzer
# @author Joshua Spisak <jjs231@pitt.edu>
export PROJECT_ROOT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
export PATH="$PATH:$PROJECT_ROOT_DIR/build"

##
# @brief goes to build directory and executes the make command
# @details creates the build directory if it does not exist and
#  executes cmake .. if needed. Also passes all arguments to make
function build() {
    root_dir=$(pwd)
    if [ ! -d ${PROJECT_ROOT_DIR}/build ]; then
        mkdir ${PROJECT_ROOT_DIR}/build
        cd ${PROJECT_ROOT_DIR}/build
    fi

    cd ${PROJECT_ROOT_DIR}/build
    jmake $@
    cd $root_dir
}

##
# Runs the lexer against all sample data provided.
function test() {
    input=$1
    verb=$2
    if [ "$1" == "-b" ]; then
        build
        input=$2
        verb=$3
    fi
    if [ -z "$input" ]; then
        input="test3b.txt"
    fi
    #TODO(joshua.spisak): implement test script
    root_dir=$(pwd)
    cd ${PROJECT_ROOT_DIR}/test
    crossword_solver DLB dict8.txt raw/$input $verb
    cd $root_dir
}
