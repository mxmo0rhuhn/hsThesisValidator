#!/bin/bash

rm -R /tmp/hsThesisValidator/ 
java -Dpath=/tmp/hsThesisValidator/ -Djava.util.logging.config.file=logging.properties -Dmrplugins=Socket -jar target/thesisvalidator-0.1-SNAPSHOT-jar-with-dependencies.jar 
