FROM node:12.18.1-buster

RUN mkdir server && cd server && npm install json-server --save-dev;
ADD server.js server/
WORKDIR server

CMD ["node", "server.js"]