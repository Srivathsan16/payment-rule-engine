# Payment-rule-engine

This project uses Quarkus, the Supersonic Subatomic Java Framework.
For database to store rule we use DynamoDB.

Further this application is deployed on AWS Lambda.
In AWS we also use api gateway to expose the lambda function as REST API.


If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Setting up Local for DynamoDB
- Install Docker 
- cd payment-rule-engine/dynamoDB

```shell script
docker-compose up
```
### Create Table

```shell script 
aws dynamodb create-table --table-name PaymentRule --attribute-definitions AttributeName=ruleId,AttributeType=S --key-schema AttributeName=ruleId,KeyType=HASH --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 --profile local-dynamodb --endpoint-url http://localhost:8000 
```

### Insert Data

```shell script     
aws dynamodb put-item \
    --table-name PaymentRules \
    --item '{
        "RuleId": {"S": "1"},
        "Criteria": {"S": "country == \"NORWAY\" && amount > 10000"},
        "Action": {"S": "verificationRequired = true; approved = false;"}
    }' \
    --profile local-dynamodb \
    --endpoint-url http://localhost:8000

aws dynamodb put-item \
    --table-name PaymentRules \
    --item '{
        "RuleId": {"S": "2"},
        "Criteria": {"S": "country != \"SWEDEN\" && country != \"DENMARK\""},
        "Action": {"S": "approved = true;"}
    }' \
    --profile local-dynamodb \
    --endpoint-url http://localhost:8000

```


## Running the application in dev mode

Build : 

```shell script
./mvnw clean install package -DskipTests -Dquarkus.profile=dev

```
Invoke Lambda :

```shell script
sam local invoke PaymentRuleEngineFunction --event event.json
```
About AWS CloudFormation template
[Link to AWS CloudFormation template README](AWSreadme.md)

## Deployment
### Build for production deployment 

```shell script
./mvnw clean install package -DskipTests -Dquarkus.profile=prod
```
### AWS Console requirements
- Upload the function.zip file in the target folder to S3 bucket "**paymenttestsriengine**"
- Go to Lambda function and upload the new zip file 
- Publish new version in lambda 
- Deploy API gateway for new version of lambda

## Testing the API
- Use the API endpoint from API Gateway to test the API in postman .
- Attaching the postman collections ubder root folder "**Rule Engine.postman_collection.json**"


# Deployed in AWS Cloud 
- VPC, Subnets , Route Tables, NAT Gateway, DynamoDB, Lambda, API Gateway are created using CloudFormation template
- Lambda is VPC linked and has access to DynamoDB
- API Gateway is linked to Lambda function
- API Gateway is deployed to prod stage
- API Gateway URL : https://900wl5pim1.execute-api.eu-north-1.amazonaws.com/prod/rules
- Import the postman collection and test the API , postman collection is in root folder "**Rule Engine.postman_collection.json**"

# Further improvements
- Given the time constraint, the application has some areas of improvement.
- The application can be further improved by adding more test cases.
- The application can be further improved by adding more validation checks.
- The application can be further improved by adding more logging.
- The application can be further improved by adding more error handling and also custom error handling.
- The logic of the application can be further improved by adding more complex rules, which means nesting rules can be checked .
- There is an attempt on nesting rules but it is not fully implemented **"PaymentRuleEngineHandlerJava.java"**.



