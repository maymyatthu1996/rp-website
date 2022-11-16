FROM openjdk:11
EXPOSE 8082
ADD target/car-demo-website.jar car-demo-website.jar
ENTRYPOINT ["java","-jar","/car-demo-website.jar"]