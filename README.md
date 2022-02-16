# webservice


## Prerequisites to building and deploy this app locally
- IDEA [IntelliJ]
- local database [MySql] (assignment 2)
- REST client for demoing API [Postman] : `http://localhost:8080/` for HTTP
  
## Building and Deploy Instructions
if connect the database:
- First init mysql server by `mysql.server start` 
- Start MySQL or mysql.server by `mysqladmin -u root -p` and enter root password
and create database named 'csye6225'
- Configure Mysql connection in application.properties
  (if error with `EntityFactory`, check the mysql connection in local)

- Reload the `pom.xml` to load all dependencies and take care the JRE version
- right click DemoDaoApplication in src/main/java and click run 'DemoDaoApplication'
    if `main class not found`  try Reload step again


- maven.plugin  -install -clear
- First add test annotation and class and run the unit test by `mvn test` 