# AWS CloudFormation Template for VPC, Lambda, API Gateway, and DynamoDB

This CloudFormation template sets up the following resources:

1. A VPC with public and private subnets.
2. An Internet Gateway and a NAT Gateway.
3. A DynamoDB table to store payment rules.
4. An IAM role for Lambda execution.
5. A Lambda function to process payment rules.
6. An API Gateway to expose the Lambda function as a REST API.

## Resources

### VPC
- **MyVPC**: Creates a Virtual Private Cloud (VPC) with a CIDR block of 10.0.0.0/16.

### Internet Gateway
- **InternetGateway**: Creates an Internet Gateway.
- **AttachGateway**: Attaches the Internet Gateway to the VPC.

### Subnets
- **PublicSubnet**: Creates a public subnet in the VPC with a CIDR block of 10.0.1.0/24.
- **PrivateSubnet**: Creates a private subnet in the VPC with a CIDR block of 10.0.2.0/24.

### Route Tables
- **PublicRouteTable**: Creates a route table for the public subnet.
- **PrivateRouteTable**: Creates a route table for the private subnet.

### Routes
- **PublicRoute**: Creates a route to the Internet Gateway in the public route table.
- **PublicSubnetRouteTableAssociation**: Associates the public subnet with the public route table.
- **PrivateSubnetRouteTableAssociation**: Associates the private subnet with the private route table.
- **PrivateRoute**: Creates a route from the private subnet to the NAT Gateway.

### NAT Gateway
- **NatGatewayEIP**: Allocates an Elastic IP address for the NAT Gateway.
- **NatGateway**: Creates a NAT Gateway in the public subnet.

### DynamoDB
- **PaymentRulesTable**: Creates a DynamoDB table named `PaymentRules` to store payment rules with `RuleId` as the partition key.

### IAM Role
- **LambdaExecutionRole**: Creates an IAM role for the Lambda function with permissions to interact with DynamoDB and CloudWatch Logs.

### Lambda Function
- **PaymentRuleEngineFunction**: Creates a Lambda function named `PaymentRuleEngineFunction` to process payment rules.

### API Gateway
- **ApiGatewayRestApi**: Creates an API Gateway REST API named `PaymentRulesApi`.
- **ApiGatewayResource**: Creates an API Gateway resource `/rules`.
- **ApiGatewayMethod**: Creates an API Gateway POST method for the `/rules` resource.
- **LambdaApiGatewayPermission**: Grants API Gateway permission to invoke the Lambda function.

## Outputs

- **VPCId**: The ID of the VPC.
- **PublicSubnetId**: The ID of the public subnet.
- **PrivateSubnetId**: The ID of the private subnet.
- **PaymentRulesTableName**: The name of the DynamoDB table.
- **ApiEndpoint**: The endpoint URL of the API Gateway for the `prod` stage.

## Usage

To deploy this CloudFormation stack, follow these steps:

#### Cloud formation is also deployed using AWS console in aws account which is being used sometime for convenience


1. Save the CloudFormation template to a file, e.g., `CloudFormation.yaml.`.
2. Use the AWS CLI to deploy the stack:

    ```sh
    aws cloudformation create-stack --stack-name PaymentRuleEngineStack --template-body file://CloudFormation.yaml --capabilities CAPABILITY_IAM
    ```

3. Wait for the stack to be created. You can check the status using:

    ```sh
    aws cloudformation describe-stacks --stack-name PaymentRuleEngineStack
    ```

4. Once the stack is created, note the outputs for `VPCId`, `PublicSubnetId`, `PrivateSubnetId`, `PaymentRulesTableName`, and `ApiEndpoint`.
