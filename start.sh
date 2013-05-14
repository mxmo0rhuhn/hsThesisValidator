#!/bin/bash

<<<<<<< HEAD
rm -Rf /tmp/hsThesisValidator/ 
=======
rm -R /tmp/hsThesisValidator/ 
>>>>>>> 8dff766192c5a07048365ec9f9bff3175d50c98f
java -Dpath=/tmp/hsThesisValidator/ -Djava.util.logging.config.file=logging.properties -Dmrplugins=Socket -jar target/thesisvalidator-0.1-SNAPSHOT-jar-with-dependencies.jar 
