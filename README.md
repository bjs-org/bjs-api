[![Build Status](https://github.com/aykborstelmann/bjs-api/workflows/Build%20and%20Deploy/badge.svg?branch=master)](https://github.com/aykborstelmann/bjs-api/actions?query=workflow%3A%22Build+and+Deploy%22)

# BJS-API
This repo has the goal to create a clean REST API for the [BJS](https://www.bundesjugendspiele.de/) with all needed things (user management, database, ...)

# Rules for development
- The API must be well documented in order to make development for other projects smoothly
- In order to prevent bugs and errors the application must rely on automated (unit) tests

# Getting started
## Development instructions
### Locally
In order to start developing and running the api locally (and not within docker-compose) you will need a
running (postgres) database. There are a few options to run a database, but it is recommended to use 
a postgres database with docker.

Therefore this project has a docker-compose file just for a database (`docker-compose.db.yml`).
To startup the database run 

`docker-compose -f docker-compose.db.yml up -d`

The api can be found on port `8080` of `localhost`.

### Docker
It is also possible to run the complete api in docker. 
To run the complete api run

`docker-compose -f docker-compose.dev.yml up`

The api can be found on port `8080` of `localhost`.

## Need help on used libraries? 
Look [here](HELP.md).
