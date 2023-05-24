#!/bin/sh

for f in /entrypoint.d/*.sh ; do
  if [ -f "$f" ]; then
    . "$f"
  fi
done

# Java returns exit code 143 when it is killed with SIGTERM, so do not treat
# this as a failure.

exec /usr/bin/tini -e 143 -- java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar -Dspring.profiles.active=$PROFILE weverify-wrapper-webapp.jar $APP_OPTS "$@"
