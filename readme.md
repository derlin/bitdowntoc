# BBData API

![Build & Tests](https://github.com/big-building-data/bbdata-api/workflows/Build/badge.svg)

This repository is the cornerstone of BBData. It contains: 

1. a SpringBoot Application exposing a REST API for submitting measures, administering objects, users, etc.
2. dockerfiles and docker-compose for local testing, including: MySQL, Cassandra, Kafka
3. the definition of the two databases at the center of BBData: MySQL & Cassandra

[TOC]

## Development setup

### Prerequisites

* Java 1.8+
* IntelliJ IDE with Kotlin support
* Docker

### Setup

Open the project in IntelliJ and let it work. Once finished, you should be able to simply run the app by 
launching the main class `ch.derlin.bbdata.BBDataApplication` (open it and right-click > run).

Of course, you will need MySQL, Cassandra and Kafka running for the whole API to run (to skip some of those deps, 
the the Profiles section).

### Cassandra, MySQL and Kafka

To setup the three dependant services, have a look at the `other` folder.
It contains all the files needed for a production setup, as well as instruction on how to run a Docker container
for local dev/testing.

### Profiles

By default, the app will launch with everything turned on, and will try to connect to MySQL, Cassandra and Kafka on localhost
on default ports (see `src/main/resources/application.properties`).

Profiles let you disable some parts of the application. This is very useful for quick testing.
To enable specific profiles, use the `-Dspring.profiles.active=XX[,YY]` JVM argument.
On IntelliJ: _Edit Configurations ... > VM Options_.


Currently available profiles (see the class `ch.derlin.bbdata.api.Profiles`):

* `unsecured`: all endpoints will be available without apikeys; the userId is automatically set to `1`;
* `input`: will only register the "input" endpoint (`POST /objects/values`);
* `output`: will only register the "output" endpoints (everything BUT the one above);
* `noc`: short for "_No Cassandra_". It won't register endpoints needing a Cassandra connection (input and values);
* `sqlstats`: use MySQL to store objects statistics, instead of Cassandra **BEWARE** this is waaaay slower !!

Profiles can be combined (when it makes sense).

__Examples__:

Output only:
```bash
java -jar bbdata.jar -Dspring.profiles.active=output
```

Output only, no security checks:
```bash
java -jar bbdata.jar -Dspring.profiles.active=output,unsecured
```

Output only, no security checks and no cassandra
(note that the output profile is not needed, as no Cassandra means no input):
```bash
java -jar bbdata.jar -Dspring.profiles.active=noc,unsecured
```

### Hidden system variables

There are two system variables that can be set (`export XX=YY`), mostly for use in tests:

* `BB_NO_KAFKA=<bool>`: this turns off the publication of augmented values to kafka in the input endpoint. Useful when we don't want to spawn a Kafka container.
* `UNSECURED_BBUSER=<int>`: this determines which `userId` is used as default when the `UNSECURED` profile is turned off. Default to 1.

In test files, those variables can be set using the `@SpringBootTest` annotation, e.g.:
```
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = ["UNSECURED_BBUSER=2"])
```

## Production

To deploy in production, you need to build the jar and override some properties in order to connect to the correct services.

1. to build the jar: `./gradlew bootJar`, the jar will be created as `./build/libs/*.jar`
2. to specify properties from a file outside the jar: https://www.baeldung.com/spring-properties-file-outside-jar

### Minimal properties to provide

Here is a sample file, values to change are in UPPERCASE:
```properties
## MySQL properties
spring.datasource.url = jdbc:mysql://HOST:PORT/bbdata2?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=bbdata-admin
spring.datasource.password=PASSWORD

## Cassandra properties
spring.data.cassandra.contact-points=IP_1,IP_2,IP_X
spring.data.cassandra.consistency-level=quorum

## Kafka properties
spring.kafka.producer.bootstrap-servers=HOST:PORT
spring.kafka.template.default-topic=bbdata2-augmented

## Secured actuators endpoints (ensure the port is not available to the outside world)
management.server.port=SECURED-PORT
```

### Executing the jar

The jar is fully executable, meaning you can do:

```bash
# default
./bbdata-api-*.jar

# with profiles: no "-D", but "--" instead !
./bbdata-api-*.jar --spring.profiles.active=unsecured,noc
```

Or, you can use the old school way:
```bash
# default
java -jar bbdata-api-*.jar

# with profiles: no -D !
java -Dspring.profiles.active=unsecured,noc -jar bbdata-api-*.jar
```

If you want to use a properties file that do not match the default names (e.g. `development.properties`), 
set the `spring.config.additional-location` (so it is loaded *alongside* and not *instead of* the default properties):
```bash
./bbdata-api-*.jar --spring.config.additional-location=development.properties
```

### Caching

Caching is interesting in one part of the application, namely the input endpoint `POST /values`. 
The application needs to fetch the metadata associated to an object (and a token) on each request. 
To speed it up, the application can be configured to cache those metadata using either an in-memory concurrent hashmap,
or an external database such as redis.

**To enable/disable caching**, set the `spring.cache.type` property. The former can take three values:

* `none`: value by default, no caching at all,
* `simple`: in-memory caching, only possible when the application runs in standalone mode, with both input and output enabled,
* `redis`: use a redis instance for caching; this is mandatory if you want to deploy input and output separately and still use a cache.

Example:
```
./bbdata-api-*.jar --spring.cache.type=simple
```

Redis default properties are shown below (can be overriden from a property file). When using `spring.cache.type=redis`,
you can override any of those properties to point to your redis server instance:

```properties
# Relevant properties in order to use redis (localhost and 6379 are the default)
spring.cache.type=redis
spring.redis.host=localhost
spring.redis.port=6379
```

By default, the cache logger is set to `INFO`. Feel free to change it using:
```properties
# default to INFO
logging.level.org.springframework.cache=TRACE
```

When caching is enabled, you can use the `DELETE /caches` actuator endpoint to clear all cache entries.
It is enabled by default (`management.endpoints.web.exposure.include=caches, ...`).


**IMPORTANT**: in case you deploy the input api and the output api separately (using profiles), 
YOU NEED TO USE AN EXTERNAL CACHE (i.e. redis). This is because the output API is responsible for evicting old entries
from the cache. If the input and the output do not run in the same JVM, the cache strategy `simple` is *dangerous*:
the input api won't see if a token gets deleted, or if an object becomes disabled.

Current caching strategy:

* one cache is used with the name `metas`,
* metadata needed by the input api (unit, object owner, object state, object type, etc) get cached with a key `objectId:token`,
* the cache is not updated when an object name or description changes (not really used by any downstream steps),
* a single cache entry gets evicted when a token is deleted,
* the entire cache is flushed when an object changes state (enabled/disabled), this is because we use compound keys,
  and have no way of using wildcard (e.g. `objectId:*`) in `@CacheEvict`. However, this operation is usually rare. 

### Async

To speed up the input endpoints, object statistics are updated asynchronously.
Under the hood, a `TheadPoolTaskExecutor` is configured through the `spring.task.execution.*` properties (see `application.properties` 
in this repo for default values). You can of course override any of those in your properties file.

If you want to **TURN OFF** asynchronous processing, simply set the custom property `async.enabled=false`.

### Logging

*NOTE*: in the default application.properties, I set the following, wich removes the spring banner, forces the
use of ansi colors, so the output is highlighted even when using `tee` and always log resolved exceptions
 (feel free to change them if you need to):
```properties
spring.main.banner-mode=off
spring.output.ansi.enabled=ALWAYS
spring.mvc.log-resolved-exception=true
```

By default, Spring Boot uses logback. Here are some relevant links and resources:

* [Spring Boot: How-to logging](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto-logging)
* [Spring Boot: logback src files](https://github.com/spring-projects/spring-boot/blob/master/spring-boot-project/spring-boot/src/main/resources/org/springframework/boot/logging/logback)
* [Logback layouts](http://logback.qos.ch/manual/layouts.html)
* [Logback syslogAppender](http://logback.qos.ch/manual/appenders.html#SyslogAppender)

Typically, in production you want to save everything to a rolling file, and maybe use syslog. You also want to be
able to change the logging configuration without restarting the app. Here is an example on how to achieve that:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- FILE: logback-spring.xml -->
<!-- scan and scanPeriod define if and how often this configuration should be scanned for change at runtime -->
<configuration scan="true" scanPeriod="1 minutes">

    <!-- override default springboot properties -->
    <!-- To see which are available, scan throught the files at
         https://github.com/spring-projects/spring-boot/tree/master/spring-boot-project/spring-boot/src/main/resources/org/springframework/boot/logging/logback -->
    <property name="LOG_FILE" value="./logs/bbdata-api.log" />
    <property name="LOG_EXCEPTION_CONVERSION_WORD" value="%rEx{5}" />

    <!-- use Spring default values, making CONSOLE and FILE readily available -->
    <include resource="org/springframework/boot/logging/logback/base.xml" />

    <!-- Add syslog logging TODO: ensure you change the host and port ! -->
    <appender name="SYSLOG" class="ch.qos.logback.classic.net.SyslogAppender">
        <syslogHost>127.0.0.1</syslogHost>
        <facility>SYSLOG</facility>
        <port>514</port>
        <throwableExcluded>false</throwableExcluded>
        <!--<suffixPattern>bbdata %m thread:%t priority:%p category:%c exception:%exception</suffixPattern>-->
        <suffixPattern>bbdata: %p [%logger, %t] %m%n${LOG_EXCEPTION_CONVERSION_WORD}</suffixPattern>
    </appender>

    <!-- LOG everything at INFO level -->
    <root level="info">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
        <appender-ref ref="SYSLOG" />
    </root>

    <!-- LOG app at TRACE level -->
    <logger name="ch.derlin.bbdata" level="trace" additivity="false">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
        <appender-ref ref="SYSLOG" />
    </logger>

</configuration>
```

In order to load this configuration at runtime, use the option `logging.config`, for example:
```bash
# either using java
java -jar -Dlogging.config=logback-spring.xml
# or executing the jar directly
./bbdata-api-*.jar --logging.config=logback-spring.xml
```

**rsyslog configuration**

When using **SYSLOG**, you need a server with `rsyslog` running. 
To allow incoming udp requests, define the following in `/etc/rsyslog.conf`:
```
$ModLoad imudp
$UDPServerRun 514
```

To filter everything from bbdata create a file `/etc/rsyslog.conf/10-bbdata.conf` and add the following:
```
if $programname == 'bbdata' then /var/log/bbdata.log
& stop
```
The rule will match any log beginning with `bbdata:` (the default *TAG* defined in SYSLOG, see `<suffixPattern>TAG: ...`). 

It is also possible to put the rule directly in `rsyslog.conf` _at the beginning_ of the rules. 
If you put it at the end, everything will be logged in `messages` as well.

To deal with tabs, new lines and special characters, also define the following property in `/etc/rsyslog.conf`: 
```
$EscapeControlCharactersOnReceive off
```

## Permission system

‼️ **tldr; IMPORTANT** Ensure that the `userGroup` with ID 1 has a meaningful name in the database (e.g. "admin") and that
you only add the platform managers to it !

The permission system in BBData uses mappings between userGroups and objectGroups.

A user can be part of one or more userGroups, either as regular user or as admin. 
Objects can be part of one or more objectGroups. 
ObjectGroups have a list of allowed userGroups (defining read permissions to objects that belong to it), 
and a single owner, which is the userGroup that created it. 
Only admin users from the owning userGroup can manage objects and permissions of an objectGroup.
Regular users can access them in read mode.
This is the same for objects: admins of the owning group has write access (e.g. editing metadata), 
regular users only read access (e.g. getting values).

There is one special case: the userGroup with `userGroupId=1` is the ⚜️ *SUPERADMIN* ⚜️.
This is the equivalent of `SUDO`: any admin of this group has read/write access to ANY resource, in read and write mode. 

(see the documentation for more info)

## Actuators and metrics

See [Spring Boot Actuator: Production-ready Features](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html).

Actuators are a way to monitor the API. They can leak sensitive information, so the management interface should
run on another port as the API, which only administrators have access to.

By default, the actuators will run on the same port (easier for debug, as they will show in openapi), but this is
definitely unsecure in production. Hence, ensure you select another port for the management interface by setting either:
```properties
# 8111 should be protected by firewall or not exposed to the outside
management.server.port=8111
```
or by disabling unsecure actuators, e.g.:
```properties
management.endpoints.web.exposure.include=info
```

Note that if you want to use one or more monitoring systems (see [Monitoring](#monitoring)), you will need to expose
all metrics.

### Customizing the `/about` (`/info`) endpoint

The actuator endpoint `/info` is mirrored in the public `/about` endpoint.
What is actually displayed can be customized using properties.

By default, SpringBoot actuator will add the to the JSON response any property with the prefix `info`.
For example, to configure the `instance-name`, define the following in your `application.properties`:
```properties
info.instance-name=My Super Instance
```

Another really cool feature is the possibility to reference *other properties files*. For example, let's say
you want to show the current cache type in use, which is defined somewhere (in this or another properties file)
using `spring.cache.type=XX`. To show this value in `/info`, simply add a property with the prefix `dynamic.info` 
(nested properties, with dots, are not allowed):
```properties
dynamic.info.cache-type=spring.cache-type
```
The JSON key `cache-type` will then appear in the `/info` endpoint. 
The pattern is thus:
```properties
dynamic.info.<JSON-KEY-NAME-WITHOUT-DOTS>=<ANOTHER PROPERTY>
```

To hide a dynamic property that is displayed, simply redefine it with an empty value, e.g. `dynamic.info.cache-type=`.

### Task executor actuator

By default, and if `sync.enabled=true`, there is a custom `/tasks` actuator that returns statistics about the task executor
(core size, active threads) and the tasks. To disable this endpoint, change exposed actuators (`id=tasks`).

### Custom metrics

Custom metrics are:

* Task Executor:
    - `async.executor` 
    - `async.executor.active` 
    - `async.executor.completed` 
    - `async.executor.idle` 
    - `async.executor.pool.core` 
    - `async.executor.pool.max` 
    - `async.executor.pool.size` 
    - `async.executor.queue.remaining` 
    - `async.executor.queued`

* Input:
    - `ch.derlin.input.rejected`
    - `ch.derlin.login.failed`
    - `ch.derlin.auth.failed`


**IMPORTANT**: as long as there is no value, the metric won't appear / doesn't exist.
This means `/metrics/ch.derlin.login.failed` will return `404` as long as no failed login occurred. 
Likewise, `metrics/async_*` won't appear if `sync.enabled=false` and/or no value has ever been submitted.

### Changing exposed actuators

[Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html#production-ready)

By default, the exposed actuators are defined in `application.properties`, using the key
`management.endpoints.web.exposure.include=`. Feel free to change this list as you see fit, or enable all using `*`.

To **turn off** all endpoints, simply add:
```properties
management.server.port=
management.endpoints.web.exposure.include=none
```

## Monitoring

There are two (complementary?) supported monitoring systems: 

* [Spring Boot Admin](https://codecentric.github.io/spring-boot-admin/2.3.0/), 
  which lets you interact with the actuators from a UI (clear the cache, change logging levels, etc.);
* [Prometheus](https://prometheus.io/) + [Grafana](https://grafana.com/),
  which lets you graph and monitor different metrics.

### Spring Boot Admin

Spring Boot Admin needs a server running, as well as a client embedded into the API.

For a basic server implementation, see https://github.com/big-building-data/spring-boot-admin
 ([nightly release](https://github.com/big-building-data/spring-boot-admin/releases/tag/nightly)).

The client is already included in the bbdata-api jar. What you need to do:

1. run a Spring Boot Admin server,
2. set the properties below in your application.properties

```properties
## Spring Boot Admin

# expose every actuator available, but ensure it runs on another (secured) port !
management.server.port=8111
management.endpoints.web.exposure.include=*

# enable spring boot admin client, and provide the server's URL
spring.boot.admin.client.enabled=true
spring.boot.admin.client.url=<URL OF THE ADMIN SERVER>
spring.boot.admin.client.instance.name=BBData test Instance
```

### Prometheus + Grafana

The BBData API ships with `io.micrometer:micrometer-registry-prometheus`. 
If the property `management.endpoint.prometheus.enabled=true` and `prometheus` is exposed (see `management.endpoints.web.exposure.include`),
you can see every available metric at http://localhost:8111/prometheus.

See the [other/monitoring's README](https://github.com/big-building-data/bbdata-api/tree/master/other/monitoring) for more info
on how to setup Prometheus and Graffana.