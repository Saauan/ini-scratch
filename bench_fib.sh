#!/usr/bin/env bash
TTIME=0
NB_RUNS=20
RUN_INI_SCRIPT="./run_ini.sh language/ini/truffle/bench/fibonacci_only_last.ini"
for (( i=0; i<NB_RUNS; i++ ))
do
TIME=$(/bin/bash $RUN_INI_SCRIPT --quiet)
echo $TIME
TTIME=$((TTIME + TIME))
done

AVG_TIME=$((TTIME / NB_RUNS))
echo "Average time over $NB_RUNS runs : $AVG_TIME"
