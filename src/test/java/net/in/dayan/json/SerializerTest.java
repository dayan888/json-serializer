package net.in.dayan.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.in.dayan.json.test.Base;
import net.in.dayan.json.test.Child;
import net.in.dayan.json.test.Master;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

public class SerializerTest {
    Properties props;
    Base base;

    @Before
    public void setUp() throws IOException {
        props = loadTestResult();
        base = createTargetObject();
    }

    @Test
    public void toJsonString() throws JsonProcessingException {
        try {
            Assert.assertEquals(props.getProperty("T001"), Serializer.getInstance().toJsonString(base, Arrays.asList(new String[]{"id", "url"}), null, false));
            Assert.assertEquals(props.getProperty("T002"), Serializer.getInstance().toJsonString(base, Arrays.asList(new String[]{"id", "url", "childArray", "childArray.id", "childSet", "childSet.name"}), null, false));
            Assert.assertEquals(props.getProperty("T003"), Serializer.getInstance().toJsonString(base, Arrays.asList(new String[]{"id", "url"}), Arrays.asList(new String[]{"date", "timestamp"}), true));
            Assert.assertEquals(props.getProperty("T004"), Serializer.getInstance().toJsonString(base, null, null, true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void toJsonString2() throws JsonProcessingException {
        try {
            Wrapper wrapper = new Wrapper(base);
            wrapper.addInclude(Arrays.asList(new String[]{"id", "url"}));
            Assert.assertEquals(props.getProperty("T001"), Serializer.getInstance().toJsonString(wrapper));

            wrapper = new Wrapper(base);
            wrapper.addInclude(Arrays.asList(new String[]{"id", "url", "childArray", "childArray.id", "childSet", "childSet.name"}));
            Assert.assertEquals(props.getProperty("T002"), Serializer.getInstance().toJsonString(wrapper));

            wrapper = new Wrapper(base);
            wrapper.addInclude("id");
            wrapper.addInclude("url");
            wrapper.addExclude(Arrays.asList(new String[]{"date", "timestamp"}));
            wrapper.setFinallyAllowed(true);
            Assert.assertEquals(props.getProperty("T003"), Serializer.getInstance().toJsonString(wrapper));

            wrapper = new Wrapper(base);
            wrapper.setFinallyAllowed(true);
            Assert.assertEquals(props.getProperty("T004"), Serializer.getInstance().toJsonString(wrapper));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Properties loadTestResult() throws IOException {
        InputStream is = SerializerTest.class.getClassLoader().getResourceAsStream("testresult.properties");
        Properties props = new Properties();
        props.load(is);
        return props;
    }

    private Base createTargetObject() throws IOException {
        Base base = new Base();
        base.id = 10;
        base.name = "hoge";
        base.setUrl(new URL("https://xxx.com/hoge?a=123"));
        base.date = new Date(1574149110643L);
        base.timestamp = new Timestamp(base.date.getTime());
        base.c = 'C';
        base.bVal = false;
        base.lVal = 99999999999999L;
        base.dVal = 0.12345d;
        base.fVal = 1.234f;
        base.bt = 1;
        base.fruit = Base.Fruit.Grape;
        base.jsonNode = new ObjectMapper().readTree("{\"j1\":123, \"j2\":\"sss\"}");
        base.master = new Master();
        base.master.id = 123;
        base.master.name = "John";
        base.master.pic = new byte[]{1,2,3,4,5};

        base.intArr = new Integer[]{1,2,3,4};

        base.childArray = new Child[]{new Child()};
        Child c1 = new Child();
        c1.id = 1;
        c1.name = "Child1";
        c1.master = new Master();
        c1.master.name = "Child1Master";
        c1.master.id = 999901;
        Child c2 = new Child();
        c2.id = 2;
        c2.name = "Child2";
        base.childList.add(c1);
        base.childList.add(c2);
        base.childSet.add(c1);
        return base;
    }
}
