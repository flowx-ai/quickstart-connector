# Setting Up FlowX AI Quickstart Connector

1. Clone the repository:
```bash 
Git clone -b feature/easy-start https://github.com/flowx-ai/quickstart-connector.git.
```
2. Adjust `TODOs` in the following YML files: `src/main/resources/config/application.yml` and `src/main/resources/application-kafka.yml`.
3. Run in terminal: ```echo '127.0.0.1 kafka' | sudo tee -a /etc/hosts```.
4. Navigate to the docker folder → `cd /path/to/quickstart-connector/docker` and run `docker-compose up -d`.
5. Open your preferred IDE and launch the current project.
7. Navigate in browser to `http://localhost:8088`.
8. Add the topics from your `application-kafka.yml` file to the `Kafka Topics` section.
9. Produce a new Kafka message on the `ai.flowx.easy-connector.in` topic with the following payload:

```json
{
  "Id": "1"
}
```
10. Check the last message from the `ai.flowx.easy.connector.out` topic.
11. Add your message processing logic to `MessageHandlerServiceImpl` `process` method.