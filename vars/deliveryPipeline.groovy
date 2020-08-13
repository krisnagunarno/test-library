#!/usr/bin/env groovy

def call(Map param){
	pipeline {
		agent {
			label "vm-dev"
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
