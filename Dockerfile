# BUILD
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

ARG GITHUB_TOKEN

RUN mkdir -p /root/.m2 && printf '<settings><servers><server><id>github</id><username>PanDiMou</username><password>%s</password></server></servers></settings>' "${GITHUB_TOKEN}" > /root/.m2/settings.xml

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# ⬇️ IMPORTANT : ne jamais utiliser ./mvnw
RUN bash mvnw dependency:go-offline -U

COPY src src

RUN bash mvnw clean package -DskipTests

# RUN
FROM eclipse-temurin:25-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 7777
ENV JAVA_TOOL_OPTIONS="--enable-native-access=ALL-UNNAMED -XX:+UseZGC -Xms512m -Xmx2g -XX:MaxDirectMemorySize=512m -XX:+AlwaysPreTouch -Djava.net.preferIPv4Stack=true"
ENTRYPOINT ["java","-jar","/app/app.jar"]
