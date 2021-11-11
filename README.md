# FLOWX Quickstart for Connectors

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/ec3388834bc74032ac69bdf8adf050f3)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=flowx-ai/quickstart-connector&amp;utm_campaign=Badge_Grade)
[![Java version](https://img.shields.io/badge/java%20version-11-yellow.svg?color=yellow)](https://adoptopenjdk.net/index.html?variant=openjdk11&jvmVariant=hotspot)
[![String boot version](https://img.shields.io/badge/spring%20boot%20version-2.5.4-9cf.svg?color=9cf)](https://spring.io/projects/spring-boot)
[![License: MIT](https://img.shields.io/badge/license-MIT-orange.svg)](https://opensource.org/licenses/MIT)

Basic setup for a connector.

Includes:
  - Kafka related configs and listener & message sender examples
  - Jaeger related configs & example

[Built with](#built-with) | [Usage](#usage) | [Contributing](#contributing) | [License](#license)

## Built with

Java 11 & Spring Boot 2.5.4

## Usage

Prerequisites:
  - [Kafka](https://kafka.apache.org/) engine
  - [Jaeger](https://www.jaegertracing.io) agent

A docker-compose file is provided for starting these dependencies locally.

## Contributing

### Branching model

- `master` branch should always have the latest code that is in production.
- `feature/issue_code` branches that fix specific github issues
- `feature/feature_name` branches for new features
- `bugfix/issue_code` branches that fix specific bugs

### Code quality

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/ec3388834bc74032ac69bdf8adf050f3)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=flowx-ai/quickstart-connector&amp;utm_campaign=Badge_Grade)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

