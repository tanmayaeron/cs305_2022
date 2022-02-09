package sqlAPI;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Name {
    public String first_name;
    public String last_name;
}

//class Input {
//    int actorId;
//}


public class SqlRunnerTest {

    SqlRunner classUnderTest;
    Connection con;

    @BeforeEach void setup(){
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/sakila",
                    "root", "root");

            SqlRunnerClass.OpPrams opp = new SqlRunnerClass.OpPrams();
            opp.con = con;
            opp.filePath = "queries.xml";
            opp.tagName = "sql";
            opp.uniqueAttributeName = "id";
            opp.paramTypeAttributeName = "paramType";

            classUnderTest = new SqlRunnerClass(opp);

        }
        catch(Exception e) {

            assertTrue(false,"setup failed");}
    }

    @AfterEach void tearDown(){

        try {
            con.close();
        }
        catch(Exception e){
            System.out.println(e);
            assertTrue(false,"failed to close connection");
        }

    }

    @Test void selectOne(){
        int actorId = 1;
        Name name  = classUnderTest.selectOne("getName",actorId,Name.class);

        assertEquals("PENELOPE",name.first_name,"mismatch in first name");
        assertEquals("GUINESS",name.last_name,"mismatch in first name");

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


    @Test void update(){

    }


    @Test void insert(){

    }


    @Test void delete(){

    }


}
