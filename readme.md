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

