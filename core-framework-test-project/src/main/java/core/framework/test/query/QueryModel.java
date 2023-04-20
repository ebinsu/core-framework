package core.framework.test.query;

import java.math.BigDecimal;

/**
 * @author ebin
 */
public class QueryModel {
    public String name;
    private String testName;
    private String voname;
    private int integerNum;
    private Double doubleNum;
    private Float floatNum;
    private long longNum;
    private int bigDecimalNum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getVoname() {
        return voname;
    }

    public void setVoname(String voname) {
        this.voname = voname;
    }
}
