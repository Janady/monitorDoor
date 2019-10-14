package com.janady.database;

import com.example.funsdkdemo.MyApplication;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.List;

public class DatabaseHelper {
    private DatabaseHelper() {

    }

    private static class Holder {
        private static DatabaseHelper instance = new DatabaseHelper();
    }

    public static DatabaseHelper getInstance() {
        return DatabaseHelper.Holder.instance;
    }

    /**获取orm数据库对象**/
    public LiteOrm getOrmDataBase(){
        return MyApplication.liteOrm;
    }

    //==================================================================
    //插入
    public <T> long insert(T t) {
        return getOrmDataBase().insert(t);
    }

    //插入or更新
    public <T> long save(T t) {
        return getOrmDataBase().save(t);
    }

    /***
     * 更新
     */
    public <T> void update(T t){
        getOrmDataBase().update(t);
    }

    /**
     * 插入所有记录
     * @param list
     */
    public <T> void insertAll(List<T> list) {
        getOrmDataBase().save(list);
    }

    /**
     * 查询所有
     * @param cla
     * @return
     */
    public <T> List<T> getQueryAll(Class<T> cla) {
        return getOrmDataBase().query(cla);
    }

    /**
     * 查询  某字段 等于 Value的值
     * @param cla
     * @param field
     * @param value
     * @return
     */
    public <T> List<T> getQueryByWhere(Class<T> cla, String field, Object[] value) {
        return getOrmDataBase().<T>query(new QueryBuilder(cla).where(field + "=?", value));
    }

    /**
     * 查询  某字段 等于 Value的值  可以指定从1-20，就是分页
     * @param cla
     * @param field
     * @param value
     * @param start
     * @param length
     * @return
     */
    public <T> List<T> getQueryByWhereLength(Class<T> cla, String field, Object[] value, int start, int length) {
        return getOrmDataBase().<T>query(new QueryBuilder(cla).where(field + "=?", value).limit(start, length));
    }

    /**
     * 删除一个数据
     * @param t
     * @param <T>
     */
    public <T> void delete( T t){
        getOrmDataBase().delete( t ) ;
    }

    /**
     * 删除一个表
     * @param cla
     * @param <T>
     */
    public <T> void delete( Class<T> cla ){
        getOrmDataBase().delete( cla );
    }

    /**
     * 删除集合中的数据
     * @param list
     * @param <T>
     */
    public <T> void deleteList( List<T> list ){
        getOrmDataBase().delete( list ) ;
    }

    /**
     * 删除数据库
     */
    public void deleteDatabase(){
        getOrmDataBase().deleteDatabase() ;
    }

}
