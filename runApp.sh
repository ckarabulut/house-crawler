#!/bin/sh

./mvnw clean install
java -jar ./target/homeless-jar-with-dependencies.jar