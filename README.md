# AWS-Three-Tier-Architecture

Created an Three-Tier Architecture that includes a web server with Nginx, an application server with a Java service on Tomcat, and a database server with PostgreSQL using AWS cloud formation.

## Architecture at a Glance

![AWS Architecture](https://user-images.githubusercontent.com/45092750/213896830-88186fc9-ef77-4464-adca-800176372c0c.png)

The components of this architecture includes a web server with NginX, an application server with tomcat and a database server with PostgreSQL.

The web server is responsible for handling incoming HTTP requests from clients and to proxy the requests to the application server using reverse proxy. The application server receives requests from the web server, retrieves data from the database server and returns the  results to the web server to be returned to the client. The application server has a Java Servlet and is using Apache Tomcat. The database server is responsible for storing and organizing data for your application. In this case, the database server is using PostgreSQL.

The web server and application server are running on separate Amazon EC2 instances, which are virtual machines in the cloud that you can use to run applications. The database server is running on an Amazon RDS instance, which is a managed database service that makes it easier to set up, operate, and scale a database in the cloud.


## Networking

The architecture consists of a VPC as well, with a public and three private subnets. The EC2 instance with Nginx should be in a public subnet to serve as a web server (accessible from the internet). It will have a public IP. The EC2 instance with Tomcat server and RDS instance should be in a private subnet  to serve as an application server (in PrivateSubnet3) and database server (in PrivateSubnet1 and PrivateSubnet2) respectively. The instances then will not have a public IP and should not be directly accessible from the internet. 

There is a resource called DB subnet group (consists of  PrivateSubnet1 and PrivateSubnet2) attached to the RDS instance which is useful when you want to isolate your DB instances from other resources in your VPC, or when you want to increase the availability of your DB instances by placing them in multiple Availability Zones (The subnets in a DB subnet group should be in different Availability Zones but within your VPC).

It also consists  of  a NAT Gateway in the public subnet to allow the instances in the private subnet to access the Internet when necessary, an Internet Gateway attached to the VPC that allows communication between instances in your VPC and the Internet (the instances in public subnet access the internet via Internet Gateway) and a route table for the public subnet with a route that points to the Internet Gateway, allowing traffic to flow to and from the Internet.

Now the instances should each have a security group that acts as a virtual firewall for your instance to control inbound and outbound traffic.
a security group for the web server with rules to allow incoming traffic on port 80 (HTTP) and port 22 (SSH).
a security group for the application server with rules to allow traffic from the web server on port 8080, port 22 (SSH) and to allow traffic from the database server on the default PostgreSQL port (5432).
a security group for the database server with rules to allow traffic only from the application server on the default PostgreSQL port (5432).

## Configure Nginx

In the web server, the nginx package is installed in the userdata. We need to configure the web server to proxy requests to the application server. This can be done by modifying the Nginx configuration to include a reverse proxy directive.

<img width="402" alt="Screenshot 2023-01-22 065759" src="https://user-images.githubusercontent.com/45092750/213896778-4086ddab-d228-4ecc-a19b-ac23959080b3.png">

Once you have added the location block to the Nginx configuration file, you need to restart the server for the changes to take effect.  ‘sudo systemctl restart nginx‘

Lastly, I have provided a custom index.html file. Modify the /use/share/nginx/html/index.html file with that one.

<img width="809" alt="Screenshot_20230108_201941" src="https://user-images.githubusercontent.com/45092750/213896802-e1481623-6830-4291-8224-b6fd9b82029d.png">

## Create Java Servlet

In the application server, the userdata has the automation to create, build and deploy the Java Servlet. Test the connection using the ‘curl’ command. The userdata also has the context.xml defined which is a configuration file that is used to define settings (database configurations) for an application that is deployed in a Servlet container.  The Java servlet mainly functions to retrieve data from the RDS and send it to the web server to be returned to the client.

The userdata also installs PostgreSQl JDBC driver and uses ‘psql’ command to create and initialize the table.

## Improvements

We can add Application Load balancer and Amazon Autoscaling group to scale and improve the architecture. We should also use database scaling techniques.

<img width="900" alt="Screenshot_20230119_140238" src="https://user-images.githubusercontent.com/45092750/213896814-9d735c10-4849-4aba-a16b-ce8fb129ac80.png">

## Getting Started

1. Create a Cloud Formation Stack using the template 'infrastructure_setup.yaml'.
2. The parameters include:
    * Environment: Select Environment from the following values: [DEV, QA, PROD]
    * AMI: Use a Amazon Linux V2 AMI
    * KeyName: Use an existing EC2 KeyPair to enable SSH access to the instance.
    * DbUsername: Database username.
    * DbPassword: Database password.
3. Once the stack is created successfully, SSH into the WEB SERVER INSTANCE and configure the nginx server. Follow the steps below:
    * sudo systemctl status nginx
    * vi /etc/nginx/nginx.conf
    * update the server block with the contents of the nginx.conf file provided here. Make sure to update the public IPs of the instance correctly.
    * sudo systemctl restart nginx
    * Override the index.html in the location '/usr/share/nginx/html' with the index.html provide here.
    * SSH into the app server instance and test the connection using, curl -I {Public IP of Web Server}
    * Enter the public IP of the web server in a browser. The web page will appear.
    * Hit the button to send request to the tomcat server at port 8080.
    * The Java servlet in the tomcat server will return the data from database server.
