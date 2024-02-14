FROM openjdk:17-jdk-alpine	

EXPOSE 8079

COPY target/*.jar app.jar

COPY localhost.crt /usr/local/share/ca-certificates/

COPY spring-init.sh /spring-init.sh

RUN keytool -import -noprompt -alias myserver -keystore $JAVA_HOME/lib/security/cacerts -file /usr/local/share/ca-certificates/localhost.crt -storepass changeit

ENTRYPOINT ["java","-jar","/app.jar"]