ARG BASEIMG
ARG IMG_VERSION
FROM $BASEIMG:$IMG_VERSION as build
ENV DANGEROUSLY_DISABLE_HOST_CHECK=true
WORKDIR /usr/src/app
COPY package.json /usr/src/app
# COPY package-lock.json /usr/src/app
RUN npm set registry=https://registry.npmjs.org/
RUN npm config set registry http://registry.npmjs.org/
RUN npm config rm proxy
RUN npm config rm https-proxy
RUN yarn
COPY . /usr/src/app
EXPOSE 3000
CMD ["yarn", "start"]