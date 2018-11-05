# Cipher Solver

This package is a WIP designed to solve [cryptogram](https://en.wikipedia.org/wiki/Cryptogram) puzzles with a dynamic programming approach. For each word in the input sentence, we look at possible matching dictionary words and create a set of Ciphers which represent potential solutions. We then compare these Ciphers against the next word and generate a new set of potential solutions.

Build with Maven:
mvn clean install

Check out the tests in TestCipherSolver.java for starters or just run "mvn clean test"
