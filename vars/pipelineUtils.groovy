#!/usr/bin/env groovy

/**
 * Utility functions for preparing tools and environment data.
 */
def prepareTools() {
    echo "🔧 Installing helper tools..."
    sh 'apk add --no-cache git bash docker-cli'
}

/**
 * Determines which branch, tag, and final Docker tag to use.
 * Returns a map: [branchToUse, imageTagToUse, dockerTagFinal]
 */
def determineBranchEnv(String branchName = null, String targetEnv = null, String imageTag = 'v1.0') {
    def branch = branchName ?: targetEnv ?: 'dev'
    def tag = imageTag ?: 'v1.0'
    def dockerTag = "${branch}-${tag}"

    echo "Using branch/environment: ${branch}"
    echo "Image tag: ${tag}"
    echo "Docker tag: ${dockerTag}"

    return [
        branchToUse   : branch,
        imageTagToUse : tag,
        dockerTagFinal: dockerTag
    ]
}

return this
