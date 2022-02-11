package sqlAPI;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Name {
    public String first_name;
    public String last_name;

    @Override public String toString(){
        return "('"+first_name+"','"+last_name+"')";
    }

}



class updateInput {
    public String last_name_value;
    public List<String> first_name_list;
}



class SakilaTest0Output {
    public int FID;
    public String title;
    public String description;
    public String category;
    public BigDecimal price;
    public int length;
    public String rating;
    public String actors;
}




public class SqlRunnerTest {

    SqlRunner classUnderTest;
    Connection con;

    @BeforeEach void setup(){
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/sakila",
                    "root", "root");
            con.setAutoCommit(false);
            SqlRunnerClass.OpPrams opp = new SqlRunnerClass.OpPrams();
            opp.con = con;
            opp.filePath = "queries.xml";
            opp.tagName = "sql";
            opp.uniqueAttributeName = "id";
            opp.paramTypeAttributeName = "paramType";

            classUnderTest = new SqlRunnerClass(opp);

        }
        catch(Exception e) {
            System.out.println(e);
        }
    }

    @AfterEach void tearDown(){

        try {
            con.close();
        }
        catch(Exception e){
            System.out.println(e);
        }

    }

    @Test void selectOne(){
        int actorId = 1;
        Name name  = classUnderTest.selectOne("getName",actorId,Name.class);

        assertEquals("PENELOPE",name.first_name,"mismatch in first name");
        assertEquals("GUINESS",name.last_name,"mismatch in first name");

    }

    @Test void typeMismatch(){
        byte actorId = 1;


        assertThrows(RuntimeException.class,()->classUnderTest.selectOne("getName",actorId,Name.class));

    }

    @Test void noRowReturned(){
        assertNull(classUnderTest.selectOne("no_query_returned",null,Name.class));
    }

    @Test void multipleRows(){
        assertThrows(RuntimeException.class,()->classUnderTest.selectOne("more_than_one_rows",null,Name.class));
    }

    @Test void selectMany(){
        int[] actorIds = {1,2,3};

        List<Name> output = classUnderTest.selectMany("getPersons",actorIds,Name.class);

        assertEquals(3,output.size(),"number of rows returned not equal to 3");
        assertEquals("PENELOPE",output.get(0).first_name);
        assertEquals("GUINESS",output.get(0).last_name);

        assertEquals("NICK",output.get(1).first_name);
        assertEquals("WAHLBERG",output.get(1).last_name);

        assertEquals("ED",output.get(2).first_name);
        assertEquals("CHASE",output.get(2).last_name);

    }


    @Test void insert(){

        Name name = new Name();
        name.first_name = "TANMAY";
        name.last_name = "AERON";
        int rowsAffected = classUnderTest.insert("insertPerson",name);
        assertEquals(1,rowsAffected);

    }

    @Test void update(){

        List<String> nameList = new ArrayList<>();
        nameList.add("PENELOPE");
        nameList.add("NICK");
        nameList.add("ED");

        updateInput input = new updateInput();
        input.first_name_list = nameList;
        input.last_name_value = "AERON";

        int rowsAffected = classUnderTest.update("update_last_name",input);

        assertEquals(10,rowsAffected);

    }


    @Test void delete(){

        Name name1 = new Name();
        name1.first_name = "TANMAY";
        name1.last_name = "AERON";
        int rowsAffected = classUnderTest.insert("insertPerson",name1);
        assertEquals(1,rowsAffected,"insertion of first person failed");

        Name name2 = new Name();
        name2.first_name = "TANMAY";
        name2.last_name = "AERON";
        rowsAffected = classUnderTest.insert("insertPerson",name2);
        assertEquals(1,rowsAffected,"insertion of second person failed");

        List<Name> nameList = new ArrayList<>();
        System.out.println(nameList.getClass().getName());
        nameList.add(name1);
        nameList.add(name2);

        rowsAffected = classUnderTest.delete("delete",nameList);
        assertEquals(2,rowsAffected,"deletion failed");


    }

    @Test void delete2(){

        Name name1 = new Name();
        name1.first_name = "TANMAY";
        name1.last_name = "AERON";
        int rowsAffected = classUnderTest.insert("insertPerson",name1);
        assertEquals(1,rowsAffected,"insertion of first person failed");

        Name name2 = new Name();
        name2.first_name = "TANMAY";
        name2.last_name = "AERON";
        rowsAffected = classUnderTest.insert("insertPerson",name2);
        assertEquals(1,rowsAffected,"insertion of second person failed");

        Name []nameList = {name1,name2};
        System.out.println(nameList.getClass().getName());


        rowsAffected = classUnderTest.delete("delete2",nameList);
        assertEquals(2,rowsAffected,"deletion failed");


    }

    @Test void complex(){
        List<SakilaTest0Output> li =classUnderTest.selectMany("random",null,SakilaTest0Output.class);
        assertEquals(371,li.size());
    }

}
