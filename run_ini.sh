#!/usr/bin/env bash

VERSION="20.0.0"

MAIN_CLASS="ini.IniMain"
SCRIPT_HOME="$(cd "$(dirname "$0")" && pwd -P)"
echo $SCRIPT_HOME

function extractGraalVMVersion() {
    local -r releasePath="${1:?Path to relese file is a required parameter}"
    grep "GRAALVM_VERSION" "$releasePath" 2> /dev/null \
        | awk 'BEGIN {FS="="} {print $2}' \
        | sed 's/"//g'
}

function fail() {
    local -r message="${1:-Unknown error}"
    local -r exitCode="${2:-1}"
    >&2 echo "$message"
    exit "$exitCode"
}

#######################################################################
# Locations of the language and launcher jars as well as the java command are
# different if I'm running from the repository or as a component in GraalVM
#######################################################################
GRAALVM_VERSION=$(extractGraalVMVersion "$SCRIPT_HOME/../release")
if [[ "$GRAALVM_VERSION" != "" ]]; then
    LANGUAGE_PATH=""
    # contains paths for both jdk8 and jdk11
    #LAUNCHER_PATH="$SCRIPT_HOME/../jre/languages/sl/launcher/sl-launcher.jar:$SCRIPT_HOME/../languages/sl/launcher/sl-launcher.jar"
    LAUNCHER_PATH="$SCRIPT_HOME/../jre/manguaegs/ini/ini.jar"
    JAVACMD="$SCRIPT_HOME/java"
    if [[ "$GRAALVM_VERSION" != "$VERSION" ]]; then
        fail "Installed in wrong version of GraalVM. Expected: $VERSION, found $GRAALVM_VERSION"
    fi
else
    LANGUAGE_PATH="$SCRIPT_HOME/language/target/ini.jar"
    LAUNCHER_PATH="$SCRIPT_HOME/launcher/target/ini-launcher.jar"
    # Check the GraalVM version in JAVA_HOME
    if [[ "$JAVA_HOME" != "" ]]; then
        GRAALVM_VERSION=$(extractGraalVMVersion "$JAVA_HOME"/release)
        if [[ "$GRAALVM_VERSION" != "" ]]; then
            if [[ "$GRAALVM_VERSION" != "$VERSION" ]]; then
                fail "Wrong version of GraalVM in \$JAVA_HOME. Expected: $VERSION, found $GRAALVM_VERSION"
            fi
        fi
        JAVACMD=${JAVACMD:=$JAVA_HOME/bin/java}
        if [[ ! -f $LANGUAGE_PATH ]]; then
            fail "Could not find language on $LANGUAGE_PATH. Did you run mvn package?"
        fi
        if [[ ! -f $LAUNCHER_PATH ]]; then
            fail "Could not find launcher on $LAUNCHER_PATH. Did you run mvn package?"
        fi
    else
        fail "JAVA_HOME is not set"
        exit 1
    fi
fi

#######################################################################
# Parse arguments, prepare Java command and execute
#######################################################################
if [[ "$GRAALVM_VERSION" != "" ]]; then
    PROGRAM_ARGS=()
    JAVA_ARGS=()

    for opt in "$@"
    do
      case $opt in
        -debug)
            JAVA_ARGS+=("-Xdebug" "-Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=y") ;;
        -dump)
            JAVA_ARGS+=("-Dgraal.Dump=Truffle:1" "-Dgraal.TruffleBackgroundCompilation=false" "-Dgraal.TraceTruffleCompilation=true" "-Dgraal.TraceTruffleCompilationDetails=true") ;;
        -disassemble)
            JAVA_ARGS+=("-XX:CompileCommand=print,*OptimizedCallTarget.callRoot" "-XX:CompileCommand=exclude,*OptimizedCallTarget.callRoot" "-Dgraal.TruffleBackgroundCompilation=false" "-Dgraal.TraceTruffleCompilation=true" "-Dgraal.TraceTruffleCompilationDetails=true") ;;
        -J*)
            opt=${opt:2}
            JAVA_ARGS+=("$opt") ;;
        *)
            PROGRAM_ARGS+=("$opt") ;;
      esac
    done
    "$JAVACMD" "${JAVA_ARGS[@]}" -Dtruffle.class.path.append="$LANGUAGE_PATH" -cp "$LAUNCHER_PATH" "$MAIN_CLASS" "${PROGRAM_ARGS[@]}"
else
    echo "Warning: Could not find GraalVM on $JAVA_HOME. Running on JDK without support for compilation."
    echo
    PROGRAM_ARGS=()
    JAVA_ARGS=()

    for opt in "$@"
    do
      case $opt in
        -debug)
            JAVA_ARGS+=("-Xdebug" "-Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=y") ;;
        -dump)
            echo "NOTE: Ignoring -dump, only supported on GraalVM." ;;
        -disassemble)
            echo "NOTE: Ignoring -disassemble" ;;
        -J*)
            opt=${opt:2}
            JAVA_ARGS+=("$opt") ;;
        *)
            PROGRAM_ARGS+=("$opt") ;;
      esac
    done
    if [[ ! -d $HOME/.m2 ]]; then
        echo "Could not find mvn cache at $HOME/.m2"
        exit 1
    fi
    GRAAL_SDK_PATH="$HOME/.m2/repository/org/graalvm/sdk/graal-sdk/$VERSION/graal-sdk-$VERSION.jar"
    TRUFFLE_API_PATH="$HOME/.m2/repository/org/graalvm/truffle/truffle-api/$VERSION/truffle-api-$VERSION.jar"
    "$JAVACMD" "${JAVA_ARGS[@]}" -cp "$GRAAL_SDK_PATH":"$LAUNCHER_PATH":"$LANGUAGE_PATH":"$TRUFFLE_API_PATH" "$MAIN_CLASS" "${PROGRAM_ARGS[@]}"
fi
