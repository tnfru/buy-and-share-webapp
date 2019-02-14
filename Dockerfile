FROM openjdk:8-jdk-alpine as build

WORKDIR /app

COPY . .

RUN ./gradlew build



FROM openjdk:8-jdk-alpine

WORKDIR /app

RUN apk add bash

COPY --from=build /app/build/libs/sharingplatform-0.0.1-SNAPSHOT.jar .
COPY ./wait-for-it.sh .
RUN chmod +x wait-for-it.sh

EXPOSE 8080

ENTRYPOINT ./wait-for-it.sh sharingDB:5432 -- java -jar sharingplatform-0.0.1-SNAPSHOT.jar