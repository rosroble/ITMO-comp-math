package ru.rosroble.eqsolver.interfaces;

import ru.rosroble.eqsolver.result.Result;
import ru.rosroble.eqsolver.exceptions.DivergenceException;

@FunctionalInterface
public interface EquationSolver {
    Result solve(Function f, double a, double b) throws DivergenceException;
}
