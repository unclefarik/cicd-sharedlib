def buildAndPush(String dockerRepo, String dockerCreds, String imageMain, String imageDev) {
    script {
        def localImage = (env.BRANCH_TO_USE == 'main') ?
            "${imageMain}:${env.IMAGE_TAG_TO_USE}" :
            "${imageDev}:${env.IMAGE_TAG_TO_USE}"

        def hubTag = "${dockerRepo}:${env.DOCKER_TAG_FINAL}"

        // Build and tag
        sh "docker build -t ${localImage} -t ${hubTag} ."

        // Push to Docker Hub
        docker.withRegistry('https://index.docker.io/v1/', dockerCreds) {
            sh "docker push ${hubTag}"
        }

        echo "✅ Built and pushed ${hubTag}"
        env.IMAGE_NAME_HUB = hubTag
    }
}

def triggerDeploy() {
    if (env.BRANCH_TO_USE == 'main') {
        build job: 'Deploy_to_main', parameters: [
            string(name: 'IMAGE_TAG', value: env.IMAGE_TAG_TO_USE)
        ]
    } else if (env.BRANCH_TO_USE == 'dev') {
        build job: 'Deploy_to_dev', parameters: [
            string(name: 'IMAGE_TAG', value: env.IMAGE_TAG_TO_USE)
        ]
    }
}

