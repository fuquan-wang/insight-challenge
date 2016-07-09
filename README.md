# Table of Contents

1. [Code Introduction] (README.md#code-introduction)
2. [Running Script] (README.md#running-script)
3. [Dependency] (README.md#dependency)
4. [Generate Test Cases] (README.md#generate-test-cases)

This is a solution to [Insight Data Engineering program 2016 September session coding challenge](https://github.com/InsightDataScience/coding-challenge).

##Code Introduction

[Back to Table of Contents] (README.md#table-of-contents)

This solution is written in Java and contains two executables:
- src.median\_degree: the solution to the challenge
- src.genTestCase: the test case generator, not used as part of the solution, but was useful in testing my code

The `src/median_degree.java` uses the following three files with separate classes:
- `src/MedianCalculator.java`: calculating the median of a data stream, with support of adding or removing elements operations
- `src/PeriodGraph.java`: building and maintaining the transaction graph. It adds or removes graph vertice/edges according to the challenge request. It uses the `MedianCalculator` class to keep track of the current median value
- `src/VemonTransParser.java`: a custom parser for the Venmo transaction JSON. This is only created to avoid using exotic libraries in this challenge, otherwise using a general JSON parser may be a more elegant implementation

##Running Script

[Back to Table of Contents] (README.md#table-of-contents)

According to challenge request, the solution can be called with the script `run.sh`, which compiles the code, read `venmo_input/venmo-trans.txt` and output the result to `venmo_output/output.txt`. The `run.sh` content is as below:
<pre>
javac src/MedianCalculator.java src/PeriodGraph.java src/VemonTransParser.java src/median_degree.java
java src.median_degree
</pre>

##Dependency 

[Back to Table of Contents] (README.md#table-of-contents)

This solution only needs `javac` and `java` commands. So please make sure java-`version`-openjdk and java-`version`-oracle-devel packages are installed on the Linux system.
This solution has been tested with 
<pre>
java-1.8.0-openjdk.x86_64
java-1.8.0-openjdk-devel.x86_64
</pre>
and
<pre>
java-1.7.0-openjdk.x86_64
java-1.7.0-openjdk-devel.x86_64
</pre>

##Generate Test Cases

[Back to Table of Contents] (README.md#table-of-contents)

This is part is optional as it is not required by the challenge. However, it is efficient to use the included program to make solid tests.

To compile:
<pre>
javac src/genTestCase.java
</pre>

To run:
<pre>
java src.genTestCase random_number_seed
</pre>
which requires you to input a seed as the argument and it generates a new file at `venmo_input/venmo-trans.txt`

To tune the behavior of the generation, please update the following parameters (default values are shown) in the code and re-compile
<pre>
int nVtx = 5;
int nLines = 15;
long startDate = 1220227200L;
int inc = 15;
int variation = 180;
</pre>
