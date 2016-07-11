#!/usr/bin/env bash

javac -cp ".:jars/json-simple-1.1.1.jar" src/MedianCalculator.java src/PeriodGraph.java src/median_degree.java
java -cp ".:jars/json-simple-1.1.1.jar" src.median_degree

