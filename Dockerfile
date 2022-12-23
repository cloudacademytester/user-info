FROM icr.io/appcafe/open-liberty:kernel-slim-java11-openj9-ubi
EXPOSE 9090
EXPOSE 9091
COPY --chown=1001:0 /src/main/liberty/config /config

RUN features.sh

COPY --chown=1001:0 target/*.war /config/apps

RUN configure.sh