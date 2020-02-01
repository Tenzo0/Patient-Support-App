#!/bin/bash

TIME="10"
URL="https://api.telegram.org/bot$TELEGRAM_BOT_TOKEN/sendMessage"
TEXT="Deploy status: $1%0A%0A📕Project:+$CI_PROJECT_NAME%0A🌏URL:+$CI_PROJECT_URL/pipelines/$CI_PIPELINE_ID/%0A📌Branch:+$CI_COMMIT_REF_SLUG%0A📋Commit:+$CI_COMMIT_TITLE%0A➡️Commit URL:+$CI_PROJECT_URL/commit/$CI_COMMIT_SHA/%0A%0A🙎‍♂️Job has been started by $GITLAB_USER_LOGIN"

curl -s --max-time $TIME -d "chat_id=$TELEGRAM_USER_ID&disable_web_page_preview=1&text=$TEXT" $URL > /dev/null