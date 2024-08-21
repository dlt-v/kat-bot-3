### KatBot 3.0 Deployment Guide
in case I forget :) )

### Remote Deployment

The application is deployed on a VPS as a Docker image straight from a tarball.

To make a tarball of the application, run the following command:
`mvn clean package`
And you're good to go!.

Then you log in to the console - preferably like mobaXterm.
Transfer the tarball to the VPS using GUI for example.
Then you can run the following commands to deploy the application:
```
docker load -i katbot.tar
docker run --env-file .env --restart=always -d -p 8080:8080 kat-bot-3:latest
```
And boom you're done!

