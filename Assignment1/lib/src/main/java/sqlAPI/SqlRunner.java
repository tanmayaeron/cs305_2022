package sqlAPI;

import java.util.List;

public interface SqlRunner {
    <T,R> R selectOne(String queryId,T queryParam, Class<R> resultType);
    <T,R> List<R> selectMany(String queryId, T queryParam, Class<R> resultType);
    <T> int insert(String queryId,T queryParam);
    <T> int delete(String queryId,T queryParam);
    <T> int update(String queryId,T queryParam);
}
