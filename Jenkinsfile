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
                    sh 'mvn package -DskipTests'
                }
                dir('flights-service') {
                    sh 'mvn package -DskipTests'
                }
                dir('bookings-service') {
                    sh 'mvn package -DskipTests'
                }
                dir('email-service') {
                    sh 'mvn package -DskipTests'
                }
                dir('eureka-server') {
                    sh 'mvn package -DskipTests'
                }
                dir('config-server') {
                    sh 'mvn package -DskipTests'
                }
            }
        }
        
        stage('Stop Old Containers') {
            steps {
                echo 'Stopping old containers...'
                sh '/usr/local/bin/docker-compose down || true'
            }
        }
        
        stage('Build and Start Services') {
            steps {
                echo 'Building images and starting services...'
                sh '/usr/local/bin/docker-compose up -d --build'
            }
        }
        
        stage('Verify Deployment') {
            steps {
                echo 'Verifying services are running...'
                sh 'sleep 30'
                sh '/usr/local/bin/docker-compose ps'
                sh '/usr/local/bin/docker ps'
            }
        }
    }
    
    post {
        always {
            echo 'Pipeline completed!'
        }
        success {
            echo 'Pipeline succeeded! Services are running.'
            sh '/usr/local/bin/docker-compose ps'
        }
        failure {
            echo 'Pipeline failed!'
            sh '/usr/local/bin/docker-compose logs --tail=50'
        }
    }
}
