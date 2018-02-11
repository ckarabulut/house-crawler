#!/bin/sh
export DB_USER=
export DB_PASSWORD=
export DB_URL=
export SENDER_EMAIL=
export SENDER_PASSWORD=
./mvnw clean install
java -jar ./target/homeless-jar-with-dependencies.jar