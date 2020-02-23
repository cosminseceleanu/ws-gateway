FROM swaggerapi/swagger-ui:v3.25.0

ARG API_SPEC_PATH

COPY $API_SPEC_PATH /usr/share/nginx/html/api-spec.yml
COPY index.html /usr/share/nginx/html/index.html

ENV BASE_URL=/swagger

RUN sed -i "s|location / {|location $BASE_URL {|g" /etc/nginx/nginx.conf

CMD exec nginx -g 'daemon off;'