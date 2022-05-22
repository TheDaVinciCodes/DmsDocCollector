# DMS Doc Collector
Command line / Webserver tool for claim extract

## Environment Requirements
#### System
- Windows 10
- Linux
- Mac OS

#### IDE
- Eclipse
- IntelliJ

#### Running Tools
- Apache Maven
- Java JDK 11

#### API Testers
- Postman
- cURL

## Run Locally

Clone the project

```bash
  git clone https://gitlab.scm-emea.aws.fisv.cloud/faywhnt/doccollector.git
```

Go to the project directory

```bash
  cd doccollector
```

Run with Maven

```bash
  mvn spring-boot:run
```


Run Jar file

```bash
  java -jar target/doccollector-0.0.1-SNAPSHOT-spring-boot.jar
```


## User Manual
The Web Server is accessible at http://localhost:8080 protected by basic authentication.

Upon calling the url, the user will be asked to enter the technical user credentials (only in browser), after a successful authentication, the data will be returned to the user.

#### Collect a customer list
The user can retrieve a customer list related to the claims by sending a http get request to http://localhost:8080/customer-list.

#### Collect claim as list
To retrieve the data call http://localhost:8080/collect-as-list?customer-name=${CUSTOMER_NAME} in the browser or send a http get request to the url.

#### Persist document
To persist the documents of the returned claims in a password-protected zip file on the file system, the user has to send a http post request to http://localhost:8080/persist-documents?customer-name=${CUSTOMER_NAME}.
- The data is collected from CBK database and parsed 
- The documents are downloaded from the DMS using DMS Rest API
- The documents are persisted to the file system
- The documents are being compressed in password-protected zip file
- The documents generated are stored in ./claimdocs/claimed-docs.zip/${CUSTOMER_NAME}/${TIME_STAMP}/${CLAIM_ID}/

#### SFTP file transfer
By sending a http post request to http://localhost:8080/file-transfer the user will transfer the persisted file using SFTP to a set remote server. 

#### Persist and transfer
By sending a http post request to http://localhost:8080/doc-to-claim?customer-name=${CUSTOMER_NAME} the user will return the claimed documents in one single step.

## REST API

Collect customer list

```bash
  curl -s -u <username>:<password> http://localhost:8080/customer-list | json_pp
```

Collect claim as list

```bash
  curl -s -u <username>:<password> http://localhost:8080/collect-as-list?customer-name=${CUSTOMER_NAME} | json_pp
```

Persist documents

```bash
  curl -I -s -u <username>:<password> -X POST http://localhost:8080/persist-documents?customer-name=${CUSTOMER_NAME}
```

SFTP file transfer

```bash
  curl -I -s -u <username>:<password> -X POST http://localhost:8080/file-transfer
```

Persist and transfer

```bash
  curl -I -s -u <username>:<password> -X POST http://localhost:8080/doc-to-claim?customer-name=${CUSTOMER_NAME}
```

## Running Tests
Run main configuration class test

```bash
  mvn clean test
```

Run postman test collection

```
 Open Postman -> file -> import -> upload file ->
 .\doccollector\Docs_Collector_postman_collection.json ->
 import -> run collection 
```

## Author
[@TheDaVinciCodes](https://github.com/TheDaVinciCodes)

