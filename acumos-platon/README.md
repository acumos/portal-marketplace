# platon

[![Build Status](https://travis-ci.org/pvorb/platon.svg?branch=develop)](https://travis-ci.org/pvorb/platon) [![Code Coverage](https://codecov.io/gh/pvorb/platon/branch/develop/graph/badge.svg)](https://codecov.io/gh/pvorb/platon)

A comment service


## Development

### Build

~~~
./mvnw clean package
~~~

### Run

#### Production

~~~
./mvnw package
java -jar target/platon-${version}.jar
~~~

where you have to replace `${version}` with the current version in the file `pom.xml`.

#### Testing

~~~
./mvnw spring-boot:run
~~~

By default, the server will start under [localhost:8080](http://localhost:8080/).

### Test

~~~
./mvnw clean verify
~~~


## License

~~~
Copyright 2016-2017 Paul Vorbach

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
~~~
