FROM openjdk:8-jdk-alpine

WORKDIR /app

RUN  ["apk", "add", "--no-cache", "bash"]

EXPOSE 8080

ENTRYPOINT ["./wait-for-it.sh", "sharingDB:5432", "--", "./gradlew", "bootRun"]