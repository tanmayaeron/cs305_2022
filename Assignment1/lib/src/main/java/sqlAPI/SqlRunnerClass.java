package sqlAPI;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.lang.reflect.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlRunnerClass implements SqlRunner{

    private final static String queryAttributeName = "textContent";
    private final static String[] primitiveClassName = {"java.lang.Integer","java.lang.Byte","java.lang.Short","java.lang.Long"
    ,"java.lang.Float","java.lang.Double","java.lang.Boolean","java.lang.Character"};
    public static class OpPrams {
        public Connection con;
        public String filePath;
        public String tagName;
        public String uniqueAttributeName;
        public String[] attributeNames;
    }

    private OpPrams params;
    private XmlParser xmlParser;
    private Statement stmt;

    public SqlRunnerClass(OpPrams parameters) {
        params = parameters;
        xmlParser = new XmlParser(params.filePath,params.tagName,params.attributeNames);
        try {
            stmt = params.con.createStatement();
        }
        catch(Exception e){System.out.println(e);}
    }

    public static boolean isPrimitiveWrapper(String className){
        for(int i=0;i<primitiveClassName.length;i++){
            if(className.equals(primitiveClassName[i])) return true;
        }
        return false;
    }





    public static <T> String replaceString(String inputStr,T queryParam) {
        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
        Matcher matcher = pattern.matcher(inputStr);

        StringBuffer buffer = new StringBuffer();

        Class cls = queryParam.getClass();


        try {
            while (matcher.find()) {
                Field field = cls.getDeclaredField(matcher.group(1));
                field.setAccessible(true);
                Object value = field.get(queryParam);
                //System.out.println(field.get());
                matcher.appendReplacement(buffer, value.toString());
            }
        }
        catch(Exception e){System.out.println(e);}

        matcher.appendTail(buffer);
        return buffer.toString();
    }

    public static <T> void populateObject(ResultSet rs,T returnObject) {
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            Class cls = returnObject.getClass();

            for(int i=1;i<=columnCount;i++){
                String columnName =  rsmd.getColumnName(i);
                Field field = cls.getDeclaredField(columnName);
                field.setAccessible(true);

                Class fieldCls = field.getClass();
                Object value = rs.getObject(i);

                field.set(returnObject,value);

            }

        } catch(Exception e) {System.out.println(e);}


        return;
    }

    public <T,R> R selectOne(String queryId,T queryParam, Class<R> resultType) {

        //check queryParam type supplied

        try {

            R returnObject = resultType.getDeclaredConstructor().newInstance();

            //R returnObject = (R)temp;
            String queryFormat = xmlParser.getElementByAttributeValue(params.uniqueAttributeName,queryId).
                    get(0).get(queryAttributeName);

            String query = replaceString(queryFormat,queryParam);

            ResultSet rs = stmt.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            System.out.println("Column count: "+rsmd.getColumnCount());


            if(rs.next()) populateObject(rs,returnObject);


            //System.out.println(returnObject);

            if(rs.next()) {
                //give error
            }

            return returnObject;
        }
        catch (Exception e) {System.out.println(e);}
        return null;
    }

    public <T,R> List<R> selectMany(String queryId, T queryParam, Class<R> resultType) {

        //check queryParamType is correct

        List<R> returnList = new ArrayList<>();

        String queryFormat = xmlParser.getElementByAttributeValue(params.uniqueAttributeName,queryId).
                get(0).get(queryAttributeName);
        String query = replaceString(queryFormat,queryParam);
        try {
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                R returnObject = resultType.getDeclaredConstructor().newInstance();
                populateObject(rs,returnObject);
                returnList.add(returnObject);
            }
        }
        catch(Exception e){System.out.println(e);}
        return returnList;

    }

    public <T> int insert(String queryId,T queryParam) {
        //check queryParam type matching
        //check if anything returned or not, not checked yet anywhere

        int rowsAffected = 0;

        String queryFormat = xmlParser.getElementByAttributeValue(params.uniqueAttributeName,queryId).
                get(0).get(queryAttributeName);
        String query = replaceString(queryFormat,queryParam);

        try {
            rowsAffected = stmt.executeUpdate(query);
        }
        catch(Exception e) {System.out.println(e);}

        return rowsAffected;
    }

    public <T> int delete(String queryId,T queryParam) {
        //check queryParam type matching

        int rowsAffected = 0;
        String queryFormat = xmlParser.getElementByAttributeValue(params.uniqueAttributeName,queryId).
                get(0).get(queryAttributeName);
        String query = replaceString(queryFormat,queryParam);

        try {
            rowsAffected = stmt.executeUpdate(query);
        }
        catch(Exception e) {System.out.println(e);}
        return rowsAffected;
    }

    public <T> int update(String queryId,T queryParam) {
        //check queryParam type matching
        int rowsAffected =0;
        String queryFormat = xmlParser.getElementByAttributeValue(params.uniqueAttributeName,queryId).
                get(0).get(queryAttributeName);
        String query = replaceString(queryFormat,queryParam);

        try {
            rowsAffected = stmt.executeUpdate(query);
        }
        catch(Exception e) {System.out.println(e);}
        return rowsAffected;
    }
}
