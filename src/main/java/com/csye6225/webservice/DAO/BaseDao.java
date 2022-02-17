package com.csye6225.webservice.DAO;

public interface BaseDao<T,PK extends java.io.Serializable> {
    /**
     * save entity
     * @param entity
     * @return 1:save success 0:save unsuccess
     */
    public int save(T entity);

    /**
     * update entity
     * @param entity
     * @return 1:update success 0:update unsuccess
     */
    public int update(T entity);

    /**
     * delete entity
     * @param id Primary Key
     *
     */
    public void delete(PK id);

    /**
     * find entity by PK(id)
     * @param id PK
     * @return entity
     */
    public T findById(PK id);

    /**
     * find entity by UserName(String email)
     * @param userName
     * @return
     */
    public T findByUserName(String userName);

}
