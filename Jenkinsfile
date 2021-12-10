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
            when {
                branch 'master'
            }
            steps {
                sh '${M2_HOME}/bin/mvn -B -DskipTests clean package' 
            }
        }
        stage ('Build Docker Image') {
        	when {
                branch 'master'
            }
        	steps{
	        	script {
	                def version = sh script: '${M2_HOME}/bin/mvn help:evaluate -Dexpression=project.version -q -DforceStdout', returnStdout: true
	                dockerImage = "registry-medialab.afp.com/weverify-wrapper:${version}"    
	                println "build image ${dockerImage}"
	                docker.withRegistry('https://'+registry, registryCredential) {
	                	def buidImage = docker.build("${dockerImage}","-f ./docker/delivery/Dockerfile .")
	                	buidImage.push()
	                	buidImage.push('latest')
	                }           	                          
	        	}
              }                
        }
        stage('Cleaning Up') {
           	when {
                branch 'master'
            }
            steps{
                sh "docker rmi --force $dockerImage"
            }
        }
      
    
       

    }
}