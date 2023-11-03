# Hungarian Algorithm in Java

This repository contains a Java implementation of the Hungarian Algorithm, specifically the `HungarianAlg3` class. The Hungarian Algorithm is a combinatorial optimization technique used to solve the assignment problem efficiently. It finds the optimal assignment of workers to jobs or tasks in a way that minimizes the total cost.

## Introduction

The `HungarianAlg3` class in this repository provides an implementation of the Hungarian Algorithm for solving the assignment problem. It takes a cost matrix as input, where each cell in the matrix represents the cost of assigning a worker to a job. The algorithm finds the optimal assignment that minimizes the total cost while ensuring that each worker is assigned to exactly one job, and each job is assigned to exactly one worker.

## Usage

To use the `HungarianAlg3` class to solve an assignment problem, follow these steps:

1. Create a 2D cost matrix where `matrix[i][j]` represents the cost of assigning worker `i` to job `j`.
2. Initialize an instance of `HungarianAlg3` with the cost matrix.

```python
double[][] costMatrix = // Initialize your cost matrix
HungarianAlg3 hungarian = new HungarianAlg3(costMatrix);
```
Execute the Hungarian Algorithm by calling the execute() method.

```python
int[] assignment = hungarian.execute();
```
The assignment array will contain the optimal assignment of workers to jobs. If assignment[i] = j, it means that worker i is assigned to job j. If assignment[i] = -1, it means that worker i is not assigned to any job.

You can compute the total cost of the assignment using the computeCost method provided in the HungarianAlg3 class.

```python
double totalCost = hungarian.computeCost(costMatrix, assignment);
```
Example
Here's a simple example of using the Hungarian Algorithm to solve an assignment problem:

```python
double[][] costMatrix = {
    {3, 2, 7},
    {2, 4, 5},
    {6, 1, 3}
};

HungarianAlg3 hungarian = new HungarianAlg3(costMatrix);
int[] assignment = hungarian.execute();
double totalCost = hungarian.computeCost(costMatrix, assignment);
```
In this example, assignment will contain the optimal assignment, and totalCost will hold the minimum total cost.
