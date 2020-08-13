#!/usr/bin/env groovy

def getAgent(){
	if(BRANCH_NAME=="master"){
		return "vm-master"
	}
	else{
		return "vm-dev"
	}
}

def call(Map param){
	def agentName = getAgent()
	pipeline {
		agent {
			label "${agentName}"
		}
		stages {
			stage('Build') {
				steps {
					sh 'mvn -B -DskipTests clean package'
				}
			}
			stage('Test') {
				steps {
					sh 'mvn test'
				}
				post {
					always {
						junit 'target/surefire-reports/*.xml'
					}
				}
			}
			stage('Build image') {
				steps {
					sh 'docker build -t my-app .'
				}
			}
			stage('Run app') {
				steps {
					sh 'docker run -p 80:80 my-app'
					telegramSend "Hello World from ${agentName}"
				}
			}
		}
		post {
			always {
				deleteDir()
			}
		}
	}
}
