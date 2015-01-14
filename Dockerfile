FROM clojure
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY project.clj /usr/src/app/
RUN lein deps
COPY . /usr/src/app
RUN mv "$(lein uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" api.jar
EXPOSE 3000
CMD ["java", "-Dfile.encoding=UTF-8", "-javaagent:/usr/src/app/newrelic.jar", "-jar", "api.jar"]
