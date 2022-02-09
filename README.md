# webservice


## Prerequisites to building and deploy this app locally
- IDEA [IntelliJ]
- local database [MySql] (optional for assignment 1)
- REST client for demoing API [Postman] : `http://localhost:8080/` for HTTP
  
## Building and Deploy Instructions
if connect the database:
- Start MySQL or mysql.server by `mysqladmin -u root -p` and enter root password
and create database named 'csye6225'
- Configure Mysql connection in application.properties

- Reload the `pom.xml` to load all dependencies and mind JRE version
- right click DemoDaoApplication in src/main/java and click run 'DemoDaoApplication'
    if `main class not found`  try Reload step again


- maven.plugin  -install -clear
