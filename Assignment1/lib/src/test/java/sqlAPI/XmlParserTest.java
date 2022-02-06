package sqlAPI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class XmlParserTest {

    XmlParser xmlParser;
    String[] attributeNames;

    @BeforeEach void initialiseParser() {
        attributeNames = new String[]{"id","paramType"};
        xmlParser = new XmlParser("queries.xml","sql",attributeNames);
    }

    @Test void selectOneByAttribute() {
        HashMap<String,String> expected = new HashMap<String,String>();
        expected.put("id","addMovie");
        expected.put("paramType","org.foo.Bar");
        expected.put("textContent","INSERT INTO my_table(x, y, x) VALUES(${propX}, ${propY}, ${propZ});");

        ArrayList<HashMap<String,String>> arrLi = xmlParser.getElementByAttributeValue("id","addMovie");
        assertTrue(arrLi.size()>0,"no element returned");
        assertEquals(expected,arrLi.get(0),"value of HashMaps differ");
    }

    @Test void selectAllByAttribute() {
        ArrayList<HashMap<String,String>> expectedElementList = new ArrayList<HashMap<String,String>>();

        HashMap<String,String> expected = new HashMap<String,String>();

        expected.put("id","findMovies");
        expected.put("paramType","org.foo.Bar");
        expected.put("textContent","SELECT a, b, c FROM my_table WHERE x=${propX} AND y=${propY};");
        expectedElementList.add(expected);

        expected = new HashMap<String,String>();
        expected.put("id","addMovie");
        expected.put("paramType","org.foo.Bar");
        expected.put("textContent","INSERT INTO my_table(x, y, x) VALUES(${propX}, ${propY}, ${propZ});");
        expectedElementList.add(expected);

        //System.out.println(xmlParser.getElementByAttributeValue("paramType","org.foo.Bar"));

        assertEquals(expectedElementList,
                xmlParser.getElementByAttributeValue("paramType","org.foo.Bar"),"ArrayList differ");

    }

    @Test void selectAll(){
        ArrayList<HashMap<String,String>> expectedElementList = new ArrayList<HashMap<String,String>>();

        HashMap<String,String> expected = new HashMap<String,String>();

        expected.put("id","findMovies");
        expected.put("paramType","org.foo.Bar");
        expected.put("textContent","SELECT a, b, c FROM my_table WHERE x=${propX} AND y=${propY};");
        expectedElementList.add(expected);

        expected = new HashMap<String,String>();
        expected.put("id","addMovie");
        expected.put("paramType","org.foo.Bar");
        expected.put("textContent","INSERT INTO my_table(x, y, x) VALUES(${propX}, ${propY}, ${propZ});");
        expectedElementList.add(expected);

        //System.out.println(xmlParser.getAllElements());

        assertEquals(expectedElementList,xmlParser.getAllElements(),"ArrayList differ");
    }

}
