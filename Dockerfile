FROM openjdk:12-alpine

RUN apk add unzip
RUN apk add bash

EXPOSE 8802
EXPOSE 9902
EXPOSE 9000

ADD ./target/universal/api-0.1.zip /

RUN unzip api-0.1.zip
RUN mv api-0.1 api

CMD ["api/bin/api", "-Dplay.http.secret.key=lkdnfosdnfoisdnfisdjfsodijfsoidfhsdfsdf4sd98f4sd89f4sd98f4"]
