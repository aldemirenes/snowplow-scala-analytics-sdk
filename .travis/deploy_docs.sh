#!/bin/bash

git config --global user.name "$USER"
git config --global user.email "$TRAVIS_BUILD_NUMBER@$TRAVIS_COMMIT"

openssl aes-256-cbc -K $encrypted_1ec7ed6de651_key -iv $encrypted_1ec7ed6de651_iv -in project/travis-deploy-key.enc -out project/travis-deploy-key -d
chmod 600 project/travis-deploy-key

eval "$(ssh-agent -s)"
ssh-add project/travis-deploy-key

sbt ghpagesPushSite