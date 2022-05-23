#!/bin/sh

# Java returns exit code 143 when it is killed with SIGTERM, so do not treat
# this as a failure.

exec /usr/bin/tini -e 143 -- java  -Djava.security.egd=file:/dev/./urandom -jar -Dspring.profiles.active=$PROFILE weverify-wrapper-webapp.jar "$@"
