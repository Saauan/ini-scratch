#!/usr/bin/env bash
TTIME=0
NB_RUNS=70
CONFIDENCE_COEF=2
RUN_INI_SCRIPT="./run_ini.sh language/ini/truffle/bench/fibonacci_only_last.ini"

declare -a INDIV_TIMES
for (( i=0; i<NB_RUNS; i++ ))
do
TIME=$(/bin/bash $RUN_INI_SCRIPT --quiet)
echo "Run number $i : $TIME"
INDIV_TIMES+=($TIME)
TTIME=$((TTIME + TIME))
done

AVG_TIME=$((TTIME / NB_RUNS))
echo "Average time over $NB_RUNS runs : $AVG_TIME"

SQSQUARE=0
for i in "${INDIV_TIMES[@]}"
do
    # echo $i
    # echo $(($i - AVG_TIME))
    SQSQUARE=$((SQSQUARE + (($i - AVG_TIME)**2)))
done
# echo "SQSQUARE = $SQSQUARE"
STD_DEVIATION=$(echo "sqrt ($SQSQUARE / $NB_RUNS)" | bc -l)
echo "Standard deviation = $STD_DEVIATION"

ERROR_MARGIN=$(echo "$CONFIDENCE_COEF * ( $STD_DEVIATION / sqrt ($NB_RUNS))" | bc -l)
echo "Error margin = $ERROR_MARGIN"
INTERVAL_LEFT=$(echo "$AVG_TIME - $ERROR_MARGIN" | bc -l)
INTERVAL_RIGHT=$(echo "$AVG_TIME + $ERROR_MARGIN" | bc -l)
echo "Confidence interval = [$INTERVAL_LEFT ; $INTERVAL_RIGHT]"