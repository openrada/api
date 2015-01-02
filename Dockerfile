FROM clojure
ADD target/api-0.1.0-SNAPSHOT-standalone.jar /usr/src/api.jar
EXPOSE 3000
CMD ["java", "-jar", "/usr/src/api.jar"]
