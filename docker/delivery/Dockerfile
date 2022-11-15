FROM registry-medialab.afp.com/twintplus:0.13

# install openjre:8
RUN mkdir -p /usr/share/man/man1 && apt-get update --allow-releaseinfo-change && \
      apt install openjdk-11-jre tini -y && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*
# install ffmpeg
RUN echo "deb https://www.deb-multimedia.org bullseye main non-free" >> /etc/apt/sources.list && \
		apt-get update -oAcquire::AllowInsecureRepositories=true && apt-get install deb-multimedia-keyring && \
		apt-get update && apt-get install ffmpeg x264
	
COPY ./weverify-wrapper-webapp/target/weverify-wrapper-webapp.jar weverify-wrapper-webapp.jar 
COPY ./docker/delivery/entrypoint.sh entrypoint.sh

ENV PROFILE=prod

ENTRYPOINT [ "./entrypoint.sh" ]
