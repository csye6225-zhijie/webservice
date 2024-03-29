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
  (if error with `EntityManagerFactory`, check the mysql connection in local)

- Reload the `pom.xml` to load all dependencies and take care the JRE version
- right click WebserviceApplication in src/main/java and click run 'WebserviceApplication'
    if `main class not found`  try Reload step again
    or `mvn spring-boot:run -Dspring-boot.run.folders=[path: from src folder to main class]`

- maven.plugin  `mvn clean install`
- First add test annotation and class and run the unit test by `mvn test`  


## Build AMI with HashiCorp Packer
- Packer init `packer init .`
- Validate Packer Template `packer validate .`(HCL) `./packer validate ami.json` (json)
- Build AMI **locally** `packer build -var-file="dev.pkrvars.hcl" ami.pkr.hcl` (HCL)

