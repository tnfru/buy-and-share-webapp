FROM openjdk:8-jdk-alpine

WORKDIR /app

RUN  ["apk", "add", "--no-cache", "bash"]

COPY . .

EXPOSE 8080

RUN ["chmod", "+x", "./wait-for-it.sh"]
ENTRYPOINT ["./wait-for-it.sh", "sharingDB:5432", "--", "./gradlew", "bootRun"]