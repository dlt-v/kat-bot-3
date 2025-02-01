### KatBot 3.0 Deployment Guide

*(In case I forget :))*

### Local Build and Docker Image Creation

1. **Build the JAR file**  
   First, ensure that the application is packaged as a JAR file using Maven:

   ```bash
   mvn clean package
   ```

2. **Build the Docker Image**  
   Once the JAR is ready, build the Docker image using the following command:

   ```bash
   docker build -t kat-bot:1.0 .
   ```

3. **Export the Docker Image as a `.tar` file**  
   To export the Docker image to a `.tar` file for deployment, run:

   ```bash
   docker save -o kat-bot-1.0.tar kat-bot:1.0
   ```

   This will create a `kat-bot-1.0.tar` file that can be transferred to the remote server.

### Remote Deployment

The application is deployed on a VPS as a Docker container using the exported `.tar` file.

1. **Transfer the Tarball**  
   Use an SCP client like MobaXterm, WinSCP, or a GUI to transfer the `kat-bot-1.0.tar` file to your VPS.

2. **Load the Docker Image**  
   Once the tarball is on the VPS, log in to the server via SSH and run the following command to load the Docker image:

   ```bash
   docker load -i kat-bot-1.0.tar
   ```

3. **Run the Docker Container**  
   After loading the image, you can run the container. Make sure you have an `.env` file with the required environment variables in the same directory. Use the following command to start the container:

   ```bash
   docker run --env-file .env --restart=always -d -p 8080:8080 kat-bot:1.0
   ```

    - `--env-file .env`: Loads environment variables from the `.env` file.
    - `--restart=always`: Ensures the container restarts automatically if it stops.
    - `-d`: Runs the container in detached mode (in the background).
    - `-p 8080:8080`: Maps port 8080 of the container to port 8080 of the host.

And boom! You're done!

---

### Additional Notes

- Ensure that Docker is installed and running on both your local machine and the VPS.
- Adjust any port mappings or environment variables as needed.
- The `docker run` command can be modified to fit specific deployment needs, such as exposing different ports or passing additional parameters.

### Command flow

KatBot listens to all messages in all allowed channels. Right now there are **two** ways to interact with KatBot:
1. via guild messages _(GuildMessageListener)_
2. via button interactions _(ButtonInteractionListener)_

Message is then passed to the **Message Handlers** which are responsible for handling the message and executing the appropriate action. Right now there are **four** message handlers:
1. **TwitterLinkHandler** - Handles Twitter links and remakes them into embeds.
2. **TimeHandler** - Detects if message contains time and proposes to the author to convert it to a timezone.
3. **CommandHandler** - Handles commands that start with `kat` and executes the appropriate action.
4. ~~**BroadcastChannelHandler**~~ (deprecated) - Handles messages that are sent to the broadcast channel and forwards them to the appropriate channel.

```mermaid
graph TD;
   id1[[Event Listeners]] ---> id2[[Message Handlers]]
   id2[[Message Handlers]] -.-> TwitterLinkHandler
   id2[[Message Handlers]] -.-> TimeHandler
   id2[[Message Handlers]] -.-> CommandHandler
   id2[[Message Handlers]] -.-> BroadcastChannelHandler

```
