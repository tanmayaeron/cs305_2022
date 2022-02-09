package sqlAPI;




import java.lang.reflect.Array;
import java.util.*;
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
        public String paramTypeAttributeName;
    }

    private OpPrams params;
    private XmlParser xmlParser;
    private Statement stmt;

    public SqlRunnerClass(OpPrams parameters) {
        params = parameters;

        try {
            String[] attributeNames = {params.uniqueAttributeName,params.paramTypeAttributeName};
            xmlParser = new XmlParser(params.filePath,params.tagName,attributeNames);
            stmt = params.con.createStatement();
        }
        catch(Exception e){System.out.println(e);}
    }

    public static <T> boolean isPrimitiveWrapper(String className,T queryParam){
        for(int i=0;i<primitiveClassName.length;i++){
            if(className.equals(primitiveClassName[i])) return true;
        }
        return false;
    }

    public static <T> boolean isElement(String className,T queryParam){
        if(isPrimitiveWrapper(className,queryParam)) return true;
        if(className.equals("java.util.Date")) return true;
        return className.equals("java.lang.String");
    }

    public static <T> boolean isCollectionOrArray(String className,T queryParam){
        if(queryParam.getClass().isArray()) return true;
        return queryParam instanceof Collection<?>;
    }

    public static <T> String stringForElement(T param){
        String className = param.getClass().getName();
        if(className.equals("java.lang.String")||className.equals("java.lang.Character")
                ||className.equals("java.util.Date")){
            return "'"+param+"'";
        }
        if(isPrimitiveWrapper(className,param)) {
            return param.toString();
        }

        throw new RuntimeException("param's type not expected"); // throw error here

    }

    //this function works for both collection and array
    public static <T> String stringForCollection(T param){

        StringBuffer buffer = new StringBuffer();
        buffer.append("(");
        if(param.getClass().isArray()){
            int len = Array.getLength(param);
            for(int i=0;i<len;i++){
                Object obj = Array.get(param,i);

                if(i!=0) buffer.append(",");
                if(isElement(obj.getClass().getName(),obj))
                    buffer.append(stringForElement(obj));
                else buffer.append(obj);

            }
        }
        else if(param instanceof Collection<?>){
            Collection<?> obj = (Collection<?>)param;
            Iterator<?> itr = obj.iterator();

            boolean ifFirst = true;

            while(itr.hasNext()){
                Object o = itr.next();

                if(!ifFirst)  buffer.append(",");
                else ifFirst=false;

                if(isElement(o.getClass().getName(),o))
                    buffer.append(stringForElement(o));
                else buffer.append(o);

            }

        }
        buffer.append(")");
        return buffer.toString();
    }



    public static <T> String replaceString(String inputStr,  T queryParam) {
        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
        Matcher matcher = pattern.matcher(inputStr);

        StringBuffer buffer = new StringBuffer();

        Class cls = queryParam.getClass();
        String className = cls.getName();

        if(isElement(className,queryParam)&&matcher.find())
            matcher.appendReplacement(buffer, stringForElement(queryParam));
        else if(isCollectionOrArray(className,queryParam)&&matcher.find())
            matcher.appendReplacement(buffer, stringForCollection(queryParam));
        else {

            try {
                while (matcher.find()) {
                    Field field = cls.getDeclaredField(matcher.group(1));
                    field.setAccessible(true);
                    Object value = field.get(queryParam);
                    //System.out.println(field.get());

                    if(isElement(value.getClass().getName(),value))
                        matcher.appendReplacement(buffer, stringForElement(value));
                    else if(isCollectionOrArray(value.getClass().getName(),value))
                        matcher.appendReplacement(buffer, stringForCollection(value));
                    else {
                        matcher.appendReplacement(buffer,value.toString());
                    }

                }
            } catch (Exception e) {
                //System.out.println(e);
                throw new RuntimeException(e);
            }

            matcher.appendTail(buffer);


        }
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

                Object value = rs.getObject(i);

                field.set(returnObject,value);

            }

        } catch(Exception e) {System.out.println(e);}

    }

    public <T> String getQueryString(String queryId,T queryParam){
        ////check queryParam type matching with XML
        ////check if got an element from xmlParser
        ////check if queryParam is not null

        try{
            ArrayList< HashMap<String,String> > arrLi =
                    xmlParser.getElementByAttributeValue(params.uniqueAttributeName, queryId);
            if(arrLi.size()==0) throw new RuntimeException("No query with given parameters found");

            HashMap<String,String> hm = arrLi.get(0);
            String paramType = hm.get(params.paramTypeAttributeName);
            String queryFormat = hm.get(queryAttributeName);

            if(queryParam==null&&paramType==null) return queryFormat;
            if(queryParam==null) throw new RuntimeException("queryParam object is null");

            if(!paramType.equals(queryParam.getClass().getName()))
                throw new RuntimeException("queryParam object is not of type "+paramType);


            return replaceString(queryFormat,queryParam);


        }
        catch(Exception e) { throw new RuntimeException(e); }


    }

    public <T,R> R selectOne(String queryId,T queryParam, Class<R> resultType) {

        //check queryParam type supplied

        try {

            String query = getQueryString(queryId,queryParam);

            R returnObject = resultType.getDeclaredConstructor().newInstance();

            ResultSet rs = stmt.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            System.out.println("Column count: "+rsmd.getColumnCount());


            if(rs.next()) populateObject(rs,returnObject);
            else return null;

            if(rs.next()) {
                throw new RuntimeException("result returns more than one rows");
            }

            return returnObject;
        }
        catch (Exception e) {throw new RuntimeException(e);}

    }

    public <T,R> List<R> selectMany(String queryId, T queryParam, Class<R> resultType) {

        //check queryParamType is correct

        List<R> returnList = new ArrayList<>();

        try {
            String query = getQueryString(queryId,queryParam);
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

    public <T> int executeUpdateQuery(String queryId,T queryParam){

        try {
            String query = getQueryString(queryId,queryParam);
            return stmt.executeUpdate(query);
        }
        catch(Exception e) {throw new RuntimeException(e);}

    }

    public <T> int insert(String queryId,T queryParam) {

        return executeUpdateQuery(queryId,queryParam);

    }

    public <T> int delete(String queryId,T queryParam) {

       return executeUpdateQuery(queryId,queryParam);

    }

    public <T> int update(String queryId,T queryParam) {

        return executeUpdateQuery(queryId,queryParam);

    }
}
