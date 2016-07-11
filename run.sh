#!/usr/bin/env bash

javac -cp ".:src/json-simple-1.1.1.jar" src/MedianCalculator.java src/PeriodGraph.java src/median_degree.java
[ ! -d venmo_output ] && mkdir venmo_output;
java -cp ".:src/json-simple-1.1.1.jar" src.median_degree

