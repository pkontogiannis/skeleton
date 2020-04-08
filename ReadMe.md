# Skeleton

## Table of contents
* [General info](#general-info)
* [Development guide](#development-guide)
* [Prerequisites](#prerequisites)
* [Code formatting](#code-formatting)
* [Checking code coverage](#checking-code-coverage)
* [Packaging](#packaging)
* [Usage](#usage)
* [Testing](#testing)
* [Versioning](#versioning)
* [Authors](#authors)
* [Technologies](#technologies) 
* [Contributing](#contributing) 
* [Copyright](#copyright) 
* [License](#license) 

## General info

This project demonstrates the [Akka HTTP](http://doc.akka.io/docs/akka-http/current/scala/http/) library and Scala to write a simple REST service. The project shows the following tasks that are typical for most Akka HTTP-based projects:

* starting standalone HTTP server,
* handling file-based configuration,
* logging,
* routing,
* deconstructing requests,
* unmarshalling JSON entities to Scala's case classes,
* marshaling Scala's case classes to JSON responses,
* error handling,
* issuing requests to external services,
* testing with mocking of external services.
* authentication using JWT  
* documentation using Swagger Open API
* dockerized application 
* integration testing end to end   

The service in the template provides User related REST endpoints.


## Development guide
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites
What things you need to install the software and how to install them

* JDK 8 (e.g. http://www.oracle.com/technetwork/java/javase/downloads/index.html);
* sbt (http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html);

### Code formatting
There are [Scalafmt](https://scalameta.org/scalafmt/) integrated to the project. Its a opinionated code formatter that
formats a code automatically instead of you. To use it, please run `sbt scalafmt` before commit or enable format on save
in IntelijIdea (should be available in other editors too).

### Checking code coverage
To generate code coverage report, please run: `sbt clean coverage test coverageReport`.
Then you will have HTML pages with reports in `/target/scala-2.13/scoverage-report`

### Packaging
Application packaging implemented via [sbt-native-packager](https://github.com/sbt/sbt-native-packager) plugin.
Currently in `build.sbt` enabled two types: docker and universal.

**Universal packager**  
To package application as a universal app, use: `sbt universal:packageBin`.
Application zip archive will be generated in `/target/universal/` folder.

**Docker packager**   
To package application as docker image, use `sbt docker:publishLocal`.
It will generate and push application image into your local docker store.
For information about publishing to external store, please, read [plugin documentation](http://www.scala-sbt.org/sbt-native-packager/formats/docker.html).

### Usage

First you have to start the dependencies for the main application using Docker. 
```
docker-compose up
```
using this command you can confirm that the dependencies already started:

| Service name      | Service port  |
| ----------------- | ------------- |
| skeletondb        | 5435          |
| skeletontestdb    | 5436          |
| skeletonpgadmin   | 8005          |
| prometheus        | 9090          |
| cadvisor          | 8050          |
| alertmanager      | 9093          |
| grafana           | 3000          |
| node-exporter     | 9100          |
| elasticsearch     | 9200 & 9300   |
| logstash          | 5000 & 9600   |
| kibana            | 5601          |
| collector         | 14269         |
| agent             | 5778          |
| query             | 16686 & 16687 |
 
Start services with sbt:

```
sbt run 
```

With the service up, you can see the Open API documentation under [Swagger page](http://localhost:8080/api-docs/swagger.json).

### Testing

In order to run the Integration tests we have to `docker-compose up` the application in order to have 
the test database running and then we can run the integration tests using `test` command:

```
$ sbt
> it:test
```

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## Authors

* **Petros Kontogiannis** - *Sr. Software Engineer* - [P. Kontogiannis](https://github.com/pkontogiannis)
* **Emmanouil Palavras** - *Sr. Software Engineer* - [E. Palavras](https://github.com/epalavras)

See also the list of [contributors](https://github.com/pkontogiannis/skeleton/graphs/contributors) who participated in this project.

## Technologies
Project is created with:
* Scala, Akka HTTP, Slick, PostgreSQL, Circe, JWT, Scalafmt, Prometheus, Kibana, Elastic, Kamon, Swagger, Flyway 

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## Copyright
Copyright (C) 2020 [Petros I. Kontogiannis](https://github.com/pkontogiannis) and [Emmanouil Palavras](https://github.com/epalavras).  
Distributed under the MIT License.

## License
This project is licensed under the MIT License - see the [LICENSE.md](License.md) file for details
