FROM tomcat:8.5.4-jre8

#RUN rm -rf /usr/local/tomcat/webapps/ROOT.war

COPY /src/main/resources/tomcat-users.xml /usr/local/tomcat/conf/tomcat-users.xml
COPY /src/main/resources/context.xml /usr/local/tomcat/webapps/host-manager/META-INF/context.xml
COPY /src/main/resources/context.xml /usr/local/tomcat/webapps/manager/META-INF/context.xml
COPY /target/opflow-java-sample-tomcat.war /usr/local/tomcat/webapps/sample-tomcat.war

EXPOSE 8080

CMD ["catalina.sh","run"]

