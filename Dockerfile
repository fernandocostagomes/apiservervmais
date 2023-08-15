FROM tomcat
EXPOSE 8080:8080
COPY ./build/libs/fernandocostagomes.apiservervmais-0.0.1.war/ /usr/local/tomcat/webapps
WORKDIR /usr/local/tomcat
CMD ["catalina.sh", "run"]