# FLOWX Easy connector

1. git clone -b feature/easy-connector https://github.com/flowx-ai/quickstart-connector.git
2. adjust `TODOs` in the following YML files: `src/main/resources/application.yml` and `src/main/resources/application-kafka.yml`
3. run in terminal: ```echo '127.0.0.1 kafka' | sudo tee -a /etc/hosts```
4. cd `/path/to/quickstart-connector/docker` and run `docker-compose up -d`
5. run current project in IDE
7. navigate in browser to `http://localhost:8088`
8. add the topics from your `application-kafka.yml` file to the `Kafka Topics` section
9. `produce` new kafka on topic `ai.flowx.easy-connector.in` with the following payload:
```json
{
  "Id": "1"
}
```
10. check the last message from the `ai.flowx.easy-connector.out` topic
11. add you business logic to `MessageHandlerServiceImpl` `process` method 