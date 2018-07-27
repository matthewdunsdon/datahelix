package com.scottlogic.deg.schemas;

import com.scottlogic.deg.schemas.common.ProfileSerialiser;
import com.scottlogic.deg.schemas.v3.ConstraintDTO;
import com.scottlogic.deg.schemas.v3.FieldDTO;
import com.scottlogic.deg.schemas.v3.RuleDTO;
import com.scottlogic.deg.schemas.v3.V3ProfileDTO;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;

public class ProfileSerialiserTests {
    @Test
    public void shouldSerialiseExampleProfile() throws IOException {
        // Arrange
        final V3ProfileDTO profile = new V3ProfileDTO();
        profile.fields = Arrays.asList(
            createField(f -> f.name = "typecode"),
            createField(f -> f.name = "price"));

        profile.rules = Arrays.asList(
            createConstraintAsRule(c -> {
                c.field = "typecode";
                c.type = "ifOfType";
                c.value = "string";
            }),
            createConstraintAsRule(c -> {
                c.type = "if";
                c.condition = createConstraint(condition -> {
                    condition.type = "or";
                    condition.constraints = Arrays.asList(
                        createConstraint(c1 -> {
                            c1.field = "typecode";
                            c1.type = "not isNull";
                        }),
                        createConstraint(c1 -> {
                            c1.field = "typecode";
                            c1.type = "isEqualTo";
                            c1.value = "type_001";
                        }));
                });
                c.then = createConstraint(then -> {
                    then.field = "price";
                    then.type = "isGreaterThanOrEqualTo";
                    then.value = 42.1;
                });
                c.elseCondition = createConstraint(elseCondition -> {
                    elseCondition.field = "price";
                    elseCondition.type = "isLessThan";
                    elseCondition.value = 42.1;
                });
            }));

        final String expectedJson =
            "{\n" +
            "  \"schemaVersion\" : \"v3\",\n" +
            "  \"fields\" : [ {\n" +
            "    \"name\" : \"typecode\"\n" +
            "  }, {\n" +
            "    \"name\" : \"price\"\n" +
            "  } ],\n" +
            "  \"rules\" : [ {\n" +
            "    \"type\" : \"ifOfType\",\n" +
            "    \"field\" : \"typecode\",\n" +
            "    \"value\" : \"string\",\n" +
            "    \"values\" : null,\n" +
            "    \"constraints\" : null,\n" +
            "    \"condition\" : null,\n" +
            "    \"then\" : null,\n" +
            "    \"else\" : null\n" +
            "  }, {\n" +
            "    \"type\" : \"if\",\n" +
            "    \"field\" : null,\n" +
            "    \"value\" : null,\n" +
            "    \"values\" : null,\n" +
            "    \"constraints\" : null,\n" +
            "    \"condition\" : {\n" +
            "      \"type\" : \"or\",\n" +
            "      \"field\" : null,\n" +
            "      \"value\" : null,\n" +
            "      \"values\" : null,\n" +
            "      \"constraints\" : [ {\n" +
            "        \"type\" : \"not isNull\",\n" +
            "        \"field\" : \"typecode\",\n" +
            "        \"value\" : null,\n" +
            "        \"values\" : null,\n" +
            "        \"constraints\" : null,\n" +
            "        \"condition\" : null,\n" +
            "        \"then\" : null,\n" +
            "        \"else\" : null\n" +
            "      }, {\n" +
            "        \"type\" : \"isEqualTo\",\n" +
            "        \"field\" : \"typecode\",\n" +
            "        \"value\" : \"type_001\",\n" +
            "        \"values\" : null,\n" +
            "        \"constraints\" : null,\n" +
            "        \"condition\" : null,\n" +
            "        \"then\" : null,\n" +
            "        \"else\" : null\n" +
            "      } ],\n" +
            "      \"condition\" : null,\n" +
            "      \"then\" : null,\n" +
            "      \"else\" : null\n" +
            "    },\n" +
            "    \"then\" : {\n" +
            "      \"type\" : \"isGreaterThanOrEqualTo\",\n" +
            "      \"field\" : \"price\",\n" +
            "      \"value\" : 42.1,\n" +
            "      \"values\" : null,\n" +
            "      \"constraints\" : null,\n" +
            "      \"condition\" : null,\n" +
            "      \"then\" : null,\n" +
            "      \"else\" : null\n" +
            "    },\n" +
            "    \"else\" : {\n" +
            "      \"type\" : \"isLessThan\",\n" +
            "      \"field\" : \"price\",\n" +
            "      \"value\" : 42.1,\n" +
            "      \"values\" : null,\n" +
            "      \"constraints\" : null,\n" +
            "      \"condition\" : null,\n" +
            "      \"then\" : null,\n" +
            "      \"else\" : null\n" +
            "    }\n" +
            "  } ]\n" +
            "}";

        // Act
        final String actualJson = new ProfileSerialiser()
            .serialise(profile)
            .replace("\r\n", "\n"); // normalise the line endings for comparison

        // Assert
        Assert.assertThat(actualJson, Is.is(expectedJson));
    }

    private static FieldDTO createField(Consumer<FieldDTO> setupField) {
        FieldDTO newField = new FieldDTO();
        setupField.accept(newField);
        return newField;
    }

    private static RuleDTO createRule(
        String description,
        ConstraintDTO... constraints) {
        RuleDTO newRule = new RuleDTO();

        newRule.description = description;
        newRule.constraints = Arrays.asList(constraints);

        return newRule;
    }

    private static ConstraintDTO createConstraint(Consumer<ConstraintDTO> setupConstraint) {
        ConstraintDTO newConstraint = new ConstraintDTO();
        setupConstraint.accept(newConstraint);
        return newConstraint;
    }

    private static RuleDTO createConstraintAsRule(Consumer<ConstraintDTO> setupConstraint) {
        ConstraintDTO newConstraint = createConstraint(setupConstraint);

        return createRule(null, newConstraint);
    }
}
