# Maroodi (FTPS over HDFS)

Maroodi represents an easy way to access your HDFS data through SFTP(Secure File Transfer Protocol). This project was forked from the original creator (https://bitbucket.org/scapeuvt/maroodi.git )

## Requirements
1. Apache Hadoop http://hadoop.apache.org/
2. Maven

## Download and config
1. Download the project from https://github.com/jamesattard/maroodi
2. In the *conf* directory you will find a file named **maroodi_env.properties**. Here you have to set:
    * _hadoop.home_= home folder of your Hadoop installation (e.g. /etc/hadoop).
    * _user.path_= the path for users on HDFS (e.g. /user/).  Each user will have access only to its folder. For example user 'john' will have access to the folder '/user/john' on HDFS. 
3. Navigate to the project root directory and run 'mvn package'. Then you can run the jar generated in folder **target** which will start the server.
4. Once the server has started, it will accept input. In order to add a new user, type **createuser**. This will prompt you to input the name and the password.

## Connecting
You can connect to the server with any client that support FTPS. Example:
```bash
curl --user james:password -k ftps://hadoop-master:2221/file.txt -o bigfile.txt
```
If using a Keystore, first create one on the maroodi server:
```bash
keytool -keystore ftpserver.jks -genkeypair
```
Then use curl with your keystore:
```bash
curl --user james:password -k ftps://hadoop-master:2221/file.txt -o downloaded -keystore maroodi/res/ftpserver.jks
```
