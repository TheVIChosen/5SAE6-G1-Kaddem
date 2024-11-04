pipeline {
    agent any

    tools {
        // Make sure Maven is installed in Jenkins and referenced here nnnn
        maven 'M2_HOME' // Adjust the Maven installation to your Jenkins setup
    }
    
    environment {
        // MySQL environment variables
        MYSQL_CONTAINER_NAME = "mysql-container"
        MYSQL_IMAGE = "mysql:5.7"
        MYSQL_ROOT_PASSWORD = "0000"
        MYSQL_DATABASE = "kaddemdb"
        MYSQL_PORT = "3306"
        // Use 'nexus-username' as the ID for both username and password
        NEXUS_CREDENTIALS = credentials('nexus-username') 
        // Docker
        DOCKER_CREDENTIALS = credentials('docker-credentials')
        DOCKER_IMAGE_NAME = 'naderouali/kaddem:latest'
    }

    stages {
        stage("Start MySQL") {
            steps {
                script {
                    // Check if MySQL container with the specified name is running
                    def mysqlRunning = sh(script: "docker ps --filter 'name=${MYSQL_CONTAINER_NAME}' --filter 'status=running' -q", returnStdout: true).trim()
                    
                    // Check if a MySQL container exists (running or stopped)
                    def mysqlExists = sh(script: "docker ps -a --filter 'name=${MYSQL_CONTAINER_NAME}' -q", returnStdout: true).trim()
        
                    if (mysqlRunning) {
                        echo "MySQL container is already running"
                    } else if (mysqlExists) {
                        echo "MySQL container exists but is stopped. Starting it..."
                        sh "docker start ${MYSQL_CONTAINER_NAME}"
                    } else {
                        echo "MySQL container does not exist. Creating and starting a new one"
                        sh """
                        docker run --name ${MYSQL_CONTAINER_NAME} \
                            -e MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD} \
                            -e MYSQL_DATABASE=${MYSQL_DATABASE} \
                            -p ${MYSQL_PORT}:3306 \
                            -d ${MYSQL_IMAGE}
                        """
                    }
                    // Check MySQL logs for issues
                    echo "Checking MySQL logs..."
                    sh "docker logs ${MYSQL_CONTAINER_NAME} || echo 'No logs available'"
                }
            }
        }
        //stage("Start SonarQube and Nexus") {
            //steps {
                //script {
                    //// Start existing SonarQube and Nexus containers without creating new ones
                    //sh 'docker start sonarqube'
                    //sh 'docker start nexus'
                    //// Wait a few seconds to ensure the services are fully started
                    //sleep 30
                //}
            //}
        //}
        stage("Start SonarQube and Nexus") {
            steps {
                script {
                    // Start existing SonarQube and Nexus containers if they are not already running
                    sh 'docker start sonarqube || echo "SonarQube already running"'
                    sh 'docker start nexus || echo "Nexus already running"'
                    
                    // Wait for SonarQube to be fully initialized
                    timeout(time: 2, unit: 'MINUTES') {
                        waitUntil {
                            def sonarqubeReady = sh(script: "curl -s -o /dev/null -w '%{http_code}' http://localhost:9000", returnStdout: true).trim() == '200'
                            def nexusRunning = sh(script: "docker inspect -f '{{.State.Running}}' nexus", returnStdout: true).trim() == 'true'
                            return sonarqubeReady && nexusRunning
                        }
                    }
                }
            }
        }
        //stage("Start Grafana and Prometheus") {
            //steps {
                //script {
                    //// Start only Grafana and Prometheus services with network_mode set to host
                    //sh 'docker compose -f ./docker-compose.yml up -d grafana prometheus'
                //}
            //}
        //}
        //stage("Start Docker Compose Services") {
            //steps {
                //script {
                    //// Start all necessary Docker Compose services
                    //sh 'docker compose -f ./docker-compose.yml up -d'
                //}
            //}
        //}
        stage("Parallel: Start Grafana and Prometheus & Docker Compose Services") {
            parallel {
                stage("Start Grafana and Prometheus") {
                    steps {
                        script {
                            // Start Grafana and Prometheus services
                            sh 'docker compose -f ./docker-compose.yml up -d grafana prometheus'
                        }
                    }
                }
                stage("Start Docker Compose Services") {
                    steps {
                        script {
                            // Start all necessary Docker Compose services
                            sh 'docker compose -f ./docker-compose.yml up -d'
                        }
                    }
                }
            }
        }
        stage("Build/Clean with Maven") {
            steps {
                // Runs Maven clean, compile, test, and package commands
                sh "mvn clean verify"
                // List contents of target directory
                sh "ls -la target/"
            }
        }
        stage("Run Tests with JUnit") {
            steps {
                // Runs JUnit tests and generates JaCoCo coverage reports
                sh "mvn test jacoco:report"
            }
            post {
                // Publish JUnit test results in Jenkins
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        stage("SonarQube Analysis") {
            steps {
                script {
                    // Use the SonarQube environment defined in Jenkins
                    withSonarQubeEnv('SonarQube') { // 'SonarQube' should match your configured SonarQube Server name
                        sh "mvn sonar:sonar"
                    }
                }
            }
        }
        stage("Deploy to Nexus") {
            steps {
                script {
                    // Use the credentials stored in 'NEXUS_CREDENTIALS' for deployment
                    sh "mvn deploy -DskipTests -Dnexus.username=${NEXUS_CREDENTIALS_USR} -Dnexus.password=${NEXUS_CREDENTIALS_PSW}"
                }
            }
        }
        //stage("Build Docker Image") {
            //steps {
                //script {
                    //// Build the Docker image
                    //sh "docker build -t ${DOCKER_IMAGE_NAME} ."
                //}
            //}
        //}
        //stage("Push Docker Image to Docker Hub") {
            //steps {
                //script {
                    //try {
                        //// Ensure the Docker image exists before pushing
                        //def imageExists = sh(script: "docker images -q ${DOCKER_IMAGE_NAME}", returnStdout: true).trim()
        
                        //if (!imageExists) {
                            //error "Docker image ${DOCKER_IMAGE_NAME} does not exist. Skipping push."
                        //}
        
                        //// Log in to Docker Hub
                        //sh "echo ${DOCKER_CREDENTIALS_PSW} | docker login -u ${DOCKER_CREDENTIALS_USR} --password-stdin"
        
                        //// Push the Docker image
                        //sh "docker push ${DOCKER_IMAGE_NAME}"
                        //echo "Successfully pushed Docker image '${DOCKER_IMAGE_NAME}' to Docker Hub."
                    //} catch (Exception e) {
                        //echo "Failed to push Docker image: ${e.getMessage()}"
                        //currentBuild.result = 'FAILURE' // Mark build as failed if push fails
                    //}
                //}
            //}
        //}
        stage("Parallel: Build and Push Docker Image") {
            parallel {
                stage("Build Docker Image") {
                    steps {
                        script {
                            // Build the Docker image
                            sh "docker build -t ${DOCKER_IMAGE_NAME} ."
                        }
                    }
                }
                stage("Push Docker Image to Docker Hub") {
                    steps {
                        script {
                            try {
                                // Ensure the Docker image exists before pushing
                                def imageExists = sh(script: "docker images -q ${DOCKER_IMAGE_NAME}", returnStdout: true).trim()
            
                                if (!imageExists) {
                                    error "Docker image ${DOCKER_IMAGE_NAME} does not exist. Skipping push."
                                }
            
                                // Log in to Docker Hub
                                sh "echo ${DOCKER_CREDENTIALS_PSW} | docker login -u ${DOCKER_CREDENTIALS_USR} --password-stdin"
            
                                // Push the Docker image
                                sh "docker push ${DOCKER_IMAGE_NAME}"
                                echo "Successfully pushed Docker image '${DOCKER_IMAGE_NAME}' to Docker Hub."
                            } catch (Exception e) {
                                echo "Failed to push Docker image: ${e.getMessage()}"
                                currentBuild.result = 'FAILURE' // Mark build as failed if push fails
                            }
                        }
                    }
                }
            }
        }
        stage("Deploy Kaddem Application") {
            steps {
                script {
                    // Deploys the Kaddem container using the image from Docker Hub
                    sh 'docker compose -f ./docker-compose.yml up -d kaddem-app'
                }
            }
        }
        stage("Stop Restarting Containers") {
            steps {
                script {
                    // Stop the containers that are in a "restarting" state
                    sh 'docker stop naderouali_5sae6_g1-nexus-1 || true'
                    sh 'docker stop naderouali_5sae6_g1-sonarqube-1 || true'
                }
            }
        }
        stage("Run Spring Boot Application as Docker Container") {
            steps {
                script {
                    // Ensure the latest image is pulled from Docker Hub
                    sh "docker pull ${DOCKER_IMAGE_NAME}"
        
                    // Step 1: Clean up any existing containers created from the specified image
                    echo "Stopping and removing any existing containers for '${DOCKER_IMAGE_NAME}'"
                    sh "docker stop \$(docker ps -aqf 'ancestor=${DOCKER_IMAGE_NAME}') || true"
                    sh "docker rm \$(docker ps -aqf 'ancestor=${DOCKER_IMAGE_NAME}') || true"
        
                    // Step 2: Initialize port and retry mechanism
                    def assignedPort = "8089" // Default port
                    int maxRetries = 3
                    int retries = 0
                    boolean started = false
        
                    // Step 3: Attempt to start the container with retries
                    while (!started && retries < maxRetries) {
                        try {
                            // Check if the port is free; assign a different one if needed
                            def portInUse = sh(script: "lsof -i :${assignedPort}", returnStatus: true) == 0
                            if (portInUse) {
                                echo "Port ${assignedPort} is already in use. Assigning a different port."
                                assignedPort = sh(script: "comm -23 <(seq 8000 9000) <(ss -tan | awk '{print \$4}' | cut -d':' -f2) | head -n 1", returnStdout: true).trim()
                                echo "Using dynamic port: ${assignedPort}"
                            } else {
                                echo "Port ${assignedPort} is free. Running container on port ${assignedPort}."
                            }
        
                            // Run the container on the selected port
                            echo "Attempting to start container '${DOCKER_IMAGE_NAME}' on port ${assignedPort} (Attempt ${retries + 1})"
                            sh "docker run -d -p ${assignedPort}:8089 ${DOCKER_IMAGE_NAME}"
                            started = true // If successful, exit the loop
        
                        } catch (Exception e) {
                            // If starting the container fails, retry with a new port
                            echo "Failed to start container on port ${assignedPort}. Retrying with a different port."
                            assignedPort = sh(script: "comm -23 <(seq 8000 9000) <(ss -tan | awk '{print \$4}' | cut -d':' -f2) | head -n 1", returnStdout: true).trim()
                            retries++
                        }
                    }
        
                    // If the container did not start after max retries, fail the build
                    if (!started) {
                        error "Failed to start container after ${maxRetries} attempts."
                    } else {
                        echo "Container started successfully on port ${assignedPort}."
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Build and tests were successful!'
            emailext(
                to: 'oualinader@gmail.com',
                from: 'nader.ouali@esprit.tn', // Set the sender email address
                replyTo: 'nader.ouali@esprit.tn', // Set the reply-to address
                subject: "${env.JOB_NAME} - Build #${env.BUILD_NUMBER} - SUCCESS",
                body: "The build ${env.BUILD_NUMBER} has completed successfully.\nCheck details at: ${env.BUILD_URL}"
            )
        }
        
        failure {
            echo 'Build or tests failed!'
            emailext(
                to: 'oualinader@gmail.com',
                from: 'nader.ouali@esprit.tn', // Set the sender email address
                replyTo: 'nader.ouali@esprit.tn', // Set the reply-to address
                subject: "${env.JOB_NAME} - Build #${env.BUILD_NUMBER} - FAILURE",
                body: "The build ${env.BUILD_NUMBER} has failed.\nCheck details at: ${env.BUILD_URL}"
            )
        }
    }
}
