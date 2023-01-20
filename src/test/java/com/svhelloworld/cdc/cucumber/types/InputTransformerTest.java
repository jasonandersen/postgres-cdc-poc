package com.svhelloworld.cdc.cucumber.types;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Testing the functionality of the {@link InputTransformer} class.
 */
public class InputTransformerTest {

    private static final Logger log = LoggerFactory.getLogger(InputTransformerTest.class);
    private static final double DELTA = 0.001;

    private final InputTransformer transformer = new InputTransformer();

    @Test
    public void convertPercentageToNumeric() {
        Map<String, String> properties = Map.of(
                "First Name", "Joe",
                "Last Name", "Andersen",
                "Currency Value", "$100",
                "Percentile", "80%",
                "Age", "49");

        TestBean bean = transformer.transformToBean(properties, TestBean.class);

        log.info("Transformed bean: {}", bean.toString());
        assertEquals(100.0, bean.getCurrencyValue(), DELTA);
        assertEquals(80.0, bean.getPercentile(), DELTA);
        assertEquals("Joe", bean.getFirstName());
        assertEquals("Andersen", bean.getLastName());
        assertEquals(49, bean.getAge());
    }

    @Test
    public void convertPercentageWithDecimalToNumeric() {
        Map<String, String> properties = Map.of(
                "First Name", "Joe",
                "Last Name", "Andersen",
                "Currency Value", "$100",
                "Percentile", "80.5%",
                "Age", "49");

        TestBean bean = transformer.transformToBean(properties, TestBean.class);

        log.info("Transformed bean: {}", bean.toString());
        assertEquals(100.0, bean.getCurrencyValue(), DELTA);
        assertEquals(80.5, bean.getPercentile(), DELTA);
        assertEquals("Joe", bean.getFirstName());
        assertEquals("Andersen", bean.getLastName());
        assertEquals(49, bean.getAge());
    }

    @Test
    public void convertCurrencyToNumeric() {
        Map<String, String> properties = Map.of(
                "First Name", "Joe",
                "Last Name", "Andersen",
                "Currency Value", "$100",
                "Age", "49");

        TestBean bean = transformer.transformToBean(properties, TestBean.class);

        log.info("Transformed bean: {}", bean.toString());
        assertEquals(100.0, bean.getCurrencyValue(), DELTA);
        assertEquals("Joe", bean.getFirstName());
        assertEquals("Andersen", bean.getLastName());
        assertEquals(49, bean.getAge());
    }

    @Test
    public void convertCurrencyWithThousandSeperatorsToNumeric() {
        Map<String, String> properties = Map.of(
                "First Name", "Joe",
                "Last Name", "Andersen",
                "Currency Value", "$100,000",
                "Age", "49");

        TestBean bean = transformer.transformToBean(properties, TestBean.class);

        log.info("Transformed bean: {}", bean.toString());
        assertEquals(100000.0, bean.getCurrencyValue(), DELTA);
        assertEquals("Joe", bean.getFirstName());
        assertEquals("Andersen", bean.getLastName());
        assertEquals(49, bean.getAge());
    }

    @Test
    public void convertNumericWithThousandSeperatorsToNumeric() {
        Map<String, String> properties = Map.of(
                "First Name", "Joe",
                "Last Name", "Andersen",
                "Currency Value", "100,000",
                "Age", "49");

        TestBean bean = transformer.transformToBean(properties, TestBean.class);

        log.info("Transformed bean: {}", bean.toString());
        assertEquals(100000.0, bean.getCurrencyValue(), DELTA);
        assertEquals("Joe", bean.getFirstName());
        assertEquals("Andersen", bean.getLastName());
        assertEquals(49, bean.getAge());
    }

    @Test
    public void convertNumericWithThousandSeperatorsAndDecimalToNumeric() {
        Map<String, String> properties = Map.of(
                "First Name", "Joe",
                "Last Name", "Andersen",
                "Currency Value", "100,000.75",
                "Age", "49");

        TestBean bean = transformer.transformToBean(properties, TestBean.class);

        log.info("Transformed bean: {}", bean.toString());
        assertEquals(100000.75, bean.getCurrencyValue(), DELTA);
        assertEquals("Joe", bean.getFirstName());
        assertEquals("Andersen", bean.getLastName());
        assertEquals(49, bean.getAge());
    }

    @Test
    public void convertCurrencyWithDecimalToNumeric() {
        Map<String, String> properties = Map.of(
                "First Name", "Joe",
                "Last Name", "Andersen",
                "Currency Value", "$100.75",
                "Age", "49");

        TestBean bean = transformer.transformToBean(properties, TestBean.class);

        log.info("Transformed bean: {}", bean.toString());
        assertEquals(100.75, bean.getCurrencyValue(), DELTA);
        assertEquals("Joe", bean.getFirstName());
        assertEquals("Andersen", bean.getLastName());
        assertEquals(49, bean.getAge());
    }

    @Test
    public void doesntConvertNonNumericCurrency() {
        Map<String, String> properties = Map.of(
                "First Name", "Joe",
                "Last Name", "Andersen",
                "Currency Value", "$100F75",
                "Age", "49");

        TestBean bean = transformer.transformToBean(properties, TestBean.class);

        log.info("Transformed bean: {}", bean.toString());
        assertEquals(0, bean.getCurrencyValue(), DELTA);
        assertEquals("Joe", bean.getFirstName());
        assertEquals("Andersen", bean.getLastName());
        assertEquals(49, bean.getAge());
    }

    @Test
    public void convertSentenceCasePropertyNames() {
        Map<String, String> properties = Map.of(
                "First Name", "Joe",
                "Last Name", "Andersen",
                "Age", "49");

        TestBean bean = transformer.transformToBean(properties, TestBean.class);

        log.info("Transformed bean: {}", bean.toString());
        assertEquals("Joe", bean.getFirstName());
        assertEquals("Andersen", bean.getLastName());
        assertEquals(49, bean.getAge());
    }

    @Test
    public void convertLowerCaseStartCamelCasePropertyNames() {
        Map<String, String> properties = Map.of(
                "firstName", "Joe",
                "lastName", "Andersen",
                "age", "49");

        TestBean bean = transformer.transformToBean(properties, TestBean.class);

        log.info("Transformed bean: {}", bean.toString());
        assertEquals("Joe", bean.getFirstName());
        assertEquals("Andersen", bean.getLastName());
        assertEquals(49, bean.getAge());
    }

    @Test
    public void convertUpperCaseStartCamelCasePropertyNames() {
        Map<String, String> properties = Map.of(
                "FirstName", "Joe",
                "LastName", "Andersen",
                "Age", "49");

        TestBean bean = transformer.transformToBean(properties, TestBean.class);

        log.info("Transformed bean: {}", bean.toString());
        assertEquals("Joe", bean.getFirstName());
        assertEquals("Andersen", bean.getLastName());
        assertEquals(49, bean.getAge());
    }

    @Test
    public void convertExtraSpacesInPropertyNames() {
        Map<String, String> properties = Map.of(
                "First  Name", "Joe",
                "Last  Name", "Andersen",
                "Age", "49");

        TestBean bean = transformer.transformToBean(properties, TestBean.class);

        log.info("Transformed bean: {}", bean.toString());
        assertEquals("Joe", bean.getFirstName());
        assertEquals("Andersen", bean.getLastName());
        assertEquals(49, bean.getAge());
    }

    @Test
    public void convertAllLowerCasePropertyNames() {
        Map<String, String> properties = Map.of(
                "first  name", "Joe",
                "last  name", "Andersen",
                "age", "49");

        TestBean bean = transformer.transformToBean(properties, TestBean.class);

        log.info("Transformed bean: {}", bean.toString());
        assertEquals("Joe", bean.getFirstName());
        assertEquals("Andersen", bean.getLastName());
        assertEquals(49, bean.getAge());
    }

    @Test
    public void convertSpacesBeforeAndAfterPropertyNames() {
        Map<String, String> properties = Map.of(
                "  First  Name  ", "Joe",
                "  Last  Name  ", "Andersen",
                "  Age     ", "49");

        TestBean bean = transformer.transformToBean(properties, TestBean.class);

        log.info("Transformed bean: {}", bean.toString());
        assertEquals("Joe", bean.getFirstName());
        assertEquals("Andersen", bean.getLastName());
        assertEquals(49, bean.getAge());
    }

    /**
     * Java bean to test against.
     */
    @SuppressWarnings("unused")
    public static class TestBean {
        private String firstName;
        private String lastName;
        private int age;
        private double currencyValue;
        private double percentile;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public double getCurrencyValue() {
            return currencyValue;
        }

        public void setCurrencyValue(double currencyValue) {
            this.currencyValue = currencyValue;
        }

        public double getPercentile() {
            return percentile;
        }

        public void setPercentile(double percentile) {
            this.percentile = percentile;
        }

        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }
}
