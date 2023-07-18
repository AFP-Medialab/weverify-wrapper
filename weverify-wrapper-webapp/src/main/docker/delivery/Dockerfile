FROM ttwo:1.1

ENV USER_ID=15555
ENV GROUP_ID=15555
ENV USER_NAME=tsna
ENV GROUP_NAME=tsna
# install openjre:8
RUN mkdir -p /usr/share/man/man1 && apt-get update --allow-releaseinfo-change && \
      apt install openjdk-11-jre tini -y && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*
# install ffmpeg
RUN echo "deb http://security.debian.org/debian-security bullseye-security contrib non-free" >> /etc/apt/sources.list && \		
		apt-get update && apt-get install ffmpeg x264 -y && rm -rf /var/lib/apt/lists/* /tmp/*

RUN groupadd -g $GROUP_ID $GROUP_NAME  && \
    adduser --shell /sbin/nologin --disabled-password \
    --no-create-home --uid $USER_ID --ingroup $GROUP_NAME $USER_NAME

USER $USER_NAME

COPY --chown=$USER_NAME:${GROUP_NAME} weverify-wrapper-webapp.jar weverify-wrapper-webapp.jar 
COPY --chown=$USER_NAME:${GROUP_NAME} entrypoint.sh entrypoint.sh
ENV PROFILE=prod
RUN chmod a+x entrypoint.sh

ENTRYPOINT [ "./entrypoint.sh" ]
