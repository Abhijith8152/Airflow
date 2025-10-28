pipeline {
    agent any
    
    triggers {
        // Poll SCM every minute (adjust as needed)
        pollSCM('H/1 * * * *')
    }
    
    environment {
        AIRFLOW_DAGS_DIR = '/opt/airflow/dags'
        DOCKER_COMPOSE_FILE = 'C:\\AIRFLOW\\airflow-docker\\docker-compose.yml'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout([$class: 'GitSCM', 
                    branches: [[name: 'refs/heads/release']], 
                    userRemoteConfigs: [[url: 'https://github.com/Abhijith8152/Airflow.git']]
                ])
            }
        }
        
        stage('Check DAG changes') {
            steps {
                script {
                    // Check if any files under dags/ changed in this commit
                    def changedFiles = sh(script: "git diff --name-only HEAD~1 HEAD", returnStdout: true).trim().split('\n')
                    def dagChanged = changedFiles.any { it.startsWith('dags/') }
                    
                    if (!dagChanged) {
                        echo "No changes in DAGs directory. Skipping deployment."
                        currentBuild.result = 'SUCCESS'
                        // Exit early - no need to proceed
                        return
                    } else {
                        echo "Detected DAG changes."
                    }
                }
            }
        }
        
        stage('Copy DAGs') {
            steps {
                sh """
                    echo "Copying DAGs to Airflow directory..."
                    cp -r dags/* ${AIRFLOW_DAGS_DIR}/
                """
            }
        }
        
        stage('Restart Airflow Docker') {
            steps {
                sh """
                    echo "Restarting Airflow Docker containers..."
                    docker compose -f ${DOCKER_COMPOSE_FILE} restart
                """
            }
        }
    }
}
