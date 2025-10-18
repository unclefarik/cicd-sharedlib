#!/usr/bin/env groovy

/**
 * Docker operations for linting, building, scanning, pushing, and deploying.
 */

def lintDockerfile() {
    echo "Running hadolint..."
    sh '''
        docker run --rm -i hadolint/hadolint:latest < Dockerfile || true
    '''
}

def buildImage(String dockerRepo, String imageMain, String imageDev,
               String branch, String imageTag, String dockerTagFinal) {
    def localImage = (branch == 'main')
        ? "${imageMain}:${imageTag}"
        : "${imageDev}:${imageTag}"

    def hubTag = "${dockerRepo}:${dockerTagFinal}"

    echo "Building image: ${hubTag}"
    sh "docker build -t ${localImage} -t ${hubTag} ."

    echo "Built image ${hubTag}"
    return hubTag
}

def scanImage(String imageRef) {
    echo "Scanning ${imageRef} for vulnerabilities..."
    sh """
        docker run --rm \
          -v \$HOME/.cache/trivy:/root/.cache/ \
          -v /var/run/docker.sock:/var/run/docker.sock \
          aquasec/trivy:latest image \
          --exit-code 1 \
          --severity HIGH,CRITICAL ${imageRef} || true
    """
}

def pushImage(String dockerCreds, String imageRef) {
    echo "Pushing ${imageRef} to Docker Hub..."
    docker.withRegistry('https://index.docker.io/v1/', dockerCreds) {
        sh "docker push ${imageRef}"
    }
    echo "Pushed ${imageRef}"
}

def triggerDeploy(String branch, String imageTag) {
    def deployJob = (branch == 'main') ? 'Deploy_to_main' :
                    (branch == 'dev')  ? 'Deploy_to_dev'  : null

    if (deployJob) {
        echo "Triggering deploy job: ${deployJob}"
        build job: deployJob, parameters: [
            string(name: 'IMAGE_TAG', value: imageTag)
        ]
    } else {
        echo "Unknown branch: ${branch} — skipping deploy."
    }
}

return this
