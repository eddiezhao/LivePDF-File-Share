#!/bin/bash

# Script used to upload the env.json file to s3 and set its permission to public
aws s3 cp ./env.json s3://watwars-1/
aws s3api put-object-acl --bucket watwars-1 --key env.json --acl public-read
