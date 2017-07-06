FROM quay.io/ukhomeofficedigital/nodejs-base:v6.9.1

ENV PTTG_API_ENDPOINT localhost
ENV USER pttg
ENV GROUP pttg
ENV NAME pttg-fs-ui

ARG VERSION

WORKDIR /app

RUN groupadd -r ${GROUP} && \
    useradd -r -g ${GROUP} ${USER} -d /app && \
    mkdir -p /app && \
    chown -R ${USER}:${GROUP} /app

COPY . /app

RUN yum install -y bzip2
RUN npm --loglevel warn install --only=dev
RUN ./node_modules/.bin/gulp
RUN chmod a+x /app/run.sh

USER pttg

EXPOSE 8000

ENTRYPOINT /app/run.sh
