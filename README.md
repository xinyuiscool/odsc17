# This folder contains slides and demo code for ODSC 2017.

## Instructions for the tutorial:

### 1. Download code
```
git clone https://git.apache.org/samza-hello-samza.git hello-samza
cd hello-samza
git checkout latest
```

### 2. Start the Samza grid
```
./bin/grid bootstrap
```

### 3. Deploy
```
./bin/deploy.sh
```

### 4. Set up input
```
./deploy/kafka/bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic pageview-input --partitions 1 --replication-factor 1
```

### 5. Run the app
```
./deploy/samza/bin/run-app.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/my-samza-app.properties
```

### 6. Start the output consumer
```
./deploy/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic my-output --property print.key=true
```

### 7. Publish test data
```
./deploy/kafka/bin/kafka-console-producer.sh --topic pageview-input --broker-list localhost:9092​
{"userId": "jack", "country": "us", "pageId":"google.com"}
{"userId": "mary", "country": "sg", "pageId":"linkedin.com"}
```
