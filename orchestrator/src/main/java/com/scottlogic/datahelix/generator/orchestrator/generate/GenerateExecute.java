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

package com.scottlogic.datahelix.generator.orchestrator.generate;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.scottlogic.datahelix.generator.common.output.GeneratedObject;
import com.scottlogic.datahelix.generator.core.generation.DataGenerator;
import com.scottlogic.datahelix.generator.core.generation.DataGeneratorMonitor;
import com.scottlogic.datahelix.generator.core.profile.Profile;
import com.scottlogic.datahelix.generator.output.outputtarget.SingleDatasetOutputTarget;
import com.scottlogic.datahelix.generator.output.writer.DataSetWriter;
import com.scottlogic.datahelix.generator.profile.reader.ProfileReader;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

public class GenerateExecute {
    private final SingleDatasetOutputTarget singleDatasetOutputTarget;
    private final ProfileReader profileReader;
    private final DataGenerator dataGenerator;
    private final DataGeneratorMonitor monitor;
    private final File profileFile;

    @Inject
    GenerateExecute(
        DataGenerator dataGenerator,
        SingleDatasetOutputTarget singleDatasetOutputTarget,
        ProfileReader profileReader,
        DataGeneratorMonitor monitor,
        @Named("config:profileFile") File profileFile) {
        this.dataGenerator = dataGenerator;
        this.singleDatasetOutputTarget = singleDatasetOutputTarget;
        this.profileReader = profileReader;
        this.monitor = monitor;
        this.profileFile = profileFile;
    }

    public void execute() throws IOException {
        Profile profile = profileReader.read(profileFile);
        Stream<GeneratedObject> generatedDataItems = dataGenerator.generateData(profile);

        outputData(profile, generatedDataItems);
    }

    private void outputData(Profile profile, Stream<GeneratedObject> generatedDataItems) throws IOException {
        singleDatasetOutputTarget.validate();

        try (DataSetWriter writer = singleDatasetOutputTarget.openWriter(profile.getFields())) {
            generatedDataItems.forEach(row -> {
                try {
                    writer.writeRow(row);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        monitor.endGeneration();
    }
}