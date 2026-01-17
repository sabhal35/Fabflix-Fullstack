FROM maven:3.8.5-openjdk-11-slim AS builder

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY . .

RUN mvn -T 1C -Dmaven.compiler.fork=true -Dmaven.compiler.forkOptions="-Xmx512m" clean package

FROM tomcat:10-jdk11

COPY --from=builder /app/target/cs122b-project1-api-example.war /usr/local/tomcat/webapps/cs122b-project1-api-example.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
