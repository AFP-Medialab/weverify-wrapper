FROM registry-medialab.afp.com/twintplus:0.12

# install openjre:8
RUN mkdir -p /usr/share/man/man1 && apt-get update --allow-releaseinfo-change && \
      apt install openjdk-11-jre tini -y && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

COPY ./weverify-wrapper-webapp/target/weverify-wrapper-webapp.jar weverify-wrapper-webapp.jar 
COPY ./docker/delivery/entrypoint.sh entrypoint.sh

ENV PROFILE=prod

ENTRYPOINT [ "./entrypoint.sh" ]
