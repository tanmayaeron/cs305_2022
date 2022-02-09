/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package sqlAPI;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class Output {
    String first_name;
}

class Input {
    int actorId;
}

class LibraryTest {
//    @Test void someLibraryMethodReturnsTrue() {
//        Library classUnderTest = new Library();
//        assertTrue(classUnderTest.someLibraryMethod(), "someLibraryMethod should return 'true'");
//    }

    @Test void sqlRunnerWorkingForSimpleCases() {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/sakila", "root", "root");
            SqlRunnerClass.OpPrams opp = new SqlRunnerClass.OpPrams();
            opp.con = con;
            opp.filePath = "queries.xml";
            opp.tagName = "sql";
            opp.uniqueAttributeName = "id";
            opp.paramTypeAttributeName = "paramType";

            SqlRunner classUnderTest = new SqlRunnerClass(opp);

            Output output = new Output();
            Input input = new Input();

            //System.out.println(input.getClass().getName());

            input.actorId = 1;


            output  = classUnderTest.selectOne("getName",input,output.getClass());

            assertEquals("PENELOPE",output.first_name,"output did not match");


        }
        catch(Exception e){assertTrue(false,"failed");
            System.out.println(e);}

    }

    @Test void stringTransformation() {
        Input input = new Input();
        input.actorId = 1;

        String output = SqlRunnerClass.replaceString("SELECT first_name from actor WHERE actor_id=${actorId};",input);
        assertEquals("SELECT first_name from actor WHERE actor_id=1;",output);


    }

    @Test void returnStringTest() {
        String input = "Dave";
        String output = SqlRunnerClass.stringForElement(input);
        assertEquals("'Dave'",output,"does not match");

    }

    @Test void stringForCollectionTest() {
        int [] arr = new int[]{1,2,3,4};
        String output = SqlRunnerClass.stringForCollection(arr);
        assertEquals("(1,2,3,4)",output,"did not match");

        ArrayList<Integer> arr2 = new ArrayList<>();
        arr2.add(1);
        arr2.add(2);
        arr2.add(3);
        arr2.add(4);


        assertEquals("(1,2,3,4)",SqlRunnerClass.stringForCollection(arr2),"did not match");

    }

}
