pipeline {
    agent any
    
    environment {
        DOCKER_HUB_CREDENTIALS = credentials('docker-hub-credentials')
        DOCKER_HUB_USERNAME = 'sujaynsv'  
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
                    sh './mvnw clean package -DskipTests'
                }
                dir('flight-service') {
                    sh './mvnw clean package -DskipTests'
                }
                dir('booking-service') {
                    sh './mvnw clean package -DskipTests'
                }
                dir('email-service') {
                    sh './mvnw clean package -DskipTests'
                }
            }
        }
        
        stage('Build Docker Images') {
            steps {
                echo 'Building Docker images...'
                script {
                    sh 'docker build -t ${DOCKER_HUB_USERNAME}/api-gateway:latest ./api-gateway'
                    sh 'docker build -t ${DOCKER_HUB_USERNAME}/flight-service:latest ./flight-service'
                    sh 'docker build -t ${DOCKER_HUB_USERNAME}/booking-service:latest ./booking-service'
                    sh 'docker build -t ${DOCKER_HUB_USERNAME}/email-service:latest ./email-service'
                }
            }
        }
        
        stage('Push to Docker Hub') {
            steps {
                echo 'Pushing images to Docker Hub...'
                script {
                    sh 'echo $DOCKER_HUB_CREDENTIALS_PSW | docker login -u $DOCKER_HUB_CREDENTIALS_USR --password-stdin'
                    sh 'docker push ${DOCKER_HUB_USERNAME}/api-gateway:latest'
                    sh 'docker push ${DOCKER_HUB_USERNAME}/flight-service:latest'
                    sh 'docker push ${DOCKER_HUB_USERNAME}/booking-service:latest'
                    sh 'docker push ${DOCKER_HUB_USERNAME}/email-service:latest'
                }
            }
        }
        
        stage('Cleanup') {
            steps {
                echo 'Cleaning up...'
                sh 'docker logout'
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
