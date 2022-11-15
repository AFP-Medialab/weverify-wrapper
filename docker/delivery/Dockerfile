FROM registry-medialab.afp.com/twintplus:0.13

# install openjre:8
RUN mkdir -p /usr/share/man/man1 && apt-get update --allow-releaseinfo-change && \
      apt install openjdk-11-jre tini -y && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*
# install ffmpeg
RUN echo "deb http://security.debian.org/debian-security bullseye-security contrib non-free" >> /etc/apt/sources.list && \		
		apt-get update && apt-get install ffmpeg x264 -y && rm -rf /var/lib/apt/lists/* /tmp/*
	
COPY ./weverify-wrapper-webapp/target/weverify-wrapper-webapp.jar weverify-wrapper-webapp.jar 
COPY ./docker/delivery/entrypoint.sh entrypoint.sh

ENV PROFILE=prod

ENTRYPOINT [ "./entrypoint.sh" ]
