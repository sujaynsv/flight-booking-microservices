pipeline {
    agent any
    
    tools {
        maven 'Maven_3.9.6'  
    }
    
    environment {
        DOCKER_HUB_CREDENTIALS = credentials('docker-hub-credentials')
        DOCKER_HUB_USERNAME = 'sujaynsv'
        PATH = "/usr/local/bin:${env.PATH}"  
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code...'
                checkout scm
            }
        }
        
        stage('Build JARs') {
            steps {
                echo 'Building JAR files...'
                dir('api-gateway') {
                    sh 'mvn clean package -DskipTests'
                }
                dir('flights-service') {
                    sh 'mvn clean package -DskipTests'
                }
                dir('bookings-service') {
                    sh 'mvn clean package -DskipTests'
                }
                dir('email-service') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }
        
        stage('Build Docker Images') {
            steps {
                echo 'Building Docker images...'
                script {
                    sh '/usr/local/bin/docker build -t ${DOCKER_HUB_USERNAME}/api-gateway:latest ./api-gateway'
                    sh '/usr/local/bin/docker build -t ${DOCKER_HUB_USERNAME}/flights-service:latest ./flights-service'
                    sh '/usr/local/bin/docker build -t ${DOCKER_HUB_USERNAME}/bookings-service:latest ./bookings-service'
                    sh '/usr/local/bin/docker build -t ${DOCKER_HUB_USERNAME}/email-service:latest ./email-service'
                }
            }
        }
        
        stage('Push to Docker Hub') {
            steps {
                echo 'Pushing images to Docker Hub...'
                script {
                    sh 'echo $DOCKER_HUB_CREDENTIALS_PSW | /usr/local/bin/docker login -u $DOCKER_HUB_CREDENTIALS_USR --password-stdin'
                    sh '/usr/local/bin/docker push ${DOCKER_HUB_USERNAME}/api-gateway:latest'
                    sh '/usr/local/bin/docker push ${DOCKER_HUB_USERNAME}/flights-service:latest'
                    sh '/usr/local/bin/docker push ${DOCKER_HUB_USERNAME}/bookings-service:latest'
                    sh '/usr/local/bin/docker push ${DOCKER_HUB_USERNAME}/email-service:latest'
                }
            }
        }
        
        stage('Cleanup') {
            steps {
                echo 'Cleaning up...'
                sh '/usr/local/bin/docker logout'
            }
        }
    }
    
    post {
        always {
            echo 'Pipeline completed!'
        }
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
