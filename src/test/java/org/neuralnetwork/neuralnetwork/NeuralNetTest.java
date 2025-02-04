package org.neuralnetwork.neuralnetwork;

import static org.junit.jupiter.api.Assertions.*;

import cave.matrix.Matrix;
import org.neuralnetwork.Approximator;
import org.neuralnetwork.Engine;
import org.neuralnetwork.LossFunction;
import org.neuralnetwork.Transform;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class NeuralNetTest {

    private Random random = new Random();

    @Test
    void testSoftMaxCrossEntropyGradient() {

        final int rows = 4;
        final int cols = 5;

        Matrix input = new Matrix(rows, cols, i -> random.nextGaussian());

        Matrix expected = new Matrix(rows, cols, i -> 0);

        for (int col = 0; col < cols; col++) {
            int randomRow = random.nextInt(rows);

            expected.set(randomRow, col, 1);
        }

        Matrix softmaxOutput = input.softmax();

        Matrix result = Approximator.gradient(input, in->{
            return LossFunction.crossEntropy(expected, in.softmax());
        });

        result.forEach((index, value) -> {
            double softmaxValue = softmaxOutput.get(index);
            double expectedValue = expected.get(index);

            assertTrue(Math.abs(value - (softmaxValue - expectedValue)) < 0.01);
        });
    }

    @Test
    void testApproximator() {

        final int rows = 4;
        final int cols = 5;

        Matrix input = new Matrix(rows, cols, i -> random.nextGaussian()).softmax();

        Matrix expected = new Matrix(rows, cols, i -> 0);

        for (int col = 0; col < cols; col++) {
            int randomRow = random.nextInt(rows);

            expected.set(randomRow, col, 1);
        }

        Matrix result = Approximator.gradient(input, in->{
            return LossFunction.crossEntropy(expected, in);
        });

        input.forEach((index, value) -> {
            double resultValue = result.get(index);
            double expectedValue = expected.get(index);

            if(expectedValue < 0.001) {
                assertTrue(Math.abs(resultValue) < 0.01);
            }
            else {
                assertTrue(Math.abs(resultValue + 1.0 /value) < 0.01);
            }
        });
    }

    @Test
    void testCrossEntropy() {
        double[] expectedValues = {1, 0, 0, 0, 0, 1, 0, 1, 0};
        Matrix expected = new Matrix(3, 3, i -> expectedValues[i]);

        Matrix actual = new Matrix (3, 3, i -> 0.05 * i * i - 4).softmax();

        Matrix result = LossFunction.crossEntropy(expected, actual);

        actual.forEach((row, col, index, value) -> {
            double expectedValue = expected.get(index);

            double loss = result.get(col);

            if (expectedValue > 0.9) {
                assertTrue(Math.abs(Math.log(value) + loss) < 0.001);
            }
        });
    }

    //@Test
    void testEngine() {
        Engine engine = new Engine();

        engine.add(Transform.DENSE, 8, 5);
        engine.add(Transform.RELU);

        engine.add(Transform.DENSE, 5);
        engine.add(Transform.RELU);

        engine.add(Transform.DENSE, 4);
        engine.add(Transform.SOFTMAX);

        Matrix input = new Matrix(5, 4, i -> random.nextGaussian());

        Matrix output = engine.runForwards(input);

        System.out.println(engine);
        System.out.println(output);
    }

    //@Test
    void testTemp() {
        int inputSize = 5;
        int layer1Size = 6;
        int layer2Size = 4;

        Matrix input = new Matrix(inputSize, 1, i -> random.nextGaussian());

        Matrix layer1Weights = new Matrix(layer1Size, input.getRows(), i -> random.nextGaussian());
        Matrix layer1Biases = new Matrix(layer1Size, 1, i -> random.nextGaussian());

        Matrix layer2Weights = new Matrix(layer2Size, layer1Weights.getRows(), i -> random.nextGaussian());
        Matrix layer2Biases = new Matrix(layer2Size, 1, i -> random.nextGaussian());

        var output = input;
        System.out.println(output);

        output = layer1Weights.multiply(output);
        System.out.println(output);

        output = output.modify(((row, col, value) -> value + layer1Biases.get(row)));
        System.out.println(output);

        output = output.modify(value -> value > 0 ? value: 0);
        System.out.println(output);

        output = layer2Weights.multiply(output);
        System.out.println(output);

        output = output.modify(((row, col, value) -> value + layer2Biases.get(row)));
        System.out.println(output);

        output = output.softmax();
        System.out.println(output);
    }

    @Test
    void testAddBias() {

        Matrix input = new Matrix(3, 3, i -> (i + 1));
        Matrix weights = new Matrix(3, 3, i -> (i + 1));
        Matrix biases = new Matrix(3, 1, i -> (i + 1));

        Matrix result = weights.multiply(input).modify(((row, col, value) -> value + biases.get(row)));

        double[] expectedValues = {+31.00000, +37.00000, +43.00000, +68.00000, +83.00000, +98.00000,
                +105.00000, +129.00000, +153.00000};

        Matrix expected = new Matrix(3, 3, i -> expectedValues[i]);

        assertTrue(expected.equals(result));

    }

    @Test
    void testRelu() {

        final int numberNeurons = 5;
        final int numberInputs = 6;
        final int inputSize = 4;

        Matrix input = new Matrix(inputSize, numberInputs, i -> random.nextDouble());
        Matrix weights = new Matrix(numberNeurons, inputSize, i -> random.nextGaussian());
        Matrix biases = new Matrix(numberNeurons, 1, i -> random.nextGaussian());

        Matrix result1 = weights.multiply(input).modify(((row, col, value) -> value + biases.get(row)));
        Matrix result2 = weights.multiply(input).modify(((row, col, value) -> value + biases.get(row))).modify(value -> value > 0 ? value: 0);

        result2.forEach((index, value) -> {
            double originalValue = result1.get(index);

            if (originalValue > 0) {
                assertTrue(Math.abs(originalValue - value) < 0.00001);
            }

            else {
                assertTrue(Math.abs(value) < 0.00001);
            }
            System.out.println(index + ", " + value);
        });
    }
}
