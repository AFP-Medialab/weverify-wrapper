pipeline {
    environment {
        registry = "registry-medialab.afp.com"
        registryCredential = "Medialab_Docker_Registry"
        version = ""
        dockerImage = ""
        buidImage = ""
    }
    agent any
    
    stages {
    	stage ('Build package') {
                    
            steps {
                sh '${M2_HOME}/bin/mvn -B -DskipTests clean package' 
                stash includes: 'weverify-wrapper-webapp/target/weverify-wrapper-webapp.jar', name: 'weverify-wrapper-webapp.jar'
            }
        }
        stage ('Build Docker Image') {
        	steps{
	        	script {
	                def version = sh script: '${M2_HOME}/bin/mvn help:evaluate -Dexpression=project.version -q -DforceStdout', returnStdout: true
	                println "version ${version}"
	                unstash 'weverify-wrapper-webapp.jar'
	                def dockerImage = "registry-medialab.afp.com/weverify-wrapper:${version}"    
	                println "image ${dockerImage}"
	                docker.withRegistry('https://'+registry, registryCredential) {
	                	def buidImage = docker.build("${dockerImage}","./docker/delivery")
	                }           	                          
	        	}
              }                
        }

      
    
       

    }
}