Grid Search [![Build Status](https://travis-ci.org/m09/grid-search.svg?branch=master)](https://travis-ci.org/m09/grid-search)
===

Basic implementation of a grid search with a simple API. Java 8 allows
a clean specification of the objective function thanks to its
lambdas. You should certainly prefer the optimization methods of the
[Commons Math library][cm] instead of this grid search implementation
if they can be used.

Common application is for example to go through hyperparameters when
tuning an algorithm.

[cm]: http://commons.apache.org/proper/commons-math/

Requirements
------------

To use this Java library, you need Java 8 and Maven 3.

Installation
------------

With Maven, you can just add the following to the `dependencies`
section of your `pom.xml`:

```xml
    <dependency>
      <groupId>eu.crydee</groupId>
      <artifactId>grid-search</artifactId>
      <version>2.1.1</version>
    </dependency>
```

If you do not use maven, you can still [download][dl] the jar from
Maven Central and use it as appropriate.

[dl]: http://search.maven.org/remotecontent?filepath=eu/crydee/grid-search/2.1.0/grid-search-2.1.1.jar

Usage
-----

The entry point of the library is its `estimate` method. For example,
to optimize the parameters a, b and c to maximize f = a - b - (c - 2)²
with a in [0, 4], b in [0, 5] and c in [0, 6], you can use:

```java
Gridsearch gridsearch = new Gridsearch();
double[] result = gridsearch.estimate(
        new double[]{4d, 5d, 6d},
        new double[]{0d, 0d, 0d},
        10,
        3,
        s -> s[0] - s[1] - Math.pow(s[2] - 2, 2),
        Objective.MAXIMIZE);
// result holds something close to [4d, 0d, 2d]
```
