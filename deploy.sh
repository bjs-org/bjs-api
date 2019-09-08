#!/usr/bin/env bash

setup_git() {
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "Travis CI"
}
upload_files() {
  git checkout master
  git remote add balena $BALENA_REMOTE
  git push balena master --force
}

setup_git
upload_files