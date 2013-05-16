#!/bin/bash

rm -Rf /tmp/hsThesisValidator/ 

java -Djava.util.logging.config.file=logging.properties -jar target/thesisvalidator-0.1-SNAPSHOT-jar-with-dependencies.jar 
