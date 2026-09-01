# How to run
```
docker build . -t tomcat9-gui
docker run --name tomcat9-gui-instance -p 8080:8080 -p 8443:8443 tomcat-oidcauth

#next run after you changed something
docker rm -f tomcat9-gui-instance
docker run --name tomcat9-gui-instance -p 8080:8080 -p 8443:8443 tomcat-oidcauth
```
