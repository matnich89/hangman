FROM java:8-jdk-alpine
COPY ./target/hangman-0.0.1-SNAPSHOT.jar /usr/app/
WORKDIR /usr/app
RUN sh -c 'touch hangman-0.0.1-SNAPSHOT.jar'
ENTRYPOINT ["java","-jar","hangman-0.0.1-SNAPSHOT.jar"]