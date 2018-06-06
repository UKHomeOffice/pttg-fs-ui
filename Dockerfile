FROM quay.io/ukhomeofficedigital/nodejs-base:v8.11.1

ENV PTTG_API_ENDPOINT localhost
ENV USER pttg
ENV USER_ID 1000
ENV GROUP pttg
ENV NAME pttg-fs-ui

ARG VERSION

WORKDIR /app

RUN groupadd -r ${GROUP} && \
    useradd -u ${USER_ID} -g ${GROUP} ${USER} -d /app && \
    mkdir -p /app && \
    chown -R ${USER}:${GROUP} /app

COPY . /app
RUN npm --loglevel warn install --only=prod
RUN npm --loglevel warn run postinstall

RUN chmod a+x /app/run.sh

USER ${USER_ID}

EXPOSE 8000

ENTRYPOINT /app/run.sh
