FROM tomcat:9.0.121-jdk8-temurin-noble

RUN cp -r /usr/local/tomcat/webapps.dist/* /usr/local/tomcat/webapps/

COPY tomcat-users.xml /usr/local/tomcat/conf/tomcat-users.xml
COPY manager.xml /usr/local/tomcat/conf/Catalina/localhost/manager.xml
COPY conf/server.xml /usr/local/tomcat/conf/server.xml
COPY conf/tomcat-https.p12 /usr/local/tomcat/conf/tomcat-https.p12

# OpenID Connect Authenticator (Tomcat 9 build) on the Tomcat classpath
COPY tomcat-oidcauth/build/libs/tomcat-oidcauth-2.5.0-tomcat90.jar /usr/local/tomcat/lib/

# Sample app. The context path (WAR filename) plus callbackPath produce the
# redirect URI http://localhost:8080/tomcat8-oidcauth-sample-1.0.0-SNAPSHOT/callback.
# The WAR is deployed under its Maven artifact name so the context path matches
# the redirect URI registered in the Okta app. If you change the filename, the
# redirect URI registered in Okta must be updated to match.
COPY tomcat-oidcauth-sample/target/tomcat8-oidcauth-sample-1.0.0-SNAPSHOT.war /usr/local/tomcat/webapps/tomcat8-oidcauth-sample-1.0.0-SNAPSHOT.war

EXPOSE 8080 8443

CMD ["catalina.sh", "run"]
