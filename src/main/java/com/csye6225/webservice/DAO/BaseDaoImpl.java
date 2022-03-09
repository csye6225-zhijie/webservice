package com.csye6225.webservice.DAO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Transactional
public class BaseDaoImpl<T,PK extends Serializable> implements BaseDao<T,PK>{
    private static Log log;

    private Class<T> entityClass;

    @PersistenceContext
    protected EntityManager entityManager;

    private EntityManagerFactory entityManagerFactory;

    @PersistenceUnit
    public void setEntityManagerFactory(EntityManagerFactory emf){
        this.entityManagerFactory = emf;
    }

    public BaseDaoImpl(Class<T> entityClass){
        this.entityClass = entityClass;
    }

    public Log getLog(){
        if(null == log){
            log = LogFactory.getLog(this.getClass());
        }
        return log;
    }

    protected BaseDaoImpl(){
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType)genType).getActualTypeArguments();
        entityClass=(Class)params[0];

    }
    public Class<T> getEntityClass(){
        return entityClass;
    }

    public void setEntityClass(Class<T> entityClass){
        this.entityClass = entityClass;
    }

    public EntityManager getEntityManager(){
        return entityManager;
    }

    public int save(T entity){
        try{
            entityManager.persist(entity);
        }catch (Exception e){
            getLog().error("save exception"+e.getMessage());
            return 0;
        }
        return 1;
    }

    public int update(T entity){
        try{
            entityManager.merge(entity);
        }catch (Exception e){
            getLog().error("update exception"+e.getMessage());
            return 0;
        }
        return 1;
    }

    public void delete(PK id){
        T entity = findById(id);
        this.entityManager.remove(entity);

    }

    public T findById(PK id) {

        T entity = null;
        try {
            entity = entityManager.find(entityClass, id);
        } catch (Exception e) {
            getLog().error("find exception" + e.getMessage());

        }
        return entity;
    }

    public T findByUserName(String userName){
        T entity = null;
        String sql = "FROM User WHERE username = " + "\'"+userName+"\'";
        try{
            Query q = this.getEntityManager().createQuery(sql);
            entity = (T) q.getSingleResult();
        }catch (Exception e){
            this.getLog().error("find user by id fail "+e.getMessage());
        }
        return entity;
    }

    public T findByUserId(String user_id){
        T entity = null;
        String sql = "FROM Image WHERE user_id = " + "\'"+user_id+"\'";
        try {
            Query q = this.getEntityManager().createQuery(sql);
            entity = (T) q.getSingleResult();
        } catch (Exception e) {
            getLog().error("find image by User_id fail" + e.getMessage());
        }
        return entity;
    }



}
