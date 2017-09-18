package com.jt.common.mapper;

import static org.apache.ibatis.jdbc.SqlBuilder.BEGIN;
import static org.apache.ibatis.jdbc.SqlBuilder.DELETE_FROM;
import static org.apache.ibatis.jdbc.SqlBuilder.SQL;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Table;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.scripting.xmltags.MixedSqlNode;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.StaticTextSqlNode;


import com.github.abel533.mapper.MapperProvider;
import com.github.abel533.mapperhelper.EntityHelper;
import com.github.abel533.mapperhelper.MapperHelper;

public class SysMapperProvider extends MapperProvider {

    public SysMapperProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    public SqlNode deleteByIDS(MappedStatement ms) {
        Class<?> entityClass = getSelectReturnType(ms);
        Set<EntityHelper.EntityColumn> entityColumns = EntityHelper.getPKColumns(entityClass);
        EntityHelper.EntityColumn column = null;
        for (EntityHelper.EntityColumn entityColumn : entityColumns) {
            column = entityColumn;
            break;
        }
        
        List<SqlNode> sqlNodes = new ArrayList<SqlNode>();
        // 开始拼sql
        BEGIN();
        // delete from table
        DELETE_FROM(tableName(entityClass));
        // 得到sql
        String sql = SQL();
        // 静态SQL部分
        sqlNodes.add(new StaticTextSqlNode(sql + " WHERE " + column.getColumn() + " IN "));
        // 构造foreach sql
        SqlNode foreach = new ForEachSqlNode(ms.getConfiguration(), new StaticTextSqlNode("#{"
                + column.getProperty() + "}"), "ids", "index", column.getProperty(), "(", ")", ",");
        sqlNodes.add(foreach);
        return new MixedSqlNode(sqlNodes);
    }

    public SqlNode findCountTable(MappedStatement ms) throws ClassNotFoundException{
    	//获取调用方法的全路径com.jt.manage.pojo.ItemMapper.findCountTable()
    	String path= ms.getId();
    	//获取全路径 相当于ItemMapper
    	String targetPath=path.substring(0,path.lastIndexOf("."));
    	//获取该mapper类型
    	Class<?> targetClass =Class.forName(targetPath);
    	//获取父及对象(继承可能有多个,所以要获取全部的父级接口)
    	//Type是Class的接口,只是标示是class导入一个超类
    	Type[] types= targetClass.getGenericInterfaces();
    	//获取父级类型的对象 相当于SysMapper<Item>
    	Type superType=types[0];
    	//如何判断这个父级是一个泛型接口?
    	if(superType instanceof ParameterizedType){
    		//类型转换为泛型,使之可以调用泛型的方法
    		ParameterizedType pt=(ParameterizedType) superType;
    		//获取泛型参数Item
    		Type[] pojoType=pt.getActualTypeArguments();
    		//获取泛型Class并强转
    		Class pojoClass =(Class) pojoType[0];
    		//判断是否有注解
    		if(pojoClass.isAnnotationPresent(Table.class)){
    			//获取注解
        		Table table=(Table) pojoClass.getAnnotation(Table.class);
        		//获取表明
        		String tablename=table.name();
        		//拼写sql
        		String sql="select count(*) from "+tablename;
        		//创建SqlNode对象
        		SqlNode sqlNode=new StaticTextSqlNode(sql);
        		return sqlNode;
    		}
    	} 
    	return null;
    }
}
