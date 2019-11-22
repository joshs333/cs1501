#!/bin/bash
WORKSPACE_DIRECTORY=/Users/joshua.spisak/Coding/school/cs1501/lab12
WORKSPACE_BUILD_DIRECTORY=/Users/joshua.spisak/Coding/school/cs1501/lab12/build
WORKSPACE_FILE_INFO_FILE=${WORKSPACE_BUILD_DIRECTORY}/JMAKE_FILE_INFO.txt
WORKSPACE_BUILD_INFO_FILE=${WORKSPACE_BUILD_DIRECTORY}/JMAKE_BUILD_INFO.txt
if [ ! $PATH == *"/Users/joshua.spisak/Coding/school/cs1501/lab12/build"* ]; then
    PATH="$PATH:/Users/joshua.spisak/Coding/school/cs1501/lab12/build"
fi
alias workspace_cd="cd $WORKSPACE_DIRECTORY"

##
# @brief goes to the build directory, makes code then returns
##
function build() {
    start_dir=$(pwd)
    if [ ! -e $WORKSPACE_BUILD_DIRECTORY ] || [ ! -f $WORKSPACE_BUILD_INFO_FILE ]; then
        mkdir -p ${WORKSPACE_BUILD_DIRECTORY}
        cd ${WORKSPACE_BUILD_DIRECTORY}
        jmake $WORKSPACE_DIRECTORY
    fi
    cd ${WORKSPACE_BUILD_DIRECTORY}
    jmake
    cd $start_dir
}

##
# @brief finds a class that was built based on name/package then executes it
# @param CLASS_TO_RUN ($1) format: [<jmake package>/](<java class>|<java package>.<java class>) class to execute
# @param ARGUMENTS_TO_EXEC ($2 - $#) arguments that will be passed to the java runtime
# @returns 1 if build not made, 2 if no arguments give, 3 if class not found, 4 if class collision and no package specified
#  5 if collision experienced with a package provided. Otherwise returns the programs return code.
##
function run() {
    # Check arguments/environment
    if [ ! -f $WORKSPACE_FILE_INFO_FILE ]; then
        echo "JMAKE_FILE_INFO.txt does not exist in build directory! Unable to execute. Please build first."
        return 1
    fi
    # Check arguments/environment
    if [ -z $1 ]; then
        echo "Insufficient arguments."
        echo "    run [<jmake package>/]<class to execute> [<arg> ... ]"
        return 2
    fi


    # Get arguments/data
    CLASS_TO_RUN=$1
    ARGUMENTS_TO_EXEC=""
    for ((i=2;i<$# + 1;++i)); do
        if [ -z "$ARGUMENTS_TO_EXEC" ]; then
            ARGUMENTS_TO_EXEC="\"${!i}\""
        else
            ARGUMENTS_TO_EXEC="$ARGUMENTS_TO_EXEC \"${!i}\""
        fi
    done
    source $WORKSPACE_FILE_INFO_FILE
    PKG_TO_FIND=$(dirname $CLASS_TO_RUN)
    CLASS_TO_FIND=$(basename $CLASS_TO_RUN)

    if [ $PKG_TO_FIND == "." ]; then
        PKG_TO_FIND=""
    fi

    FOUND=false
    CLASS_COLLISION=false
    ORIGINAL_COLLISION=false
    FULL_CLASS_PATH=""
    FOUND_PACKAGE_NAME=""
    for ((i=0;i<${#JMAKE_CLASS_NAME[@]};++i)); do
        MATCH=false
        if [ ! -z $PKG_TO_FIND ]; then
            if [ $CLASS_TO_FIND == ${JMAKE_CLASS_NAME[i]} ] && [ $PKG_TO_FIND == ${JMAKE_CLASS_PKG[i]} ]; then
                MATCH=true
            fi
        elif [ $CLASS_TO_FIND == ${JMAKE_CLASS_NAME[i]} ]; then
            MATCH=true
        fi
        if [ $MATCH == true ] && [ $FOUND == true ]; then
            CLASS_COLLISION=true
        elif [ $MATCH == true ]; then
            FOUND=true
            FULL_CLASS_PATH=${JMAKE_CLASS_FULL_NAME[i]}
            FOUND_PACKAGE_NAME=${JMAKE_CLASS_PKG[i]}
        fi
    done
    ORIGINAL_COLLISION=$CLASS_COLLISION
    # If we didn't find it or we have a collision then we will try to resolve it
    # To the full class name (package and class)
    if [ $FOUND == false ] || [ $CLASS_COLLISION == true ]; then
        FOUND=false
        CLASS_COLLISION=false
        FULL_CLASS_PATH=""
        FOUND_PACKAGE_NAME=""
        for ((i=0;i<${#JMAKE_CLASS_FULL_NAME[@]};++i)); do
            MATCH=false
            if [ ! -z $PKG_TO_FIND ]; then
                if [ $CLASS_TO_FIND == ${JMAKE_CLASS_FULL_NAME[i]} ] && [ $PKG_TO_FIND == ${JMAKE_CLASS_PKG[i]} ]; then
                    MATCH=true
                fi
            elif [ $CLASS_TO_FIND == ${JMAKE_CLASS_FULL_NAME[i]} ]; then
                MATCH=true
            fi
            if [ $MATCH == true ] && [ $FOUND == true ]; then
                CLASS_COLLISION=true
            elif [ $MATCH == true ]; then
                FOUND=true
                FULL_CLASS_PATH=${JMAKE_CLASS_FULL_NAME[i]}
                FOUND_PACKAGE_NAME=${JMAKE_CLASS_PKG[i]}
            fi
        done
    fi
    # Check results
    if [ $FOUND == false ]; then
        if [ ! $ORIGINAL_COLLISION == true ]; then
            echo "Collision experienced finding ${CLASS_TO_FIND}, please specify a jmake package using the format [<jmake package>/]<class to execute> to help."
            return 4
        else
            echo "Unable to find ${CLASS_TO_FIND}."
            return 3
        fi
    fi
    if [ $CLASS_COLLISION == true ]; then
        if [ -z $PKG_TO_FIND ]; then
            echo "Collision experienced finding ${CLASS_TO_FIND}, please specify a jmake package using the format [<jmake package>/]<class to execute> to help."
            return 4
        else
            echo "Collision experienced finding ${CLASS_TO_FIND} in ${PKG_TO_FIND}. Please specify the full path to the class (eg: <java package>.<class name>)."
            return 5
        fi
    fi
    PKG_CLASS_PATH=${JMAKE_BUILD_DIRECTORY}/${JMAKE_PKG_DIR_PREFIX}${FOUND_PACKAGE_NAME}
    eval "java -cp $PKG_CLASS_PATH ${FULL_CLASS_PATH} ${ARGUMENTS_TO_EXEC}"
    return $?
}
_run_completions() {
    if [ "${#COMP_WORDS[@]}" == "2" ] && [ -f "$WORKSPACE_FILE_INFO_FILE" ]; then
        source $WORKSPACE_FILE_INFO_FILE
        options=()
        for ((i=0;i<${#JMAKE_CLASS_NAME[@]};++i)); do
            CLASS_MATCH=false
            PKG_MATCH=false
            NAME_MATCH=false
            for ((j=0;j<${#JMAKE_CLASS_NAME[@]};++j)); do
                if [ $i == $j ]; then
                    continue
                fi
                if [ ${JMAKE_CLASS_NAME[i]} == ${JMAKE_CLASS_NAME[j]} ] && [ ${JMAKE_CLASS_FULL_NAME[i]} == ${JMAKE_CLASS_FULL_NAME[j]} ]; then
                    # short name & full class name match (package can be added to either to make a match)
                    CLASS_MATCH=true
                elif [ ${JMAKE_CLASS_NAME[i]} == ${JMAKE_CLASS_NAME[j]} ] && [ ${JMAKE_CLASS_PKG[i]} == ${JMAKE_CLASS_PKG[j]} ]; then
                    # short name & package name match (full path name can be used to make a match)
                    PKG_MATCH=true
                elif [ ${JMAKE_CLASS_NAME[i]} == ${JMAKE_CLASS_NAME[j]} ]; then
                    # Only the short name matches, full name or package name added will match
                    NAME_MATCH=true
                fi
            done
            # The fully resolved path should always work
            options+=("${JMAKE_CLASS_PKG[i]}/${JMAKE_CLASS_FULL_NAME[i]}")
            if [ $CLASS_MATCH == true ] && [ $PKG_MATCH == true ]; then
                # nothing special happens in this case...
                $()
            elif [ $CLASS_MATCH == true ]; then
                # the package can be added to the short name to make it match
                options+=("${JMAKE_CLASS_PKG[i]}/${JMAKE_CLASS_NAME[i]}")
            elif [ $PKG_MATCH == true ]; then
                # the using the full package name will produce a match
                options+=("${JMAKE_CLASS_FULL_NAME[i]}")
            elif [ $NAME_MATCH == true ]; then
                # the using the full package name or adding a package will resolve it will produce a match
                options+=("${JMAKE_CLASS_FULL_NAME[i]}")
                options+=("${JMAKE_CLASS_PKG[i]}/${JMAKE_CLASS_NAME[i]}")
            else
                # Anything can be used to resolve it!
                options+=("${JMAKE_CLASS_FULL_NAME[i]}")
                options+=("${JMAKE_CLASS_PKG[i]}/${JMAKE_CLASS_NAME[i]}")
                options+=("${JMAKE_CLASS_NAME[i]}")
            fi
        done
        options="${options[@]}"
        COMPREPLY=($(compgen -W "$options" ${COMP_WORDS[1]}))
    else
        COMPREPLY=($(compgen -f -d ${COMP_WORDS[$COMP_CWORD]}))
    fi
}
complete -F _run_completions run
