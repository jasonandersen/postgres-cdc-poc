package com.svhelloworld.cdc.cucumber.types;

import io.cucumber.datatable.DataTable;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Transforms Cucumber's {@link DataTable} objects or {@link Map}s derived from data tables into Java beans.
 *
 * @see InputTransformerTest
 */
@Component
public class InputTransformer {

    private static final Logger log = LoggerFactory.getLogger(InputTransformer.class);
    /**
     * Regular expression pattern that will check for white space in the interior of a string.
     */
    private static final Pattern whitespacePattern = Pattern.compile("^.+\\s.+$");
    /**
     * Regular expression pattern that will check if a value represents US currency.
     */
    private static final Pattern currencyPattern = Pattern.compile("^\\$[\\d,.]+");
    /**
     * Regular expression pattern that will check if a value represents a percentage
     */
    private static final Pattern percentagePattern = Pattern.compile("^[\\d.]+%$");
    /**
     * Regular expression pattern that will determine if a value is numeric with thousands seperators.
     */
    private static final Pattern thousandsSeperatorPattern = Pattern.compile("^[\\d,.]+$");

    /**
     * Transforms a {@link Map} of string keys and values into a bean.
     * @param rawProperties - map of property values keyed by name
     * @param targetClass - the class to transform the bean into
     * @return a populated instance of the Java bean
     */
    public <T> T transformToBean(
            Map<String, String> rawProperties,
            Class<T> targetClass) {

        log.debug("Input map: {}", rawProperties);
        try {
            // Instantiate the target bean with reflection using the bean's default constructor
            T beanInstance = targetClass.getDeclaredConstructor().newInstance();

            // Convert the property names to conform to Java bean convention and simplify any
            // complex numeric property values such as currency or percentages
            Map<String, String> convertedProperties = prepareProperties(rawProperties);

            // Create the bean
            BeanUtils.populate(beanInstance, convertedProperties);
            log.debug("Output bean: {}", beanInstance);
            return beanInstance;
        } catch (Exception e) {
            log.error("Failed to create {} from {}", targetClass.getSimpleName(), rawProperties, e);
            throw new InputTransformationException(e);
        }
    }

    /**
     * Java bean mappers require camel case property names and simplified numeric strings.
     */
    private Map<String, String> prepareProperties(Map<String, String> originalMap) {
        Map<String, String> preparedMap = new HashMap<>();
        for (String originalKey : originalMap.keySet()) {
            String preparedKey = preparePropertyName(originalKey);
            String preparedValue = prepareValue(originalMap.get(originalKey));
            preparedMap.put(preparedKey, preparedValue);
        }
        log.debug("Prepared map: {}", preparedMap);
        return preparedMap;
    }

    /**
     * Only convert keys to camel case if they have whitespace in them.
     */
    private String preparePropertyName(String key) {
        String converted = key.trim();
        if (whitespacePattern.matcher(converted).matches()) {
            return CaseUtils.toCamelCase(converted, false);
        }
        // single word property names need to be lower case
        return StringUtils.uncapitalize(converted);
    }

    /**
     * Some numeric values like currency and percentages need a little massaging before converting correctly.
     */
    private String prepareValue(String original) {
        String convertedValue = original.trim();
        if (isCurrency(convertedValue)) {
            convertedValue = convertedValue.replaceAll("[$,]", "");
        }
        if (isPercentage(convertedValue)) {
            convertedValue = convertedValue.replace("%", "");
        }
        if (isNumericWithThousandsSeperators(convertedValue)) {
            convertedValue = convertedValue.replace(",", "");
        }
        return convertedValue;
    }

    /**
     * @return true if the value is a numeric with thousands seperators
     */
    private boolean isNumericWithThousandsSeperators(String value) {
        return thousandsSeperatorPattern.matcher(value).matches();
    }

    /**
     * @return true if the value represents a percentage value.
     */
    private boolean isPercentage(String value) {
        return percentagePattern.matcher(value).matches();
    }

    /**
     * @return true if the value represents a currency.
     */
    private boolean isCurrency(String value) {
        return currencyPattern.matcher(value).matches();
    }

}
