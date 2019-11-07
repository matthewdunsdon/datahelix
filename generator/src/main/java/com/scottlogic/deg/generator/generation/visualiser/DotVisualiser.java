/*
 * Copyright 2019 Scott Logic Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.scottlogic.deg.generator.generation.visualiser;

import com.scottlogic.deg.generator.decisiontree.DecisionTree;
import com.scottlogic.deg.generator.decisiontree.visualisation.DecisionTreeVisualisationWriter;

import java.io.IOException;

public class DotVisualiser implements Visualiser {

    private final DecisionTreeVisualisationWriter decisionTreeVisualisationWriter;

    DotVisualiser(DecisionTreeVisualisationWriter decisionTreeVisualisationWriter) {
        this.decisionTreeVisualisationWriter = decisionTreeVisualisationWriter;
    }

    @Override
    public void printTree(String title, DecisionTree decisionTree) {
        try {
            System.err.println("VISUALISING decision tree with title: " + title);
            decisionTreeVisualisationWriter.writeDot(decisionTree, title, title);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            decisionTreeVisualisationWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
