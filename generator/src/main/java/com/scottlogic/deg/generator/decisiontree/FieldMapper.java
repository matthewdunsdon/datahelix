package com.scottlogic.deg.generator.decisiontree;

import com.scottlogic.deg.generator.Field;
import com.scottlogic.deg.generator.reducer.ConstraintFieldSniffer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Given a decision tree, find which constraints and decisions act on which fields and return a map from them to fields
 */
class FieldMapper {

    private class ObjectFields {
        public Object object;
        public Set<Field> fields;

        ObjectFields(Object object, Set<Field> fields) {
            this.object = object;
            this.fields = fields;
        }

        ObjectFields(Object object, Field field) {
            this.object = object;
            this.fields = Collections.singleton(field);
        }
    }

    private final ConstraintFieldSniffer constraintSniffer = new ConstraintFieldSniffer();

    private Stream<ObjectFields> mapConstraintToFields(ConstraintNode node) {
        return Stream.concat(
            node.getAtomicConstraints()
                .stream()
                .map(constraint -> new ObjectFields(constraint, constraintSniffer.detectField(constraint))),
            node.getDecisions()
                .stream()
                .map(decision -> new ObjectFields(
                    decision,
                    decision
                        .getOptions()
                        .stream()
                        .flatMap(this::mapConstraintToFields)
                        .flatMap(objectField -> objectField.fields.stream())
                        .collect(Collectors.toSet()))
                    // TODO: This will only produce mappings from the root decisions/constraints. (Technically all we need, but investigate the below if needed)
//                        .flatMap(map -> Stream.of(
//                            map.fields, // this part is technically not used, but no reason not to keep it
//                            new ObjectFields(decision, map.fields)
//                        ))
        ));
    }

//    private Stream<Field> mapConstraintToFields(ConstraintNode node) {
//        return Stream.concat(
//            node.getAtomicConstraints()
//                .stream()
//                .map(constraintSniffer::detectField),
//            node.getDecisions()
//                .stream()
//                .flatMap(decision -> decision.getOptions()
//                    .stream()
//                    .flatMap(this::mapConstraintToFields)));
//    }

    Map<Object, Set<Field>> mapRulesToFields(DecisionTree profile){
        return Stream.concat(
                mapConstraintToFields(profile.getRootNode()),
                Stream.of(new ObjectFields(profile.getRootNode(), profile.getFields().stream().collect(Collectors.toSet()))))
            .collect(
                Collectors.toMap(
                map -> map.object,
                map -> map.fields
            ));
    }
}
