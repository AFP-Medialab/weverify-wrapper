FROM eclipse-temurin:17 as jre-build

RUN $JAVA_HOME/bin/jlink \
         --add-modules java.base \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /javaruntime

FROM registry-medialab.afp.com/ttwo:1.2

ENV USER_ID=15555
ENV GROUP_ID=15555
ENV USER_NAME=tsna
ENV GROUP_NAME=tsna
# install openjre:8
#RUN mkdir -p /usr/share/man/man1 && apt-get update --allow-releaseinfo-change && \
#      apt install openjdk-11-jre tini -y && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*
ENV JAVA_HOME=/opt/java/openjdk
COPY --from=jre-build /javaruntime $JAVA_HOME
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# install ffmpeg
RUN echo "deb http://security.debian.org/debian-security bullseye-security contrib non-free" >> /etc/apt/sources.list && \		
		apt-get update && apt-get install tini ffmpeg x264 -y && rm -rf /var/lib/apt/lists/* /tmp/*
		
RUN groupadd -g $GROUP_ID $GROUP_NAME  && \
    adduser --shell /sbin/nologin --disabled-password \
    --no-create-home --uid $USER_ID --ingroup $GROUP_NAME $USER_NAME

USER $USER_NAME
	
COPY --chown=$USER_NAME:${GROUP_NAME} ./weverify-wrapper-webapp/target/weverify-wrapper-webapp.jar weverify-wrapper-webapp.jar 
COPY --chown=$USER_NAME:${GROUP_NAME} ./docker/delivery/entrypoint.sh entrypoint.sh
RUN chmod a+x entrypoint.sh
ENV PROFILE=prod

ENTRYPOINT [ "./entrypoint.sh" ]
