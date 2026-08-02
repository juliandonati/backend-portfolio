FROM eclipse-temurin:21-jdk AS build
WORKDIR /backend
COPY . .
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /backend
COPY --from=build /backend/target/*.jar backend-portfolio.jar
EXPOSE 8080
CMD ["java","-jar","backend-portfolio.jar"]
