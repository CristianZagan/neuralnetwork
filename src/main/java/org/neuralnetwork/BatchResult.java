package org.neuralnetwork;

import cave.matrix.Matrix;

import java.util.LinkedList;

public class BatchResult {
    private LinkedList<Matrix> io = new LinkedList<>();
    private LinkedList<Matrix> weightErrors = new LinkedList<>();
    private Matrix inputError;

    public LinkedList<Matrix> getIo() {
        return io;
    }

    public void  addIo(Matrix m) {
        io.add(m);
    }

    public Matrix getOutput() {
        return io.getLast();
    }

    public Matrix getInputError() {
        return inputError;
    }

    public void setInputError(Matrix inputError) {
        this.inputError = inputError;
    }

    public LinkedList<Matrix> getWeightErrors() {
        return weightErrors;
    }

    public void addWeightError(Matrix weightError) {
        weightErrors.addFirst(weightError);
    }
}
